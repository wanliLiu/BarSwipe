package com.barswipe.volume.wave;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by soli on 19/03/2017.
 */

public class AudioRecordView extends FrameLayout {

    private Context ctx;

    private HorizontalScrollView scrollView;
    private PcmWaveView waveView;
    private PlayWaveView playBackView;

    private boolean isRecording = false;

    private int currentScroolX = 0, offset = 0;

    private float lastX = 0;

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
//        addView(scrollView = getScroolView());
        addView(playBackView = getPlayBackView());

//        scrollView.setOnTouchListener(new scrollTouch());


//        scrollView.setSmoothScrollingEnabled(false);

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
    private HorizontalScrollView getScroolView() {
        HorizontalScrollView scrollView = new HorizontalScrollView(ctx);
        scrollView.setLayoutParams(getDefaultLayoutParams());
        scrollView.setHorizontalScrollBarEnabled(false);

        scrollView.addView(waveView = getWaveView());

        return scrollView;
    }

    /**
     * @return
     */
    private LayoutParams getDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     *
     */
    private class scrollTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            playBackView.logOut("getScrollX____" + scrollView.getScrollX());
            //录制的时候，禁止滑动
            if (isRecording || offset <= 0)
                return true;

            boolean iscanScroll = false;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float delta = lastX - event.getX();
                    if (delta > 0) {
                        //向右滑动
                        if (scrollView.getScrollX() >= currentScroolX)
                            iscanScroll = true;
                    }
                    lastX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            return iscanScroll;
        }
    }

    /**
     * @param
     */
    public void testPostionUpdate() {

        isRecording = true;


        waveView.setRecording(true);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRecording) {
                    playBackView.updatePosition(5);
                    waveView.updateData(5);
//                    if (offset > playBackView.getHalfScreenWidth())
//                        scrollView.scrollBy(5, 0);
                }
            }
        }, 0, 50);
    }

    /**
     *
     */
    public void startRecord() {

    }

    /**
     *
     */
    public void stopRecord() {
        isRecording = !isRecording;
        waveView.setRecording(isRecording);
//        if (!isRecording)
//            currentScroolX = scrollView.getScrollX();
//        else
//            scrollView.scrollTo(currentScroolX, 0);
    }

}
