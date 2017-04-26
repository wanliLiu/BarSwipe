package com.barswipe.animation.collect;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.daimajia.easing.Techniques;
import com.daimajia.easing.YoYo;


public class ExampleActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example);

        final TextView t = (TextView) findViewById(R.id.notice);
        t.setText("Please input your Email and Password");

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(findViewById(R.id.edit_area));

                t.setText("Wrong password!");
            }
        });

        final TextView t2 = (TextView) findViewById(R.id.notice2);
        t2.setText("Please input your Email and Password");

        findViewById(R.id.submit2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YoYo.with(Techniques.Shake).playOn(findViewById(R.id.edit_area2));

                t2.setText("Wrong password!");
            }
        });
    }
}
