package com.barswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ThirdActivity extends BaseActivity {

    private boolean isProgressShow = false;
    private ImageView bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("ThirdActivity");

//        StatusBarUtil.setStatusBarColor();
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintColor(getResources().getColor(R.color.color_white));

//        ViewGroup decorView = (ViewGroup)getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ;// SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        decorView.setSystemUiVisibility(uiOptions);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (isProgressShow) {
                    dissMissProgress();
                } else {
                    showProgress();
                    startActivity(new Intent(ThirdActivity.this, BlurActivity.class));
                }
            }
        });

        findViewById(R.id.Test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(toolbar, "Replace with your own action", Snackbar.LENGTH_INDEFINITE).setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(toolbar, "You click mE", Snackbar.LENGTH_SHORT).show();
                    }
                }).show();

                Crouton.makeText(ThirdActivity.this, "这是一个demo这是一个demo这是一个demo这是一个demo这是一个demo这是一个demo这是一个demo", Style.CONFIRM).show();

                Crouton.makeText(ThirdActivity.this, "Another 是一个demo这是一个demo这是一是一个demo这是一个demo这是一one", Style.ALERT, (ViewGroup) findViewById(R.id.dtesTe)).show();
            }
        });

        EventBus.getDefault().register(this);

        findViewById(R.id.Fourth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThirdActivity.this, FourthActivity.class));
            }
        });
    }

    @Subscribe
    public void onEvent(String dsd) {
        View view = findViewById(R.id.back);
        view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Snackbar.make(findViewById(R.id.another), "Fist show in Screen", Snackbar.LENGTH_LONG).show();
    }

    private void showProgress() {
        if (!isProgressShow) {
            isProgressShow = true;
            ViewGroup view = (ViewGroup) (getWindow().getDecorView().findViewById(android.R.id.content));
            bar = new ImageView(this);
//            bar.setBackgroundResource(R.anim.loading_dialog);
//            AnimationDrawable an=(AnimationDrawable) bar.getBackground();
//            an.start();//不能在Oncreate里启动，因为还没绑定好

            bar.setImageResource(R.mipmap.icon_loading_1);
            ObjectAnimator animator = ObjectAnimator.ofFloat(bar, "rotation", 0, 360);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ObjectAnimator.INFINITE);
            animator.setRepeatCount(-1);
            animator.setDuration(500);
            animator.start();

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            bar.setLayoutParams(params);
            view.addView(bar);
        }
    }

    public void dissMissProgress() {
        if (isProgressShow) {
            isProgressShow = false;
            ObjectAnimator animator = ObjectAnimator.ofFloat(bar, "alpha", 1.0f, 0.0f).setDuration(500);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup view = (ViewGroup) (getWindow().getDecorView().findViewById(android.R.id.content));
                    view.removeView(bar);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }
}
