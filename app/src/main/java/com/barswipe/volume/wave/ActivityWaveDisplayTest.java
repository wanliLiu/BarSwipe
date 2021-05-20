package com.barswipe.volume.wave;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.barswipe.R;

/**
 * Created by Soli on 2017/4/6.
 */

public class ActivityWaveDisplayTest extends AppCompatActivity {

    private AudioRecordView waveRecy;
    private WaveEditView waveEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_view_another);
        waveRecy = (AudioRecordView) findViewById(R.id.testRecycle);

        waveEdit = (WaveEditView) findViewById(R.id.waveEdit);
//        waveEdit.testScrooCenter();

    }


    private Handler testHan = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            waveRecy.test();
            // TODO: 09/04/2017 这里注意改了， WaveEditView  记得在项目中也需要修改
            waveEdit.updatePosition();
            testHan.sendEmptyMessageDelayed(1, AudioConfig._newWaveTime);
        }
    };

    /**
     * @param view
     */
    public void onTest(View view) {
        switch (view.getId()) {
            case R.id.recyTest:
                testHan.removeMessages(1);
                waveRecy.stopRecording();
                break;
            case R.id.recyTes1t:
                waveRecy.startRecording();
                testHan.sendEmptyMessage(1);
                break;
        }
    }

}
