package com.barswipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.barswipe.SwipeBackLayout.app.SwipeBackActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by SoLi on 2015/12/7.
 */
public class BaseActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.quiet_fixedly);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_left_in, R.anim.quiet_fixedly);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.quiet_fixedly, R.anim.push_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    @SuppressLint("NewApi")
    public  void hideSystemUI(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

//    protected void onResume() {
//        // 判断是否是android4.4
//        if (android.os.Build.VERSION.SDK_INT > 18) {
//            // 获得根视图
//            View view = getWindow().getDecorView();
//            // 获得根布局
//            ViewGroup vGroup = (ViewGroup) (view.findViewById(android.R.id.content));
//            // 用于判断应用是否已经退出沉浸模式了。
//            int status = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_VISIBLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            // 判断是否要开启沉浸模式
//            if (true) {
//                // 需要开启沉浸模式则把actionbar先隐藏掉，不然在Activity跳转时会闪出来。
//                // 进入沉浸模式
//                hideSystemUI(view);
//                // 在根布局获得第一个控件，也就是最上层的layout。把内边距设为0
//                vGroup.getChildAt(0).setPadding(0, 0, 0, 0);
//            } else if (view.getSystemUiVisibility() == status){
////                // 如果应用已经退出沉浸模式，但是这个activity还是在沉浸模式内，则退出沉浸模式。
////                actionBar.show();
////                SystemUI.showSystemUI(view);
////                // 获得系统栏高度和actionbar高度，设置内边距。
////                Rect frame = new Rect();
////                view.getWindowVisibleDisplayFrame(frame);
////                vGroup.getChildAt(0).setPadding(0, actionBar.getHeight() + frame.top, 0, 0);
//            }
//        }
//        super.onResume();
//    }

}
