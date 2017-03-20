package com.barswipe.volume.wave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by Soli on 2017/3/17.
 */

public class AcitivtyWaveTest extends AppCompatActivity {

    private AudioRecordView recordView;
    private TextView recordTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_test);
        recordView = (AudioRecordView) findViewById(R.id.recordView);
        recordTime = (TextView) findViewById(R.id.recordTime);
        recordView.setOnRecordListener(new FansSoundFile.onRecordStatusListener() {
            @Override
            public void onRecordTime(double fractionComplete, final String time) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordTime.setText(time);
                    }
                });
            }

            @Override
            public void onRealVolume(int volume) {

            }
        });
    }

    private boolean isPause = false;

    public void onView(View view) {
        switch (view.getId()) {
            case R.id.huadong:
                recordView.testPostionUpdate();
                break;

            case R.id.btnRecord:
                recordView.startRecord();
                break;
            case R.id.btnPause:
                isPause = !isPause;
                recordView.pause(isPause);
                break;
            case R.id.btnStop:
                recordView.stopRecord();
                break;
        }
    }
}
