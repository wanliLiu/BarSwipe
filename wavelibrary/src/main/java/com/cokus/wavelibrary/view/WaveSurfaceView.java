package com.cokus.wavelibrary.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

    private int timeMargin, timeViewHeight, dotRadius;
    private int totalTime = 90 * 1000;


    public int getLine_off() {
        return line_off;
    }


    public void setLine_off(int line_off) {
        this.line_off = line_off;
    }


    public WaveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.holder = getHolder();
        holder.addCallback(this);

        timeViewHeight = dip2px(20);
        timeMargin = dip2px(10);
        dotRadius = dip2px(3);
    }

    /**
     * @param dp
     * @return
     */
    private int dip2px(int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
        return (int) px;
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

                int waveheight = timeViewHeight + (sfv.getHeight() - timeViewHeight) / 2 - dotRadius;

                Paint paintLine = new Paint();
                Paint centerLine = new Paint();
                Paint circlePaint = new Paint();
                circlePaint.setColor(Color.rgb(246, 131, 126));
                circlePaint.setAntiAlias(true);

                paintLine.setColor(Color.rgb(169, 169, 169));
                centerLine.setColor(Color.rgb(39, 199, 175));

                canvas.drawLine(0, timeViewHeight, sfv.getWidth(), timeViewHeight, paintLine);//最上面的那根线

                canvas.drawCircle(dotRadius, timeViewHeight - dotRadius, dotRadius, circlePaint);// 上面小圆
                canvas.drawLine(dotRadius, timeViewHeight, dotRadius, sfv.getHeight(), circlePaint);//垂直的线
                canvas.drawCircle(dotRadius, sfv.getHeight() - dotRadius, dotRadius, circlePaint);// 下面小圆
                canvas.drawLine(0, waveheight, sfv.getWidth(), waveheight, centerLine);//中心线
                canvas.drawLine(0, sfv.getHeight() - dotRadius * 2, sfv.getWidth(), sfv.getHeight() - dotRadius * 2, paintLine);//最下面的那根线


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

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(25);
        for (int i = 0, j = 0; i < totalTime; i += 250, j++) {
            if (j % 4 == 0) {
                canvas.drawLine(j * timeMargin, 0, j * timeMargin, timeMargin * 2, mPaint);
                // Turn, e.g. 67 seconds into "1:07"
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
                canvas.drawText(timecodeStr, j * timeMargin + dip2px(2), 20, mPaint);

            } else {
                canvas.drawLine(j * timeMargin, timeMargin, j * timeMargin, timeMargin * 2, mPaint);
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        initSurfaceView(this);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}
