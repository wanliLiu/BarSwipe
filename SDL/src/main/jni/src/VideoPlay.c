//
// Created by Soli on 2017/8/31.
//
/**
 * 使用FFMpeg2和SDL2在android播放音频   视频
 * http://dande618.github.io/blog/2013/10/29/FFMepg/
 */
#include "VideoPlay.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include <libavutil/avstring.h>
#include <SDL.h>
#include <android/log.h>
#include <libavutil/imgutils.h>


//屏幕参数
int SCREEN_W = 1920;
int SCREEN_H = 1080;

//设置buffer输出格式，YUV：1， RGB：0
#define BUFFER_FMT_YUV 0

#define SDL_AUDIO_BUFFER_SIZE 1024
#define MAX_AUDIOQ_SIZE (1 * 1024 * 1024)
#define FF_ALLOC_EVENT   (SDL_USEREVENT)
#define FF_REFRESH_EVENT (SDL_USEREVENT + 1)
#define FF_QUIT_EVENT (SDL_USEREVENT + 2)
#define AVCODEC_MAX_AUDIO_FRAME_SIZE 192000

//使用NDK的log
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"ERROR: ", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"INFO: ", __VA_ARGS__)

/**
 *
 */
typedef struct PacketQueue {
    AVPacketList *first_pkt, *last_pkt;
    int nb_packets;
    int size;
    SDL_mutex *mutex;
    SDL_cond *cond;
} PacketQueue;


/**
 *
 */
typedef struct VideoState {
    char filename[1024];
    AVFormatContext *ic;
    int videoStream, audioStream;
    AVStream *audio_st;
    AVFrame *audio_frame;
    PacketQueue audioq;
    unsigned int audio_buf_size;
    unsigned int audio_buf_index;
    AVCodecContext *pCodecContext;
    AVPacket audio_pkt;
    uint8_t *audio_pkt_data;
    int audio_pkt_size;
    uint8_t *audio_buf;
    uint8_t *audio_buf1;
    DECLARE_ALIGNED(16, uint8_t, audio_buf2)[AVCODEC_MAX_AUDIO_FRAME_SIZE * 4];
    enum AVSampleFormat audio_src_fmt;
    enum AVSampleFormat audio_tgt_fmt;
    int audio_src_channels;
    int audio_tgt_channels;
    int64_t audio_src_channel_layout;
    int64_t audio_tgt_channel_layout;
    int audio_src_freq;
    int audio_tgt_freq;
    struct SwrContext *swr_ctx;
    SDL_Thread *parse_tid;
    int quit;
} VideoState;

VideoState *global_video_state;

/**
 *
 * @param q
 */
static void packet_queue_init(PacketQueue *q) {
    memset(q, 0, sizeof(PacketQueue));
    q->mutex = SDL_CreateMutex();
    q->cond = SDL_CreateCond();
}

/**
 *向队列中添加元素
 * @param q
 * @param pkt
 * @return
 */
static int packet_queue_put(PacketQueue *q, AVPacket *pkt) {
    AVPacketList *pkt1;

    pkt1 = (AVPacketList *) av_malloc(sizeof(AVPacketList));
    if (!pkt1) {
        return -1;
    }
    pkt1->pkt = *pkt;
    pkt1->next = NULL;

    SDL_LockMutex(q->mutex);

    if (!q->last_pkt) {
        //第一次的时候，也就是插入第一个元素
        q->first_pkt = pkt1;
    } else {
        q->last_pkt->next = pkt1;
    }

    q->last_pkt = pkt1;
    q->nb_packets++;
    q->size += pkt1->pkt.size;
    SDL_CondSignal(q->cond);
    SDL_UnlockMutex(q->mutex);
    return 0;
}

/**
 * 从队列中取出元素
 * @param q
 * @param pkt
 * @param block
 * @return
 */
