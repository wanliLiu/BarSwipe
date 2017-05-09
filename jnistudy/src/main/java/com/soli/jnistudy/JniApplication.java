package com.soli.jnistudy;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Soli on 2017/5/9.
 */

public class JniApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Stetho是一个Android应用的调试工具 Chrome Developer Tools
        Stetho.initializeWithDefaults(this);
    }
}
