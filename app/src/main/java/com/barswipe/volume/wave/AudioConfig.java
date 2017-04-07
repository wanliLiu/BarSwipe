package com.barswipe.volume.wave;

import java.math.BigDecimal;

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
     * 一个小格画多少个波形柱子
     */
    public static final int _waveCount = 3;

    /**
     * 一个波形柱子代表的时间
     */
    public static final int _waveTime = (new BigDecimal(_timeSpace / _waveCount).setScale(0, BigDecimal.ROUND_HALF_UP)).intValue();

    /**
     * 一个item绘制几s的时间
     */
    public static final int _itemSecondes = 3;

    /**
     * 一个大隔总共有好多波形柱子
     */
    public static final int _itemWaveCount = _itemSecondes * _dividerCount * _waveCount;
}
