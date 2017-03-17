package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.barswipe.volume.BaseWaveView;

import java.util.Random;

/**
 * Created by Soli on 2017/3/17.
 */

public class PcmWaveView extends BaseWaveView {

    private boolean isInit = false;
    private Paint wavePaint;

    private boolean taka = false;

    public PcmWaveView(Context context) {
        super(context);
    }

    public PcmWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PcmWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context mctx) {
        super.init(mctx);

        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStrokeWidth(4);
        wavePaint.setColor(Color.parseColor("#e0e0e0"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = totalTimeSec * (dividerCount * timeMargin);
        viewHeight = getPhoneW(ctx);
        waveCenterPos = timeViewHeight + (viewHeight - timeViewHeight) / 2 - dotRadius;
        setMeasuredDimension(viewWidth, viewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initOnDraw(canvas);//时间
        onDrawWare(canvas);//wave
    }


    /**
     * @param canvas
     */
    private void onDrawWare(Canvas canvas) {
//        if (!taka)
//            return;
//        taka = false;
//        int temp = canvas.save();
//        canvas.translate(offset,waveCenterPos);
//        int ran = new Random().nextInt(80);
//        canvas.drawLine(0, -ran, 0, ran, wavePaint);
//        canvas.restoreToCount(temp);

        for (int i = 0; i < offset; i += wavePaint.getStrokeWidth()) {
            int ran = new Random().nextInt(80);
            canvas.drawLine(i, waveCenterPos - ran, i, waveCenterPos + ran, wavePaint);
        }
    }

    /**
     * 画刻度
     */
    private void initOnDraw(Canvas canvas) {
//        if (isInit)
//            return;
//        isInit = true;
        canvas.drawColor(Color.parseColor("#ffffff"));//清楚画布
        for (int i = 0, j = 0; i < totalTimeMs; i += timeSpace, j++) {
            if (j % dividerCount == 0) {
                canvas.drawLine(j * timeMargin, 0, j * timeMargin, timeViewHeight, timeLinePain);
                int seconds = i / 1000;
                String minutes = "" + (seconds / 60);
                String second = "" + (seconds % 60);
                if (seconds / 60 < 10) {
                    minutes = "0" + minutes;
                }
                if ((seconds % 60) < 10) {
                    second = "0" + second;
                }
                String timecodeStr = minutes + ":" + second;
                canvas.drawText(timecodeStr, j * timeMargin + dip2px(2), timeTextPaint.getTextSize(), timeTextPaint);
            } else {
                canvas.drawLine(j * timeMargin, timeViewHeight - timeMargin, j * timeMargin, timeViewHeight, timeLinePain);
            }
        }
        canvas.drawLine(0, timeViewHeight, viewWidth, timeViewHeight, timeLinePain);//时间下面这根线
        canvas.drawLine(0, waveCenterPos, viewWidth, waveCenterPos, timeLinePain);//中心线
        canvas.drawLine(0, viewHeight - dotRadius * 2, viewWidth, viewHeight - dotRadius * 2, timeLinePain);//最下面的那根线
    }

    /**
     * @param mOffset
     */
    public void updateData(int mOffset) {
        offset = mOffset;
        taka = true;
        invalidate();
    }
}
