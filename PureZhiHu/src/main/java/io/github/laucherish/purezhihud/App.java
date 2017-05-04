package io.github.laucherish.purezhihud;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import io.github.laucherish.purezhihud.utils.AppContextUtil;
import io.github.laucherish.purezhihud.utils.L;

/**
 * Created by laucherish on 16/3/17.
 */
public class App extends Application {

    private static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
        AppContextUtil.init(this);
        L.init();

        //Stetho是一个Android应用的调试工具 Chrome Developer Tools
        Stetho.initializeWithDefaults(this);
    }

    // 获取ApplicationContext
    public static Context getContext() {
        return mApplicationContext;
    }
}
