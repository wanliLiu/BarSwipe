package com.barswipe.volume.wave;

import com.czt.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.LinkedList;

public class FansMp3EncodeThread extends Thread {

    private onEncodeCompleteListener listener;

    //======================Lame Default Settings=====================
    private final int DEFAULT_SAMPLING_RATE = 44100;
    public static int DEFAULT_LAME_MP3_QUALITY = 2;
    /**
     * 与DEFAULT_CHANNEL_CONFIG相关，因为是mono单声，所以是1
     */
    public static int DEFAULT_LAME_IN_CHANNEL = 1;
    /**
     * Encoded bit rate. MP3 file will be encoded with bit rate 32kbps
     */
    public static int DEFAULT_LAME_MP3_BIT_RATE = 128;

    private byte[] mMp3Buffer;
    private int mNumSamples;  // Number of samples per channel.
    private FileOutputStream mFileOutputStream;
    private String filePath;
    private int mChannels, playStart, playEnd, bufferSize;
    private ShortBuffer mSamples;
    /**
     * 保存数据的时候干脆一起把波形数据也处理了
     */
    private LinkedList<String> waveData;

    /**
     * @param buffer      pcm音频流数据
     * @param numSamples  需要编码的数据
     * @param file        保存的文件
     * @param mbufferSize 录音是的缓存
     * @param channels
     * @param mPlayStart  开始位置
     * @param mPlayEnd    结束位置
     * @param tempWateDat 波形数据
     * @param mlistener   回调接口
     * @throws FileNotFoundException
     */
    public FansMp3EncodeThread(ShortBuffer buffer, int numSamples, File file, int mbufferSize, int channels, int mPlayStart, int mPlayEnd, LinkedList<String> tempWateDat, onEncodeCompleteListener mlistener) throws FileNotFoundException {
        super("DataEncodeThread");
        mSamples = buffer;
        mNumSamples = numSamples;
        mChannels = channels;
        playStart = mPlayStart;
        playEnd = mPlayEnd;
        bufferSize = mbufferSize;
        this.mFileOutputStream = new FileOutputStream(file);
        filePath = file.getAbsolutePath();
        mMp3Buffer = new byte[(int) (7200 + (mbufferSize * 2 * 1.25))];

        /*
         * Initialize lame buffer
		 * mp3 sampling rate is the same as the recorded pcm sampling rate
		 * The bit rate is 32kbps
		 */
        LameUtil.init(DEFAULT_SAMPLING_RATE, DEFAULT_LAME_IN_CHANNEL, DEFAULT_SAMPLING_RATE, DEFAULT_LAME_MP3_BIT_RATE, DEFAULT_LAME_MP3_QUALITY);

        waveData = tempWateDat;

        listener = mlistener;
    }


    @Override
    public void run() {
        super.run();
        short[] mBuffer = new short[bufferSize];
        int encodeLen = 0;
        int startIndex = playStart * mChannels;
        int endIndex = playEnd * mChannels;
        mSamples.position(startIndex);
        int limit = endIndex == 0 ? mNumSamples * mChannels : endIndex;
        while (mSamples.position() < limit) {
            int numSamplesLeft = limit - mSamples.position();
            if (numSamplesLeft >= mBuffer.length) {
                mSamples.get(mBuffer);
                encodeLen = mBuffer.length;
            } else {
//                for (int i = numSamplesLeft; i < mBuffer.length; i++) {
//                    mBuffer[i] = 0;
//                }
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
            listener.onEncodeComplete(filePath, getWateDataStr());
        }
    }

    /**
     * 获取波形数据，方便后面处理
     *
     * @return
     */
    private String getWateDataStr() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < waveData.size(); i++) {
            buffer.append(waveData.get(i));
            buffer.append(",");
        }
        if (buffer.length() > 0)
            buffer.deleteCharAt(buffer.length() - 1);

        return buffer.toString();
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
}
