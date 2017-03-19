package com.barswipe.volume.wave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.barswipe.R;

/**
 * Created by Soli on 2017/3/17.
 */

public class AcitivtyWaveTest extends AppCompatActivity {

    private AudioRecordView recordView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_test);
        recordView = (AudioRecordView) findViewById(R.id.recordView);
    }

    public void onView(View view) {
        switch (view.getId()) {
            case R.id.huadong:
                recordView.testPostionUpdate();
                break;
            case R.id.pause:
                recordView.stopRecord();
                break;
        }
    }
}
