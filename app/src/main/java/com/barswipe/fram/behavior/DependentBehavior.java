package com.barswipe.fram.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by soli on 06/11/2016.
 */

public class DependentBehavior extends CoordinatorLayout.Behavior<TextView> {

    public DependentBehavior() {
        super();
    }

    public DependentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, TextView child, View directTargetChild, View target, int nestedScrollAxes) {
//        if (target instanceof NestedScrollingChildView)
//            return true;
            return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
//        return false;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, TextView child, View target, int dx, int dy, int[] consumed) {
        ViewCompat.offsetTopAndBottom(coordinatorLayout.findViewById(R.id.depentent), dy > 0 ? 5 : -5);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof Button;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        int offset = dependency.getTop() - child.getTop();
        ViewCompat.offsetTopAndBottom(child, offset);
        return true;
    }
}
