package com.barswipe.volume.wave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by Soli on 2017/3/27.
 */

public class ActivityVolumPlay extends AppCompatActivity implements View.OnClickListener {

    private String volumPath, waveData;

    private WavePlayView wavePlay;
    private View actionViewTop, actionViewBottom;
    private TextView audioDuration;
    private ImageView audioPlay;

    private FansAudioPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_voume_play);

        wavePlay = (WavePlayView) findViewById(R.id.wavePlay);
        actionViewTop = findViewById(R.id.actionViewTop);
        actionViewBottom = findViewById(R.id.actionViewBottom);

        audioPlay = (ImageView) findViewById(R.id.audioPlay);
        audioPlay.setOnClickListener(this);

        audioDuration = (TextView) findViewById(R.id.audioDuration);
        audioDuration.setText(getIntent().getStringExtra("duration"));
        Intent intent = getIntent();
        if (intent != null) {
            volumPath = intent.getStringExtra("volumePath");
            waveData = intent.getStringExtra("waveData");
            wavePlay.setWaveData(waveData);
        }


        player = new FansAudioPlayer();
        player.setOnAudioPlayListener(new onAudioDetailPlayListener() {
            @Override
            public void onStartPrepare() {
                audioPlay.setImageResource(R.mipmap.icon_play_stop);
            }

            @Override
            public void onStartPlay() {

            }

            @Override
            public void onError(int extra) {

            }

            @Override
            public void onPause() {
                audioPlay.setImageResource(R.mipmap.icon_play);
            }

            @Override
            public void onAudioPlayComplete() {
                wavePlay.onAudioPlayComplete();
                audioPlay.setImageResource(R.mipmap.icon_play);

            }

            @Override
            public void onAudioPlayProgress(double timeMs) {
                wavePlay.updateData();
            }
        });

    }

    private void showOrHide() {
        if (actionViewTop.getVisibility() != View.VISIBLE) {
            actionViewTop.setVisibility(View.VISIBLE);
            actionViewBottom.setVisibility(View.VISIBLE);
        } else {
            actionViewTop.setVisibility(View.GONE);
            actionViewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMediaplayer:
//                player.play("https://testcdn.same.com/sense/E0E6FBB3-8BCE-4DBB-BACA-5E0E8FDC50EE.mp3");
                break;
            case R.id.btnTest:
                showOrHide();
                break;
            case R.id.audioPlay:
                dealAudioPlayOrStop();
                break;
        }
    }

    /**
     * 处理音频的播放或者停止
     */
    private void dealAudioPlayOrStop() {
//        int tag = Integer.parseInt(audioPlay.getTag().toString());
//        if (tag == 0) {
//            audioPlay.setTag(1);
//            audioPlay.setImageResource(R.mipmap.icon_play_stop);
//            //开始播放
//        } else {
//            audioPlay.setTag(0);
//            audioPlay.setImageResource(R.mipmap.icon_play);
//            //停止播放
//        }

        player.play(volumPath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release();
        }
    }
}
