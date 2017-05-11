package io.github.laucherish.purezhihud.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.laucherish.purezhihud.utils.PrefUtil;
import io.github.laucherish.purezhihud.utils.swipeback.SwipeBackActivity;
import io.github.laucherish.purezhihud.utils.swipeback.SwipeBackLayout;

/**
 * Created by laucherish on 16/3/15.
 */
public abstract class BaseActivity extends SwipeBackActivity {

    protected SwipeBackLayout mSwipeBackLayout;
    private Unbinder unbinder;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefUtil.getThemeRes());
//        if (isDay())
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            {
//                getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }else{
//                if (android.os.Build.BRAND.contains("Xiaomi")) {
//                    XiaoMisetStatusBarDarkMode(true, this);
//                } else if (android.os.Build.BRAND.contains("Meizu")) {
//                    MeizusetStatusBarDarkIcon(true, this);
//                }
//            }
//        }else {
//            if (android.os.Build.BRAND.contains("Xiaomi")) {
//                XiaoMisetStatusBarDarkMode(false, this);
//            } else if (android.os.Build.BRAND.contains("Meizu")) {
//                MeizusetStatusBarDarkIcon(false, this);
//            }
//        }

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        unbinder = ButterKnife.bind(this);
        afterCreate(savedInstanceState);
    }


    /**
     * 小米
     *
     * @param darkmode
     * @param activity
     */
    private void XiaoMisetStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 魅族手机
     *
     * @param dark
     * @param activity
     * @return
     */
    private void MeizusetStatusBarDarkIcon(boolean dark, Activity activity) {
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
            } catch (Exception e) {
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}
