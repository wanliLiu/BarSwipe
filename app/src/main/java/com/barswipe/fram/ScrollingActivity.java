package com.barswipe.fram;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.barswipe.R;

public class ScrollingActivity extends AppCompatActivity {

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NestedScrollView ds = (NestedScrollView) findViewById(R.id.nestScrollView);
                AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
                if (index == 0) {
                    ds.setNestedScrollingEnabled(false);
                } else if (index == 1) {
                    ds.setNestedScrollingEnabled(true);
                } else if (index == 2) {
                    app_bar.setExpanded(true, true);
                } else if (index == 3) {
                    app_bar.setExpanded(false, true);
                }
                index = (index + 1) % 4;
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
}
