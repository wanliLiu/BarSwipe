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
    public interface OnCompletionListener {
        public void onCompletion();

        public void onPlayProgress(int timeMs);
    }

    private ShortBuffer mSamples;
    private int mSampleRate;
    private int mChannels;
    private int mNumSamples;  // Number of samples per channel.
    private AudioTrack mAudioTrack;
    private short[] mBuffer;
    private int mPlaybackStart;  // Start offset, in samples.
    private Thread mPlayThread;
    private boolean mKeepPlaying;
    private OnCompletionListener mListener;
    private Timer timer;

    public FansSamplePlayer(ShortBuffer samples, int sampleRate, int channels, int numSamples) {
        mSamples = samples;
        mSampleRate = sampleRate;
        mChannels = channels;
        mNumSamples = numSamples;
        mPlaybackStart = 0;
        int bufferSize = AudioTrack.getMinBufferSize(
                mSampleRate,
                mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        // make sure minBufferSize can contain at least 1 second of audio (16 bits sample).
        if (bufferSize < mChannels * mSampleRate * 2) {
            bufferSize = mChannels * mSampleRate * 2;
        }
        mBuffer = new short[3675]; // bufferSize is in Bytes. 3675 667
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                mSampleRate,
                mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
        mKeepPlaying = true;
    }

    public FansSamplePlayer(FansSoundFile mSoundFile) {
        this(mSoundFile.getSamples(), mSoundFile.getSampleRate(), mSoundFile.getChannels(), mSoundFile.getNumSamples());
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mListener = listener;
    }

    public boolean isPlaying() {
        return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    public boolean isPaused() {
        return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }


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
                int position = mPlaybackStart * mChannels;
                mSamples.position(position);
                int limit = mNumSamples * mChannels;
                while (mSamples.position() < limit && mKeepPlaying) {
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
                    mListener.onCompletion();
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
                    mListener.onPlayProgress(getCurrentPosition());
                }
            }
        }, 10, 8);

    }

    public void pause() {
        if (isPlaying()) {
            mAudioTrack.pause();
            // mAudioTrack.write() should block if it cannot write.
        }
    }

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

    public void release() {
        stop();
        mAudioTrack.release();
    }

    public void seekTo(int msec) {
        boolean wasPlaying = isPlaying();
        stop();
        mPlaybackStart = (int) (msec * (mSampleRate / 1000.0));
        if (mPlaybackStart > mNumSamples) {
            mPlaybackStart = mNumSamples;  // Nothing to play...
        }
        mAudioTrack.setNotificationMarkerPosition(mNumSamples - 1 - mPlaybackStart);
        if (wasPlaying) {
            start();
        }
    }

    public int getCurrentPosition() {
        int curPos = 0;
        try {
            curPos = (int) ((mPlaybackStart + mAudioTrack.getPlaybackHeadPosition()) * (1000.0 / mSampleRate));
        } catch (Exception e) {
        }
        Log.e("playBack", curPos + "");
        return curPos;
    }
}
