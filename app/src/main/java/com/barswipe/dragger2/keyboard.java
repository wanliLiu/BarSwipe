package com.barswipe.dragger2;

/**
 * Created by Soli on 2017/5/10.
 */

public class keyboard {
    private static final String TAG = "keyboard";

    private String from;

    public keyboard() {

        System.out.println("这是键盘");
    }

    public keyboard(String from) {
        this.from = from;
        System.out.println("这是产自" + from + "的键盘");
    }
}
