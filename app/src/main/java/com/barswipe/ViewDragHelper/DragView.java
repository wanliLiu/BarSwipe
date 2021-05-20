package com.barswipe.ViewDragHelper;

import android.content.Context;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by soli on 5/29/16.
 * <p/>
 * refrences
 * http://blog.csdn.net/lmj623565791/article/details/46858663
 * http://www.it165.net/pro/html/201505/40127.html
 */
public class DragView extends FrameLayout {

    private final String TAG = DragView.class.getSimpleName();

    private View dragView;

    private ViewDragHelper dragHelper;

    private int initLeft, initRight;

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
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
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
        if (getChildCount() > 0) {
            dragView = getChildAt(0);
            dragView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    initLeft = dragView.getLeft();
                    initRight = dragView.getRight();
                    Log.e(TAG, "X-" + dragView.getLeft() + ";" + dragView.getRight() + "Y");
                }
            });
        }
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

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
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


            return Math.min(Math.max(leftbound, left), rightbound);
//            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.e(TAG, "clampViewPositionVertical " + top + "," + dy);
            final int topbound = getPaddingTop();
            final int bottombound = getMeasuredHeight() - getPaddingBottom() - child.getMeasuredHeight();

            return Math.min(Math.max(top, topbound), bottombound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
//            dragHelper.smoothSlideViewTo(releasedChild, initLeft, initRight);
            dragHelper.settleCapturedViewAt(initLeft, initRight);
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
            if ((edgeFlags & ViewDragHelper.EDGE_RIGHT) != 0) {
                dragHelper.captureChildView(dragView, pointerId);
            }
        }
    }
}
