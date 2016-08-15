package com.barswipe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by soli on 8/15/16.
 */
public class SquareViewGroup extends FrameLayout {
    public SquareViewGroup(Context context) {
        super(context);
    }

    public SquareViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
