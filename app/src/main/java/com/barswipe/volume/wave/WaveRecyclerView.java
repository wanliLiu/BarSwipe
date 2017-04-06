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

import java.util.List;

/**
 * Created by Soli on 2017/4/5.
 */

public class WaveRecyclerView extends RecyclerView {

    private final String TAG = "WaveRecyclerView";

    private scrollListener listener;
    private Context ctx;

    private MyLinearLayoutManager linear;


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
        linear.setCanScroll(true);
        setLayoutManager(linear);
        setAdapter(new TestWaveAdapter());
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

//            Log.e(TAG, "滑动距离" + "dx:" + dx + "   dy:" + dy + "  getScrollX:" + recyclerView.getScrollX() + "  getScollXDistance:" + getScollXDistance());
        }
    }

    /**
     * @return
     */
    public int getScollXDistance() {
        int position = linear.findFirstVisibleItemPosition();
        View firstVisiableChildView = linear.findViewByPosition(position);
        if (firstVisiableChildView != null)
            return 0;
        int itemHeight = firstVisiableChildView.getWidth();
        itemHeight = (position) * itemHeight - firstVisiableChildView.getLeft();
        Log.e(TAG, "  getScollXDistance:" + itemHeight);
        return itemHeight;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
//        Log.e(TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        Log.e(TAG, "onTouchEvent");
        return super.onTouchEvent(e);
    }

    public void test(List<String> data) {
//        View view = linear.findViewByPosition(data.size() > 36 ? linear.findFirstVisibleItemPosition() + 1 : linear.findFirstVisibleItemPosition());
        View view = linear.findViewByPosition((data.size() - 1) / 36);
        if (view != null && view instanceof TestPcmWaveView) {
            TestPcmWaveView waveView = (TestPcmWaveView) view;
            int startindex = (data.size() - 1) / 36;
            int endindex = 0;
            if (startindex > 0)
                startindex *= 36;
            endindex = data.size();
            waveView.setWaveData(data.subList(startindex, endindex));

            if (getScollXDistance() > waveView.getHalfScreenWidth())
                scrollBy(waveView.getWaveWidth(), 0);
        }
    }

    /**
     *
     */
    private class MyLinearLayoutManager extends LinearLayoutManager {

        private boolean isCanScroll = false;

        public MyLinearLayoutManager(Context context) {
            this(context, HORIZONTAL, false);
        }

        public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setCanScroll(boolean canScroll) {
            isCanScroll = canScroll;
        }

        public void toggleScroll() {
            isCanScroll = !isCanScroll;
        }

        @Override
        public boolean canScrollHorizontally() {
            return isCanScroll && super.canScrollHorizontally();
        }
    }


    private class TestWaveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(new TestPcmWaveView(ctx)) {

            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof TestPcmWaveView) {
                TestPcmWaveView view = (((TestPcmWaveView) (holder.itemView)));
                view.setSeconds(position);
//                int pos = position >= 4 ? position - 4 : -1;
//                ((TestPcmWaveView) (holder.itemView)).setSeconds(pos);
            }
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }
}