static int packet_queue_get(PacketQueue *q, AVPacket *pkt, int block) {
    AVPacketList *pkt1;
    int ret;

    SDL_LockMutex(q->mutex);

    for (;;) {
        if (global_video_state->quit) {
            ret = -1;
            break;
        }

        pkt1 = q->first_pkt;
        if (pkt1) {
            q->first_pkt = pkt1->next;
            if (!q->first_pkt) {
                q->last_pkt = NULL;
            }
            q->nb_packets--;
            q->size -= pkt1->pkt.size;
            *pkt = pkt1->pkt;

            av_free(pkt1);
            ret = 1;
            break;
        } else if (!block) {
            ret = 0;
            break;
        } else {
            SDL_CondWait(q->cond, q->mutex);
        }
    }

    SDL_UnlockMutex(q->mutex);

    return ret;
}

/**
 * 清楚整个队列
 * @param q
 */
static void packet_queue_flush(PacketQueue *q) {
    AVPacketList *pkt, *pkt1;

    SDL_LockMutex(q->mutex);
    for (pkt = q->first_pkt; pkt != NULL; pkt = pkt1) {
        pkt1 = pkt->next;
        av_packet_unref(&pkt->pkt);
        av_freep(&pkt);
    }
    q->last_pkt = NULL;
    q->first_pkt = NULL;
    q->nb_packets = 0;
    q->size = 0;
    SDL_UnlockMutex(q->mutex);
}

/**
 *
 * @param is
 * @return
 */
static int audio_decode_frame(VideoState *is) {
    int len1, len2, decoded_data_size;
    AVPacket *pkt = &is->audio_pkt;
    int got_frame = 0;
    int64_t dec_channel_layout;
    int wanted_nb_samples, resampled_data_size;

    for (;;) {
        while (is->audio_pkt_size > 0) {
            if (!is->audio_frame) {
                if (!(is->audio_frame = av_frame_alloc())) {
                    return AVERROR(ENOMEM);
                }
            } else
                av_frame_unref(is->audio_frame);

//            int get = avcodec_send_packet()
            len1 = avcodec_decode_audio4(is->audio_st->codec, is->audio_frame, &got_frame, pkt);
            if (len1 < 0) {
                // error, skip the frame
                is->audio_pkt_size = 0;
                break;
            }

            is->audio_pkt_data += len1;
            is->audio_pkt_size -= len1;

            if (!got_frame)
                continue;

            decoded_data_size = av_samples_get_buffer_size(NULL,
                                                           is->audio_frame->channels,
                                                           is->audio_frame->nb_samples,
                                                           is->audio_frame->format, 1);

            dec_channel_layout = (is->audio_frame->channel_layout && is->audio_frame->channels
                                                                     ==
                                                                     av_get_channel_layout_nb_channels(
                                                                             is->audio_frame->channel_layout))
                                 ? is->audio_frame->channel_layout
                                 : av_get_default_channel_layout(is->audio_frame->channels);

            wanted_nb_samples = is->audio_frame->nb_samples;

            //fprintf(stderr, "wanted_nb_samples = %d\n", wanted_nb_samples);

            if (is->audio_frame->format != is->audio_src_fmt ||
                dec_channel_layout != is->audio_src_channel_layout ||
                is->audio_frame->sample_rate != is->audio_src_freq ||
                (wanted_nb_samples != is->audio_frame->nb_samples && !is->swr_ctx)) {
                if (is->swr_ctx)
                    swr_free(&is->swr_ctx);
                is->swr_ctx = swr_alloc_set_opts(NULL,
                                                 is->audio_tgt_channel_layout,
                                                 is->audio_tgt_fmt,
                                                 is->audio_tgt_freq,
                                                 dec_channel_layout,
                                                 is->audio_frame->format,
                                                 is->audio_frame->sample_rate,
                                                 0, NULL);
                if (!is->swr_ctx || swr_init(is->swr_ctx) < 0) {
                    fprintf(stderr, "swr_init() failed\n");
                    break;
                }
                is->audio_src_channel_layout = dec_channel_layout;
                is->audio_src_channels = is->audio_st->codec->channels;
                is->audio_src_freq = is->audio_st->codec->sample_rate;
                is->audio_src_fmt = is->audio_st->codec->sample_fmt;
            }
            if (is->swr_ctx) {
                // const uint8_t *in[] = { is->audio_frame->data[0] };
                const uint8_t **in = (const uint8_t **) is->audio_frame->extended_data;
                uint8_t *out[] = {is->audio_buf2};
                if (wanted_nb_samples != is->audio_frame->nb_samples) {
                    if (swr_set_compensation(is->swr_ctx,
                                             (wanted_nb_samples - is->audio_frame->nb_samples)
                                             * is->audio_tgt_freq / is->audio_frame->sample_rate,
                                             wanted_nb_samples * is->audio_tgt_freq /
                                             is->audio_frame->sample_rate) < 0) {
                        fprintf(stderr, "swr_set_compensation() failed\n");
                        break;
                    }
                }

                len2 = swr_convert(is->swr_ctx, out,
                                   sizeof(is->audio_buf2)
                                   / is->audio_tgt_channels
                                   / av_get_bytes_per_sample(is->audio_tgt_fmt),
                                   in, is->audio_frame->nb_samples);
                if (len2 < 0) {
                    fprintf(stderr, "swr_convert() failed\n");
                    break;
                }
                if (len2 == sizeof(is->audio_buf2) / is->audio_tgt_channels /
                            av_get_bytes_per_sample(is->audio_tgt_fmt)) {
                    fprintf(stderr, "warning: audio buffer is probably too small\n");
                    swr_init(is->swr_ctx);
                }
                is->audio_buf = is->audio_buf2;
                resampled_data_size =
                        len2 * is->audio_tgt_channels * av_get_bytes_per_sample(is->audio_tgt_fmt);
            } else {
                resampled_data_size = decoded_data_size;
                is->audio_buf = is->audio_frame->data[0];
            }
            // We have data, return it and come back for more later
            return resampled_data_size;
        }

        if (pkt->data)
            av_packet_unref(pkt);
        memset(pkt, 0, sizeof(*pkt));
        if (is->quit)
            return -1;
        if (packet_queue_get(&is->audioq, pkt, 1) < 0)
            return -1;

        is->audio_pkt_data = pkt->data;
        is->audio_pkt_size = pkt->size;
    }
}

