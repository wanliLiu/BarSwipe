package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Soli on 2017/4/5.
 */

public class TestPcmWaveView extends BaseWaveView {

    private int seconds = -1;

    public TestPcmWaveView(Context context) {
        super(context);
    }

    public TestPcmWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestPcmWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context mctx) {
        super.init(mctx);

        viewWidth = dividerCount * timeMargin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#ffffff"));//清楚画布

        for (int i = 0; i < dividerCount; i++) {
            if (i == 0) {
                canvas.drawLine(i, 0, i, timeViewHeight, timeLinePain);
                if(seconds >= 0){
                    String minutes = "" + (seconds / 60);
                    String second = "" + (seconds % 60);
                    if (seconds / 60 < 10) {
                        minutes = "0" + minutes;
                    }
                    if ((seconds % 60) < 10) {
                        second = "0" + second;
                    }
                    String timecodeStr = minutes + ":" + second;
                    canvas.drawText(timecodeStr, i + dip2px(2), timeTextPaint.getTextSize(), timeTextPaint);
                }
            } else
                canvas.drawLine(i * timeMargin, timeViewHeight - timeMargin, i * timeMargin, timeViewHeight, timeLinePain);
        }

        canvas.drawLine(0, timeViewHeight, viewWidth, timeViewHeight, timeLinePain);//时间下面这根线
        canvas.drawLine(0, waveCenterPos, viewWidth, waveCenterPos, timeLinePain);//中心线
        canvas.drawLine(0, viewHeight - dotRadius, viewWidth, viewHeight - dotRadius, timeLinePain);//最下面的那根线

        onDrawWare(canvas);
    }

    /**
     * @param canvas
     */
    private void onDrawWare(Canvas canvas) {
        for (int i = 0; i < viewWidth; i += waveWidth) {
//            int _2_3 = waveHeight * 3 / 4;
//            double dis = (volume * _2_3) / 2.0f + 0.5;
            int dis = 30;
//        canvas.drawLine(halfScreenWidth + offset, waveCenterPos - (float)dis, halfScreenWidth + offset, waveCenterPos + (float)dis, wavePaint);
            wavePaint.setColor(Color.parseColor("#e0e0e0"));
            canvas.drawLine(i, waveCenterPos - (float) dis, i, waveCenterPos - dip2px(1), wavePaint);
            wavePaint.setColor(Color.parseColor("#33e0e0e0"));
            if (dis > dip2px(10))
                dis -= dip2px(10);
            canvas.drawLine(i, waveCenterPos + dip2px(1), i, waveCenterPos + (float) dis, wavePaint);
        }
    }


    public void setSeconds(int ms) {
        seconds = ms;
        updateDisplay();
    }
}
