package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/4/20.
 */

public class HeadSetEvent {

    private boolean isSet = false;

    public HeadSetEvent(boolean set) {
        isSet = set;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }
}