/**
 *
 * @param userdata
 * @param stream
 * @param len
 */
void audio_callback(void *userdata, Uint8 *stream, int len) {
    VideoState *is = (VideoState *) userdata;
    int len1, audio_data_size;

    while (len > 0) {
        if (is->audio_buf_index >= is->audio_buf_size) {
            audio_data_size = audio_decode_frame(is);

            if (audio_data_size < 0) {
                /* silence */
                is->audio_buf_size = 1024;
                memset(is->audio_buf, 0, is->audio_buf_size);
            } else {
                is->audio_buf_size = audio_data_size;
            }
            is->audio_buf_index = 0;
        }

        len1 = is->audio_buf_size - is->audio_buf_index;
        if (len1 > len) {
            len1 = len;
        }

        memcpy(stream, (uint8_t *) is->audio_buf + is->audio_buf_index, len1);
        len -= len1;
        stream += len1;
        is->audio_buf_index += len1;
    }
}

/**
 *
 * @param is
 * @param stream_index
 * @return
 */
int stream_component_open(VideoState *is, int stream_index) {
    AVFormatContext *ic = is->ic;
    AVCodecContext *codecCtx;
    AVCodec *codec;
    SDL_AudioSpec wanted_spec, spec;
    int64_t wanted_channel_layout = 0;
    int wanted_nb_channels;
    const int next_nb_channels[] = {0, 0, 1, 6, 2, 6, 4, 6};

    if (stream_index < 0 || stream_index >= ic->nb_streams) {
        return -1;
    }

    codecCtx = ic->streams[stream_index]->codec;
    wanted_nb_channels = codecCtx->channels;
    if (!wanted_channel_layout ||
        wanted_nb_channels != av_get_channel_layout_nb_channels(wanted_channel_layout)) {
        wanted_channel_layout = av_get_default_channel_layout(wanted_nb_channels);
        wanted_channel_layout &= ~AV_CH_LAYOUT_STEREO_DOWNMIX;
    }

    wanted_spec.channels = av_get_channel_layout_nb_channels(wanted_channel_layout);
    wanted_spec.freq = codecCtx->sample_rate;
    if (wanted_spec.freq <= 0 || wanted_spec.channels <= 0) {
        fprintf(stderr, "Invalid sample rate or channel count!\n");
        return -1;
    }
    wanted_spec.format = AUDIO_S16SYS;
    wanted_spec.silence = 0;
    wanted_spec.samples = SDL_AUDIO_BUFFER_SIZE;
    wanted_spec.callback = audio_callback;
    wanted_spec.userdata = is;

    //输入我们希望的参数 初始化成功 获取实际使用的参数
    while (SDL_OpenAudio(&wanted_spec, &spec) < 0) {
        fprintf(stderr, "SDL_OpenAudio (%d channels): %s\n", wanted_spec.channels, SDL_GetError());
        wanted_spec.channels = next_nb_channels[FFMIN(7, wanted_spec.channels)];
        if (!wanted_spec.channels) {
            fprintf(stderr, "No more channel combinations to tyu, audio open failed\n");
            return -1;
        }
        wanted_channel_layout = av_get_default_channel_layout(wanted_spec.channels);
    }

    if (spec.format != AUDIO_S16SYS) {
        fprintf(stderr, "SDL advised audio format %d is not supported!\n", spec.format);
        return -1;
    }
    if (spec.channels != wanted_spec.channels) {
        wanted_channel_layout = av_get_default_channel_layout(spec.channels);
        if (!wanted_channel_layout) {
            fprintf(stderr, "SDL advised channel count %d is not supported!\n", spec.channels);
            return -1;
        }
    }

    fprintf(stderr, "%d: wanted_spec.format = %d\n", __LINE__, wanted_spec.format);
    fprintf(stderr, "%d: wanted_spec.samples = %d\n", __LINE__, wanted_spec.samples);
    fprintf(stderr, "%d: wanted_spec.channels = %d\n", __LINE__, wanted_spec.channels);
    fprintf(stderr, "%d: wanted_spec.freq = %d\n", __LINE__, wanted_spec.freq);

    fprintf(stderr, "%d: spec.format = %d\n", __LINE__, spec.format);
    fprintf(stderr, "%d: spec.samples = %d\n", __LINE__, spec.samples);
    fprintf(stderr, "%d: spec.channels = %d\n", __LINE__, spec.channels);
    fprintf(stderr, "%d: spec.freq = %d\n", __LINE__, spec.freq);

    is->audio_src_fmt = is->audio_tgt_fmt = AV_SAMPLE_FMT_S16;
    is->audio_src_freq = is->audio_tgt_freq = spec.freq;
    is->audio_src_channel_layout = is->audio_tgt_channel_layout = wanted_channel_layout;
    is->audio_src_channels = is->audio_tgt_channels = spec.channels;

    codec = avcodec_find_decoder(codecCtx->codec_id);
    if (!codec || (avcodec_open2(codecCtx, codec, NULL) < 0)) {
        fprintf(stderr, "Unsupported codec!\n");
        return -1;
    }

    ic->streams[stream_index]->discard = AVDISCARD_DEFAULT;
    switch (codecCtx->codec_type) {
        case AVMEDIA_TYPE_AUDIO:
            is->audioStream = stream_index;
            is->audio_st = ic->streams[stream_index];
            is->audio_buf_size = 0;
            is->audio_buf_index = 0;
            memset(&is->audio_pkt, 0, sizeof(is->audio_pkt));
            packet_queue_init(&is->audioq);
            SDL_PauseAudio(0);
            break;
        case AVMEDIA_TYPE_VIDEO:
            break;
        default:
            break;
    }
}

