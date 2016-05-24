package com.barswipe.Scroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by SoLi on 2016/5/24.
 */
public class ScrollerActivity extends Activity implements View.OnClickListener {
    private TextView tv;
    private Button bt_scrollLeft;
    private Button bt_scrollRight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        tv = (TextView) findViewById(R.id.tv_scroll);

        bt_scrollLeft = (Button) findViewById(R.id.bt_scrollLeft);
        bt_scrollRight = (Button) findViewById(R.id.bt_scrollRight);

        findViewById(R.id.bt_scrollBy).setOnClickListener(this);
        bt_scrollLeft.setOnClickListener(this);
        bt_scrollRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_scrollLeft:
                tv.scrollBy(20, 0);
                break;
            case R.id.bt_scrollRight:
                tv.scrollTo(-100, 0);
                break;
            case R.id.bt_scrollBy:
                tv.scrollBy(-30, -30);
                break;
            case R.id.startScrooler:
                startActivity(new Intent(this,MultiScreenActivity.class));
                break;
        }
        int tvscrllX = tv.getScrollX();
        int tvscrllY = tv.getScrollY();
        System.out.println(" tvscrllX ---> " + tvscrllX + " --- tvscrllY ---> " + tvscrllY);
    }
}
