package com.barswipe.volume.wave;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Soli on 2017/3/17.
 */

public class AcitivtyWaveTest extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.recordView)
    AudioRecordView recordView;


    @Bind(R.id.recordTime)
    TextView recordTime;
    @Bind(R.id.btnRecord)
    ImageView btnRecord;
    @Bind(R.id.btnPlay)
    ImageView btnPlay;
    @Bind(R.id.btnClip)
    View btnClip;
    @Bind(R.id.btnClipIcon)
    ImageView btnClipIcon;
    @Bind(R.id.btnClipText)
    TextView btnClipText;

    enum ActionStatus {
        parareStart,//准备开始，初始状态
        startRecording,//开始录音，录音状态
        pasueRecording,//暂停录音
        stopRecording,//暂停录音
        playAudio,//播放录音
        stopPlayAudio,//停止播放录音
        clipAudio//裁剪
    }

    private FansSamplePlayer player;

    private ActionStatus currentStatus = ActionStatus.parareStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_test);
        ButterKnife.bind(this);

        btnPlay.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnClip.setOnClickListener(this);

        updateUi();

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
            public void onRealVolume(double volume) {

            }

            @Override
            public void onRecordStop(FansSoundFile soundFile) {
                initPlayer(soundFile);
            }
        });
    }

    /**
     * 准备开始录音
     */
    private void parperRecordStatus() {
        recordTime.setText("00:00.00");

        btnPlay.setImageResource(R.mipmap.icon_play_disable);
        btnPlay.setEnabled(false);

        btnRecord.setImageResource(R.mipmap.icon_start_recording);
        btnRecord.setEnabled(true);

        btnClip.setEnabled(false);
        btnClipText.setText("裁剪");
        btnClipIcon.setImageResource(R.mipmap.icon_clip_disable);
        btnClipText.setTextColor(Color.parseColor("#e0e0e0"));
    }

    /**
     * 正在录音
     */
    public void startRecordStatus() {
        btnPlay.setImageResource(R.mipmap.icon_play_disable);
        btnPlay.setEnabled(false);

        recordTime.setText("00:00.00");
        btnRecord.setImageResource(R.mipmap.icon_record_stop);
        btnRecord.setEnabled(true);

        btnClip.setEnabled(false);
        btnClipText.setText("裁剪");
        btnClipIcon.setImageResource(R.mipmap.icon_clip_disable);
        btnClipText.setTextColor(Color.parseColor("#e0e0e0"));
    }

    /**
     * 停止录音
     */
    private void recordStopStauts() {
        btnPlay.setImageResource(R.mipmap.icon_play);
        btnPlay.setEnabled(true);

        btnRecord.setImageResource(R.mipmap.icon_start_recording);
        btnRecord.setEnabled(true);

        btnClip.setEnabled(true);
        btnClipText.setText("裁剪");
        btnClipIcon.setImageResource(R.mipmap.icon_clip);
        btnClipText.setTextColor(Color.parseColor("#6CA5FF"));
    }

    /**
     * 播放录音
     */
    private void playAudioStatus() {

        btnPlay.setImageResource(R.mipmap.icon_play_stop);
        btnPlay.setEnabled(true);

        btnRecord.setImageResource(R.mipmap.icon_record_disable);
        btnRecord.setEnabled(false);

        btnClip.setEnabled(true);
        btnClipText.setText("裁剪");
        btnClipIcon.setImageResource(R.mipmap.icon_clip);
        btnClipText.setTextColor(Color.parseColor("#6CA5FF"));
    }


    /**
     * 播放录音
     */
    private void stopplayAudioStatus() {

        btnPlay.setImageResource(R.mipmap.icon_play);
        btnPlay.setEnabled(true);

        btnRecord.setImageResource(R.mipmap.icon_start_recording);
        btnRecord.setEnabled(true);

        btnClip.setEnabled(true);
        btnClipText.setText("裁剪");
        btnClipIcon.setImageResource(R.mipmap.icon_clip);
        btnClipText.setTextColor(Color.parseColor("#6CA5FF"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                if (currentStatus == ActionStatus.stopRecording) {
                    currentStatus = ActionStatus.playAudio;
                    recordView.startPlay();
                    player.start();
                } else if (currentStatus == ActionStatus.playAudio) {
                    currentStatus = ActionStatus.stopPlayAudio;
                    recordView.stopPlay();
                    player.stop();
                } else if (currentStatus == ActionStatus.stopPlayAudio) {
                    currentStatus = ActionStatus.playAudio;
                    recordView.startPlay();
                    player.start();
                }
                updateUi();
                break;
            case R.id.btnRecord:
                if (currentStatus == ActionStatus.parareStart) {
                    currentStatus = ActionStatus.startRecording;
                    recordView.startRecord();
                } else if (currentStatus == ActionStatus.startRecording) {
                    currentStatus = ActionStatus.stopRecording;
                    recordView.stopRecord();
                } else if (currentStatus == ActionStatus.stopRecording) {
                    currentStatus = ActionStatus.startRecording;
                    recordView.startRecord();
                }
                updateUi();
                break;
            case R.id.btnClip:
                break;
        }
    }

    /**
     *
     */
    private void updateUi() {
        switch (currentStatus) {
            case parareStart:
                parperRecordStatus();
                break;
            case startRecording:
                startRecordStatus();
                break;
            case stopRecording:
                recordStopStauts();
                break;
            case playAudio:
                playAudioStatus();
                break;
            case stopPlayAudio:
                stopplayAudioStatus();
                break;
            case clipAudio:
                Toast.makeText(this, "还没做", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     *
     */
    private void initPlayer(FansSoundFile soundFile) {
        player = new FansSamplePlayer(soundFile);
        player.setOnCompletionListener(new FansSamplePlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentStatus == ActionStatus.playAudio) {
                            currentStatus = ActionStatus.stopRecording;
                            player.stop();
                            recordView.stopPlay();
                        }
                        updateUi();
                    }
                });
            }

            @Override
            public void onPlayProgress(int timeMs) {
                recordView.updateData(timeMs);
            }
        });
    }
}