/*
static void stream_component_close(VideoState *is, int stream_index) {
        AVFormatContext *oc = is->;
        AVCodecContext *avctx;

        if(stream_index < 0 || stream_index >= ic->nb_streams)        return;
        avctx = ic->streams[stream_index]->codec;

}
*/

/**
 *
 * @param arg
 * @return
 */
static int decode_thread(void *arg) {
    VideoState *is = (VideoState *) arg;
    AVFormatContext *ic = NULL;
    AVPacket pkt1, *packet = &pkt1;
    int ret, i, audio_index = -1, video_index = -1;

    is->audioStream = -1;
    global_video_state = is;
    if (avformat_open_input(&ic, is->filename, NULL, NULL) != 0) {
        return -1;
    }
    is->ic = ic;
    if (avformat_find_stream_info(ic, NULL) < 0) {
        return -1;
    }
    av_dump_format(ic, 0, is->filename, 0);
    for (i = 0; i < ic->nb_streams; i++) {
        if (ic->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO && audio_index < 0) {
            audio_index = i;
        } else if (ic->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO && video_index < 0) {
            video_index = i;
        }
    }


    if (audio_index >= 0) {
        stream_component_open(is, audio_index);
    }

    if (is->audioStream < 0) {
        fprintf(stderr, "%s: could not open codecs\n", is->filename);
        goto fail;
    }
    // main decode loop
    for (;;) {
        if (is->quit) break;
        if (is->audioq.size > MAX_AUDIOQ_SIZE) {
            SDL_Delay(10);
            continue;
        }
        ret = av_read_frame(is->ic, packet);
        if (ret < 0) {
            if (ret == AVERROR_EOF || avio_feof(is->ic->pb)) {
                break;
            }
            if (is->ic->pb && is->ic->pb->error) {
                break;
            }
            continue;
        }

        if (packet->stream_index == is->audioStream) {
            packet_queue_put(&is->audioq, packet);
        } else {
            av_packet_unref(packet);
        }
    }

    while (!is->quit) {
        SDL_Delay(100);
    }

    fail:
    {
        SDL_Event event;
        event.type = FF_QUIT_EVENT;
        event.user.data1 = is;
        SDL_PushEvent(&event);
    }

    return 0;
}

