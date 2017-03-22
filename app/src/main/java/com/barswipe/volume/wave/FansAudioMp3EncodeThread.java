package com.barswipe.volume.wave;

import com.czt.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;

public class FansAudioMp3EncodeThread extends Thread {

    private onEncodeCompleteListener listener;

    //======================Lame Default Settings=====================
    private final int DEFAULT_SAMPLING_RATE = 44100;
    private final int DEFAULT_LAME_MP3_QUALITY = 7;
    /**
     * 与DEFAULT_CHANNEL_CONFIG相关，因为是mono单声，所以是1
     */
    private final int DEFAULT_LAME_IN_CHANNEL = 1;
    /**
     * Encoded bit rate. MP3 file will be encoded with bit rate 32kbps
     */
    private final int DEFAULT_LAME_MP3_BIT_RATE = 32;

    private byte[] mMp3Buffer;
    private int mNumSamples;  // Number of samples per channel.
    private FileOutputStream mFileOutputStream;
    private String path;
    private int mChannels, playStart, playEnd, bufferSize;
    private ShortBuffer mSamples;

    /**
     * @param buffer      pcm音频流数据
     * @param numSamples  需要编码的数据
     * @param file        保存的文件
     * @param mbufferSize 录音是的缓存
     * @param channels
     * @param mPlayStart
     * @param mPlayEnd
     * @throws FileNotFoundException
     */
    public FansAudioMp3EncodeThread(ShortBuffer buffer, int numSamples, File file, int mbufferSize, int channels, int mPlayStart, int mPlayEnd, onEncodeCompleteListener mlistener) throws FileNotFoundException {
        super("DataEncodeThread");
        mSamples = buffer;
        mNumSamples = numSamples;
        mChannels = channels;
        playStart = mPlayStart;
        playEnd = mPlayEnd;
        bufferSize = mbufferSize;
        this.mFileOutputStream = new FileOutputStream(file);
        path = file.getAbsolutePath();
        mMp3Buffer = new byte[(int) (7200 + (mbufferSize * 2 * 1.25))];

        /*
         * Initialize lame buffer
		 * mp3 sampling rate is the same as the recorded pcm sampling rate
		 * The bit rate is 32kbps
		 */
        LameUtil.init(DEFAULT_SAMPLING_RATE, DEFAULT_LAME_IN_CHANNEL, DEFAULT_SAMPLING_RATE, DEFAULT_LAME_MP3_BIT_RATE, DEFAULT_LAME_MP3_QUALITY);

        listener = mlistener;
    }


    @Override
    public void run() {
        super.run();
        short[] mBuffer = new short[bufferSize];
        int encodeLen = 0;
        int position = playStart * mChannels;
        mSamples.position(position);
        int limit = mNumSamples * mChannels;
        while (mSamples.position() < limit) {
            int numSamplesLeft = limit - mSamples.position();
            if (numSamplesLeft >= mBuffer.length) {
                mSamples.get(mBuffer);
                encodeLen = mBuffer.length;
            } else {
                for (int i = numSamplesLeft; i < mBuffer.length; i++) {
                    mBuffer[i] = 0;
                }
                mSamples.get(mBuffer, 0, numSamplesLeft);
                encodeLen = numSamplesLeft;
            }

            int encodedSize = LameUtil.encode(mBuffer, mBuffer, encodeLen, mMp3Buffer);
            if (encodedSize > 0) {
                try {
                    mFileOutputStream.write(mMp3Buffer, 0, encodedSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        flushAndRelease();

        if (listener != null) {
            listener.onEncodeComplete();
        }
    }


    /**
     * Flush all data left in lame buffer to file
     */
    private void flushAndRelease() {
        //将MP3结尾信息写入buffer中
        final int flushResult = LameUtil.flush(mMp3Buffer);
        if (flushResult > 0) {
            try {
                mFileOutputStream.write(mMp3Buffer, 0, flushResult);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mFileOutputStream != null) {
                    try {
                        mFileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                LameUtil.close();
            }
        }
    }

    public interface onEncodeCompleteListener {

        public void onEncodeComplete();
    }
}
