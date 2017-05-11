package com.barswipe.dragger2;

import com.barswipe.Myapplication;

import javax.inject.Inject;

/**
 * Created by Soli on 2017/5/11.
 */

public class Jason {
    @Inject
    House house;
    public Jason() {
        Myapplication.getHouseComponent().inject(this);
        System.out.println("Jason"+house.hashCode());
    }
}
