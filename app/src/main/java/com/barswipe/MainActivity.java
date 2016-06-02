package com.barswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.barswipe.DragGridView.gridview.MainActivityDragGridView;
import com.barswipe.EaseInterpolator.MainActivityAnimation;
import com.barswipe.FloatView.FloatWindowService;
import com.barswipe.Scroller.ScrollerActivity;
import com.barswipe.ViewDragHelper.ViewDragHelperStudyActivity;
import com.barswipe.snapscrollview.ProductDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);

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

        findViewById(R.id.studyDragr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivityDragGridView.class));
            }
        });

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

        Crouton.cancelAllCroutons();
    }

}
