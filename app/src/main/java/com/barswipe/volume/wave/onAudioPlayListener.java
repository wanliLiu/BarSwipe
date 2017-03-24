package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/3/24.
 */

public interface onAudioPlayListener {
    /**
     * 录音播放完成
     */
    public void onAudioPlayComplete();

    /**
     * 录音播放进度
     *
     * @param timeMs
     */
    public void onAudioPlayProgress(double timeMs);
}
