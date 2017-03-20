package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.barswipe.volume.BaseWaveView;

/**
 * Created by Soli on 2017/3/17.
 */

public class PlayWaveView extends BaseWaveView {

    public PlayWaveView(Context context) {
        super(context);
    }

    public PlayWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);//清楚画布
        canvas.drawCircle(timeMargin + offset, timeViewHeight, dotRadius, playIndexPaint);// 上面小圆
        canvas.drawLine(timeMargin + offset, timeViewHeight, timeMargin + offset, viewHeight, playIndexPaint);//垂直的线
        canvas.drawCircle(timeMargin + offset, viewHeight - dotRadius, dotRadius, playIndexPaint);// 下面小圆
    }

    /**
     *
     */
    public void updatePosition() {
        offset += waveWidth;
        if (offset > halfScreenWidth - timeMargin)
            return;

        updateDisplay();
    }
}
