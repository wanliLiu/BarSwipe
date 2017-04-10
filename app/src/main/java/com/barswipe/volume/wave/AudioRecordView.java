package com.barswipe.volume.wave;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.barswipe.volume.wave.util.MusicSimilarityUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Soli on 2017/4/5.
 */

public class AudioRecordView extends RecyclerView {

    private final String TAG = "WaveRecyclerView";

    private Context ctx;

    private WaveLinearLayoutManager linear;
    private TestWaveAdapter adapter;

    private List<String> wavedata = new ArrayList<>();

    /**
     * 表示是否正在录制，如果正在录制，就禁止滑动，反之可以滑动有效距离
     */
    private boolean isRecording = false;

    /**
     * 能显示的有效时间区域块
     */
    private int displayTime = 5;
    /**
     * 音频录制滑动的距离，从开始到停止
     */
    private int currentRecordMaxScrollX = 0;

    private onScrollTimeChangeListener timeChangeListener;
    private onParpareStartRecordingListener parpareListener;

    /**
     * 移动到某个位置还是动画好看
     */
    private boolean onParpareRecording = false, onParparePlaying = false, isAnimtion = false;


    public AudioRecordView(Context context) {
        super(context);
        init(context);
    }

    public AudioRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioRecordView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @param context
     */
    private void init(Context context) {
        ctx = context;

        linear = new WaveLinearLayoutManager(ctx);
        setLayoutManager(linear);

        adapter = new TestWaveAdapter();
        setAdapter(adapter);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AudioRecordView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //滑动到默认位置
                if (linear != null)
                    linear.scrollToPositionWithOffset(1, getInitOffset_one());
                currentRecordMaxScrollX = getStartOffset();
            }
        });
    }

    /**
     * @return
     */
    private int getInitOffset_one() {
        BaseWaveView view = getBaseView();
        if (view != null)
            return view.getTimeMargin();

        return 0;
    }

    /**
     * @return
     */
    private int getStartOffset() {
        BaseWaveView view = getBaseView();
        if (view != null)
            return view.getStartOffset();

        return 0;
    }

    /**
     * @return
     */
    private int getHalfScreenWidth() {
        BaseWaveView view = getBaseView();
        if (view != null)
            return view.getHalfScreenWidth();

        return 0;
    }

    /**
     * 获取一个波形柱子的宽度
     *
     * @return
     */
    private int getWaveWidth() {
        BaseWaveView view = getBaseView();
        if (view != null)
            return (int) view.getWaveWidth();

        return 0;
    }

    /**
     * 录制波形的数据在视图上的宽度
     *
     * @return
     */
    private int getRecordWaveDataWidth() {
        if (wavedata == null)
            return 0;
        return wavedata.size() * getWaveWidth();
    }

    /**
     * 获取波形基本参数
     *
     * @return
     */
    private BaseWaveView getBaseView() {
        if (linear != null) {
            View view = linear.findViewByPosition(linear.findFirstVisibleItemPosition());
            if (view != null && view instanceof BaseWaveView) {
                return (BaseWaveView) view;
            }
        }

        return null;
    }

    /**
     * 获取水平滑动距离
     *
     * @return
     */
    private int getScollDistance() {
        int position = linear.findFirstVisibleItemPosition();
        View view = linear.findViewByPosition(position);
        if (view == null)
            return 0;

        Log.e(TAG, "findFirstVisibleItemPosition---" + position);

        int scroolX = 0;
        //position 为0 宽度和其他item不一样

        if (position == 0)
            scroolX = -view.getLeft();
        else {
//        Log.e(TAG, "itemWidth---" + itemWidth);
//        Log.e(TAG, "getLeft--_" + view.getLeft());
            scroolX = ((BaseWaveView) view).getHalfScreenWidth() + (position - 1) * view.getWidth() - view.getLeft();
        }

        Log.e(TAG, "getScollXDistance:" + scroolX);
        return scroolX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRecording)
            return false;
        return super.onTouchEvent(event);
    }

    /**
     *
     */
    public void test() {
        wavedata.add(String.valueOf(new Random().nextInt(150) + 5));
        dealOneWaveDataDisplay();
    }

    /**
     * 获取能录制的最大时间，即距离
     *
     * @return
     */
    private int getCanRecordMaxOffset() {
        BaseWaveView view = getBaseView();
        if (view != null) {
            return view.getCanRecordMaxOffset();
        }
        return 0;
    }

    /**
     * 更新数据
     */
    public void updateData(double volume) {
        wavedata.add(String.valueOf(volume));
        if (getRecordWaveDataWidth() >= getCanRecordMaxOffset()) {
            wavedata.remove(wavedata.size() - 1);
            if (timeChangeListener != null) {
                timeChangeListener.onAudioRecordToMaxTime();
            }
        } else
            dealOneWaveDataDisplay();
    }

    /**
     * 获取需要绘制波形的视图
     *
     * @return
     */
    private int getTargetViewPosition() {
        if (wavedata != null) {
            return (wavedata.size() - 1) / AudioConfig._itemWaveCount + 1;
        }
        return 0;
    }


    /**
     * 每次更新一个波形柱子
     */
    private void dealOneWaveDataDisplay() {
        if (!isRecording)
            return;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                View view = linear.findViewByPosition(getTargetViewPosition());
                if (view != null && view instanceof ItemPcmWaveView) {
                    ItemPcmWaveView waveView = (ItemPcmWaveView) view;
                    int startindex = (wavedata.size() - 1) / AudioConfig._itemWaveCount;
                    int endindex = 0;
                    if (startindex > 0)
                        startindex *= AudioConfig._itemWaveCount;
                    endindex = wavedata.size();
                    waveView.setWaveData(wavedata.subList(startindex, endindex));

                    dealIfAddItemCount();

                    dealIfScroolByView();
                }
            }
        });
    }

    /**
     * 处理是否添加adapter 的count,这个是实现无限制显示波形的关键
     */
    private void dealIfAddItemCount() {
        if (wavedata != null) {
            int waveWidth = getWaveWidth();
            double leftRone = getTotalViewWidth(false) - wavedata.size() * waveWidth;
            Log.e(TAG, "剩余空间：" + leftRone);
            if (leftRone <= getHalfScreenWidth()) {
                displayTime++;
                Log.e(TAG, "数量添加:" + displayTime);
                adapter.notifyItemInserted(displayTime - 1);
            }
        }
    }

    /**
     * 处理是否移动视图波形宽度
     */
    private void dealIfScroolByView() {
        if (wavedata != null) {
            int waveWidth = getWaveWidth();
            if (wavedata.size() * waveWidth > getStartOffset() + waveWidth)
                scrollBy(waveWidth, 0);
            else {
                updateTimeSelection(isRecording, wavedata.size() * waveWidth);
            }
        }
    }

    /**
     * 获取整个视图的宽度
     *
     * @param isNeedHeadWidth 是否需要头部的宽度
     * @return
     */
    private int getTotalViewWidth(boolean isNeedHeadWidth) {
        int position = linear.findFirstVisibleItemPosition();
        if (position == 0)
            position = linear.findLastVisibleItemPosition();
        View view = linear.findViewByPosition(position);
        if (view != null && view instanceof ItemPcmWaveView) {
            int viewWidth = (isNeedHeadWidth ? getHalfScreenWidth() : 0) + (adapter.getItemCount() - 1) * view.getWidth();
            Log.e(TAG, "item总宽度:" + viewWidth);
            return viewWidth;
        }
        return 0;
    }

    /**
     * 获取能滑动的最小距离
     *
     * @return
     */
    private int getMinScrollOffset() {
        int min = getStartOffset() - getRecordWaveDataWidth();
        if (min <= 0)
            min = 0;
        Log.e(TAG, "getMinScrollOffset:" + min);
        return min;
    }

    /**
     * 获取能滑动的最大距离
     *
     * @return
     */
    private int getMaxScrollOffset() {
        Log.e(TAG, "getMaxScrollOffset:" + currentRecordMaxScrollX);
        return currentRecordMaxScrollX;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        Log.e(TAG,"onScrolled");
        getTimeSelectPos();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        Log.e(TAG,"onScrollStateChanged:" + state);
        getTimeSelectPos();
        dealOnParpareAction(state);
    }

    /**
     * @param state
     */
    private void dealOnParpareAction(int state) {
        if (onParparePlaying || onParpareRecording) {
            if (state == SCROLL_STATE_SETTLING)
                isAnimtion = true;
            if (isAnimtion && state == SCROLL_STATE_IDLE) {
                callBackOnParapreAction(onParpareRecording ? true : false);
            }
        }
    }

    /**
     *
     */
    private void callBackOnParapreAction(boolean isRecording) {
        if (parpareListener != null) {
            parpareListener.onParareStartAction(isRecording);
            isAnimtion = false;
            if (isRecording)
                onParpareRecording = false;
            else
                onParparePlaying = false;
        }
    }

    /**
     * 滑动的时候更新时间显示
     */
    private void getTimeSelectPos() {
        updateTimeSelection(isRecording, getScollDistance() - getMinScrollOffset());
    }

    /**
     * 视图滑动的时候更新时间显示
     *
     * @param isRecord 为true表示录制的时间，反之是滑动显示的时间
     * @param pos
     */
    private void updateTimeSelection(boolean isRecord, float pos) {
        if (pos <= 0)
            pos = 0;
        Log.e(TAG, "滑动位置：" + pos);
        if (timeChangeListener != null) {
            double time = pixelsToMillisecs(pos) * 1.0f / 1000.0f;
            timeChangeListener.onTimeChange(isRecord, time, MusicSimilarityUtil.getRecordTimeString(time));
        }
    }

    /**
     * 主要用来显示滑动的区域，控制视图在有效的区域内滑动
     */
    private class WaveLinearLayoutManager extends LinearLayoutManager {
        public WaveLinearLayoutManager(Context context) {
            this(context, HORIZONTAL, false);
        }

        public WaveLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
            if (!isRecording) {
                Log.e(TAG, "变化前_变化差值：" + dx);
                int scrollx = getScollDistance();
                int willDis = scrollx + dx;
                Log.e(TAG, "即将滑动到：" + willDis);
                if (dx > 0) {
                    //左滑动
                    int maxLimit = getMaxScrollOffset();
                    if (willDis >= maxLimit) {
                        stopScroll();
                        dx = maxLimit - scrollx;
                    }
                } else {
                    //右滑动
                    int miniLimit = getMinScrollOffset();
                    if (willDis <= miniLimit) {
                        stopScroll();
                        dx = miniLimit - scrollx;
                    }
                }

                Log.e(TAG, "变化后_变化差值：" + dx);
            }
            return super.scrollHorizontallyBy(dx, recycler, state);
        }
    }

    /**
     * 波形
     */
    private class TestWaveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int ViewTypeHead = 1, ViewTypeContent = 2;

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return ViewTypeHead;
            return ViewTypeContent;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(viewType == ViewTypeHead ? new ItemPcmWaveViewHead(ctx) : new ItemPcmWaveView(ctx)) {

            };
        }

        /**
         * 根据时间块获取相应时间波形数据
         *
         * @param pos
         * @return
         */
        private List<String> getWaveList(int pos) {
            if (wavedata == null || wavedata.isEmpty())
                return null;
            int startindex = pos * AudioConfig._itemWaveCount, endindex = startindex + AudioConfig._itemWaveCount;
            if (startindex > wavedata.size())
                return null;
            if (endindex > wavedata.size())
                endindex = wavedata.size();
            return wavedata.subList(startindex, endindex);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.e(TAG, "位置:" + position);
            if (getItemViewType(position) != ViewTypeHead && holder.itemView instanceof ItemPcmWaveView) {
                ItemPcmWaveView view = (((ItemPcmWaveView) (holder.itemView)));
                view.setSecondsAndWaveData(position - 1, getWaveList(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            return displayTime;
        }
    }

    /**
     * 滑动到录制的指定位置
     */
    private void scroolToRecordPos() {
        int scroolx = currentRecordMaxScrollX - getScollDistance();
        if (scroolx == 0) {
            if (parpareListener != null)
                parpareListener.onParareStartAction(true);
        } else {
            onParpareRecording = true;
//            scrollBy(scroolx, 0);
            smoothScrollBy(scroolx,0);
        }
    }

    /**
     * 开始录制
     */
    public void startRecording() {
        setRecording(true);
        scroolToRecordPos();
    }

    /**
     * @return
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        setRecording(false);
        currentRecordMaxScrollX = getScollDistance();
        Log.e(TAG, "currentRecordMaxScrollX:" + currentRecordMaxScrollX);
    }

    /**
     * @param recording
     */
    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    /**
     * @param timeChangeListener
     */
    public void setTimeChangeListener(onScrollTimeChangeListener timeChangeListener) {
        this.timeChangeListener = timeChangeListener;
    }

    /**
     * 获取刻度宽度
     *
     * @return
     */
    private int getTimeMargin() {
        BaseWaveView view = getBaseView();
        if (view != null) {
            return view.getTimeMargin();
        }
        return 0;
    }

    /**
     * 像素对应的时间
     *
     * @param pixels
     * @return 返回时间ms
     */
    public double pixelsToMillisecs(float pixels) {
        double onePixelTime = AudioConfig._timeSpace * 1.0d / getTimeMargin() * 1.0d;
        return onePixelTime * pixels;
    }

    /**
     * 时间对于像素
     *
     * @param ms
     * @return
     */
    public int millisecsToPixels(double ms) {
        return (int) (getTimeMargin() * ms * 1.0f / AudioConfig._timeSpace * 1.0f + 0.5);
    }


    /**
     * @param data
     */
    private void copyList(LinkedList<String> data) {
        wavedata = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            wavedata.add(data.get(i));
        }
    }

    /**
     * 根据剩余的波形数据计算出时间
     */
    private void calurateTimeSeconds() {
        int itemCount = wavedata.size() / AudioConfig._itemWaveCount;
        int leftMore = wavedata.size() % AudioConfig._itemWaveCount;
        if (leftMore > 0)
            itemCount += 1;

        displayTime = 1 + itemCount + 4;
    }

    /**
     * 在音频编辑后，重新刷新视图
     */
    public void refreshAfterEdit(final LinkedList<String> data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyList(data);
                calurateTimeSeconds();
                // TODO: 2017/4/10 这里还要优化  编辑音频后显示有问题
                adapter.notifyDataSetChanged();
            }
        }).start();
    }

    /**
     * @param mListener
     */
    public void setOnParpareStartRecordingListener(onParpareStartRecordingListener mListener) {
        parpareListener = mListener;
    }

    /**
     *
     */
    public interface onParpareStartRecordingListener {
        /**
         * 动画执行完准备开始某个动作
         *
         * @param isRecording true表示可以开始录音数据这块
         *                    false表示可以开始播放语音
         */
        public void onParareStartAction(boolean isRecording);
    }
}
