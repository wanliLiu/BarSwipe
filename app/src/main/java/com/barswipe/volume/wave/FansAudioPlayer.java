package com.barswipe.volume.wave;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Soli on 2017/3/27.
 * MediaPlayer音频播放
 * <p>
 * todo 后面需要优化，就是音频播放焦点问题
 * http://blog.csdn.net/songshizhuyuan/article/details/32900965
 */
public class FansAudioPlayer {

    private MediaPlayer player;
    private onAudioDetailPlayListener mListener;

    private Timer timer;

    //是否正常播放
    private boolean isDonePlay = false;

    /**
     *
     */
    public FansAudioPlayer() {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        initDefaultListener();
    }

    /**
     *
     */
    private void initDefaultListener() {
        //播放完成
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isDonePlay = false;
                if (timer != null)
                    timer.cancel();
                if (mListener != null)
                    mListener.onAudioPlayComplete();
            }
        });

        //准备完成播放
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mListener != null) {
                    mListener.onStartPlay();
                }
                player.start();
                initGetPlaybackPosition();
            }
        });

        //处理错误信息
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (mListener != null)
                    mListener.onError(extra);
                return false;
            }
        });
    }

    /**
     *
     */
    private void initGetPlaybackPosition() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mListener != null) {
                        int duration = player.getDuration();
                        int leftTime = duration - player.getCurrentPosition() + 1000;
                        if (leftTime >= duration)
                            leftTime = duration;
                        Log.e("播放剩余时间", leftTime + "");
                        mListener.onAudioPlayProgress(leftTime);
//                        mListener.onAudioPlayProgress(player.getCurrentPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, AudioConfig._waveTime);
    }


    /**
     * @param playListener
     */
    public void setOnAudioPlayListener(onAudioDetailPlayListener playListener) {
        mListener = playListener;
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {

        return player != null ? player.isPlaying() : false;
    }

    /**
     * 播放语音
     *
     * @param path
     */
    public void play(String path) {
        try {
            if (isPlaying()) {
                if (timer != null)
                    timer.cancel();
                player.pause();
                if (mListener != null)
                    mListener.onPause();
            } else {
                if (!isDonePlay) {
                    isDonePlay = true;
                    player.reset();
                    player.setDataSource(path);
                    if (mListener != null)
                        mListener.onStartPrepare();
                    player.prepareAsync();//异步缓冲
                } else {
                    player.start();
                    initGetPlaybackPosition();
                    if (mListener != null) {
                        mListener.onStartPlay();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    public void release() {
        if (timer != null)
            timer.cancel();

        if (isPlaying()) {
            player.stop();
        }
        if (player != null) {
            player.release();
        }
        player = null;
    }
}
