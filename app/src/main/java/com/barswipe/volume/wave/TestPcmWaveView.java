package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Soli on 2017/4/5.
 */

public class TestPcmWaveView extends BaseWaveView {

    /**
     * 一个item显示 多长时间的波形
     */
    private int itemSecondes = AudioConfig._itemSecondes;

    /**
     * 时间块，能显示的时间范围有dividerSeconds决定
     */
    private int position = -1;

    /**
     * item的时间波形数据
     */
    private List<String> waveData = new ArrayList<>();

    public TestPcmWaveView(Context context) {
        super(context);
        dataInit();
    }

    public TestPcmWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dataInit();
    }

    public TestPcmWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dataInit();
    }

    /**
     *
     */
    private void dataInit() {
        viewWidth = (dividerCount * timeMargin) * itemSecondes;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    /**
     * @return
     */
    private String getRandColorCode() {
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        return "#" + r + g + b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isDebug) {
            canvas.drawColor(Color.parseColor(getRandColorCode()));//清楚画布
        } else
            canvas.drawColor(Color.parseColor("#ffffff"));//清楚画布
//        canvas.drawColor(Color.parseColor("#cc000000"));//清楚画布

        for (int i = 0; i < viewWidth; i += timeMargin) {
            if (i % (dividerCount * timeMargin) == 0) {
                canvas.drawLine((i + timeLinePain.getStrokeWidth() / 2), 0, (i + timeLinePain.getStrokeWidth() / 2), timeViewHeight, timeLinePain);
                if (position >= 0) {
                    int sec = position + i / (dividerCount * timeMargin);
                    String minutes = "" + (sec / 60);
                    String second = "" + (sec % 60);
                    if (sec / 60 < 10) {
                        minutes = "0" + minutes;
                    }
                    if ((sec % 60) < 10) {
                        second = "0" + second;
                    }
                    String timecodeStr = minutes + ":" + second;
                    canvas.drawText(timecodeStr, (i + timeLinePain.getStrokeWidth() / 2) + dip2px(2), timeTextPaint.getTextSize(), timeTextPaint);
                }
            } else
                canvas.drawLine(i + timeLinePain.getStrokeWidth() / 2, timeViewHeight - timeMargin, i + timeLinePain.getStrokeWidth() / 2, timeViewHeight, timeLinePain);
        }

        canvas.drawLine(0, timeViewHeight, viewWidth, timeViewHeight, timeLinePain);//时间下面这根线
        canvas.drawLine(0, waveCenterPos, viewWidth, waveCenterPos, timeLinePain);//中心线
        canvas.drawLine(0, viewHeight - dotRadius, viewWidth, viewHeight - dotRadius, timeLinePain);//最下面的那根线

        onDrawWare(canvas);

        if (isDebug) {
            if (position >= 0)
                canvas.drawText("" + position / 3, dip2px(10), viewHeight - dip2px(10) - timeTextPaint.getTextSize(), timeTextPaint);
        }
    }

    /**
     * @param canvas
     */
    private void onDrawWare(Canvas canvas) {
        if (waveData == null || waveData.isEmpty())
            return;

        for (int i = 0, j = 0; i < viewWidth && j < waveData.size(); i += waveWidth, j++) {
//            int _2_3 = waveHeight * 3 / 4;
//            double dis = (volume * _2_3) / 2.0f + 0.5;
            int dis = Integer.parseInt(waveData.get(j));
//        canvas.drawLine(halfScreenWidth + offset, waveCenterPos - (float)dis, halfScreenWidth + offset, waveCenterPos + (float)dis, wavePaint);
            wavePaint.setColor(Color.parseColor("#e0e0e0"));
            canvas.drawLine(i + wavePaint.getStrokeWidth() / 2, waveCenterPos - (float) dis, i + wavePaint.getStrokeWidth() / 2, waveCenterPos - dip2px(1), wavePaint);
            wavePaint.setColor(Color.parseColor("#33e0e0e0"));
            if (dis > dip2px(10))
                dis -= dip2px(10);
            canvas.drawLine(i + wavePaint.getStrokeWidth() / 2, waveCenterPos + dip2px(1), i + wavePaint.getStrokeWidth() / 2, waveCenterPos + (float) dis, wavePaint);
        }
    }

    /**
     * 数据深度拷贝
     *
     * @param test
     */
    private void copyList(List<String> test) {
        waveData = new ArrayList<>();

        if (test == null || test.isEmpty())
            return;

        for (int i = 0; i < test.size(); i++) {
            waveData.add(test.get(i));
        }
    }

    /**
     * 录制的时候数据输入
     *
     * @param data
     */
    public void setWaveData(List<String> data) {
        copyList(data);
        updateDisplay();
    }

    /**
     * 视图滑动的时候数据输入
     *
     * @param pos
     * @param data
     */
    public void setSecondsAndWaveData(int pos, List<String> data) {
        position = pos * itemSecondes;
        copyList(data);
        updateDisplay();
    }
}
