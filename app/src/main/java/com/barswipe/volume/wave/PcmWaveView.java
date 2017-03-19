package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.Scroller;

import com.barswipe.volume.BaseWaveView;

import java.util.Random;

/**
 * Created by Soli on 2017/3/17.
 */

public class PcmWaveView extends BaseWaveView {

    final protected Object mLock = new Object();

    private Paint wavePaint;

    private boolean isRecording = false;

    private boolean mIsDraw = true, isInit = false;

    private Canvas mCanvas = new Canvas();
    private Bitmap mBitmap;

    private Scroller mScroller;
    private int mScrollLastX;

    private int scroolX, currentX;


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

        mScroller = new Scroller(mctx);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PcmWaveView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                scroolX = getStartOffset();
                PcmWaveView.this.scrollTo(scroolX, 0);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = totalTimeSec * (dividerCount * timeMargin) + screenWidth;
        waveCenterPos = timeViewHeight + (viewHeight - timeViewHeight) / 2 - dotRadius;
        setMeasuredDimension(viewWidth, viewHeight);

        if (!isInit) {
            isInit = true;
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565);
                mCanvas.setBitmap(mBitmap);
            }

            initDraw(mCanvas);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsDraw && mBitmap != null) {
            synchronized (mLock) {
                canvas.drawBitmap(mBitmap, 0, 0, new Paint());
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsDraw = false;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }


    /**
     * @param canvas
     */
    private void onDrawWare(Canvas canvas) {
        int ran = new Random().nextInt(150);
        canvas.drawLine(halfScreenWidth + offset, waveCenterPos - ran, halfScreenWidth + offset, waveCenterPos + ran, wavePaint);
    }

    /**
     * 画刻度
     */
    private void initDraw(final Canvas canvas) {

        if (mBitmap == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                canvas.drawColor(Color.parseColor("#ffffff"));//清楚画布

                int _1s = halfScreenWidth % (dividerCount * timeMargin);
                int _250ms = _1s / timeMargin;
                int _250ms_left = _1s % timeMargin;
                int startHeight;

                for (int i = _250ms_left, seconds = 0; i <= viewWidth; i += timeMargin) {

                    if (i == _250ms_left && _1s > 0) {
                        startHeight = timeViewHeight - timeMargin;
                        for (int k = 0; k < _250ms; k++)
                            canvas.drawLine(_250ms_left + k * timeMargin, startHeight, _250ms_left + k * timeMargin, timeViewHeight, timeLinePain);
                        i += _250ms_left * _250ms;
                    }

                    if ((i - _1s) % (dividerCount * timeMargin) == 0) {
                        startHeight = 0;
                        if (i >= halfScreenWidth) {
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
                            seconds++;
                        }
                    } else
                        startHeight = timeViewHeight - timeMargin;

                    canvas.drawLine(i, startHeight, i, timeViewHeight, timeLinePain);
                }


                canvas.drawLine(0, timeViewHeight, viewWidth, timeViewHeight, timeLinePain);//时间下面这根线
                canvas.drawLine(0, waveCenterPos, viewWidth, waveCenterPos, timeLinePain);//中心线
                canvas.drawLine(0, viewHeight - dotRadius * 2, viewWidth, viewHeight - dotRadius * 2, timeLinePain);//最下面的那根线

                updateDisplay();
            }
        }).start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isRecording || offset <= 0)
            return super.onTouchEvent(event);

        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;

                scroolX += dataX;

                if (dataX > 0) {
                    if (scroolX >= currentX)
                        scroolX = currentX;
                } else {

                    if (scroolX <= getStartOffset() - offset)
                        scroolX = getStartOffset() - offset;
                    if (scroolX <= 0)
                        scroolX = 0;
                }

                scrollTo(scroolX, 0);
                mScrollLastX = x;
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * @param mOffset
     */
    public void updateData(int mOffset) {
        offset += mOffset;
        onDrawWare(mCanvas);
        updateDisplay();

        if (offset > halfScreenWidth - timeMargin)
            scrollBy(mOffset, 0);
    }


    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
        if (!isRecording)
            currentX = scroolX = getScrollX();
        else
            scrollTo(currentX, 0);
    }
}
