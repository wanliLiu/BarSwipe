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

    //采样率 就是1s采集多少个点，这里就16000个点就是16000个数据
    private int mSampleRate = 44100;//44100 8000
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int mChannels = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
    private int mNumSamples;  // 采样了多少次--total number of samples per channel in audio file
    private ByteBuffer mPCMBytes;  // Raw audio data
    private ShortBuffer mPCMSamples;  // shared buffer with mPCMBytes.
    private ByteBuffer waveBytes;//波形数据

    private AudioRecord audioRecord;
    private short[] buffer;
    private audioRecordThread recordThread;
    private boolean isRecording = false, isPause = false;

    public FansSoundFile() {
        int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, channelConfig, audioFormat);
        int _83_ms = (int) (mSampleRate * (0.25f / 3.0f));
        if (_83_ms >= minBufferSize) {
            minBufferSize = _83_ms * 2;
        }
        buffer = new short[minBufferSize / 2];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate, channelConfig, audioFormat, minBufferSize);

        // Allocate memory for 20 seconds first. Reallocate later if more is needed.
        mPCMBytes = ByteBuffer.allocate(20 * mSampleRate * 2);
        mPCMBytes.order(ByteOrder.LITTLE_ENDIAN);
        mPCMSamples = mPCMBytes.asShortBuffer();

        recordThread = new audioRecordThread();
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
        recordThread.start();
    }

    /**
     * @param isPause
     */
    public void pause(boolean isPause) {
        this.isPause = isPause;
    }

    /**
     *
     */
    public void stopRecord() {
        isRecording = false;
    }

    /**
     *
     */
    private class audioRecordThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (isRecording) {
                if (isPause) {
                    try {
                        Thread.sleep(50);
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // check if mPCMSamples can contain 1024 additional samples.
                if (mPCMSamples.remaining() < 1024) {
                    // Try to allocate memory for 10 additional seconds.
                    int newCapacity = mPCMBytes.capacity() + 10 * mSampleRate * 2;
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
            audioRecord.stop();
            audioRecord.release();
            mNumSamples = mPCMSamples.position();
            mPCMSamples.rewind();
            mPCMBytes.rewind();
        }
    }

    /**
     * @param elapsedTime
     */
    private void calculateTime(double elapsedTime) {
        int min = (int) (elapsedTime / 60);
        float sec = (float) (elapsedTime - 60 * min);
        Log.e("录制的时间", elapsedTime + "___" + String.format("%d:%05.2f", min, sec));
        if (listener != null)
            listener.onRecordTime(elapsedTime, String.format("%2d:%05.2f", min, sec));
    }

    /**
     * 此计算方法来自samsung开发范例
     *
     * @param buffer   buffer
     * @param readSize readSize
     */
    private void calculateRealVolume(short[] buffer, int readSize) {

        int mVolume = 0;
        double sum = 0;
        for (int i = 0; i < readSize; i++) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            sum += buffer[i] * buffer[i];
        }
        if (readSize > 0) {
            double amplitude = sum / readSize;
            mVolume = (int) Math.sqrt(amplitude);
            Log.e("音量大小", String.valueOf(mVolume));
            if (listener != null)
                listener.onRealVolume(mVolume);
        }
    }


    public interface onRecordStatusListener {
        /**
         * Will be called by the SoundFile class periodically
         * with values between 0.0 and 1.0.  Return true to continue
         * loading the file or recording the audio, and false to cancel or stop recording.
         */
        public void onRecordTime(double fractionComplete, String time);

        public void onRealVolume(int volume);
    }

}
