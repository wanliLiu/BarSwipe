package com.barswipe.volume.wave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.barswipe.util.FileSizeUtil;

/**
 * Created by Soli on 2017/3/27.
 */

public class ActivityVolumPlay extends BaseActivity {

    private VolumePlayView volumePlay;
    private MediaVolumePlayView mediaVolumePlay;

    private TextView fileSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_voume_play);

        fileSize = (TextView) findViewById(R.id.fileSize);
        volumePlay = (VolumePlayView) findViewById(R.id.volumePlay);
        mediaVolumePlay = (MediaVolumePlayView) findViewById(R.id.MediavolumePlay);

        mediaVolumePlay.setVolumeDuration(getIntent().getStringExtra("duration"));
        volumePlay.setVolumeDuration(getIntent().getStringExtra("duration"));
        Intent intent = getIntent();
        if (intent != null) {
            String volumPath = intent.getStringExtra("volumePath");
            String waveData = intent.getStringExtra("waveData");
            volumePlay.setData(volumPath, waveData);
            mediaVolumePlay.setData(volumPath, waveData);
            fileSize.setText(FileSizeUtil.getAutoFileOrFilesSize(volumPath));

            fileSize.append("\nQUALITY：" + FansMp3EncodeThread.DEFAULT_LAME_MP3_QUALITY);
            fileSize.append("\nEncode kbps:" + FansMp3EncodeThread.DEFAULT_LAME_MP3_BIT_RATE);
        }

        volumePlay.showViewInDelete();
        volumePlay.setonActionTypeListener(type -> Toast.makeText(ActivityVolumPlay.this, "" + type, Toast.LENGTH_SHORT).show());

        mediaVolumePlay.showViewInDelete();
        mediaVolumePlay.setonActionTypeListener(type -> Toast.makeText(ActivityVolumPlay.this, "" + type, Toast.LENGTH_SHORT).show());
    }
}
