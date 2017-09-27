package com.barswipe.Scroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Button;

import com.barswipe.BaseActivity;
import com.barswipe.R;

/**
 * Created by SoLi on 2016/5/24.
 */
public class ScrollerActivity extends BaseActivity implements View.OnClickListener {
    private View tv;
    private Button bt_scrollLeft;
    private Button bt_scrollRight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        tv = findViewById(R.id.tv_scroll);

        bt_scrollLeft = (Button) findViewById(R.id.bt_scrollLeft);
        bt_scrollRight = (Button) findViewById(R.id.bt_scrollRight);

        findViewById(R.id.bt_scrollBy).setOnClickListener(this);
        findViewById(R.id.bt_offset).setOnClickListener(this);
        bt_scrollLeft.setOnClickListener(this);
        bt_scrollRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_scrollLeft:
                findViewById(R.id.test).scrollBy(20, 0);
                break;
            case R.id.bt_scrollRight:
                findViewById(R.id.test).scrollTo(-100, 0);
                break;
            case R.id.bt_scrollBy:
                findViewById(R.id.test).scrollBy(-30, -30);
                break;
            case R.id.startScrooler:
                startActivity(new Intent(this, MultiScreenActivity.class));
                break;
            case R.id.bt_offset:
//                tv.offsetLeftAndRight(10);
//                tv.offsetTopAndBottom(10);
                ViewCompat.offsetLeftAndRight(tv, 10);
                ViewCompat.offsetTopAndBottom(tv, 10);
                break;
        }
        int tvscrllX = findViewById(R.id.test).getScrollX();
        int tvscrllY = findViewById(R.id.test).getScrollY();
        System.out.println(" tvscrllX ---> " + tvscrllX + " --- tvscrllY ---> " + tvscrllY);
    }
}
