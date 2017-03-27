package com.barswipe.volume.wave;

/**
 * Created by soli on 27/03/2017.
 */

public interface onAudioDetailPlayListener extends onAudioPlayListener {

    /**
     * 开始前的准备，可以用来根据相应状态做出相应的操作
     */
    public void onStartPrepare();

    /**
     * 以及完成准备，并开始播放
     */
    public void onStartPlay();

    /**
     * 错误处理，主要处理
     *
     * @param extra MEDIA_ERROR_IO（File or network related operation errors）
     *              MEDIA_ERROR_TIMED_OUT（Some operation takes too long to complete, usually more than 3-5 seconds.）
     */
    public void onError(int extra);

    /**
     * 停止播放
     */
    public void onPause();
}
