package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.Random;

/**
 * Created by Soli on 2017/4/5.
 */

public class ItemPcmWaveViewHead extends BaseWaveView {


    public ItemPcmWaveViewHead(Context context) {
        super(context);
        dataInit();
    }

    public ItemPcmWaveViewHead(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dataInit();
    }

    public ItemPcmWaveViewHead(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dataInit();
    }

    /**
     *
     */
    private void dataInit() {
        viewWidth = halfScreenWidth;
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

        int _1s = viewWidth % (dividerCount * timeMargin);
        int _250ms = _1s / timeMargin;
        int _250ms_left = _1s % timeMargin;
        for (int i = _250ms_left; i <= viewWidth; i += timeMargin) {
            int startHeight;
            if (i == _250ms_left && _1s > 0) {
                startHeight = timeViewHeight - timeMargin;
                for (int k = 0; k < _250ms; k++) {
                    float x = _250ms_left + k * timeMargin + timeLinePain.getStrokeWidth() / 2;
                    canvas.drawLine(x, startHeight, x, timeViewHeight, timeLinePain);
                }

                i += _250ms_left * _250ms;
            }
            if ((i - _1s) % (dividerCount * timeMargin) == 0) {
                startHeight = 0;
            } else
                startHeight = timeViewHeight - timeMargin;

            canvas.drawLine(i + timeLinePain.getStrokeWidth() / 2, startHeight, i + timeLinePain.getStrokeWidth() / 2, timeViewHeight, timeLinePain);
        }


        canvas.drawLine(0, timeViewHeight, viewWidth, timeViewHeight, timeLinePain);//时间下面这根线
        canvas.drawLine(0, waveCenterPos, viewWidth, waveCenterPos, timeLinePain);//中心线
        canvas.drawLine(0, viewHeight - dotRadius, viewWidth, viewHeight - dotRadius, timeLinePain);//最下面的那根线

    }

}
