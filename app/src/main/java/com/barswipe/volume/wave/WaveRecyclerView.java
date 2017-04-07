package com.barswipe.volume.wave;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Soli on 2017/4/5.
 */

public class WaveRecyclerView extends RecyclerView {

    private final String TAG = "WaveRecyclerView";

    private scrollListener listener;
    private Context ctx;

    private MyLinearLayoutManager linear;
    private TestWaveAdapter adapter;

    private List<String> wavedata = new ArrayList<>();

    private boolean isCanScroll = false;

    private int displayTime = 3;//目前显示3s时间，然后后面根据录制的时间后移动

    public WaveRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public WaveRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaveRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @param context
     */
    private void init(Context context) {
        ctx = context;
        addOnScrollListener(listener = new scrollListener());


        linear = new MyLinearLayoutManager(ctx);
        setLayoutManager(linear);

        adapter = new TestWaveAdapter();
        setAdapter(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (listener != null)
            removeOnScrollListener(listener);
    }

    private class scrollListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            Log.e(TAG, "滑动距离" + "dx:" + dx + "   dy:" + dy + "  getScrollX:" + recyclerView.getScrollX() + "  getScollXDistance:" + getScollXDistance());
        }
    }

    /**
     * @return
     */
    public int getScollXDistance() {
        int position = linear.findFirstVisibleItemPosition();
        View firstVisiableChildView = linear.findViewByPosition(position);
        if (firstVisiableChildView == null)
            return 0;
        Log.e(TAG, "findFirstVisibleItemPosition---" + position);
        int itemWidth = firstVisiableChildView.getWidth();
        Log.e(TAG, "itemWidth---" + itemWidth);
        Log.e(TAG, "getLeft--_" + firstVisiableChildView.getLeft());
        itemWidth = (position) * itemWidth - firstVisiableChildView.getLeft();
        Log.e(TAG, "getScollXDistance:" + itemWidth);
        return itemWidth;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
//        Log.e(TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        Log.e(TAG, "onTouchEvent");
        if (!isCanScroll)
            return true;
        return super.onTouchEvent(e);
    }

    private Handler testHan = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            wavedata.add(String.valueOf(new Random().nextInt(150) + 5));
            test(wavedata);
            testHan.sendEmptyMessageDelayed(1, 83);
        }
    };

    /**
     *
     */
    public void test() {
        testHan.sendEmptyMessage(1);
//        wavedata.add(String.valueOf(new Random().nextInt(150) + 5));
//        test(wavedata);
    }

    public void test(List<String> data) {
        View view = linear.findViewByPosition((data.size() - 1) / AudioConfig._itemWaveCount);
        if (view != null && view instanceof TestPcmWaveView) {
            TestPcmWaveView waveView = (TestPcmWaveView) view;
            int startindex = (data.size() - 1) /  AudioConfig._itemWaveCount;
            int endindex = 0;
            if (startindex > 0)
                startindex *=  AudioConfig._itemWaveCount;
            endindex = data.size();
            waveView.setWaveData(data.subList(startindex, endindex));

            double leftRone = getTotalViewWidth() - data.size() * waveView.getWaveWidth();
            Log.e(TAG, "剩余空间：" + leftRone);
            if (leftRone <= waveView.getHalfScreenWidth()) {
                displayTime++;
                Log.e(TAG, "数量添加:" + displayTime);
                adapter.notifyItemInserted(displayTime - 1);
            }

            if (data.size() * waveView.getWaveWidth() >= waveView.getHalfScreenWidth()) {
                scrollBy((int)waveView.getWaveWidth(), 0);
            }

        }
    }

    /**
     * 获取整个视图的宽度
     *
     * @return
     */
    private int getTotalViewWidth() {

        View viewLast = linear.findViewByPosition(adapter.getItemCount() - 1);
        if (viewLast != null) {
            Log.e(TAG, "item总宽度:" + viewLast.getWidth() * adapter.getItemCount());
            return viewLast.getWidth() * adapter.getItemCount();
        }

        return 0;
    }

    /**
     *
     */
    private class MyLinearLayoutManager extends LinearLayoutManager {
        public MyLinearLayoutManager(Context context) {
            this(context, HORIZONTAL, false);
        }

        public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }


    private class TestWaveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(new TestPcmWaveView(ctx)) {

            };
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        /**
         * @param pos
         * @return
         */
        private List<String> getWaveList(int pos) {
            if (wavedata == null || wavedata.isEmpty())
                return null;
            int startindex = pos *  AudioConfig._itemWaveCount, endindex = startindex +  AudioConfig._itemWaveCount;
            if (startindex > wavedata.size())
                return null;
            if (endindex > wavedata.size())
                endindex = wavedata.size();
            return wavedata.subList(startindex, endindex);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.e(TAG, "位置" + position);
            if (holder.itemView instanceof TestPcmWaveView) {
                TestPcmWaveView view = (((TestPcmWaveView) (holder.itemView)));
//                view.setSeconds(position);
                view.setSecondsAndWaveData(position, getWaveList(position));

//                int pos = position >= 4 ? position - 4 : -1;
//                ((TestPcmWaveView) (holder.itemView)).setSeconds(pos);
            }
        }

        @Override
        public int getItemCount() {
            return displayTime;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }


    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public void toggleScroll() {
        isCanScroll = !isCanScroll;

        if (isCanScroll)
        {
            testHan.removeMessages(1);
        }
    }
}
