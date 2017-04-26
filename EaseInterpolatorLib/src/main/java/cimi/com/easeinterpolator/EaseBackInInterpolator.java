package cimi.com.easeinterpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

/**
 * Created by cimi on 15/7/2.
 */

public class EaseBackInInterpolator implements Interpolator {

    public EaseBackInInterpolator() {
    }

    public EaseBackInInterpolator(Context context, AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        return input * input * ((1.70158f + 1) * input - 1.70158f);
    }
}
