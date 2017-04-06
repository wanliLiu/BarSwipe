package com.barswipe.volume.wave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.barswipe.R;

/**
 * Created by Soli on 2017/3/27.
 */

public class ActivityVolumPlay extends AppCompatActivity {

    private VolumePlayView volumePlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_voume_play);

        volumePlay = (VolumePlayView) findViewById(R.id.volumePlay);
        volumePlay.setVolumeDuration(getIntent().getStringExtra("duration"));
        Intent intent = getIntent();
        if (intent != null) {
            String volumPath = intent.getStringExtra("volumePath");
            String waveData = intent.getStringExtra("waveData");
            volumePlay.setData(volumPath, waveData);
        }

        volumePlay.showViewInDelete();
        volumePlay.setonActionTypeListener(new VolumePlayView.onActionTypeListener() {
            @Override
            public void onActionType(int type) {
                Toast.makeText(ActivityVolumPlay.this, "" + type, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
