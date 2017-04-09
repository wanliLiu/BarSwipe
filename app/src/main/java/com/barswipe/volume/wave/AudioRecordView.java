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

import java.util.ArrayList;
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
    private int displayTime = 30;

    private int limitIndex = 5;

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
    private int getScreenHalfWidth() {
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
        dealWaveDataDisplay();
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
     * 分配波形数据 并显示
     */
    private void dealWaveDataDisplay() {
        View view = linear.findViewByPosition(getTargetViewPosition());
        if (view != null && view instanceof TestPcmWaveView) {
            TestPcmWaveView waveView = (TestPcmWaveView) view;
            int startindex = (wavedata.size() - 1) / AudioConfig._itemWaveCount;
            int endindex = 0;
            if (startindex > 0)
                startindex *= AudioConfig._itemWaveCount;
            endindex = wavedata.size();
            waveView.setWaveData(wavedata.subList(startindex, endindex));

            //// TODO: 09/04/2017  根据条件自动添加item
            double leftRone = getTotalViewWidth() - wavedata.size() * waveView.getWaveWidth();
            Log.e(TAG, "剩余空间：" + leftRone);
            if (leftRone <= waveView.getHalfScreenWidth()) {
                displayTime++;
                Log.e(TAG, "数量添加:" + displayTime);
                adapter.notifyItemInserted(displayTime - 1);
            }

            if (wavedata.size() * waveView.getWaveWidth() >= waveView.getHalfScreenWidth() - waveView.getTimeMargin()) {
                scrollBy((int) waveView.getWaveWidth(), 0);
            }

        }
    }

    /**
     * @param pos
     */
    private int getPointDistance(int pos) {
        View view = linear.findViewByPosition(linear.findFirstVisibleItemPosition());
        if (view != null) {
            Log.e(TAG, "getPointDistance:" + "__pos__" + pos + "_" + view.getWidth() * pos);
            return view.getWidth() * pos;
        }
        return 0;
    }

    /**
     * 获取整个视图的宽度
     *
     * @return
     */
    private int getTotalViewWidth() {

        // TODO: 09/04/2017 这里看是否去掉head的宽度
        View view = linear.findViewByPosition(linear.findFirstVisibleItemPosition());
        if (view != null) {
            Log.e(TAG, "item总宽度:" + view.getWidth() * adapter.getItemCount());
            return view.getWidth() * adapter.getItemCount();
        }

        return 0;
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
            int willDis = getScollDistance() + dx;
            if (dx > 0) {
                //左滑动
//                Log.e(TAG, "scrollHorizontallyBy--temp:" + temp);
//                int limit = getPointDistance(limitIndex);
//                Log.e(TAG, "scrollHorizontallyBy--limit:" + limit);
//                if (temp >= limit) {
//                    stopScroll();
//                    dx = limit - getScollDistance();
//                    Log.e(TAG, "scrollHorizontallyBy--处理:" + dx);
//                }
            } else {
                //右滑动
                int limit = getStartOffset() - getRecordWaveDataWidth();
                if (willDis <= limit) {
                    stopScroll();
                    dx = 0;
                }

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
            return new RecyclerView.ViewHolder(viewType == ViewTypeHead ? new TestPcmWaveViewHead(ctx) : new TestPcmWaveView(ctx)) {

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
            if (holder.itemView instanceof TestPcmWaveView) {
                TestPcmWaveView view = (((TestPcmWaveView) (holder.itemView)));
                view.setSecondsAndWaveData(position - 1, getWaveList(position));
            }
        }

        @Override
        public int getItemCount() {
            return displayTime;
        }
    }

    /**
     * 开始录制
     */
    public void startRecording() {
        setRecording(true);
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        setRecording(false);
    }

    /**
     * @param recording
     */
    public void setRecording(boolean recording) {
        isRecording = recording;
    }
}
