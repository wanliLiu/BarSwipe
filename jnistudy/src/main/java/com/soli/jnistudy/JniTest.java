package com.soli.jnistudy;

/**
 * Created by soli on 16/04/2017.
 */

public class JniTest {

    static {
        System.loadLibrary("JniTest");
    }

    public static native String getStringFromJni();
}
