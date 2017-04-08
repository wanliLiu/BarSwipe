package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;


/**
 * Created by Soli on 2017/3/27.
 * 音频播放，显示波形视图
 */
public class WavePlayView extends BaseWaveView {

    private String[] waveData;

    /**
     * playback进度
     */
    private int playBackOffset = 0;

    /**
     * 是否显示playback
     */
    private boolean isShowPlayBack = false;

    /**
     * 开始绘制
     */
    private int startX = dotRadius;

    public WavePlayView(Context context) {
        super(context);
        dataInit();
    }

    public WavePlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dataInit();
    }

    public WavePlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dataInit();
    }

    /**
     *
     */
    private void dataInit() {
        playBackOffset = startX = dotRadius;
        timeLinePain.setStrokeWidth(dip2px(0.5f));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        waveHeight = viewHeight - dotRadius;
        timeViewHeight = 0;
        waveCenterPos = waveHeight / 2 + dip2px(10);
        setMeasuredDimension(viewWidth, viewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#ffffff"));//清楚画布

        canvas.drawLine(startX, 0, startX, waveHeight, timeLinePain);//左边那根线
        canvas.drawLine(0, timeLinePain.getStrokeWidth() / 2, viewWidth, timeLinePain.getStrokeWidth() / 2, timeLinePain);//上面那根线
        canvas.drawLine(startX, waveCenterPos, viewWidth, waveCenterPos, timeLinePain); //中间那根线
        canvas.drawLine(0, viewHeight - timeLinePain.getStrokeWidth() / 2 - dotRadius, viewWidth, viewHeight - timeLinePain.getStrokeWidth() / 2 - dotRadius, timeLinePain);//下面那根线

        drawWave(canvas);

        if (isShowPlayBack)
            drawPlayBack(canvas);
    }

    /**
     * 画playPack  或者编辑线
     *
     * @param canvas
     */
    private void drawPlayBack(Canvas canvas) {
        canvas.drawLine(playBackOffset, 0, playBackOffset, waveHeight, playIndexPaint);//垂直的线
        canvas.drawCircle(playBackOffset, viewHeight - dotRadius, dotRadius, playIndexPaint);// 下面小圆
    }

    /**
     * 注意选中和没选中的颜色
     *
     * @param canvas
     */
    private void drawWave(Canvas canvas) {

        if (waveData == null || waveData.length == 0)
            return;

        for (float i = startX + waveWidth, j = offset; i < viewWidth && j < waveData.length; i += waveWidth, j++) {

            Double volume = Double.valueOf(waveData[(int)j]);
            int _2_3 = (int) (waveHeight * 0.8);
            double dis = (volume * _2_3) / 2.0f + 0.5 + 5;
//        canvas.drawLine(i, waveCenterPos - (float) dis, i, waveCenterPos + (float) dis, wavePaint);
            wavePaint.setColor(Color.parseColor(i < playBackOffset ? "#e0e0e0" : "#EEEEEE"));
            canvas.drawLine(i, waveCenterPos - (float) dis, i, waveCenterPos - dip2px(1), wavePaint);
            wavePaint.setColor(Color.parseColor("#33e0e0e0"));
            if (dis > dip2px(10))
                dis -= dip2px(10);
            canvas.drawLine(i, waveCenterPos + dip2px(1), i, waveCenterPos + (float) dis, wavePaint);
        }
    }

    /**
     * 设置波形数据
     *
     * @param waveDataSt
     */
    public void setWaveData(String waveDataSt) {
        offset = 0;
        playBackOffset = startX;
        if (!TextUtils.isEmpty(waveDataSt)) {
            waveData = waveDataSt.split(",");
            invalidate();
        }
    }

    /**
     *
     */
    public void updateData() {

        showPlayback(true);

        playBackOffset += waveWidth;
        if (playBackOffset > viewWidth / 2) {
            playBackOffset = viewWidth / 2;
            offset++;
        }

        updateDisplay();
    }

    /**
     * 是否显示
     */
    public void showPlayback(boolean isShow) {
        isShowPlayBack = isShow;
    }

    /**
     *
     */
    public void onAudioPlayComplete() {
        showPlayback(false);
        offset = 0;
        playBackOffset = startX;
        updateDisplay();
    }

}
