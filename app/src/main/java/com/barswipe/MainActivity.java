package com.barswipe;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.DragGridView.gridview.MainActivityDragGridView;
import com.barswipe.Scroller.ScrollerActivity;
import com.barswipe.ViewDragHelper.ViewDragHelperStudyActivity;
import com.barswipe.animation.EaseInterpolator.MainActivityAnimation;
import com.barswipe.snapscrollview.ProductDetailActivity;
import com.jakewharton.rxbinding.view.RxView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    private String TAG = "启动";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintColor(getResources().getColor(R.color.color_orange));

        findViewById(R.id.Test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.quiet_fixedly);
            }
        });

        findViewById(R.id.dsdsd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TrignalActivity.class));
            }
        });

        findViewById(R.id.webView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });

        findViewById(R.id.startScrooller).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScrollerActivity.class));
            }
        });
        findViewById(R.id.startProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductDetailActivity.class));
            }
        });

        findViewById(R.id.studyEaseAnimation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivityAnimation.class));
            }
        });

        final TextView view = (TextView) findViewById(R.id.bottomLine);
        view.setTag(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = getResources().getString(R.string.str_rank_desc_renqi);
                if ((Integer) v.getTag() == 1) {
                    view.setText(str);
                    v.setTag(2);
                } else {
                    v.setTag(1);
                    view.setText(R.string.str_rank_desc_renqi);
                }
            }
        });

        RxView.clicks(findViewById(R.id.studyDragr))
                .throttleFirst(ViewConfiguration.getDoubleTapTimeout(), TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showToastFullscreen();
                        startActivity(new Intent(MainActivity.this, MainActivityDragGridView.class));
                    }
                });
//        findViewById(R.id.studyDragr).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, MainActivityDragGridView.class));
//            }
//        });

        findViewById(R.id.studyViewDragHelper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ViewDragHelperStudyActivity.class));
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, BlurActivity.class));
//                overridePendingTransition(R.anim.push_left_in, R.anim.quiet_fixedly);
//            }
//        });

        try {
            JSONObject object = new JSONObject();
            object.put("tes1", 1);
            object.put("tes2", 2);

            JSONObject objec1t = new JSONObject();
            objec1t.put("sdsdsd", 1);
            objec1t.put("tesadss2", 2);
            JSONArray array = new JSONArray();
            array.put(objec1t);
            JSONObject objec11t = new JSONObject();
            objec11t.put("sdsdswd", 123);
            objec11t.put("tesadss2", 2232);
            array.put(objec11t);

            object.put("Array", array);

            Log.e("org.gson", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        Crouton.cancelAllCroutons();
    }


    private void showToastFullscreen() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.test_toast_fullscreen, null, false);

        Toast mToast = new Toast(this);
        // 设置Toast的位置
        mToast.setGravity(Gravity.FILL, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(layout);

        mToast.show();

        Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.test_toast_fullscreen);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG,"onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG,"onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG,"onSaveInstanceState");
    }
}
