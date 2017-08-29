#include <stdlib.h>

#include "SDL.h"
#include <android/log.h>
#include "Video_sdl.h"
#include <libavformat/avformat.h>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"SDL_study", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"SDL_study", __VA_ARGS__)

typedef struct Sprite {
    SDL_Texture *texture;
    Uint16 w;
    Uint16 h;
} Sprite;


/* Adapted from SDL's testspriteminimal.c */
static Sprite
LoadSprite(const char *file, SDL_Renderer *renderer) {
    Sprite result;
    result.texture = NULL;
    result.w = 0;
    result.h = 0;

    SDL_Surface *temp;

    /* Load the sprite image */
    temp = SDL_LoadBMP(file);
    if (temp == NULL) {
        fprintf(stderr, "Couldn't load %s: %s\n", file, SDL_GetError());
        return result;
    }
    result.w = temp->w;
    result.h = temp->h;

    /* Create texture from the image */
    result.texture = SDL_CreateTextureFromSurface(renderer, temp);
    if (!result.texture) {
        fprintf(stderr, "Couldn't create texture: %s\n", SDL_GetError());
        SDL_FreeSurface(temp);
        return result;
    }
    SDL_FreeSurface(temp);

    return result;
}

static void
draw(SDL_Window *window, SDL_Renderer *renderer, const Sprite sprite) {
    int w, h;
    SDL_GetWindowSize(window, &w, &h);
    SDL_Rect destRect = {w / 2 - sprite.w / 2, h / 2 - sprite.h / 2, sprite.w, sprite.h};
    /* Blit the sprite onto the screen */
    SDL_RenderCopy(renderer, sprite.texture, NULL, &destRect);
    //测试
    SDL_RenderDrawLine(renderer, 0, h / 2, w, h / 2);
}

/**
 *
 * @param argc
 * @param argv
 * @return
 */
static int
displayBmp(int argc, char *argv[]) {

    SDL_Window *window;
    SDL_Renderer *renderer;

    if (SDL_CreateWindowAndRenderer(0, 0, 0, &window, &renderer) < 0)
        exit(2);

    Sprite sprite = LoadSprite("image.bmp", renderer);
    if (sprite.texture == NULL)
        exit(2);

    /* Main render loop */
    Uint8 done = 0;
    SDL_Event event;
    while (!done) {
        /* Check for events */
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT || event.type == SDL_KEYDOWN ||
                event.type == SDL_FINGERDOWN) {
                done = 1;
            }
        }

        /* Draw a gray background */
        SDL_SetRenderDrawColor(renderer, 0xC8, 0x64, 0x21, 0x1A);//C864211A
        SDL_RenderClear(renderer);

        draw(window, renderer, sprite);

        /* Update the screen! */
        SDL_RenderPresent(renderer);

        SDL_Delay(10);
    }


    //这里相当于java 的System.exit(0);
    exit(0);
}

//*******************************Audio play************************/

//Buffer:
//|-----------|-------------|
//chunk-------pos---len-----|
static Uint8 *audio_chunk;
static Uint32 audio_len;
static Uint8 *audio_pos;

/* Audio Callback
 * The audio function callback takes the following parameters:
 * stream: A pointer to the audio buffer to be filled
 * len: The length (in bytes) of the audio buffer
 *
*/
static void
fill_audio(void *udata, Uint8 *stream, int len) {
    //SDL 2.0
    SDL_memset(stream, 0, len);

    if (audio_len == 0)        /*  Only  play  if  we  have  data  left  */
        return;
    len = (len > audio_len ? audio_len : len);    /*  Mix  as  much  data  as  possible  */

    SDL_MixAudio(stream, audio_pos, len, SDL_MIX_MAXVOLUME);

    audio_pos += len;
    audio_len -= len;
}

/**
 * 播放音频
 * @param argv
 * @param argv
 * @return
 */
static int
playPCMAudio(int argc, char *argv[]) {
    //Init
    if (SDL_Init(SDL_INIT_AUDIO | SDL_INIT_TIMER)) {
        LOGE("Could not initialize SDL - %s\n", SDL_GetError());
        return -1;
    }
    //SDL_AudioSpec
    SDL_AudioSpec wanted_spec;
    wanted_spec.freq = 44100;
    wanted_spec.format = AUDIO_S16SYS;
    wanted_spec.channels = 2;
    wanted_spec.silence = 0;
    wanted_spec.samples = 1024;
    wanted_spec.callback = fill_audio;

    if (SDL_OpenAudio(&wanted_spec, NULL) < 0) {
        LOGE("can't open audio.\n");
        return -1;
    }

    FILE *fp = fopen("/storage/emulated/0/convexd/audio.pcm", "rb+");
    if (fp == NULL) {
        LOGE("cannot open this file\n");
        return -1;
    }
    //For YUV420P
    int pcm_buffer_size = 4096;
    char *pcm_buffer = (char *) malloc(pcm_buffer_size);
    int data_count = 0;


    Uint8 done = 0;
    SDL_Event event;
    while (!done) {

        /* Check for events */
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT || event.type == SDL_KEYDOWN ||
                event.type == SDL_FINGERDOWN) {
                done = 1;
            }
        }

        if (fread(pcm_buffer, 1, pcm_buffer_size, fp) != pcm_buffer_size) {
            // Loop
            fseek(fp, 0, SEEK_SET);
            fread(pcm_buffer, 1, pcm_buffer_size, fp);
            data_count = 0;
        }
        LOGE("Now Playing %10d Bytes data.\n", data_count);
        data_count += pcm_buffer_size;
        //Set audio buffer (PCM data)
        audio_chunk = (Uint8 *) pcm_buffer;
        //Audio buffer length
        audio_len = pcm_buffer_size;
        audio_pos = audio_chunk;
        //Play
        SDL_PauseAudio(0);
        while (audio_len > 0)//Wait until finish
            SDL_Delay(1);


    }

    return 0;
}

