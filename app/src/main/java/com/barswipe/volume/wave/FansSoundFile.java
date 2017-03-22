/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barswipe.volume.wave;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.barswipe.volume.BaseWaveView;
import com.czt.mp3recorder.DataEncodeThread;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * 参考-------
 * android MediaCodec 音频编解码的实现——转码
 * http://blog.csdn.net/TinsanMr/article/details/51049179
 */
@SuppressLint("NewApi")
public class FansSoundFile {
    private onRecordStatusListener listener = null;
    //保存的格式，mp3 或amr
    private boolean recordFormatIsMp3 = true;

    //采样率 就是1s采集多少个点，这里就16000个点就是16000个数据
    private int mSampleRate = recordFormatIsMp3 ? 44100 : 8000;//44100 8000
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int mChannels = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
    private int bit = audioFormat == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8;
    private int mNumSamples;
    private ByteBuffer mPCMBytes;  // Raw audio data
    private ShortBuffer mPCMSamples;  // shared buffer with mPCMBytes.
    private ByteBuffer waveBytes;//波形数据

    private AudioRecord audioRecord;
    private short[] buffer;
    private audioRecordThread recordThread;
    private boolean isRecording = false;
    //1s采集多少个字节
    private int bytesOnSecond = (mSampleRate * bit * mChannels) / 8;

    private DataEncodeThread mEncodeThread;

    public FansSoundFile() {
        int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, channelConfig, audioFormat);//44100----3584--81.26ms 8000---640

        //多少字节代表一个波
        int bytesOnewave = (int) ((BaseWaveView.timeSpace * 1.0f / BaseWaveView.waveCount * 1.0f) * bytesOnSecond / 1000);
        int bytesCache = bytesOnSecond / 2;
        if (bytesCache < minBufferSize)
            minBufferSize *= 2;
        else
            minBufferSize = bytesCache;

        buffer = new short[bytesOnewave / 2];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate, channelConfig, audioFormat, minBufferSize);

        // Allocate memory for 20 seconds first. Reallocate later if more is needed.
        mPCMBytes = ByteBuffer.allocate(20 * bytesOnSecond);
        mPCMBytes.order(ByteOrder.LITTLE_ENDIAN);
        mPCMSamples = mPCMBytes.asShortBuffer();
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getChannels() {
        return mChannels;
    }

    public int getNumSamples() {
        return mNumSamples;  // Number of samples per channel.
    }

    public ShortBuffer getSamples() {
        if (mPCMSamples != null) {
            return mPCMSamples.asReadOnlyBuffer();
        } else {
            return null;
        }
    }

    /**
     * @param mlistener
     */
    public void setRecordListener(onRecordStatusListener mlistener) {
        listener = mlistener;
    }

    /**
     *
     */
    public void startRecord() {
        isRecording = true;
        audioRecord.startRecording();
        recordThread = new audioRecordThread();
        recordThread.start();
    }

    /**
     *
     */
    public void stopRecord() {
        isRecording = false;
        audioRecord.stop();
        recordThread = null;
    }

    /**
     *
     */
    private class audioRecordThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (isRecording) {
                if (mPCMSamples.remaining() <= buffer.length * 2) {
                    // Try to allocate memory for 10 additional seconds.
                    int newCapacity = mPCMBytes.capacity() + 10 * bytesOnSecond;
                    ByteBuffer newDecodedBytes = null;
                    try {
                        newDecodedBytes = ByteBuffer.allocate(newCapacity);
                    } catch (OutOfMemoryError oome) {
                        break;
                    }
                    int position = mPCMSamples.position();
                    mPCMBytes.rewind();
                    newDecodedBytes.put(mPCMBytes);
                    mPCMBytes = newDecodedBytes;
                    mPCMBytes.order(ByteOrder.LITTLE_ENDIAN);
                    mPCMBytes.rewind();
                    mPCMSamples = mPCMBytes.asShortBuffer();
                    mPCMSamples.position(position);
                }
                int readSize = audioRecord.read(buffer, 0, buffer.length);
                if (readSize == AudioRecord.ERROR_INVALID_OPERATION ||
                        readSize == AudioRecord.ERROR_BAD_VALUE) {
                    break;
                } else if (readSize > 0) {
                    mPCMSamples.put(buffer, 0, readSize);
                }
                calculateTime((float) (mPCMSamples.position()) / mSampleRate);
                calculateRealVolume(buffer, readSize);
            }
            mNumSamples = mPCMSamples.position();
//            mPCMSamples.rewind();
//            mPCMBytes.rewind();

            if (listener != null)
                listener.onRecordStop(FansSoundFile.this);
        }
    }

    /**
     * @param elapsedTime
     */
    private void calculateTime(double elapsedTime) {
        int min = (int) (elapsedTime / 60);
        float sec = (float) (elapsedTime - 60 * min);
        String strMin = (min < 10 ? "0" + min : String.valueOf(min)) + ":";
        String recordTimeStr = strMin + String.format("%05.2f", sec);
        Log.e("录制的时间", elapsedTime + "___" + recordTimeStr);
        if (listener != null)
            listener.onRecordTime(elapsedTime, recordTimeStr);
    }

    /**
     * 此计算方法来自samsung开发范例
     *
     * @param buffer   buffer
     * @param readSize readSize
     */
    private void calculateRealVolume(short[] buffer, int readSize) {
        double mVolume;
//        double sum = 0;
//        for (int i = 0; i < readSize; i++) {
//            // 这里没有做运算的优化，为了更加清晰的展示代码
//            sum += buffer[i] * buffer[i];
//        }
//        if (readSize > 0) {
//            double amplitude = sum / readSize;
//            mVolume = 0.1 * Math.log10(amplitude);
//            Log.e("音量最大值：", mVolume + "");
//            if (listener != null)
//                listener.onRealVolume(mVolume);
//        }

        short max = 0;
        for (int i = 0; i < readSize; i++) {
            if (Math.abs(buffer[i]) > max)
                max = (short) Math.abs(buffer[i]);
        }
        mVolume = max * 1.0f / Short.MAX_VALUE * 1.0f;
        Log.e("音量最大值-----：", mVolume + "");
        if (listener != null)
            listener.onRealVolume(mVolume);


//        double sumVolume = 0.0;
//        double avgVolume = 0.0;
//        for (int i = 0; i < readSize; i++) {
//            sumVolume += Math.abs(buffer[i]);
//        }
//        avgVolume = (sumVolume / readSize) * 1.0f / Short.MAX_VALUE * 1.0f;
//        Log.e("音量最大值-----：", avgVolume + "");
//        if (listener != null)
//            listener.onRealVolume(avgVolume);
    }

    /**
     * @param file
     */
    public void saveAudioFile(File file, FansAudioMp3EncodeThread.onEncodeCompleteListener listener) {
        if (!isRecording) {//没有录制
            try {
                FansAudioMp3EncodeThread thread = new FansAudioMp3EncodeThread(getSamples(), getNumSamples(), file, buffer.length, getChannels(), 0, 0, listener);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface onRecordStatusListener {
        /**
         * Will be called by the SoundFile class periodically
         * with values between 0.0 and 1.0.  Return true to continue
         * loading the file or recording the audio, and false to cancel or stop recording.
         */
        public void onRecordTime(double fractionComplete, String time);

        public void onRealVolume(double volume);

        public void onRecordStop(FansSoundFile soundFile);
    }

}