/**
 * 把FFmpeg的日志打印输出到adb
 * @param ptr
 * @param level
 * @param fmt
 * @param vl
 */
static void log_callback_null(void *ptr, int level, const char *fmt, va_list vl) {
    static int print_prefix = 1;
    static int count;
    static char prev[1024];
    char line[1024];
    static int is_atty;

    av_log_format_line(ptr, level, fmt, vl, line, sizeof(line), &print_prefix);

    strcpy(prev, line);
    //sanitize((uint8_t *)line);

//    switch (level) {
//        case AV_LOG_DEBUG:
//            __android_log_print(ANDROID_LOG_DEBUG, "ffmpeg: ", "%s", line);
//            break;
//        case AV_LOG_ERROR:
    __android_log_print(ANDROID_LOG_ERROR, "ffmpeg: ", "%s", line);
//            break;
//        case AV_LOG_INFO:
//            __android_log_print(ANDROID_LOG_INFO, "ffmpeg: ", "%s", line);
//            break;
//        case AV_LOG_WARNING:
//            __android_log_print(ANDROID_LOG_WARN, "ffmpeg: ", "%s", line);
//            break;
//    }
}

/**
 *
 * @return
 */
static int play_video() {

    SDL_Event event;
    //FFmpeg Parameters
    AVFormatContext *pFormatCtx;
    int videoStream, audioStream;
    AVCodecContext *pCodecCtx;
    AVCodecParameters *avCodecParameters;
    AVCodec *pCodec;
    AVFrame *pFrame, *pFrame_out;
    AVPacket *packet;

    //size buffer
    uint8_t *out_buffer;

    static struct SwsContext *img_convert_ctx;

//SDL Parameters
    SDL_Window *sdlWindow;
    SDL_Texture *sdlTexture;
    SDL_Rect sdlRect;
    SDL_Renderer *renderer;
//    SDL_Event event;

//    把FFmpeg的日志打印输出到adb
    av_log_set_callback(log_callback_null);

    //获取文件名
    const char *mediaUri = "/storage/emulated/0/convexd/1989823-102-086-0009.mp4";
//    const char *mediaUri = "http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";

    av_register_all();//注册所有支持的文件格式以及编解码器

    //分配一个AVFormatContext
    pFormatCtx = avformat_alloc_context();

    //判断文件流是否能打开
    if (avformat_open_input(&pFormatCtx, mediaUri, NULL, NULL) != 0) {
        LOGE("Couldn't open input stream! \n");
        return -1;
    }
    //判断能够找到文件流信息
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGE("couldn't find open stream information !\n");
        return -1;
    }
    //打印文件信息
    av_dump_format(pFormatCtx, -1, mediaUri, 0);
    videoStream = audioStream = -1;
    for (int i = 0; i < pFormatCtx->nb_streams; i++)
        //新版本ffmpeg将AVCodecContext *codec替换成*codecpar
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = i;
        } else if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO)
            audioStream = i;

    if (videoStream == -1 || audioStream == -1) {
        LOGE("Couldn't find a video stream or audio stream!\n");
        return -1;
    }


    // Get a pointer to the codec context for the video stream
    avCodecParameters = pFormatCtx->streams[videoStream]->codecpar;

    // Find the decoder for the video stream
    pCodec = avcodec_find_decoder(avCodecParameters->codec_id);


    if (pCodec) {
        LOGI("find decoder: %d", avCodecParameters->codec_id);
    }

    //alloc a codecContext
    pCodecCtx = avcodec_alloc_context3(pCodec);


    //transform
    if (avcodec_parameters_to_context(pCodecCtx, avCodecParameters) < 0) {
        LOGE("copy the codec parameters to context fail!");
        return -1;
    }
    //打开codec
    int errorCode = avcodec_open2(pCodecCtx, pCodec, NULL);
    if (errorCode < 0) {
        LOGE("Unable to open codec!\n");
        return -1;
    };

    //alloc frame of ffmpeg decode
    pFrame = av_frame_alloc();

    if (pFrame == NULL) {
        LOGE("Unable to allocate an AVFrame!\n");
        return -1;
    }

    //decode packet
    packet = av_packet_alloc();

    pFrame_out = av_frame_alloc();
