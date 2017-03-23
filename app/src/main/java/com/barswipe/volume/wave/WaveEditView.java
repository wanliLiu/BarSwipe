package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.barswipe.volume.BaseWaveView;
import com.barswipe.volume.wave.util.MusicSimilarityUtil;

import java.util.Random;

/**
 * Created by Soli on 2017/3/17.
 */

public class WaveEditView extends BaseWaveView {

    /**
     * 没选中啥
     */
    private final int SelectNone = -1;
    /**
     * 选中了开始那根线
     */
    private final int inSelectStart = 1;
    /**
     * 选中了结束那根线
     */
    private final int inSelectEnd = 2;
    /**
     * 选中播放位置的那更新
     */
    private final int inSelectPlayBack = 3;

    /**
     * 编辑区域参数
     */
    private int editStart = timeMargin, editEnd;
    //是否处于裁剪状态
    private boolean isInEditMode = true;

    //一屏，除去开始的timeMargin，剩下的有多少个timeMargin
    private int smallDivCount = 0;
    private int startX = editStart, endx = 0;

    private Rect selectStart, selectEnd, selectPlayBack;

    private int lastX = 0, editOffset = editStart;

    //playbak选择，通过它来选中，拖动
    private int playbackTopWidth = 0;
    //最少剪切的秒数
    private int minClipTime = 1000;

    private int currentSelect = SelectNone;

    private Paint selectPaint = new Paint();

    public WaveEditView(Context context) {
        super(context);
        dataInit();
    }

