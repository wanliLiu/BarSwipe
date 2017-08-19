package com.barswipe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * wrap_content --- MeasureSpec.AT_MOST
 * match_parent 和 fill_parent--- MeasureSpec.EXACTLY
 *
 * @param <T>
 */
public class AutoWrapListView<T> extends ViewGroup {

    protected List<List<View>> mAllViews = new ArrayList<>();
    protected List<Integer> mLineHeight = new ArrayList<>();
    protected List<Integer> mLineWidth = new ArrayList<>();
    private AutoWrapAdapter<T> myCustomAdapter;
    private List<View> lineViews = new ArrayList<>();

    private int maxRows = -1;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        int rows = 0;
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                //上一行的视图宽度
                width = Math.max(width, lineWidth);
                //下一行的行宽
                lineWidth = childWidth;
                //到目前为止上一行的高度
                height += lineHeight;
                //下一行的行高
                lineHeight = childHeight;

                if (maxRows > 0 && ++rows >= maxRows)
                    break;

            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                //最后加上最后一行的高度
                height += lineHeight;
            }
        }

        if (modeWidth == MeasureSpec.EXACTLY) {
            width = sizeWidth;
        } else {
            width += getPaddingLeft() + getPaddingRight();
        }

        if (modeHeight == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else {
            height += getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();
        int rows = 0;

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lineWidth > width - getPaddingLeft() - getPaddingRight()) {
                if (maxRows > 0 && ++rows >= maxRows)
                    break;
                //上一行的相关数据
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                mLineWidth.add(lineWidth);

                lineWidth = 0;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);

        }
        //最后一行
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);


        int left;
        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            left = getPaddingLeft();
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                int rc = left + child.getMeasuredWidth();
                int bc = top + child.getMeasuredHeight();

                child.layout(left, top, rc, bc);

                left += child.getMeasuredWidth();
            }
            top += lineHeight;
        }

    }

    /**
     * @param adapter
     */
    public void setAdapter(AutoWrapAdapter<T> adapter) {
        this.myCustomAdapter = adapter;
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

    /**
     * @param maxRows
     */
    public void setMaxNumRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getMaxRows() {
        return maxRows;
    }
}
