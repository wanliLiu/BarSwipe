package com.barswipe.volume.wave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.barswipe.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Soli on 2017/4/6.
 */

public class ActivityWaveDisplayTest extends AppCompatActivity {

    private WaveRecyclerView waveRecy;

    private List<String> wavedata = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_view_another);
        waveRecy = (WaveRecyclerView) findViewById(R.id.testRecycle);
    }


    /**
     * @param view
     */
    public void onTest(View view) {
        switch (view.getId()) {
            case R.id.recyTest:
//                waveRecy.scrollToPosition(4);
//                linear.setCanScroll(true);
//                waveRecy.scrollBy(10, 0);
//                linear.setCanScroll(false);
                wavedata.clear();
                break;
            case R.id.recyTes1t:
//                linear.toggleScroll();
                wavedata.add(String.valueOf(new Random().nextInt(150)));
                waveRecy.test(wavedata);
                break;
        }
    }

}
