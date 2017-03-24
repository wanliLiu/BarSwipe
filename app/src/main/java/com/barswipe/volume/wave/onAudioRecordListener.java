package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/3/24.
 * 录音相关回调函数
 */
public interface onAudioRecordListener extends onScrollTimeChangeListener {

    /**
     * 录制过程中获取的 时间间隔音频数据
     *
     * @param volume
     */
    public void onAudioRecordVolume(double volume);

    /**
     * 录音结束
     */
    public void onAudioRecordStop();
}
