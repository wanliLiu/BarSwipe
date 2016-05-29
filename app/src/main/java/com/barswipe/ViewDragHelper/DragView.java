package com.barswipe.ViewDragHelper;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by soli on 5/29/16.
 */
public class DragView extends FrameLayout {

    private final String TAG = DragView.class.getSimpleName();

    private View dragView;

    private ViewDragHelper dragHelper;

    public DragView(Context context) {
        super(context);
        Init(context);
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    /**
     * @param ctx
     */
    private void Init(Context ctx) {
        dragHelper = ViewDragHelper.create(this, 1.0f, new dragCallback());
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        for (int i = 0; i < getChildCount(); i++) {
//            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
//        }
//    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0)
            dragView = getChildAt(0);
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//
//        final int left = getMeasuredWidth() / 2 - dragView.getMeasuredWidth() / 2;
//        final int top = getMeasuredHeight() / 2 - dragView.getMeasuredHeight() / 2;
//        final int right = dragView.getMeasuredWidth();
//        final int bottom = dragView.getMeasuredWidth();
//        dragView.layout(left,top,right,bottom);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel();
            return false;
        }

        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    /**
     *
     */
    private class dragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == dragView;
        }


        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.e(TAG, "clampViewPositionHorizontal " + left + "," + dx);

            final int leftbound = getPaddingLeft();
            final int rightbound = getMeasuredWidth() - getPaddingRight() - child.getMeasuredWidth();


//            return Math.min(Math.max(leftbound,left),rightbound);
            return left;
        }
    }
}
