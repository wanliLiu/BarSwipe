package com.barswipe.volume.wave;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.io.File;

/**
 * Created by soli on 19/03/2017.
 */

public class AudioRecordView extends FrameLayout {

    private Context ctx;
    private PcmWaveView waveView;
    private WaveEditView playBackView;

    private FansSoundFile soundFile;

    private FansSoundFile.onRecordStatusListener listener;

    public AudioRecordView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AudioRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioRecordView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * @param mCtx
     */
    private void init(Context mCtx) {
        ctx = mCtx;

        addView(waveView = getWaveView());
        addView(playBackView = getPlayBackView());


        // TODO: 2017/3/23 测试
        waveView.setVisibility(GONE);

        if (listener != null) {
            waveView.setTimeChangeListener(listener);
            playBackView.setTimeChangeListener(listener);
        }

        soundFile = new FansSoundFile();
        soundFile.setRecordListener(new FansSoundFile.onRecordStatusListener() {

            @Override
            public void onScrollTimeChange(double fractionComplete, String time) {
                if (listener != null)
                    listener.onScrollTimeChange(fractionComplete, time);
            }

            @Override
            public void onRealVolume(double volume) {
                if (waveView.isRecording()) {
                    playBackView.updatePosition();
                    waveView.updateData(volume);
                }
            }

            @Override
            public void onRecordStop(FansSoundFile soundFile) {
                if (listener != null)
                    listener.onRecordStop(soundFile);
            }
        });
    }

    /**
     * @param mlistner
     */
    public void setOnRecordListener(FansSoundFile.onRecordStatusListener mlistner) {
        listener = mlistner;
        if (waveView != null)
            waveView.setTimeChangeListener(listener);
        if (playBackView != null)
            playBackView.setTimeChangeListener(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(waveView.getScreenWidth(), waveView.getViewHeight());
    }

    /**
     * @return
     */
    private WaveEditView getPlayBackView() {
        WaveEditView waveEditView = new WaveEditView(ctx);
        waveEditView.setLayoutParams(getDefaultLayoutParams());

        return waveEditView;
    }

    /**
     * @return
     */
    private PcmWaveView getWaveView() {
        PcmWaveView view = new PcmWaveView(ctx);
        view.setLayoutParams(getDefaultLayoutParams());
        return view;
    }

    /**
     * @return
     */
    private LayoutParams getDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * @param
     */
    public void testPostionUpdate() {
//        waveView.setRecording(true);
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (isRecording) {
//                    playBackView.updatePosition();
//                    waveView.updateData();
////                    if (offset > playBackView.getHalfScreenWidth())
////                        scrollView.scrollBy(5, 0);
//                }
//            }
//        }, 0, 50);
    }

    /**
     *
     */
    public void startRecord() {
        soundFile.startRecord();
        waveView.setRecording(true);
    }

    /**
     *
     */
    public void stopRecord() {
        soundFile.stopRecord();
        waveView.setRecording(false);
    }

    /**
     * @param file
     * @param listener
     */
    public void saveAudioFile(File file, onEncodeCompleteListener listener) {
        // TODO: 22/03/2017 保存文件的条件
        if (!waveView.isRecording()) {
            soundFile.saveAudioFile(file, listener);
        }
    }

    /**
     *
     */
    public double startPlay() {
        return waveView.startPlay();
    }

    public void stopPlay(boolean stopfrom) {
        waveView.stopPlay(stopfrom);
    }

    public void updateData(double timeMs) {
        waveView.updatePlayPosition(timeMs);
    }
}
