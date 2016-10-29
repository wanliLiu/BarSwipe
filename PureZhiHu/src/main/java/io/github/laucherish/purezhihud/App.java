package io.github.laucherish.purezhihud;

import android.app.Application;
import android.content.Context;

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
    }

    // 获取ApplicationContext
    public static Context getContext() {
        return mApplicationContext;
    }
}