#if BUFFER_FMT_YUV
    //output frame for SDL
    enum AVPixelFormat pixel_fmt = AV_PIX_FMT_YUV420P;

#else
    //output RGBFrame
    enum AVPixelFormat pixel_fmt = AV_PIX_FMT_RGBA;
#endif
    out_buffer = av_malloc(
            (size_t) av_image_get_buffer_size(pixel_fmt, pCodecCtx->width, pCodecCtx->height, 1));
    av_image_fill_arrays(pFrame_out->data, pFrame_out->linesize, out_buffer,
                         pixel_fmt, pCodecCtx->width, pCodecCtx->height, 1);
    //transform size and format of CodecCtx,立方插值
    img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
                                     pCodecCtx->width, pCodecCtx->height, pixel_fmt,
                                     SWS_BICUBIC, NULL,
                                     NULL, NULL);

    //初始化SDL
    if (SDL_Init(SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER)) {
        LOGE("SDL_Init failed %s", SDL_GetError());
        exit(1);
    }

    //设置window属性
    sdlWindow = SDL_CreateWindow("Convexd_SDL", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
                                 SCREEN_W, SCREEN_H,
                                 SDL_WINDOW_RESIZABLE | SDL_WINDOW_FULLSCREEN | SDL_WINDOW_OPENGL);

    if (!sdlWindow) {
        LOGE("SDL: could not set video mode - exiting\n");
        exit(1);
    }
    //create renderer and set parameter
    renderer = SDL_CreateRenderer(sdlWindow, -1, 0);

#if BUFFER_FMT_YUV
    Uint32 sdl_out_fmt = SDL_PIXELFORMAT_IYUV;
#else
    Uint32 sdl_out_fmt = SDL_PIXELFORMAT_RGBA32;
#endif
    //Allocate a place to put our yuv image on that screen,set sdl_display_resolution
    sdlTexture = SDL_CreateTexture(renderer,
                                   sdl_out_fmt, SDL_TEXTUREACCESS_STREAMING,
                                   pCodecCtx->width, pCodecCtx->height);
