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

    private PcmWaveView waveView;
    private PlayWaveView playView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_test);
        waveView = (PcmWaveView) findViewById(R.id.waveView);
        playView = (PlayWaveView) findViewById(R.id.playView);
    }

    /**
     * @param view
     */
    private int index = 10;

    public void onView(View view) {
        switch (view.getId()) {
            case R.id.huadong:
                playView.updatePosition(index);
                waveView.updateData(index);
                index += 10;
//                waveView.smoothScrollBy(10);
                break;
        }
    }
}
