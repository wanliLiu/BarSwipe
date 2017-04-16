package com.soli.jnistudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by soli on 16/04/2017.
 */

public class JniTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_jnitest);

        TextView stringFrom = (TextView) findViewById(R.id.stringFrom);
        stringFrom.setText(JniTest.getStringFromJni());

    }
}
