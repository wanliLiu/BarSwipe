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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.nio.ShortBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class FansSamplePlayer {

    private ShortBuffer mSamples;
    private int mSampleRate;
    private int mChannels;
    private int mNumSamples;  // Number of samples per channel.
    private AudioTrack mAudioTrack;
    private short[] mBuffer;
    private int mPlaybackStart, mPlaybackEnd;  // Start offset, in samples.
    private Thread mPlayThread;
    private boolean mKeepPlaying;
    private onAudioPlayListener mListener;
    private Timer timer;

    /**
     * 一个波形柱子代表的时间
     */
    private double timSpace = AudioConfig._waveTime;

    public FansSamplePlayer(ShortBuffer samples, int sampleRate, int channels, int numSamples) {
        mSamples = samples;
        mSampleRate = sampleRate;
        mChannels = channels;
        mNumSamples = numSamples;
        mPlaybackStart = mPlaybackEnd = 0;

        //44100----7088  8000----1312
        int bufferSize = AudioTrack.getMinBufferSize(
                mSampleRate,
                mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        int bytesOnSecond = (mSampleRate * 16 * mChannels) / 8;
        int bytesOnewave = (int) (timSpace * 1.0f * bytesOnSecond / 1000);
        if (bytesOnewave > bufferSize) {
            bufferSize = bytesOnewave;
        }
        mBuffer = new short[bufferSize / 2]; //
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                mSampleRate,
                mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize * 2,
                AudioTrack.MODE_STREAM);
        mKeepPlaying = true;
    }

    public FansSamplePlayer(FansSoundFile mSoundFile) {
        this(mSoundFile.getSamples(), mSoundFile.getSampleRate(), mSoundFile.getChannels(), mSoundFile.getNumSamples());
    }

    /**
     * @param listener
     */
    public void setOnAudioPlayListener(onAudioPlayListener listener) {
        mListener = listener;
    }

    public boolean isPlaying() {
        return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    public boolean isPaused() {
        return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    /**
     *
     */
    public void start() {
        if (isPlaying()) {
            return;
        }

        mKeepPlaying = true;
        mAudioTrack.flush();
        mAudioTrack.play();
        // Setting thread feeding the audio samples to the audio hardware.
        // (Assumes mChannels = 1 or 2).
        mPlayThread = new Thread() {
            public void run() {
                int startIndex = mPlaybackStart * mChannels;
                int endIndex = mPlaybackEnd * mChannels;
                mSamples.position(startIndex);
                int limit = endIndex == 0 ? mNumSamples * mChannels : endIndex;
                while (mKeepPlaying && mSamples.position() < limit) {
                    int numSamplesLeft = limit - mSamples.position();
                    if (numSamplesLeft >= mBuffer.length) {
                        mSamples.get(mBuffer);
                    } else {
                        for (int i = numSamplesLeft; i < mBuffer.length; i++) {
                            mBuffer[i] = 0;

                        }
                        mSamples.get(mBuffer, 0, numSamplesLeft);
                    }
                    mAudioTrack.write(mBuffer, 0, mBuffer.length);
                }

                if (mListener != null) {
                    mListener.onAudioPlayComplete();
                }

                if (timer != null)
                    timer.cancel();
            }
        };

        mPlayThread.start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onAudioPlayProgress(getCurrentPosition());
                }
            }
        }, 10, AudioConfig._waveTime);

    }

    /**
     *
     */
    public void pause() {
        if (isPlaying()) {
            mAudioTrack.pause();
            // mAudioTrack.write() should block if it cannot write.
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (isPlaying() || isPaused()) {
            mKeepPlaying = false;
            mAudioTrack.pause();  // pause() stops the playback immediately.
            mAudioTrack.stop();   // Unblock mAudioTrack.write() to avoid deadlocks.
            if (mPlayThread != null) {
                try {
                    mPlayThread.join();
                } catch (InterruptedException e) {
                }
                mPlayThread = null;
            }
            mAudioTrack.flush();  // just in case...
        }
    }

    /**
     *
     */
    public void release() {
        stop();
        mAudioTrack.release();
    }

    /**
     * 根据时间，算出在数据中的位置
     *
     * @param ms
     * @return
     */
    private int getDataPlayIndex(double ms) {
        return (int) (ms * (mSampleRate * 1.0d / 1000.0d));
    }

    /**
     * @param msStart
     */
    public void seekTo(double msStart) {
        seekTo(msStart, 0);
    }

    /**
     * 跳转到哪里播放
     *
     * @param msStart 开始时间
     * @param msEnd   结束时间
     */
    public void seekTo(double msStart, double msEnd) {
        //结束时间不为0，并且结束时间必须大于开始时间
        if (msEnd > 0.0d && msEnd < msStart)
            return;
//        boolean wasPlaying = isPlaying();
//        stop();
        mPlaybackStart = getDataPlayIndex(msStart);
        if (mPlaybackStart > mNumSamples) {
            mPlaybackStart = mNumSamples;
        }

        mPlaybackEnd = getDataPlayIndex(msEnd);
        if (mPlaybackEnd > mNumSamples) {
            mPlaybackEnd = mNumSamples;
        }
//        if (wasPlaying) {
//            start();
//        }
    }

    /**
     * 获取当前播放进度时间
     *
     * @return
     */
    private double getCurrentPosition() {
        double curPos = 0;
        try {
            curPos = (mPlaybackStart + mAudioTrack.getPlaybackHeadPosition()) * (1000.0f / mSampleRate * 1.0f);
        } catch (Exception e) {
        }
        Log.e("播放时间：", String.valueOf(curPos));
        return curPos;
    }
}
