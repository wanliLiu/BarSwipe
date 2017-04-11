package com.barswipe.volume.wave;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;
import com.barswipe.util.FileUtil;
import com.barswipe.volume.wave.util.MusicSimilarityUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Soli on 2017/3/17.
 */

public class AcitivtyWaveTestRecycler extends AppCompatActivity implements View.OnClickListener, onAudioRecordListener, onAudioPlayListener, onWaveEditListener, AudioRecordView.onParpareStartRecordingListener {

    // TODO: 26/03/2017 硬件加速关闭 android:hardwareAccelerated="false"

    @Bind(R.id.recordView)
    AudioRecordView recordView;
    @Bind(R.id.waveEdit)
    WaveEditView waveEdit;

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

    // TODO: 2017/3/24 titlabar需要弄
    @Bind(R.id.txt_cancle)
    TextView txt_cancle;
    @Bind(R.id.btnActionDone)
    TextView btnActionDone;

    @Bind(R.id.editDelete)
    TextView editDelete;
    @Bind(R.id.editClip)
    TextView editClip;

    // TODO: 2017/3/24 到时候改成titlebar
    @Bind(R.id.recordtitle)
    TextView recordtitle;

    //音频数据录制
    private FansSoundFile soundFile;
    //音频数据播放
    private FansSamplePlayer player;

    //总共录制的时间
    private double recordTotalTime = 0.0d;

    //能录制的最小时间 2s
    private double recordMinTime = 2;
    //录制的实际大于最小录制时间 就可以编辑和保存当前录制的音频数据
    private boolean isCanAction = false, isCanRecord = true;

    enum ActionStatus {
        parareStart,//准备开始，初始状态
        startRecording,//开始录音，录音状态
        stopRecording,//暂停录音
        playAudio,//播放录音,包括裁剪中
        stopPlayAudio,//停止播放录音 包括裁剪中
        clipAudio,//裁剪
    }

    private ActionStatus currentStatus = ActionStatus.parareStart;

    private ProgressDialog dialog;

