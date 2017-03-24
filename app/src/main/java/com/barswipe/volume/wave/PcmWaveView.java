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
import com.barswipe.volume.wave.util.MusicSimilarityUtil;

/**
 * Created by Soli on 2017/3/17.
 */

public class PcmWaveView extends BaseWaveView {

    final protected Object mLock = new Object();

    private boolean isCanScroll = false;

    private boolean mIsDraw = true, isInit = false;

    private Canvas mCanvas = new Canvas();
    private Bitmap mBitmap;

    private Scroller mScroller;
    private int mScrollLastX;

    private int scroolX, currentX;
    //单位ms
    private int playbackPosition;


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
    public void init(Context mctx) {
        super.init(mctx);

        mScroller = new Scroller(mctx);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PcmWaveView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentX = scroolX = getStartOffset();
                PcmWaveView.this.scrollTo(scroolX, 0);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

        if (isInEditMode()) {
            return;
        }

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
        //// TODO: 2017/3/23  这里图片还不释放，反复进入增大内存开销,这里是个优化的点
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mCanvas.setBitmap(null);
            mCanvas = null;
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }


    /**
     * @param canvas
     */
    private void onDrawWare(Canvas canvas, double volume) {
        int _2_3 = waveHeight * 3 / 4;
        double dis = (volume * _2_3) / 2.0f + 0.5;
//        canvas.drawLine(halfScreenWidth + offset, waveCenterPos - (float)dis, halfScreenWidth + offset, waveCenterPos + (float)dis, wavePaint);
        wavePaint.setColor(Color.parseColor("#e0e0e0"));
        canvas.drawLine(halfScreenWidth + offset, waveCenterPos - (float) dis, halfScreenWidth + offset, waveCenterPos - dip2px(1), wavePaint);
        wavePaint.setColor(Color.parseColor("#33e0e0e0"));
        if (dis > dip2px(10))
            dis -= dip2px(10);
        canvas.drawLine(halfScreenWidth + offset, waveCenterPos + dip2px(1), halfScreenWidth + offset, waveCenterPos + (float) dis, wavePaint);
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
                for (int i = _250ms_left, seconds = 0; i <= viewWidth; i += timeMargin) {
                    int startHeight;
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
                canvas.drawLine(0, viewHeight - dotRadius, viewWidth, viewHeight - dotRadius, timeLinePain);//最下面的那根线

                updateDisplay();
            }
        }).start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isCanScroll || offset <= 0)
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
                updatePlayBackPosition();
                scrollTo(scroolX, 0);
                mScrollLastX = x;
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否需要改变界面时间显示
     */
    public void updatePlayBackPosition() {
        playbackPosition = scroolX;
        if (offset < halfScreenWidth)
            playbackPosition = offset - (currentX - scroolX);
        logOut("playBack_" + playbackPosition + "录制的时间_" + pixelsToMillisecs(playbackPosition));
        if (timeChangeListener != null) {
            double time = pixelsToMillisecs(playbackPosition) * 1.0f / 1000.0f;
            timeChangeListener.onTimeChange(false, time, MusicSimilarityUtil.getRecordTimeString(time));
        }
    }

    /**
     *
     */
    public void updateData(double volume) {
        offset += waveWidth;
        onDrawWare(mCanvas, volume);
        updateDisplay();

        if (offset > halfScreenWidth - timeMargin)
            scrollBy(waveWidth, 0);
    }

    /**
     * 音频播放更新位置
     */
    public void updatePlayPosition(double timeUs) {
        int piex = millisecsToPixels(timeUs);
        if (offset < halfScreenWidth) {
            piex += getStartOffset() - offset;
        }
//        playbackPosition = piex;
        scrollTo(piex, 0);
        if (timeChangeListener != null) {
            double time = timeUs * 1.0f / 1000.0f;
            timeChangeListener.onTimeChange(false, time, MusicSimilarityUtil.getRecordTimeString(time));
        }
    }


    public boolean isRecording() {
        return isCanScroll;
    }

    /**
     * @param recording
     */
    public void setRecording(boolean recording) {
        isCanScroll = recording;
        if (!isCanScroll) {
            currentX = scroolX = getScrollX();
            updatePlayBackPosition();
        } else
            scrollTo(currentX, 0);
    }

    /**
     * 开始播放，
     *
     * @return 播放的位置，为0从头开始播放，不为0，从相应的位置播放
     * 单位时间ms
     */
    public double startPlay() {
        isCanScroll = true;
        if (playbackPosition > 0 && playbackPosition < offset) {
            return pixelsToMillisecs(playbackPosition);
        }

        if (offset >= halfScreenWidth) {
            scroolX = 0;
            scrollTo(0, 0);
        } else {
            scroolX -= offset;
            scrollTo(getStartOffset() - offset, 0);
        }

        return 0.0d;
    }

    /**
     * 停止播放，是重那里停止，
     *
     * @param stopFrom true 自然播放完成
     *                 false 认为停止
     */
    public void stopPlay(boolean stopFrom) {
        isCanScroll = false;
        if (stopFrom)
            scrollTo(currentX, 0);
        scroolX = getScrollX();
        updatePlayBackPosition();
    }
}