/**
 *
 * @param argc
 * @param argv
 */
static void
displayAll(int argc, char *argv[]) {
    //Init
    if (SDL_Init(SDL_INIT_AUDIO | SDL_INIT_TIMER)) {
        LOGE("Could not initialize SDL - %s\n", SDL_GetError());
        return;
    }


    SDL_Window *window;
    SDL_Renderer *renderer;

    if (SDL_CreateWindowAndRenderer(0, 0, 0, &window, &renderer) < 0)
        exit(2);

    Sprite sprite = LoadSprite("image.bmp", renderer);
    if (sprite.texture == NULL)
        exit(2);


    //SDL_AudioSpec
    SDL_AudioSpec wanted_spec;
    wanted_spec.freq = 44100;
    wanted_spec.format = AUDIO_S16SYS;
    wanted_spec.channels = 2;
    wanted_spec.silence = 0;
    wanted_spec.samples = 1024;
    wanted_spec.callback = fill_audio;

    if (SDL_OpenAudio(&wanted_spec, NULL) < 0) {
        LOGE("can't open audio.\n");
        return;
    }

    FILE *fp = fopen("/storage/emulated/0/convexd/audio.pcm", "rb+");
    if (fp == NULL) {
        LOGE("cannot open this file\n");
        return;
    }
    //For YUV420P
    int pcm_buffer_size = 4096;
    char *pcm_buffer = (char *) malloc(pcm_buffer_size);
    int data_count = 0;


    Uint8 done = 0;
    SDL_Event event;
    while (!done) {
        /* Check for events */
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT || event.type == SDL_KEYDOWN ||
                event.type == SDL_FINGERDOWN) {
                done = 1;
            }
        }
        /* Draw a gray background */
        SDL_SetRenderDrawColor(renderer, 0xC8, 0x64, 0x21, 0x1A);//C864211A
        SDL_RenderClear(renderer);

        draw(window, renderer, sprite);

        /* Update the screen! */
        SDL_RenderPresent(renderer);

        if (fread(pcm_buffer, 1, pcm_buffer_size, fp) != pcm_buffer_size) {
            // Loop
            fseek(fp, 0, SEEK_SET);
            fread(pcm_buffer, 1, pcm_buffer_size, fp);
            data_count = 0;
        }
        LOGE("Now Playing %10d Bytes data.\n", data_count);
        data_count += pcm_buffer_size;
        //Set audio buffer (PCM data)
        audio_chunk = (Uint8 *) pcm_buffer;
        //Audio buffer length
        audio_len = pcm_buffer_size;
        audio_pos = audio_chunk;
        //Play
        SDL_PauseAudio(0);
        while (audio_len > 0)//Wait until finish
            SDL_Delay(1);
    }

    exit(0);
}

static void
ifFFmpegIsOkay() {
    char info[40000] = {0};
    av_register_all();
    AVInputFormat *if_temp = av_iformat_next(NULL);
    while (if_temp) {
        sprintf(info, "%s,%s", info, if_temp->name);
        if_temp = if_temp->next;
    }
    sprintf(info, "%s\n%s\n", info, "输出");
    AVOutputFormat *of_temp = av_oformat_next(NULL);
    while (of_temp) {
        sprintf(info, "%s,%s", info, of_temp->name);
        of_temp = of_temp->next;
    }


    LOGE("%s", info);
    LOGE("Ffmpeg is Okay,free to go");
}

#define DisplayBmp 0
#define AudioPlay 0
#define VideoPlay 1

/**
 * 入口函数
 * @param argc
 * @param argv
 * @return
 */
int main(int argc, char *argv[]) {

    ifFFmpegIsOkay();

#if VideoPlay
    VideoSDL_play(argc, argv);
#elif  DisplayBmp
    displayBmp(argc, argv);
#elif AudioPlay
    playPCMAudio(argc, argv);
#else
    displayAll(argc, argv);
#endif
    return 0;
}

