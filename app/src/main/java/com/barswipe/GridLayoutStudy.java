package com.barswipe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;


/**
 * http://www.cnblogs.com/Chenshuai7/p/5321766.html
 */
public class GridLayoutStudy extends AppCompatActivity {


    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout_study);

        RxView.clicks(findViewById(R.id.test))
                .subscribe(avoid -> dd());
    }


    private void dd() {
        index = (index + 1) % 7;
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grd);
        View view = gridLayout.getChildAt(7);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) view.getLayoutParams();
        params.columnSpec = GridLayout.spec(index, GridLayout.CENTER, 0f);
        view.setLayoutParams(params);
    }
}