    public WaveEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dataInit();
    }

    public WaveEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dataInit();
    }

    /**
     *
     */
    private void dataInit() {
        smallDivCount = (screenWidth - editStart) / timeMargin - 1;
        editEnd = endx = editStart + (smallDivCount) * timeMargin;

        playbackTopWidth = dip2px(8);

        selectPaint = new Paint();
        selectPaint.setAntiAlias(true);
        selectPaint.setColor(Color.parseColor("#feffea"));

        selectStart = new Rect();
        selectEnd = new Rect();
        selectPlayBack = new Rect();

        double recordTime = 12980;
        timeSpace = recordTime * 1.0d / smallDivCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }
        super.onDraw(canvas);

        if (isInEditMode) {
            initEditDraw(canvas);
        } else {
            recordingUpdate(canvas);
        }
    }

    /**
     * 画刻度
     */
    private void initEditDraw(final Canvas canvas) {
        canvas.drawColor(Color.parseColor("#ffffff"));//清楚画布

        for (int i = 0; i <= smallDivCount; i++) {
            int tempStartX = i * timeMargin + editStart;
            int startHeight = 0;
            if (i % dividerCount == 0) {
                double seconds = i * timeSpace * 1.0d / 1000.0d;
                int minute = (int) (seconds / 60.0f);
                int second = (int) (seconds % 60.0f);
                String time = "";
                if (minute < 10)
                    time += "0" + minute;
                else
                    time += String.valueOf(minute);
                time += ":";
                if (second < 10)
                    time += "0" + second;
                else
                    time += String.valueOf(second);
                canvas.drawText(time, tempStartX + dip2px(2), timeTextPaint.getTextSize(), timeTextPaint);
            } else {
                startHeight = timeViewHeight - timeMargin;
            }
            canvas.drawLine(tempStartX, startHeight, tempStartX, timeViewHeight, timeLinePain);
        }
        canvas.drawLine(0, timeViewHeight, screenWidth, timeViewHeight, timeLinePain);//时间下面这根线
        canvas.drawLine(editStart, waveCenterPos, editEnd, waveCenterPos, timeLinePain);//中心线
        canvas.drawLine(0, viewHeight - dotRadius, screenWidth, viewHeight - dotRadius, timeLinePain);//最下面的那根线

        //绘制选择的区域
        Rect selectRect = new Rect(startX, timeViewHeight, endx, viewHeight - dotRadius);
        canvas.drawRect(selectRect, selectPaint);

        //波形线  缩放比例
        drawWave(canvas);

        //选择的边界
        drawPlayBack(canvas, startX, selectStart);
        drawPlayBack(canvas, endx, selectEnd);

        //指示位置线
        drawPlayback(canvas, selectPlayBack);

        if (isDebug) {
            Paint temp = new Paint();
            temp.setAntiAlias(true);
            temp.setColor(Color.RED);
            temp.setStrokeWidth(1);
            temp.setStyle(Paint.Style.STROKE);
            canvas.drawRect(selectStart, temp);
            canvas.drawRect(selectEnd, temp);
            canvas.drawRect(selectPlayBack, temp);
        }

    }

    /**
     * @param canvas
     * @param rect
     */
    private void drawPlayback(Canvas canvas, Rect rect) {

        Path path = new Path();
        path.moveTo(editOffset, timeViewHeight);
        path.lineTo(editOffset - playbackTopWidth, timeViewHeight - playbackTopWidth);
        path.lineTo(editOffset + playbackTopWidth, timeViewHeight - playbackTopWidth);
        path.lineTo(editOffset, timeViewHeight);
        path.close();

        playIndexPaint.setPathEffect(new CornerPathEffect(10));
        canvas.drawPath(path, playIndexPaint);
        playIndexPaint.setPathEffect(null);
        canvas.drawLine(editOffset, timeViewHeight - playbackTopWidth, editOffset, viewHeight - dotRadius, playIndexPaint);

        if (rect != null) {
            rect.set(editOffset - playbackTopWidth * 2, timeViewHeight - playbackTopWidth * 2, editOffset + playbackTopWidth * 2, timeViewHeight + playbackTopWidth);
        }
    }

    /**
     * @param canvas
     */
    private void drawWave(Canvas canvas) {
        for (int i = editStart; i < editEnd; i += waveWidth) {
            int volume = new Random().nextInt(80);
            if (i < startX)
                wavePaint.setColor(Color.parseColor("#e0e0e0"));
            else
                wavePaint.setColor(Color.parseColor("#EEEEEE"));
            canvas.drawLine(i, waveCenterPos - (float) volume, i, waveCenterPos + (float) volume, wavePaint);
        }
    }

    /**
     * 画playPack  或者编辑线
     *
     * @param canvas
     * @param position
     */
    private void drawPlayBack(Canvas canvas, int position, Rect rect) {
//        if (!isInEditMode)
        canvas.drawCircle(position, timeViewHeight, dotRadius, playIndexPaint);// 上面小圆
        canvas.drawLine(position, timeViewHeight, position, viewHeight, playIndexPaint);//垂直的线
        canvas.drawCircle(position, viewHeight - dotRadius, dotRadius, playIndexPaint);// 下面小圆
        if (rect != null) {
            rect.set(position - dotRadius * 2, timeViewHeight, position + dotRadius * 2, viewHeight);
        }
    }

    /**
     * 录制状态更新界面
     */
    private void recordingUpdate(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);//清楚画布
        drawPlayBack(canvas, timeMargin + offset, null);
    }

    /**
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInEditMode)//只有编辑模式的是才可以处理滑动事件
            return super.onTouchEvent(event);

        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                return checkIfSelectSomething((int) event.getX(), (int) event.getY()) != SelectNone;
            case MotionEvent.ACTION_MOVE:
                int dataX = x - lastX;
                switch (currentSelect) {
                    case inSelectStart:
                        startX += dataX;
                        if (dataX > 0) {//向右滑动
                            int rightMax = endx - millisecsToPixels(minClipTime);
                            if (startX >= rightMax) {
                                startX = rightMax;
                                endx += dataX;
                                if (endx >= editEnd)
                                    endx = editEnd;
                            }
                            //开始位置超过playback后，需要一起移动
                            if (startX >= editOffset) {
                                editOffset = startX;
                                updatePlayBackSelectTime();
                            }
                        } else {//向左滑动
                            if (startX <= editStart)
                                startX = editStart;
                        }
                        break;
                    case inSelectEnd:
                        endx += dataX;
                        if (dataX > 0) {//向右滑动
                            if (endx >= editEnd)
                                endx = editEnd;
                        } else {//向左滑动
                            int leftMin = startX + millisecsToPixels(minClipTime);
                            if (endx <= leftMin) {
                                endx = leftMin;
                                startX += dataX;
                                if (startX <= editStart)
                                    startX = editStart;
                            }

                            if (endx <= editOffset) {
                                editOffset = endx;
                                updatePlayBackSelectTime();
                            }
                        }
                        break;
                    case inSelectPlayBack:
                        editOffset += dataX;
                        if (dataX > 0) {//向右滑动
                            if (editOffset >= endx)
                                editOffset = endx;
                        } else {//向左滑动
                            if (editOffset <= startX)
                                editOffset = startX;
                        }
                        updatePlayBackSelectTime();
                        break;
                }
                lastX = x;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                currentSelect = SelectNone;
                postInvalidate();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 更新playback选择的时间
     */
    private void updatePlayBackSelectTime() {
        if (timeChangeListener != null) {
            double time = pixelsToMillisecs(editOffset - editStart) / 1000.0f;
            timeChangeListener.onScrollTimeChange(time, MusicSimilarityUtil.getRecordTimeString(time));
        }
    }

    /**
     * 根据按下的位置判断选中那个视图
     */
    private int checkIfSelectSomething(int pointX, int pointY) {

        if (selectStart.contains(pointX, pointY)) {
            currentSelect = inSelectStart;
        } else if (selectEnd.contains(pointX, pointY)) {
            currentSelect = inSelectEnd;
        } else if (selectPlayBack.contains(pointX, pointY)) {
            currentSelect = inSelectPlayBack;
        } else
            currentSelect = SelectNone;

        logOut("选中的位置---_" + currentSelect);

        return currentSelect;
    }

    /**
     *
     */
    public void updatePosition() {
        isInEditMode = false;
        offset += waveWidth;
        if (offset > halfScreenWidth - timeMargin)
            return;

        updateDisplay();
    }

}