//    默认全屏可以不用设置
//    sdlRect.x = 0;
//    sdlRect.y = 0;
//    sdlRect.w = SCREEN_W;
//    sdlRect.h = SCREEN_H;

    // Read frames
    while (av_read_frame(pFormatCtx, packet) >= 0) {
        // Is this a packet from the video stream?
        if (packet->stream_index == videoStream) {
            //decoder allocate frame to pFrame,new api
            LOGI("%s", "Got Video Packet Succeed");
            int getPacketCode = avcodec_send_packet(pCodecCtx, packet);
            if (getPacketCode == 0) {
                int getFrameCode = avcodec_receive_frame(pCodecCtx, pFrame);
                LOGI("%d", getFrameCode);
                // Did we get a video frame?
                if (getFrameCode == 0) {
                    LOGI("%s", "Got Video Frame Succeed");

                    //scale Frame
                    sws_scale(img_convert_ctx, (const uint8_t *const *) pFrame->data,
                              pFrame->linesize, 0, pFrame->height,
                              pFrame_out->data, pFrame_out->linesize);
#if (BUFFER_FMT_YUV == 1)

                    SDL_UpdateYUVTexture(sdlTexture, NULL, pFrame_out->data[0], pFrame_out->linesize[0],
                                         pFrame_out->data[1],pFrame_out->linesize[1],
                                         pFrame_out->data[2],pFrame_out->linesize[2]);
#else
                    SDL_UpdateTexture(sdlTexture, NULL, pFrame_out->data[0],
                                      pCodecCtx->width * 4);  //4通道：pitch = width×4
#endif
                    SDL_RenderClear(renderer);
                    SDL_RenderCopy(renderer, sdlTexture, NULL, NULL);
                    SDL_RenderPresent(renderer);
                    //设置每秒25帧，1000/25 = 40
                    SDL_Delay(40);
                    SDL_PollEvent(&event);
                    switch (event.type) {
                        case SDL_QUIT:
                            SDL_Quit();
                            exit(0);
                        case SDL_KEYDOWN:
                            SDL_Quit();
                            exit(0);
                        default:
                            break;
                    }
                } else if (getFrameCode == AVERROR(EAGAIN)) {
                    LOGE("%s", "Frame is not available right now,please try another input");
                } else if (getFrameCode == AVERROR_EOF) {
                    LOGE("%s", "the decoder has been fully flushed");
                } else if (getFrameCode == AVERROR(EINVAL)) {
                    LOGE("%s", "codec not opened, or it is an encoder");
                } else {
                    LOGI("%s", "legitimate decoding errors");
                }
            }
        }
        // Free the packet that was allocated by av_read_frame
        av_packet_unref(packet);
    }
    sws_freeContext(img_convert_ctx);
    av_frame_free(&pFrame_out);
    av_frame_free(&pFrame);
    avcodec_close(pCodecCtx);
    avformat_close_input(&pFormatCtx);
    //   SDL_Quit();
}

/**
 *
 * @return
 */
static int play_audio() {
    __android_log_print(ANDROID_LOG_INFO, "SDL", "%d", avcodec_version());
    SDL_Event event;
    VideoState *is;

    av_register_all();
    avformat_network_init();

//    把FFmpeg的日志打印输出到adb
    av_log_set_callback(log_callback_null);

    is = (VideoState *) av_mallocz(sizeof(VideoState));

//    if (argc < 2) {
//       fprintf(stderr, "Usage: test <file>\n");
//        exit(1);
//    }

    if (SDL_Init(SDL_INIT_AUDIO)) {
        fprintf(stderr, "Could not initialize SDL - %s\n", SDL_GetError());
        exit(1);
    }

//    const char *Url = "http://video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";
    const char *Url = "/storage/emulated/0/convexd/1989823-102-086-0009.mp4";
    av_strlcpy(is->filename, Url, sizeof(is->filename));

    const char *name = "Particles";
    is->parse_tid = SDL_CreateThread(decode_thread, name, is);
    if (!is->parse_tid) {
        av_free(is);
        return -1;
    }

    for (;;) {
        SDL_WaitEvent(&event);
        switch (event.type) {
            case FF_QUIT_EVENT:
            case SDL_QUIT:
                is->quit = 1;
                SDL_Quit();
                exit(0);
                break;
            default:
                break;
        }
    }
}

#define video_play 0
#define audio_play 1

void VideoPlay(int argc, char *argv[]) {

#if audio_play
    play_audio();
#endif

#if video_play
    play_video();
#endif

    return;
}