package com.barswipe.volume.wave;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by soli on 19/03/2017.
 */

public class AudioRecordView extends FrameLayout {

    private Context ctx;
    private PcmWaveView waveView;
    private PlayWaveView playBackView;

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


        soundFile = new FansSoundFile();
        soundFile.setRecordListener(new FansSoundFile.onRecordStatusListener() {

            @Override
            public void onRecordTime(double fractionComplete, String time) {
                if (listener != null)
                    listener.onRecordTime(fractionComplete, time);
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

    public void setOnRecordListener(FansSoundFile.onRecordStatusListener mlistner) {
        listener = mlistner;
    }

    /**
     * @return
     */
    private PlayWaveView getPlayBackView() {
        PlayWaveView playWaveView = new PlayWaveView(ctx);
        playWaveView.setLayoutParams(getDefaultLayoutParams());

        return playWaveView;
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
     *
     */
    public void startPlay()
    {
        waveView.startPlay();
    }

    public void stopPlay()
    {
        waveView.stopPlay();
    }

    public void updateData(int timeMs)
    {
        waveView.updatePlayPosition(timeMs);
    }
}
