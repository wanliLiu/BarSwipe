package com.barswipe.volume;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;
import com.barswipe.util.FileSizeUtil;
import com.barswipe.util.FileUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

/**
 * Created by Soli on 2017/1/16.
 */

public class MainActivity_Volume extends AppCompatActivity implements View.OnClickListener {

    public static MainActivity_Volume instance;

    public final String MEDIARECORDER = "mediarecorder";

    public final String AUDIORECORDER = "audiorecorder";

    public TextView duration, voice,size;

    private Button audiorecorder, mediarecorder, play;

    public boolean recordering = false;

    private long originalTime = -1l;

    private String filePath;

    private Timer timer;

    private String getTime() {
        long time = System.currentTimeMillis() - originalTime;
        long second = time / 1000l;
        long mSecond = time - second * 1000;
        return second + "." + mSecond;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_valume);
        duration = (TextView) findViewById(R.id.duration);
        voice = (TextView) findViewById(R.id.voice);
        play = (Button) findViewById(R.id.play);
        audiorecorder = (Button) findViewById(R.id.audiorecorder);
        mediarecorder = (Button) findViewById(R.id.mediarecorder);
        size = (TextView)findViewById(R.id.size);
        audiorecorder.setOnClickListener(this);
        mediarecorder.setOnClickListener(this);
        play.setOnClickListener(this);
    }

    public void mediarecorder() {
        if (!recordering) {
            audiorecorder.setVisibility(View.GONE);
            filePath = FileUtil.getDownLoadFilePath(this, MEDIARECORDER + "_" + System.currentTimeMillis() + ".amr").getAbsolutePath();
            startTimer();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (recordering) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                duration.setText(getTime());
                            }
                        });
                    }
                }
            }, 0, 1);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                double ratio = MediaRecorderUtil.recorder.getMaxAmplitude();
                                if (ratio > 1)
                                    ratio = 20 * Math.log10(ratio);
                                voice.setText("当前音量：" + ratio);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 0, 500);
            MediaRecorderUtil.startRecordering(filePath);
        } else {
            MediaRecorderUtil.stopRecordering();
            audiorecorder.setVisibility(View.VISIBLE);
            stopTimer();

            if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                size.append("\n文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(filePath));
            }
        }

    }

    public void audiorecorder() {
        if (!recordering) {
            recordering = true;
            mediarecorder.setVisibility(View.GONE);
            filePath = FileUtil.getDownLoadFilePath(this, MEDIARECORDER + "_" + System.currentTimeMillis() + ".pcm").getAbsolutePath();
            startTimer();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            duration.setText(getTime());
                        }
                    });
                }
            }, 0, 1);
            AudioRecorderUtil.startRecordering(filePath);
        } else {
            stopTimer();
            AudioRecorderUtil.stopRecording();
            mediarecorder.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                size.append("\n文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(filePath));
            }
        }

    }

    private void startTimer() {
        originalTime = System.currentTimeMillis();
        recordering = true;
    }

    private void stopTimer() {
        timer.cancel();
        recordering = false;
    }

    @Override
    public void onClick(final View v) {

        RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        switch (v.getId()) {
                            case R.id.mediarecorder:
                                mediarecorder();
                                break;
                            case R.id.audiorecorder:
                                audiorecorder();
                                break;
                            case R.id.play:
                                startPlaying();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }


    private void startPlaying() {
        if (!TextUtils.isEmpty(filePath) && new File(filePath).exists())
            MediaPlayerUtil.startPlaying(filePath.contains(".pcm") ? filePath.replace(".pcm", ".wav") : filePath);
    }

    public void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
