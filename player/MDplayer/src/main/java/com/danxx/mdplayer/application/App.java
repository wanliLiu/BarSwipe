package com.danxx.mdplayer.application;

import android.app.Application;
import android.content.Context;

import com.danxx.mdplayer.model.CacheManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;

/**
 * Created by Danxx on 2016/5/30.
 */
public class App extends Application {
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fresco.initialize(mContext);
        //初始化缓存模块
        CacheManager.getInstance().init(mContext);
        //Stetho是一个Android应用的调试工具 Chrome Developer Tools
        Stetho.initializeWithDefaults(this);
    }

    public Context getAppContext() {
        return mContext;
    }

}