    // TODO: 2017/4/11 这里要添加 
    private boolean isScrolling = false, isFromPlayingStop = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_view_recycler);
        ButterKnife.bind(this);

        btnPlay.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnClip.setOnClickListener(this);

        // TODO: 23/03/2017 这个地方到时候还是换成 titlebar
        txt_cancle.setOnClickListener(this);
        btnActionDone.setOnClickListener(this);

        editDelete.setOnClickListener(this);
        editClip.setOnClickListener(this);

        updateUi();

        initAudioRecord();
    }

    /**
     *
     */
    private void dealonPlaySelectionEnd() {
        if (currentStatus == ActionStatus.playAudio &&
                recordView.isPlaying() &&
                player != null && player.isPlaying()
                && recordView.isScroolToMaxPos()) {
            Log.e("拖动", "有进来");
            onAudioPlayComplete();
            isFromPlayingStop = false;
        }
    }

    /**
     * 如果在播放的时候，在移动，播放就停止，移动完后在播放
     */
    private void dealonPlayingSelection() {
        if (currentStatus == ActionStatus.playAudio || isFromPlayingStop) {
            if (recordView.isPlaying() && isScrolling) {
                isFromPlayingStop = true;
                if (player != null)
                    player.stop();
            }
            if (!isScrolling) {
                currentStatus = ActionStatus.playAudio;
                updateUi();
                startPlay();
            }
        }
    }

    /**
     *
     */
    private void showActionDoingDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setTitle(getResources().getString(R.string.transfer_wait_title));
            dialog.setMessage(getResources().getString(R.string.transfer_wait_message));
        }

        if (!dialog.isShowing())
            dialog.show();

        else
            dialog.dismiss();
    }

    /**
     * 音频相关录制
     */
    private void initAudioRecord() {

        recordView.setTimeChangeListener(this);
        recordView.setOnParpareStartRecordingListener(this);
        recordView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("WaveRecyclerView", "test_onScrollStateChanged:" + newState);
                isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
                dealonPlayingSelection();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dealonPlaySelectionEnd();
            }
        });

        waveEdit.setTimeChangeListener(this);
        waveEdit.setOnWaveEditListener(this);

        soundFile = new FansSoundFile();
        soundFile.setOnAudioRecordListener(this);
    }

    /**
     * 大于最小录制的时间才可以编辑音频 和 发布
     */
    private void updateCanActionUI() {
        if (!isCanAction) {
            if (recordTotalTime >= recordMinTime) {
                isCanAction = true;
                btnActionDone.setTextColor(Color.parseColor("#6CA5FF"));
                btnActionDone.setEnabled(true);
            } else {
                btnActionDone.setTextColor(Color.parseColor("#e0e0e0"));
                btnActionDone.setEnabled(false);
            }
        }
    }

    @Override
    public void onActionCando(boolean isCan) {
        if (isCan && editDelete.isEnabled())
            return;
        if (!isCan && !editDelete.isEnabled())
            return;
        editDelete.setEnabled(isCan);
        editDelete.setTextColor(Color.parseColor(isCan ? "#6CA5FF" : "#e0e0e0"));

        editClip.setEnabled(isCan);
        editClip.setTextColor(Color.parseColor(isCan ? "#6CA5FF" : "#e0e0e0"));
    }

    @Override
    public void onTimeChange(final boolean from, final double fractionComplete, final String time) {
        // TODO: 2017/3/24 注意这里的线程  要判断是否是主线程
        // TODO: 2017/3/24 这里需要对裁剪的最小时间做判断，从而对裁剪入口的显示状态
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordTime.setText(time);
                if (from) {
                    recordTotalTime = fractionComplete;
                    updateCanActionUI();
                }
            }
        });
    }

    @Override
    public void onAudioRecordVolume(double volume) {
        if (recordView.isRecording()) {
            recordView.updateData(volume);
            waveEdit.updatePosition();
        }
    }

    /**
     * 录音达到最大值
     */
    @Override
    public void onAudioRecordToMaxTime() {
        isCanRecord = false;
        recordTotalTime = AudioConfig._totalTimeSec;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startRecord();
            }
        });
    }

    @Override
    public void onAudioRecordStop() {
        initAudioPlayer();
    }

    /**
     *
     */
    private void initAudioPlayer() {
        if (player != null)
            player.release();
        //初始化 播放器
        player = new FansSamplePlayer(soundFile);
        player.setOnAudioPlayListener(this);
    }

    /**
     * 录音编辑完成
     */
    @Override
    public void onAudioEditComplete() {
        recordView.refreshAfterEdit(soundFile.getWaveBytes());
        waveEdit.refreshAfterEdit(soundFile.getWaveBytes().size());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showActionDoingDialog();

                //更新时间
                isCanAction = false;
                isCanRecord = true;
                updateCanActionUI();

                initAudioPlayer();

                // TODO: 25/03/2017 编辑后录音这里的数据要重新绘制
                enterClipMode(true);
            }
        });
    }

    @Override
    public void onAudioPlayComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentStatus == ActionStatus.playAudio) {
                    if (isInAudioEdit()) {
                        currentStatus = ActionStatus.stopPlayAudio;
                    } else {
                        currentStatus = ActionStatus.stopRecording;
                    }
                    stopPlay(!isFromPlayingStop);
                }
                updateUi();
            }
        });
    }

    @Override
    public void onAudioPlayProgress(double timeMs) {
        updateData(timeMs);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 2017/3/24 释放相关资源
        if (soundFile != null)
            soundFile.release();
        if (player != null)
            player.release();
    }

    @Override
    public void onParareStartAction(boolean isRecording, double time) {
        if (isRecording) {
            //数据这里准备开始
            if (soundFile != null) {
                soundFile.startRecord();
            }
        } else {
            if (player != null) {
                player.seekTo(time);
                player.start();
            }
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        isFromPlayingStop = false;
        if (isCanRecord && recordTotalTime < AudioConfig._totalTimeSec) {
            //界面准备开始录制
//            recordView.startRecording();
            recordView.smoothStartRecording();
//            //数据这里准备开始
//            soundFile.startRecord();
        } else {
            currentStatus = ActionStatus.stopRecording;
            stopRecord();
            updateUi();
            Toast.makeText(AcitivtyWaveTestRecycler.this, "最多只能录制" + AudioConfig._totalTimeSec + "s", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 结束录制
     */
    private void stopRecord() {
        //先停止数据
        soundFile.stopRecord();
        //在同时页面停止
        recordView.stopRecording();
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        isFromPlayingStop = false;
        if (isInAudioEdit()) {
            waveEdit.setPlaying(true);
            player.seekTo(waveEdit.getPlayBackStartTime(), waveEdit.getSelectEndTime());
            player.start();
        } else {
//            recordView.startPlay();
            recordView.smoothStartPlay(true);
        }
    }

    /**
     * 停止播放
     *
     * @param stopfrom ture
     */
    private void stopPlay(boolean stopfrom) {
        if (isInAudioEdit()) {
            waveEdit.stopPlay(stopfrom);
        } else {
            //界面处理停止播放相关事情
            recordView.stopPlay(stopfrom);
        }
        player.stop();
    }

    /**
     * 是否可以保存文件
     *
     * @return
     */
    private boolean ifCanSaveFile() {

        if (!isCanAction) {
            Toast.makeText(this, "录制时间太短，请继续录制！", Toast.LENGTH_SHORT).show();
            return false;
        }

        //如果正在录制也给出提示
        if (currentStatus == ActionStatus.startRecording) {
            Toast.makeText(this, "录音正在进行，请停止录音在操作！", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentStatus == ActionStatus.playAudio) {
            stopPlay(false);
        }

        return true;
    }

    /**
     *
     */
    private void saveAudioFile() {
        if (ifCanSaveFile()) {
            // TODO: 2017/3/24  保存的文件位置  和保存文件过程中的加载框需要处理
            File wolumPath = FileUtil.getDownLoadFilePath(this, "fanAudioSave" + "_" + System.currentTimeMillis() + (AudioConfig.recordFormatIsMp3 ? ".mp3" : ".amr"));
            showActionDoingDialog();
            soundFile.saveAudioFile(wolumPath, new onEncodeCompleteListener() {
                @Override
                public void onEncodeComplete(final String path, final String waveData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showActionDoingDialog();
                            File file = new File(path);
                            if (file.exists()) {
                                testOpemVolumePlay(path, waveData);
                                Toast.makeText(AcitivtyWaveTestRecycler.this, "文件保存成功--" + path, Toast.LENGTH_SHORT).show();
                                AcitivtyWaveTestRecycler.this.finish();
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 测试打开播放界面显示
     */
    private void testOpemVolumePlay(String filePath, String waveData) {
        Intent intent = new Intent(this, ActivityVolumPlay.class);
        intent.putExtra("volumePath", filePath);
        intent.putExtra("waveData", waveData);
        intent.putExtra("duration", MusicSimilarityUtil.getRecordTimeSeconds(recordTotalTime));
        startActivity(intent);
    }


    /**
     * 是否进入编辑模式
     *
     * @param isEnter
     */
    public void enterClipMode(boolean isEnter) {
        recordView.setVisibility(isEnter ? GONE : VISIBLE);
        waveEdit.enterEditMode(isEnter, recordTotalTime * 1000, soundFile.getWaveBytes());
    }

    /**
     * 更新播放的位置
     *
     * @param timeMs
     */
    public void updateData(double timeMs) {
        if (isInAudioEdit())
            waveEdit.updatePlayBackPosition(timeMs);
        else {
            recordView.updatePlayPosition(timeMs);
        }
    }

    /**
     * 编辑按钮的状态
     *
     * @param isEnable
     */
    private void clipViewEnable(boolean isEnable) {

        if (isEnable && !isCanAction)
            isEnable = false;

        btnClip.setEnabled(isEnable);
//        btnClipText.setText("裁剪");
        btnClipIcon.setImageResource(isEnable ? R.mipmap.icon_clip : R.mipmap.icon_clip_disable);
        btnClipText.setTextColor(Color.parseColor(isEnable ? "#6CA5FF" : "#e0e0e0"));
    }

    /**
     * 准备开始录音
     */
    private void parperRecordStatus() {

        btnActionDone.setTextColor(Color.parseColor("#e0e0e0"));
        btnActionDone.setEnabled(false);

        recordTime.setText("00:00.00");

        btnPlay.setImageResource(R.mipmap.icon_play_disable);
        btnPlay.setEnabled(false);

        btnRecord.setImageResource(R.mipmap.icon_start_recording);
        btnRecord.setEnabled(true);

        clipViewEnable(false);
    }

    /**
     * 正在录音
     */
    public void startRecordStatus() {
        btnPlay.setImageResource(R.mipmap.icon_play_disable);
        btnPlay.setEnabled(false);

//        recordTime.setText("00:00.00");
        btnRecord.setImageResource(R.mipmap.icon_record_stop);
        btnRecord.setEnabled(true);

        clipViewEnable(false);
    }

    /**
     * 停止录音
     */
    private void recordStopStauts() {
        btnPlay.setImageResource(R.mipmap.icon_play);
        btnPlay.setEnabled(true);

        btnRecord.setImageResource(R.mipmap.icon_start_recording);
        btnRecord.setEnabled(true);

        clipViewEnable(true);
    }

    /**
     * 播放录音
     */
    private void playAudioStatus() {

        btnPlay.setImageResource(R.mipmap.icon_play_stop);
        btnPlay.setEnabled(true);
        if (!isInAudioEdit()) {
            btnRecord.setImageResource(R.mipmap.icon_record_disable);
            btnRecord.setEnabled(false);
            clipViewEnable(true);
        }
    }


    /**
     * 停止播放录音
     */
    private void stopplayAudioStatus() {

        btnPlay.setImageResource(R.mipmap.icon_play);
        btnPlay.setEnabled(true);
        if (!isInAudioEdit()) {
            btnRecord.setImageResource(R.mipmap.icon_start_recording);
            btnRecord.setEnabled(true);
            clipViewEnable(true);
        }
    }

    /**
     * 是否处于编辑页面
     *
     * @return
     */
    private boolean isInAudioEdit() {
        return editClip.getVisibility() == VISIBLE && editDelete.getVisibility() == VISIBLE;
    }

    /**
     * @param view
     * @param isShow
     */
    private void alphaView(final View view, final boolean isShow) {

        if (isShow)
            view.setVisibility(VISIBLE);
        ViewCompat.animate(view).alpha(isShow ? 1.0f : 0.0f).setDuration(250).setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                if (!isShow)
                    view.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 是否进入音频编辑模式
     *
     * @param isEnter true 进入编辑模式
     *                false 退出编辑模式
     */
    private void enterAudioEditUI(boolean isEnter) {

        recordtitle.setText(isEnter ? "语音编辑" : "录制语音");

        btnPlay.setImageResource(R.mipmap.icon_play);
        btnPlay.setEnabled(true);

        btnRecord.setImageResource(isEnter ? R.mipmap.icon_record_disable : R.mipmap.icon_start_recording);
        btnRecord.setEnabled(!isEnter);

        alphaView(btnClip, !isEnter);

        alphaView(recordView, !isEnter);

        // TODO: 23/03/2017  改变titlbar 相关显示，这里demo就简单处理一下
        alphaView(btnActionDone, !isEnter);

        alphaView(editDelete, isEnter);
        alphaView(editClip, isEnter);

        enterClipMode(isEnter);
    }

    /**
     * 分开处理编辑 和录音的情况
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //当在滑动的时候不进行点击操作
        if (isScrolling)
            return;

        switch (v.getId()) {
            case R.id.btnPlay:
                if (currentStatus == ActionStatus.clipAudio ||
                        currentStatus == ActionStatus.stopRecording ||
                        currentStatus == ActionStatus.stopPlayAudio) {
                    currentStatus = ActionStatus.playAudio;
                    startPlay();
                } else if (currentStatus == ActionStatus.playAudio) {
                    currentStatus = ActionStatus.stopPlayAudio;
                    stopPlay(false);
                }
                updateUi();
                break;
            case R.id.btnRecord:
                if (currentStatus == ActionStatus.parareStart ||
                        currentStatus == ActionStatus.stopRecording ||
                        currentStatus == ActionStatus.stopPlayAudio) {
                    currentStatus = ActionStatus.startRecording;
                    startRecord();
                } else if (currentStatus == ActionStatus.startRecording) {
                    currentStatus = ActionStatus.stopRecording;
                    stopRecord();
                }
                updateUi();
                break;
            case R.id.btnClip:
                if (currentStatus == ActionStatus.stopPlayAudio ||
                        currentStatus == ActionStatus.playAudio ||
                        currentStatus == ActionStatus.stopRecording) {
                    if (currentStatus == ActionStatus.playAudio) {
                        stopPlay(false);
                    }
                    currentStatus = ActionStatus.clipAudio;
                    updateUi();
                }
                break;
            case R.id.txt_cancle:
                onBackPressed();
                break;
            case R.id.btnActionDone:
                saveAudioFile();
                break;
            case R.id.editDelete:
                onAudioEdit(false);
                break;
            case R.id.editClip:
                onAudioEdit(true);
                break;

        }
    }

    /**
     * 执行编辑操作
     *
     * @param isclip
     */
    private void onAudioEdit(boolean isclip) {

        // TODO: 26/03/2017  编辑音频最好给出再次确认框
        Toast.makeText(this, "你确定这样做？？？？？", Toast.LENGTH_SHORT).show();

        showActionDoingDialog();
        soundFile.dealAudioEidt(isclip, waveEdit.getSelectStartTime(), waveEdit.getSelectEndTime());
    }

    /**
     * 退出编辑模式
     *
     * @return ture 已经退出
     * false 不需要退出
     */
    private boolean exitAudioEditMode() {
        if (isInAudioEdit()) {
            if (currentStatus == ActionStatus.playAudio) {
                stopPlay(false);
            }
            currentStatus = ActionStatus.stopRecording;
            enterAudioEditUI(false);
            recordView.updatePlayBackPosition();
            return true;
        }
        return false;
    }

    /**
     * 退出给出相应提示，如果没有录制，就直接退出，如果正在播放，停止播放，给出是否要进一步退出的提示
     *
     * @return
     */
    private boolean exitAttention() {
        if (recordTotalTime > 0) {
            if (currentStatus == ActionStatus.playAudio) {
                currentStatus = ActionStatus.stopRecording;
                updateUi();
                stopPlay(false);
            } else if (currentStatus == ActionStatus.startRecording) {
                currentStatus = ActionStatus.stopRecording;
                updateUi();
                stopRecord();
            }
            Toast.makeText(this, "有音频录制的数据这里需要弹出确认框", Toast.LENGTH_LONG).show();
            // TODO: 2017/3/24 暂时改为true,方便调试,实际为false
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!exitAudioEditMode()) {
            if (exitAttention())
                super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 根据状态显示
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
                enterAudioEditUI(true);
                break;
        }
    }
}
