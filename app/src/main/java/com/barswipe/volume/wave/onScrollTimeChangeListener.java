package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/3/23.
 */

public interface onScrollTimeChangeListener {

    /**
     * 时间
     *
     * @param from             ture录制界面，其他地方过来就是fasle
     * @param fractionComplete 时间
     * @param time             格式好的时间
     */
    public void onTimeChange(boolean from, double fractionComplete, String time);

}
