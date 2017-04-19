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

import com.barswipe.volume.wave.util.MusicSimilarityUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.LinkedList;

/**
 * 参考-------
 * android MediaCodec 音频编解码的实现——转码
 * http://blog.csdn.net/TinsanMr/article/details/51049179
 */
@SuppressLint("NewApi")
public class FansSoundFile {

    private onAudioRecordListener listener;

    //采样率 就是1s采集多少个点，这里就16000个点就是16000个数据
    private int mSampleRate = AudioConfig.recordFormatIsMp3 ? 44100 : 8000;//44100 8000
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int mChannels = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
    private int bit = audioFormat == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8;
    private int mNumSamples;
    private ByteBuffer mPCMBytes;  // Raw audio data
    private ShortBuffer mPCMSamples;  // shared buffer with mPCMBytes.
    private LinkedList<String> waveBytes;//波形数据

    private AudioRecord audioRecord;
    private short[] buffer;
    private audioRecordThread recordThread;
    private boolean isRecording = false;
    //1s采集多少个字节
    private int bytesOnSecond = (mSampleRate * bit * mChannels) / 8;

    /**
     * 一个波形柱子代表的时间
     */
    private double timSpace = AudioConfig._waveTime;

    public FansSoundFile() {
        int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, channelConfig, audioFormat);//44100----3584--81.26ms 8000---640

        //多少字节代表一个波形柱子
        int bytesOnewave = (int) (timSpace * 1.0f * bytesOnSecond / 1000);
        int bytesCache = bytesOnSecond / 2;
        if (bytesCache < minBufferSize)
            minBufferSize *= 2;
        else
            minBufferSize = bytesCache;

