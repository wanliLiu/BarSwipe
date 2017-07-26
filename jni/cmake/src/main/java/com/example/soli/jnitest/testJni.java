package com.example.soli.jnitest;

/**
 * TODO: 2017/6/27 此处需要输入描述文字
 *
 * @author Soli
 * @Time 2017/6/27
 */
public class testJni {

    static {
        System.loadLibrary("test2");
    }

    public static native String getString();
}
