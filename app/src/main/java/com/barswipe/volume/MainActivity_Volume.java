package com.barswipe.volume;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;
import com.barswipe.util.FileSizeUtil;
import com.barswipe.util.FileUtil;
import com.barswipe.volume.pcm.pcm2amr.MainActivity_pcm_amr;
import com.barswipe.volume.wave.AudioFxActivity;
import com.barswipe.volume.wave.AudioMaker;
import com.barswipe.volume.wave.MainActivity_good_view;
import com.barswipe.volume.wave.MainActivity_wave;
import com.barswipe.volume.wave.StudyAudioRecord;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Soli on 2017/1/16.
 */

public class MainActivity_Volume extends AppCompatActivity implements View.OnClickListener {

    public static MainActivity_Volume instance;

    public final String MEDIARECORDER = "mediarecorder";

    public final String AUDIORECORDER = "audiorecorder";

    public TextView duration, voice, size;
    private EditText timeInput;

    private Button audiorecorder, mediarecorder, play;

    public boolean recordering = false;

    private int isMediaRecord = 1;

    private long originalTime = -1l;

    private String filePath;
    private CountDownTimer countDownTimer;

    private Timer timer;

    private Uri uri;

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
        size = (TextView) findViewById(R.id.size);
        audiorecorder.setOnClickListener(this);
        mediarecorder.setOnClickListener(this);
        play.setOnClickListener(this);


        RxView.clicks(findViewById(R.id.recycle))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, RecordPlayActivity.class)));
        RxView.clicks(findViewById(R.id.job))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, StudyAudioRecord.class)));
        ;
        RxView.clicks(findViewById(R.id.pcm2amr))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, MainActivity_pcm_amr.class)));

        RxView.clicks(findViewById(R.id.audioWave))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, MainActivity_wave.class)));
        RxView.clicks(findViewById(R.id.audioMaker))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, AudioMaker.class)));

        RxView.clicks(findViewById(R.id.AudioFxActivity))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, AudioFxActivity.class)));

        RxView.clicks(findViewById(R.id.goodView))
                .subscribe(a -> startActivity(new Intent(MainActivity_Volume.this, MainActivity_good_view.class)));

        timeInput = (EditText) findViewById(R.id.timeInput);
        countDownTimer = new CountDownTimer() {
            @Override
            public void onTick(long millisUntilFinished) {
                duration.setText("录制倒计时" + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                if (isMediaRecord == 1) {
                    MediaRecorderUtil.stopRecordering();
                    audiorecorder.setVisibility(View.VISIBLE);
                    stopTimer();
                } else {
                    stopTimer();
                    AudioRecorderUtil.stopRecording();
                    mediarecorder.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    size.setText("\n文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(filePath));
                }
            }
        };
    }

    private void startRecord() {
        AudioRecordManager manager = AudioRecordManager.getInstance();
        manager.setMaxVoiceDuration(Integer.parseInt(timeInput.getText().toString()));
        manager.setListener(new AudioRecordManager.StateListener() {
            @Override
            public void onComplete(Uri path) {
                uri = path;
                audiorecorder.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(path.getPath()) && new File(path.getPath()).exists()) {
                    size.setText("\n文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(path.getPath()));
                }
            }
        });
        manager.startRecord(this.getWindow().getDecorView());
    }

    public void mediarecorder() {
        if (!recordering) {
            isMediaRecord = 1;
            audiorecorder.setVisibility(View.GONE);
            filePath = FileUtil.getDownLoadFilePath(this, MEDIARECORDER + "_" + System.currentTimeMillis() + ".amr").getAbsolutePath();
            startTimer();
            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (recordering) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                duration.setText(getTime());
//                            }
//                        });
//                    }
//                }
//            }, 0, 1);

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
//            MediaRecorderUtil.stopRecordering();
//            audiorecorder.setVisibility(View.VISIBLE);
//            stopTimer();
//
//            if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
//                size.setText("\n文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(filePath));
//            }
        }

    }

    public void audiorecorder() {
        if (!recordering) {
            isMediaRecord = 2;
            recordering = true;
            mediarecorder.setVisibility(View.GONE);
            filePath = FileUtil.getDownLoadFilePath(this, MEDIARECORDER + "_" + System.currentTimeMillis() + ".pcm").getAbsolutePath();
            startTimer();
            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            duration.setText(getTime());
//                        }
//                    });
//                }
//            }, 0, 1);
            AudioRecorderUtil.startRecordering(filePath);
        } else {
//            stopTimer();
//            AudioRecorderUtil.stopRecording();
//            mediarecorder.setVisibility(View.VISIBLE);
//
//            if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
//                size.setText("\n文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(filePath));
//            }
        }

    }

    private void startTimer() {
//        originalTime = System.currentTimeMillis();
        recordering = true;
    }

    private void stopTimer() {
        timer.cancel();
        recordering = false;
    }

    @Override
    public void onClick(final View v) {

        RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    switch (v.getId()) {
                        case R.id.mediarecorder: {
                            if (TextUtils.isEmpty(timeInput.getText().toString())) {
                                Toast.makeText(MainActivity_Volume.this, "输入录制的时间", Toast.LENGTH_SHORT).show();
                                return;
                            }
//                                mediarecorder();
//                                countDownTimer.setTime(Integer.parseInt(timeInput.getText().toString()) * 1000, 1000);
//                                countDownTimer.start();
                            startRecord();
                        }
                        break;
                        case R.id.audiorecorder: {
                            if (TextUtils.isEmpty(timeInput.getText().toString())) {
                                Toast.makeText(MainActivity_Volume.this, "输入录制的时间", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            audiorecorder();
                            countDownTimer.setTime(Integer.parseInt(timeInput.getText().toString()) * 1000, 1000);
                            countDownTimer.start();

//                                startRecord();
                        }
                        break;
                        case R.id.play:
                            startPlaying();
                            break;
                        default:
                            break;
                    }
                });
    }


    private void startPlaying() {
        if (uri != null) {
            AudioPlayManager.getInstance().startPlay(this, uri, null);
            return;
        }
        //MediaPlayer 什么格式的文件都能播放样
        if (!TextUtils.isEmpty(filePath) && new File(filePath).exists())
            MediaPlayerUtil.startPlaying(filePath.contains(".pcm") ? filePath.replace(".pcm", ".wav") : filePath);
    }

    public void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
