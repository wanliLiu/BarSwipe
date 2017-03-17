package com.cokus.wavelibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 该类只是一个初始化surfaceview的封装
 *
 * @author cokus
 */
public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private int line_off;//上下边距距离

    /**
     * 时间间隔宽度，dp单位，默认是10dp
     */
    private int timeMargin;
    /**
     * timeMargin 代表的时间 单位ms
     */
    private int timeSpace = 250;
    /**
     * 一大隔的时间  1000ms
     */
    private int timeBigSpace = 1000;
    /**
     * 时间区域的高度，默认是20dp
     */
    private int timeViewHeight;
    /**
     * 进度刻度线的圆点半径 默认3dp
     */
    private int dotRadius;

    /**
     * 像素偏移位置，对应时间和整体波形的移动距离
     */
    private int offset = 0;


    public int getLine_off() {
        return line_off;
    }


    public void setLine_off(int line_off) {
        this.line_off = line_off;
    }


    public WaveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.holder = getHolder();
        holder.addCallback(this);

        timeMargin = dip2px(10);
        timeViewHeight = dip2px(20);
        dotRadius = dip2px(3);
    }

    /**
     * 像素对应的时间
     *
     * @param pixels
     * @return 返回时间ms
     */
    public double pixelsToSeconds(int pixels) {
        double onePixelTime = timeSpace * 1.0d / timeMargin * 1.0d;
        return onePixelTime * pixels;
    }

    /**
     * @param dp
     * @return
     */
    private int dip2px(int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
        return (int) px;
    }

    public void test(int offset) {
        this.offset = offset;
        initSurfaceView(this);
    }


    /**
     * @author cokus
     * init surfaceview
     */
    public void initSurfaceView(final SurfaceView sfv) {
        new Thread() {
            public void run() {
                Canvas canvas = sfv.getHolder().lockCanvas(new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));// 关键:获取画布
                if (canvas == null) {
                    return;
                }
                //canvas.drawColor(Color.rgb(241, 241, 241));// 清除背景
                canvas.drawARGB(255, 239, 239, 239);

                onDrawTime(canvas);//时间

//                int waveheight = timeViewHeight + (sfv.getHeight() - timeViewHeight) / 2 - dotRadius;
//
//                Paint paintLine = new Paint();
//                Paint centerLine = new Paint();
//                Paint circlePaint = new Paint();
//                circlePaint.setColor(Color.rgb(246, 131, 126));
//                circlePaint.setAntiAlias(true);
//
//                paintLine.setColor(Color.rgb(169, 169, 169));
//                centerLine.setColor(Color.rgb(39, 199, 175));
//
//                canvas.drawLine(0, timeViewHeight, sfv.getWidth(), timeViewHeight, paintLine);//最上面的那根线
//
//                canvas.drawCircle(dotRadius, timeViewHeight - dotRadius, dotRadius, circlePaint);// 上面小圆
//                canvas.drawLine(dotRadius, timeViewHeight, dotRadius, sfv.getHeight(), circlePaint);//垂直的线
//                canvas.drawCircle(dotRadius, sfv.getHeight() - dotRadius, dotRadius, circlePaint);// 下面小圆
//                canvas.drawLine(0, waveheight, sfv.getWidth(), waveheight, centerLine);//中心线
//                canvas.drawLine(0, sfv.getHeight() - dotRadius * 2, sfv.getWidth(), sfv.getHeight() - dotRadius * 2, paintLine);//最下面的那根线


                sfv.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
            }

            ;
        }.start();

    }


    /**
     * 画刻度
     */
    private void onDrawTime(Canvas canvas) {
        if (canvas == null) return;

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(25);

        int startOffset = offset > 0 ? timeMargin - offset % timeMargin : 0;
        for (int pixel = startOffset; pixel < getWidth(); pixel += timeMargin) {
            int time = 0;
            if (offset > 0) {
                int temp = offset + timeMargin - offset % timeMargin;
                if (pixel != startOffset)
                    temp += pixel - startOffset;
                time = (int) pixelsToSeconds(temp);
            } else {
                time = (int) pixelsToSeconds(pixel);
            }
            if (time % timeBigSpace == 0) {
                //整数 一大隔
                canvas.drawLine(pixel, 0, pixel, timeMargin * 2, mPaint);
                // Turn, e.g. 67 seconds into "1:07"
                time /= timeBigSpace;
                String minutes = "" + (time / 60);
                String second = "" + (time % 60);
                if (time / 60 < 10) {
                    minutes = "0" + minutes;
                }
                if ((time % 60) < 10) {
                    second = "0" + second;
                }
                String timecodeStr = minutes + ":" + second;
                canvas.drawText(timecodeStr, pixel + dip2px(2), 20, mPaint);

            } else if (time > 0 && time % timeSpace == 0) {
                //timeSpace 小隔
                canvas.drawLine(pixel, timeMargin, pixel, timeMargin * 2, mPaint);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initSurfaceView(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
