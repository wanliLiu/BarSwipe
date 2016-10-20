package com.barswipe;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.another).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
            }
        });

        findViewById(R.id.anotsdher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity.this, BlurActivity.class));
            }
        });

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimaryDark));

        EventBus.getDefault().register(this);

        findViewById(R.id.crotuon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration configuration = new Configuration.Builder()
                        .setDuration(Configuration.DURATION_INFINITE)
                        .build();
                Style style = new Style.Builder()
                        .setConfiguration(configuration)
                        .setBackgroundColor(R.color.colorPrimary)
                        .setTextColor(R.color.colorAccent)
                        .setImageResource(R.mipmap.icon_loading_10)
                        .setTextSize(30)
                        .build();
                Crouton.makeText(SecondActivity.this, "第二次点击", style, (ViewGroup) findViewById(R.id.dtesTe)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ((Crouton)v.getTag()).hide();
                        Snackbar.make(v, "你为啥要点击我啊？？？", Snackbar.LENGTH_SHORT).show();
                        OpenNotify();
                    }
                }).show();
            }
        });

        int actionBarContainerId = Resources.getSystem().getIdentifier("action_mode_bar_stub", "id", "android");
        View actionBarContainer = this.findViewById(actionBarContainerId);
        if (actionBarContainer != null) {
            Toast.makeText(this, "不是吧颠三倒四", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.Fourth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                backgroundAlpha(0.5f);
                DisplayMetrics dm = new DisplayMetrics();
                SecondActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
//                final PopupWindow pup = new PopupWindow(getLayoutInflater().inflate(R.layout.popupwindeow,null), dm.widthPixels,ViewGroup.LayoutParams.WRAP_CONTENT, true);
                final PopupWindow pup = new PopupWindow(getLayoutInflater().inflate(R.layout.popupwindeow, null), dm.widthPixels, dm.heightPixels * 2 / 3, true);
                pup.setFocusable(true);
                pup.setOutsideTouchable(true);
                pup.setAnimationStyle(R.style.popwin_anim_style);
//                backgroundAlpha(0.5f);
//                ColorDrawable cd = new ColorDrawable(0x000000);
////                pup.setBackgroundDrawable(cd);
                pup.setBackgroundDrawable(new BitmapDrawable());
//                pup.showAsDropDown(findViewById(R.id.toolbar), 0, 0);
                pup.showAsDropDown(findViewById(R.id.toolbar));
                pup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                    }
                });
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = SecondActivity.this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void OpenNotify() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        try {
            Object service = getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = null;
            if (service != null) {
                if (currentApiVersion <= 16) {
                    expand = statusbarManager.getMethod("expand");
                } else {
                    expand = statusbarManager.getMethod("expandNotificationsPanel");
                }
                expand.setAccessible(true);
                expand.invoke(service);
            }

        } catch (Exception e) {
        }

    }

    public void onEvent(String dsd) {
        View view = findViewById(R.id.back);
        view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Crouton.cancelAllCroutons();
    }
}
