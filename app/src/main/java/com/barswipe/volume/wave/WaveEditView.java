package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.barswipe.volume.wave.util.MusicSimilarityUtil;

import java.util.LinkedList;

/**
 * Created by Soli on 2017/3/17.
 * 音频编辑视图
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
    private boolean isInEditMode = false, isPlaying = false;

    //一屏，除去开始的timeMargin，剩下的有多少个timeMargin
    private int smallDivCount = 0;

    private RectF selectStart, selectEnd, selectPlayBack;

    private int lastX = 0, startX = editStart, endx = 0, editOffset = editStart;

    //playbak选择，通过它来选中，拖动
    private int playbackTopWidth = 0;
    //最少剪切的秒数
    private int minClipTime = 2000;

    private int currentSelect = SelectNone;

    private Paint selectPaint = new Paint();
    /**
     * 在默认timeSpace情况下 能显示的最大时间区域值
     */
    private double defaultMaxTime = 0.0d, defaultTimeSpace = AudioConfig._timeSpace;

    //波形数据
    public LinkedList<String> wavedata;

    private onWaveEditListener editListener;

    /**
     * 是否滚动到中间了
     */
    private boolean isInMiddle = false;

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

        defaultMaxTime = smallDivCount * defaultTimeSpace;

        playbackTopWidth = dip2px(8);

        selectPaint = new Paint();
        selectPaint.setAntiAlias(true);
        selectPaint.setColor(Color.parseColor("#feffea"));

        selectStart = new RectF();
        selectEnd = new RectF();
        selectPlayBack = new RectF();
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
        canvas.drawLine(0, viewHeight - dotRadius, screenWidth, viewHeight - dotRadius, timeLinePain);//最下面的那根线

        //绘制选择的区域
        Rect selectRect = new Rect(startX, timeViewHeight, endx, viewHeight - dotRadius);
        canvas.drawRect(selectRect, selectPaint);

        //波形线  缩放比例
        drawWave(canvas);

        canvas.drawLine(0, waveCenterPos, screenWidth, waveCenterPos, timeLinePain);//中心线

        //选择的边界
        drawPlayBack(canvas, startX, selectStart);
        drawPlayBack(canvas, endx, selectEnd);

        //指示位置线
        drawPlayback(canvas, selectPlayBack);

        if (isDebug) {
//            Paint temp = new Paint();
//            temp.setAntiAlias(true);
//            temp.setColor(Color.RED);
//            temp.setStrokeWidth(1);
//            temp.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(selectStart, temp);
//            canvas.drawRect(selectEnd, temp);
//            canvas.drawRect(selectPlayBack, temp);
        }

    }

    /**
     * @param canvas
     * @param rect
     */
    private void drawPlayback(Canvas canvas, RectF rect) {

        Path path = new Path();
        path.moveTo(editOffset, timeViewHeight);
        path.lineTo(editOffset - playbackTopWidth, timeViewHeight - playbackTopWidth);
        path.lineTo(editOffset + playbackTopWidth, timeViewHeight - playbackTopWidth);
        path.lineTo(editOffset, timeViewHeight);
        path.close();

        //选中的原色
        if (currentSelect == inSelectPlayBack) {
            playIndexPaint.setColor(Color.RED);
        }

        playIndexPaint.setPathEffect(new CornerPathEffect(10));
        canvas.drawPath(path, playIndexPaint);
        playIndexPaint.setPathEffect(null);
        canvas.drawLine(editOffset, timeViewHeight - playbackTopWidth, editOffset, viewHeight - dotRadius, playIndexPaint);

        //back
        playIndexPaint.setColor(Color.parseColor("#6CA5FF"));

        if (rect != null) {
            rect.set(editOffset - playbackTopWidth, timeViewHeight - playbackTopWidth, editOffset + playbackTopWidth, viewHeight - dotRadius);
        }
    }

    /**
     * 注意选中和没选中的颜色
     *
     * @param canvas
     */
    private void drawWave(Canvas canvas) {

        if (wavedata == null || wavedata.size() == 0)
            return;

        for (int i = editStart, j = 0; i < editEnd; i += waveWidth, j++) {
            int pos = (int) (j * timeSpace * 1.0f / defaultTimeSpace * 1.0f);
            if (pos >= wavedata.size())
                pos = wavedata.size() - 1;
            Double volume = Double.valueOf(wavedata.get(pos));
            int _2_3 = waveHeight * 3 / 4;
            double dis = (volume * _2_3) / 2.0f + 0.5 + 5;
//            canvas.drawLine(i, waveCenterPos - (float) dis, i, waveCenterPos + (float) dis, wavePaint);
            wavePaint.setColor(Color.parseColor(i < startX || i > endx ? "#e0e0e0" : "#EEEEEE"));
            canvas.drawLine(i, waveCenterPos - (float) dis, i, waveCenterPos - dip2px(1), wavePaint);
            wavePaint.setColor(Color.parseColor("#33e0e0e0"));
            if (dis > dip2px(10))
                dis -= dip2px(10);
            canvas.drawLine(i, waveCenterPos + dip2px(1), i, waveCenterPos + (float) dis, wavePaint);
        }
    }

    /**
     * 画playPack  或者编辑线
     *
     * @param canvas
     * @param position
     */
    private void drawPlayBack(Canvas canvas, float position, RectF rect) {
//        if (!isInEditMode)
        //选中的原色
        if ((position == startX && currentSelect == inSelectStart) ||
                (position == endx && currentSelect == inSelectEnd)) {
            playIndexPaint.setColor(Color.RED);
        }

        canvas.drawCircle(position, timeViewHeight, dotRadius, playIndexPaint);// 上面小圆
        canvas.drawLine(position, timeViewHeight, position, viewHeight, playIndexPaint);//垂直的线
        canvas.drawCircle(position, viewHeight - dotRadius, dotRadius, playIndexPaint);// 下面小圆

        //back
        playIndexPaint.setColor(Color.parseColor("#6CA5FF"));

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
        //只有编辑模式的是才可以处理滑动事件
        if (!isInEditMode || isPlaying)//播放的时候也不处理滑动事件
            return super.onTouchEvent(event);

        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                checkIfSelectSomething((int) event.getX(), (int) event.getY());

                //跟新选中的原色
                if (currentSelect != SelectNone)
                    postInvalidate();

                return currentSelect != SelectNone;
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
                        updateCanEditStaut();
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
                        updateCanEditStaut();
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
            timeChangeListener.onTimeChange(false, time, MusicSimilarityUtil.getRecordTimeString(time));
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
     * 这里面这个offset参数，如果有重录的时候还需要在合适的时候清零
     */
    public void updatePosition() {
        if (!isInMiddle) {
            offset += waveWidth;
            if (offset >= getStartOffset()) {
                offset = getStartOffset();
                isInMiddle = true;
            }

            isInEditMode = false;
            updateDisplay();
        }
    }


    /**
     * @param isEnter
     * @param recordTime 录制的时间
     * @param list       波形数据
     */
    public void enterEditMode(boolean isEnter, double recordTime, LinkedList<String> list) {
        isInEditMode = isEnter;
        if (isInEditMode) {
            //记录的时间大于默认能显示的最大时间区域
            wavedata = list;
            if (recordTime > defaultMaxTime) {
                timeSpace = recordTime * 1.0d / smallDivCount;
                editEnd = endx = editStart + (smallDivCount) * timeMargin;
            } else {
                timeSpace = defaultTimeSpace;
                editEnd = endx = editStart + millisecsToPixels(recordTime);
            }

            startX = editStart;
            editOffset = startX + (endx - startX) * 1 / 4;
            updatePlayBackSelectTime();
            updateCanEditStaut();
        }
        updateDisplay();
    }

    /**
     * 获取播放的开始时间，这里以playback为准
     *
     * @return
     */
    public double getPlayBackStartTime() {
        return pixelsToMillisecs((editOffset == endx ? startX : editOffset) - editStart);
    }

    /**
     * 获取选择区域的开始时间
     *
     * @return
     */
    public double getSelectStartTime() {
        return pixelsToMillisecs(startX - editStart);
    }

    /**
     * 获取选择区域的结束时间
     *
     * @return
     */
    public double getSelectEndTime() {
        return pixelsToMillisecs(endx - editStart);
    }

    /**
     * 是否在播放
     *
     * @param playing
     */
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    /**
     * @param isFrom ture自然播放结束
     */
    public void stopPlay(boolean isFrom) {
        setPlaying(false);
        if (isFrom) {
            editOffset = endx;
            updateDisplay();
            if (timeChangeListener != null) {
                double time = pixelsToMillisecs(editOffset) / 1000.0f;
                timeChangeListener.onTimeChange(false, time, MusicSimilarityUtil.getRecordTimeString(time));
            }
        }
    }

    /**
     * 改变播放位置
     *
     * @param timeUs
     */
    public void updatePlayBackPosition(double timeUs) {
        editOffset = millisecsToPixels(timeUs) + editStart;
        if (editOffset >= endx) {
            editOffset = endx;
        }
        updateDisplay();
        if (timeChangeListener != null) {
            double time = timeUs * 1.0f / 1000.0f;
            timeChangeListener.onTimeChange(false, time, MusicSimilarityUtil.getRecordTimeString(time));
        }
    }

    /**
     *
     */
    private void updateCanEditStaut() {
        if (editListener != null) {
            editListener.onActionCando(startX != editStart || endx != editEnd);
        }
    }

    /**
     * @param mlistener
     */
    public void setOnWaveEditListener(onWaveEditListener mlistener) {
        editListener = mlistener;
    }

    /**
     * 在音频编辑后，重新刷新视图，这里主要是保存变量
     *
     * @param waveSize 目前波形的数量值
     */
    public void refreshAfterEdit(int waveSize) {
        isInMiddle = false;
        offset = 0;
        offset = waveSize * waveWidth;
        if (offset > getStartOffset()) {
            offset = getStartOffset();
        }
    }
}
