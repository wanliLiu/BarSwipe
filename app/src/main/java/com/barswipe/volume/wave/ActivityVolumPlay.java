package com.barswipe.volume.wave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barswipe.R;

/**
 * Created by Soli on 2017/3/27.
 */

public class ActivityVolumPlay extends AppCompatActivity {

    private VolumePlayView volumePlay;
    private WaveRecyclerView waveRecy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_voume_play);

        waveRecy = (WaveRecyclerView) findViewById(R.id.testRecycle);

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

        waveRecy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        waveRecy.setAdapter(new TestWaveAdapter());

    }

    /**
     * @param view
     */
    public void onTest(View view) {
        switch (view.getId()) {
            case R.id.recyTest:
//                waveRecy.scrollToPosition(4);
                waveRecy.scrollBy(10, 0);
                break;
        }
    }


    private class TestWaveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(new TestPcmWaveView(ActivityVolumPlay.this)) {

            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof TestPcmWaveView) {
                if (position > 4){
                    ((TestPcmWaveView) (holder.itemView)).setSeconds(position - 4);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 60;
        }

    }
}