        buffer = new short[bytesOnewave / 2];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate, channelConfig, audioFormat, minBufferSize);

        waveBytes = new LinkedList<>();
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
//            return mPCMSamples.asReadOnlyBuffer();
            return mPCMSamples;
        } else {
            return null;
        }
    }

    /**
     * @return
     */
    public LinkedList<String> getWaveBytes() {
        if (waveBytes == null)
            return new LinkedList<>();
        return waveBytes;
    }

    /**
     * @param mlistener
     */
    public void setOnAudioRecordListener(onAudioRecordListener mlistener) {
        listener = mlistener;
    }

    /**
     *
     */
    public void startRecord() {
        try {
            isRecording = true;
            audioRecord.startRecording();
            recordThread = new audioRecordThread();
            recordThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void stopRecord() {
        try {
            isRecording = false;
            audioRecord.stop();
            recordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        stopRecord();
        if (audioRecord != null)
            audioRecord.release();
        audioRecord = null;
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
                if (!isRecording)
                    break;

                int readSize = audioRecord.read(buffer, 0, buffer.length);

                if (!isRecording)
                    break;

                if (readSize == AudioRecord.ERROR_INVALID_OPERATION ||
                        readSize == AudioRecord.ERROR_BAD_VALUE) {
                    break;
                } else if (readSize > 0) {
                    mPCMSamples.put(buffer, 0, readSize);
                }
                calculateRealVolume(buffer, readSize);

//                if (isRecording)
//                    calculateTime((float) (mPCMSamples.position()) / mSampleRate);

                if (!isRecording)
                    mPCMSamples.position(mPCMSamples.position() - readSize);
            }
            mNumSamples = mPCMSamples.position();
//            mPCMSamples.rewind();
//            mPCMBytes.rewind();

            if (listener != null)
                listener.onAudioRecordStop();
        }
    }

    /**
     * @param elapsedTime
     */
    private void calculateTime(double elapsedTime) {
        if (listener != null)
            listener.onTimeChange(true, elapsedTime, MusicSimilarityUtil.getRecordTimeString(elapsedTime));
    }

    /**
     * 此计算方法来自samsung开发范例
     *
     * @param buffer   buffer
     * @param readSize readSize
     */
    private void calculateRealVolume(short[] buffer, int readSize) {
        double mVolume;
        short max = 0;
        for (int i = 0; i < readSize; i++) {
            if (!isRecording)
                break;
            if (Math.abs(buffer[i]) > max)
                max = (short) Math.abs(buffer[i]);
        }
        if (isRecording) {
            mVolume = max * 1.0f / Short.MAX_VALUE * 1.0f;
            Log.e("音量最大值-----：", mVolume + "");
            if (listener != null) {
                waveBytes.add(String.valueOf(mVolume));
                listener.onAudioRecordVolume(mVolume);
            }
        }
    }

    /**
     * @param file
     */
    public void saveAudioFile(File file, onEncodeCompleteListener listener) {

        // TODO: 22/03/2017 保存文件的时候要显示加载对话框，文件很大的时候，可能很慢
        if (!isRecording) {//没有录制
            if (AudioConfig.recordFormatIsMp3) {
                try {
                    FansMp3EncodeThread thread = new FansMp3EncodeThread(getSamples(), getNumSamples(), file, buffer.length, getChannels(), 0, 0, waveBytes, listener);
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //amr编码
                try {
                    FansAmrEncodeThread thread = new FansAmrEncodeThread(getSamples(), getNumSamples(), file, getChannels(), 0, 0, waveBytes, listener);
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param misClip
     * @param mstartTime
     * @param mendTime
     */
    public void dealAudioEidt(boolean misClip, double mstartTime, double mendTime) {
        new Thread(new dealAudioRunnable(misClip, mstartTime, mendTime)).start();
    }

    /**
     * 处理音视频的剪辑
     */
    private class dealAudioRunnable implements Runnable {

        private boolean isClip;
        private int dataStartIndex, dataEndIndex, waveStartIndex, waveEndIndex;

        /**
         * @param misClip    true 裁剪
         *                   false 删除
         * @param mstartTime 开始时间
         * @param mendTime   结束时间
         */
        public dealAudioRunnable(boolean misClip, double mstartTime, double mendTime) {
            isClip = misClip;

            dataStartIndex = getDataPlayIndex(mstartTime);
            dataEndIndex = getDataPlayIndex(mendTime);

            waveStartIndex = (int) (mstartTime / timSpace * 1.0f);
            waveEndIndex = (int) (mendTime / timSpace * 1.0f);
            Log.e("时间数据", "mstartTime:" + mstartTime + "__mendTime:" + mendTime);
            Log.e("index数据", "dataStartIndex:" + dataStartIndex + "__dataEndIndex:" + dataEndIndex);
            Log.e("位置数据", "waveStartIndex:" + waveStartIndex + "___waveEndIndex:" + waveEndIndex);
        }

        /**
         * 根据时间，算出在数据中的位置
         *
         * @param ms
         * @return
         */
        private int getDataPlayIndex(double ms) {
            int index = (int) (ms * (mSampleRate * 1.0d / 1000.0d));
            if (index > mNumSamples)
                index = mNumSamples;
            return index;
        }

        @Override
        public void run() {

            short[] datas = null;
            LinkedList<String> wave = new LinkedList<>();
            if (isClip)//剪切
            {
                //先处理音频数据
                int len = dataEndIndex - dataStartIndex;
                datas = new short[len];
                mPCMSamples.position(dataStartIndex);
                for (int i = 0; i < len; i++) {
                    datas[i] = mPCMSamples.get();
                }

                //在处理波形数据
                for (int i = waveStartIndex; i < waveEndIndex; i++) {
                    wave.add(waveBytes.get(i));
                }


            } else {
                //先处理音频数据
                int len = mPCMSamples.position() - (dataEndIndex - dataStartIndex);
                datas = new short[len];
                mPCMSamples.position(0);
                for (int i = 0; i < len; i++) {
                    if (i == dataStartIndex) {
                        mPCMSamples.position(dataEndIndex);
                    }
                    datas[i] = mPCMSamples.get();
                }

                //在处理波形数据
                len = waveBytes.size() - (waveEndIndex - waveStartIndex);
                for (int i = 0; i < len; i++) {
                    if (i < waveStartIndex)
                        wave.add(waveBytes.get(i));
                    else
                        wave.add(waveBytes.get(i + (waveEndIndex - waveStartIndex)));
                }

            }

            mPCMSamples.clear();
            mPCMSamples.put(datas);
            mNumSamples = mPCMSamples.position();

            waveBytes = wave;

            calculateTime((float) (mPCMSamples.position()) / mSampleRate);
            if (listener != null) {
                listener.onAudioEditComplete();
            }
        }
    }
}
