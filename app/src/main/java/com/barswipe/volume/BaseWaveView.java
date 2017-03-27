package com.barswipe.volume;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.barswipe.volume.wave.onScrollTimeChangeListener;

/**
 * Created by Soli on 2017/3/17.
 */

public class BaseWaveView extends View {

    private String TAG = "WaveView";
    protected boolean isDebug = true;

    protected Context ctx;
    protected int viewWidth, viewHeight, screenWidth, halfScreenWidth, startOffset, waveHeight;
    /**
     * 时间间隔宽度，dp单位，默认是10dp
     */
    protected int timeMargin;
    /**
     * timeMargin 代表的时间 单位ms
     */
    protected double timeSpace = 250.0d;
    /**
     * 一个大隔有多少个小隔
     */
    protected int dividerCount = 4;
    /**
     * 时间区域的高度，默认是20dp
     */
    protected int timeViewHeight;
    /**
     * 进度刻度线的圆点半径 默认3dp
     */
    protected int dotRadius;
    /**
     * 录制的最大时间
     */
    private int totalTimeSec = 90;
    /**
     * 能够录制的最大时间,通过距离来算
     */
    private int canRecordMaxOffset;
    //250ms一小隔绘制
    protected int waveCount = 3;
    protected int waveWidth = 0;

    protected int waveCenterPos = 0;

    protected int offset = 0;

    protected Paint timeTextPaint, timeLinePain, playIndexPaint, wavePaint;

    protected onScrollTimeChangeListener timeChangeListener;

    public BaseWaveView(Context context) {
        super(context);
        init(context);
    }

    public BaseWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     *
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BaseWaveView.this.invalidate();
        }
    };

    /**
     *
     */
    protected void updateDisplay() {
        handler.sendEmptyMessage(0);
    }

    /**
     * @param dp
     * @return
     */
    protected int dip2px(int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
        return (int) px;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, viewHeight);
    }

    /**
     *
     */
    public void init(Context mctx) {
        ctx = mctx;

        timeMargin = dip2px(10);
        timeViewHeight = dip2px(25);
        dotRadius = dip2px(4);

        waveWidth = timeMargin / waveCount;

        screenWidth = getScreenW(ctx);
        halfScreenWidth = screenWidth / 2;
        viewHeight = screenWidth * 3 / 4;
        startOffset = halfScreenWidth - timeMargin;

        waveHeight = viewHeight - timeViewHeight - dotRadius;

        viewWidth = totalTimeSec * (dividerCount * timeMargin) + screenWidth;
        waveCenterPos = timeViewHeight + (viewHeight - timeViewHeight) / 2 - dotRadius;

        canRecordMaxOffset = totalTimeSec * (dividerCount * timeMargin);
        initPaint();
    }

    /**
     * @param size
     * @return
     */
    private int getPainTextSize(int size) {
        if (ctx instanceof Activity) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) (ctx)).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return (int) (displayMetrics.density * size);
        }

        return size;
    }

    /**
     *
     */
    private void initPaint() {
        timeTextPaint = new Paint();
        timeTextPaint.setAntiAlias(true);
        timeTextPaint.setColor(Color.parseColor("#6CA5FF"));
        timeTextPaint.setTextSize(getPainTextSize(9));

        timeLinePain = new Paint();
        timeLinePain.setAntiAlias(true);
        timeLinePain.setStrokeWidth(2);
        timeLinePain.setColor(Color.parseColor("#e0e0e0"));

        playIndexPaint = new Paint();
        playIndexPaint.setAntiAlias(true);
        playIndexPaint.setStrokeWidth(5);
        playIndexPaint.setColor(Color.parseColor("#6CA5FF"));

        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStrokeWidth(waveWidth - 2);
        wavePaint.setColor(Color.parseColor("#e0e0e0"));
    }


    /**
     * 像素对应的时间
     *
     * @param pixels
     * @return 返回时间ms
     */
    public double pixelsToMillisecs(int pixels) {
        double onePixelTime = timeSpace * 1.0d / timeMargin * 1.0d;
        return onePixelTime * pixels;
    }

    /**
     * 时间对于像素
     *
     * @param ms
     * @return
     */
    public int millisecsToPixels(double ms) {
        return (int) (timeMargin * ms * 1.0f / timeSpace * 1.0f + 0.5);
    }

    /**
     * 获取手机分辨率--W
     */
    private int getScreenW(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * @param str
     */
    public void logOut(String str) {
        if (isDebug)
            Log.e(TAG, str);
    }

    public void setTimeChangeListener(onScrollTimeChangeListener timeChangeListener) {
        this.timeChangeListener = timeChangeListener;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getHalfScreenWidth() {
        return halfScreenWidth;
    }

    public void setHalfScreenWidth(int halfScreenWidth) {
        this.halfScreenWidth = halfScreenWidth;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getTotalTimeSec() {
        return totalTimeSec;
    }

    public void setTotalTimeSec(int totalTimeSec) {
        this.totalTimeSec = totalTimeSec;
    }

    public int getCanRecordMaxOffset() {
        return canRecordMaxOffset;
    }

    public void setCanRecordMaxOffset(int canRecordMaxOffset) {
        this.canRecordMaxOffset = canRecordMaxOffset;
    }
}
