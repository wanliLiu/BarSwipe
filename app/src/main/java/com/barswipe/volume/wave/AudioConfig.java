package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/4/7.
 */

public class AudioConfig {

    /**
     * 一小格时间宽度，单位dp
     */
    public static final int _timeMargin = 10;

    /**
     * 一小格时间 单位ms
     */
    public static final double _timeSpace = 250.0d;

    /**
     * 一个大格有多少个小格，这里一个大格就是 _dividerCount * _timeSpace s
     */
    public static final int _dividerCount = 4;

    /**
     * 进度条圆点的半径 单位dp
     */
    public static final int _dotRadius = 4;

    /**
     * 1s分成 100ms的平均份数
     */
    public static final int _newWaveTime = 100;

    /**
     * 一个小格画多少个波形柱子
     */
    public static final int _waveCount = (int) ((_dividerCount * _timeSpace) / _newWaveTime);

    /**
     * 录制的最大时间 单位s
     */
    public static final int _totalTimeSec = 10 * 60;

    /**
     * 一个波形柱子代表的时间
     */
    public static final int _waveTime = _newWaveTime;

    /**
     * 一个item绘制几s的时间
     */
    public static final int _itemSecondes = 4;

    /**
     * 一个大隔总共有好多波形柱子
     */
    public static final int _itemWaveCount = _itemSecondes * _waveCount;

    /**
     * 保存的格式，mp3 或amr
     */
    public static boolean recordFormatIsMp3 = false;

}
