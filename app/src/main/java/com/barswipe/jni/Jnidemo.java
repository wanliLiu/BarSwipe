package com.barswipe.jni;

/**
 * Created by soli on 16/04/2017.
 */

public class Jnidemo {

    static {
        System.loadLibrary("jnidemo");
    }

    public static native String getStringFromJni();
}
