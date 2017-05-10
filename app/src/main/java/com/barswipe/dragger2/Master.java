package com.barswipe.dragger2;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Soli on 2017/5/10.
 */

public class Master {
    public Master() {
    }
    @Inject
    public Master(@Named("taste") String string) {
        System.out.println("这是" + string + "Master");
    }
}
