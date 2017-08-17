package com.barswipe.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AutoWrapListView<T> extends ViewGroup {

    private final String TAG = AutoWrapListView.class.getSimpleName();
    private AutoWrapAdapter<T> myCustomAdapter;

    private boolean addChildType;
    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.getData().containsKey("getRefreshThreadHandler")) {
                    setAddChildType(false);
                    myCustomAdapter.notifyCustomListView(AutoWrapListView.this);
                }
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        }

    };
    private int dividerWidth = 20;
    private int dividerHeight = 30;
    private int viewWidth = 0;
    private int maxNumRows = -1;

    public AutoWrapListView(Context context) {
        super(context);
    }

    public AutoWrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoWrapListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean arg0, int Left, int Top, int Right, int Bottom) {
        final int count = getChildCount();
        int lengthX = getPaddingLeft();
        int lengthY = getPaddingTop();
        int rows = 0;
        int height = 0;
        //布局所有子View
        int i;
        for (i = 0; i < count; i++) {

            final View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            //AutoWrapListView 功能，所有的item高度是一致的，所以这里取第一个item的高度作为所有单个item的高度
            if (i == 0)
                height = child.getMeasuredHeight();

            int leftZone;//行的剩余宽度
            //如果是第一列
            if (lengthX == getPaddingLeft()) {
                leftZone = viewWidth;
            } else
                leftZone = viewWidth - lengthX;

            boolean isNeedChange = false;
            boolean isBigThanViewWidth = false;
            boolean isSpecial = false;
            //如果需要的宽度大于剩余的宽度，就要换行
            if (width >= leftZone) {
                rows++;
                if (rows >= maxNumRows && maxNumRows > 0) {
                    if (i == 0 && width >= viewWidth)
                        isSpecial = true;
                    else
                        break;
                }
                isNeedChange = true;
                lengthX = getPaddingLeft();
                //直接就占了一行
                if (width >= viewWidth) {
                    width = viewWidth;
                    isBigThanViewWidth = true;
                }
                //如果是第一行，并且占满整行的话，高度不调整
                if (i != 0)
                    lengthY += height + getDividerHeight();
            }
            child.layout(lengthX, lengthY, lengthX + width, lengthY + height);
            if (isNeedChange) {
                if (!isBigThanViewWidth) {
                    lengthX += width + getDividerWidth();
                } else {
                    lengthY += height + getDividerHeight();
                }
            } else {
                lengthX += width + getDividerWidth();
            }

            if (isSpecial) {
                break;
            }
        }
        //计算整个视图的高度，主要是通过有多少行来计算的
        if (count > 0) {
            LayoutParams lp = getLayoutParams();
            rows = (rows == maxNumRows ? rows : rows + 1);
            lp.height = getPaddingTop() + rows * height + (rows - 1) * getDividerHeight() + getPaddingBottom();
            setLayoutParams(lp);
            //删除多余没显示的视图
//            if (i < count)
//                removeOtherView(i);
        }
        if (isAddChildType()) {
            //使得数据会调用两次
            new Thread(new RefreshCustomThread()).start();
        }
    }

    /**
     * 当有限制的时候移除多余的item
     *
     * @param start
     */
    private void removeOtherView(int start) {
        for (; start < getChildCount(); start++) {
            final View child = getChildAt(start);
            if (child != null)
                removeView(child);
        }
    }

    /**
     * @param child
     * @param width
     */
    private void setViewWidth(View child, int width) {
        LayoutParams params = child.getLayoutParams();
        params.width = width;
        child.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量父容器
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth = width - getPaddingLeft() - getPaddingRight();

        setMeasuredDimension(width, height);
        //测量子View
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private final boolean isAddChildType() {
        return addChildType;
    }

    public void setAddChildType(boolean maddChildType) {
        addChildType = maddChildType;
    }

    private final int getDividerHeight() {
        return dividerHeight;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    private final int getDividerWidth() {
        return dividerWidth;
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
    }

    /**
     * @param adapter
     */
    public void setAdapter(AutoWrapAdapter<T> adapter) {
        this.myCustomAdapter = adapter;
        setAddChildType(true);
        adapter.notifyCustomListView(this);
    }

    /**
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        myCustomAdapter.setOnItemClickListener(listener);
    }

    /**
     * Corresponding Item long click event
     *
     * @param listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        myCustomAdapter.setOnItemLongClickListener(listener);
    }

    private final void sendMsgHanlder(Handler handler, Bundle data) {
        Message msg = handler.obtainMessage();
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * @param maxRows
     */
    public void setMaxNumRows(int maxRows) {
        maxNumRows = maxRows;
    }

    private final class RefreshCustomThread implements Runnable {

        @Override
        public void run() {
            Bundle b = new Bundle();
            try {
                Thread.sleep(50);
            } catch (Exception e) {

            } finally {
                b.putBoolean("getRefreshThreadHandler", true);
                sendMsgHanlder(handler, b);
            }
        }
    }

}
