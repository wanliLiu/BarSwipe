package com.barswipe.volume;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.barswipe.R;
import com.barswipe.util.FileUtil;

import java.io.File;

/**
 * Created by Soli on 2017/1/19.
 */


public class AudioRecordManager implements Handler.Callback {

    private static final String TAG = "AudioRecordManager";
    private int RECORD_INTERVAL;
    private AudioState mCurAudioState;
    private View mRootView;
    private Context mContext;
    private Handler mHandler;
    private AudioManager mAudioManager;
    private MediaRecorder mMediaRecorder;
    private Uri mAudioPath;
    private long smStartRecTime;
    private AudioManager.OnAudioFocusChangeListener mAfChangeListener;
    private PopupWindow mRecordWindow;
    private ImageView mStateIV;
    private TextView mStateTV;
    private TextView mTimerTV;
    AudioState idleState;
    AudioState recordState;
    AudioState cancelState;
    AudioState timerState;

//    static AudioRecordManager sInstance;

    private  StateListener listener;
    /**
     *
     * @return
     */
    public static AudioRecordManager getInstance() {
        return new AudioRecordManager();
    }

    /**\
     *
     */
    public AudioRecordManager() {
        RECORD_INTERVAL = 60;
        idleState = new AudioRecordManager.IdleState();
        recordState = new AudioRecordManager.RecordState();
        cancelState = new AudioRecordManager.CancelState();
        timerState = new AudioRecordManager.TimerState();
        Log.d(TAG, TAG);
//        if (Build.VERSION.SDK_INT < 21) {
//            try {
//                TelephonyManager e = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
//                e.listen(new PhoneStateListener() {
//                    public void onCallStateChanged(int state, String incomingNumber) {
//                        switch (state) {
//                            case 1:
//                                sendEmptyMessage(6);
//                            case 0:
//                            case 2:
//                            default:
//                                super.onCallStateChanged(state, incomingNumber);
//                        }
//                    }
//                }, 32);
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        }

        mCurAudioState = idleState;
        idleState.enter();
    }

    public final boolean handleMessage(Message msg) {
        Log.i(TAG, "handleMessage " + msg.what);
        AudioStateMessage m;
        switch (msg.what) {
            case 2:
                sendEmptyMessage(2);
                break;
            case 7:
//                m = AudioStateMessage.obtain();
//                m.what = msg.what;
//                m.obj = msg.obj;
//                sendMessage(m);
                //认为停止
                stopRecord();
                break;
            case 8:
                m = AudioStateMessage.obtain();
                m.what = 7;
                m.obj = msg.obj;
                sendMessage(m);
        }

        return false;
    }

    /**
     *
     * @param root
     */
    private void initView(View root) {
        mHandler = new Handler(root.getHandler().getLooper(), this);
        LayoutInflater inflater = LayoutInflater.from(root.getContext());
        View view = inflater.inflate(R.layout.rc_wi_vo_popup, null);
        mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
        mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
        mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
        mRecordWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRecordWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
        mRecordWindow.setFocusable(true);
        mRecordWindow.setOutsideTouchable(false);
        mRecordWindow.setTouchable(false);
    }

    private void setTimeoutView(int counter) {
        if (mRecordWindow != null) {
            mStateIV.setVisibility(View.GONE);

            mStateTV.setText(R.string.rc_voice_rec);
//            mStateTV.setBackgroundResource(17170445);
            mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
            mTimerTV.setVisibility(View.VISIBLE);
        }

    }

    /**
     *
     */
    private void setRecordingView() {
        Log.d(TAG, "setRecordingView");
        if (mRecordWindow != null) {
            mStateIV.setVisibility(View.VISIBLE);
            mStateIV.setImageResource(R.drawable.rc_ic_volume_1);
            mStateTV.setVisibility(View.VISIBLE);
            mStateTV.setText(R.string.rc_voice_rec);
//            mStateTV.setBackgroundResource(1717View.VISIBLE445);
            mTimerTV.setVisibility(View.GONE);
        }

    }

    /**
     *
     */
    private void setCancelView() {
        Log.d(TAG, "setCancelView");
        if (mRecordWindow != null) {
            mTimerTV.setVisibility(View.GONE);
            mStateIV.setVisibility(View.VISIBLE);
            mStateIV.setImageResource(R.drawable.rc_ic_volume_cancel);
            mStateTV.setVisibility(View.VISIBLE);
            mStateTV.setText(R.string.rc_voice_cancel);
            mStateTV.setBackgroundResource(R.drawable.rc_corner_voice_style);
        }
    }

    /**
     *
     */
    private void destroyView() {
        Log.d(TAG, "destroyView");
        if (mRecordWindow != null) {
            mHandler.removeMessages(7);
            mHandler.removeMessages(8);
            mHandler.removeMessages(2);
            mRecordWindow.dismiss();
            mRecordWindow = null;
            mStateIV = null;
            mStateTV = null;
            mTimerTV = null;
            mHandler = null;
            mContext = null;
            mRootView = null;
        }

    }

    /**
     *
     * @param maxVoiceDuration
     */
    public void setMaxVoiceDuration(int maxVoiceDuration) {
        RECORD_INTERVAL = maxVoiceDuration;
    }

    /**
     *
     * @return
     */
    public int getMaxVoiceDuration() {
        return RECORD_INTERVAL;
    }

    /**
     *
     * @param rootView
     */
    public void startRecord(View rootView) {
        mRootView = rootView;
        mContext = rootView.getContext().getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAfChangeListener != null) {
            mAudioManager.abandonAudioFocus(mAfChangeListener);
            mAfChangeListener = null;
        }

        mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                Log.d(TAG, "OnAudioFocusChangeListener " + focusChange);
                if (focusChange == -1) {
                    mAudioManager.abandonAudioFocus(mAfChangeListener);
                    mAfChangeListener = null;
                    sendEmptyMessage(6);
                }

            }
        };
        sendEmptyMessage(1);
    }

    /**
     *
     */
    public void willCancelRecord() {
        sendEmptyMessage(3);
    }

    /**
     *
     */
    public void continueRecord() {
        sendEmptyMessage(4);
    }

    /**
     *
     */
    public void stopRecord() {
        sendEmptyMessage(5);
    }

    /**
     *
     */
    public void destroyRecord() {
        AudioStateMessage msg = new AudioStateMessage();
        msg.obj = Boolean.valueOf(true);
        msg.what = 5;
        sendMessage(msg);
    }

    void sendMessage(AudioStateMessage message) {
        mCurAudioState.handleMessage(message);
    }

    /**
     *
     * @param event
     */
    void sendEmptyMessage(int event) {
        AudioStateMessage message = AudioStateMessage.obtain();
        message.what = event;
        mCurAudioState.handleMessage(message);
    }

    /**
     *
     */
    private void startRec() {
        Log.d(TAG, "startRec");

        try {
            muteAudioFocus(mAudioManager, true);
            mAudioManager.setMode(AudioManager.STREAM_VOICE_CALL);
            mMediaRecorder = new MediaRecorder();

            try {
                Resources e = mContext.getResources();
                int bps = e.getInteger(e.getIdentifier("rc_audio_encoding_bit_rate", "integer", mContext.getPackageName()));
                mMediaRecorder.setAudioSamplingRate(8000);
                mMediaRecorder.setAudioEncodingBitRate(bps);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            mMediaRecorder.setAudioChannels(1);
            // 设置麦克风为音频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频文件的编码
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            // 设置输出文件的格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//可以设置成 MediaRecorder.AudioEncoder.AMR_NB

//            mAudioPath = Uri.fromFile(new File(mContext.getCacheDir(), System.currentTimeMillis() + "temp.amr"));
            mAudioPath = Uri.fromFile(FileUtil.getDownLoadFilePath(mContext, "mediarecorder" + "_" + System.currentTimeMillis() + ".amr"));

            mMediaRecorder.setOutputFile(mAudioPath.getPath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            Message e1 = Message.obtain();
            e1.what = 7;
            e1.obj = Integer.valueOf(10);
            mHandler.sendMessageDelayed(e1, (long) (RECORD_INTERVAL * 1000));
//            mHandler.sendMessageDelayed(e1, (long) (RECORD_INTERVAL * 1000 - 10000));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @return
     */
    private boolean checkAudioTimeLength() {
        long delta = SystemClock.elapsedRealtime() - smStartRecTime;
        return delta < 1000L;
    }

    /**
     *
     */
    private void stopRec() {
        Log.d(TAG, "stopRec");
        try {
            muteAudioFocus(mAudioManager, false);
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void deleteAudioFile() {
        Log.d(TAG, "deleteAudioFile");
        if (mAudioPath != null) {
//            File file = new File(mAudioPath.getPath());
//            if (file.exists()) {
//                file.delete();
//            }
        }

    }

    /**
     * 发送文件
     */
    private void sendAudioFile() {
        Log.d(TAG, "sendAudioFile path = " + mAudioPath);
        if (mAudioPath != null) {
            File file = new File(mAudioPath.getPath());
            if (!file.exists() || file.length() == 0L) {
                Log.e(TAG, "sendAudioFile fail cause of file length 0 or audio permission denied");
                return;
            }
            int duration = (int) (SystemClock.elapsedRealtime() - smStartRecTime) / 1000;
            // TODO: 2017/1/19

            if (listener != null)
                listener.onComplete(mAudioPath);
        }
    }

    public void setListener(StateListener listener) {
        this.listener = listener;
    }

    public Uri getmAudioPath() {
        return mAudioPath;
    }

    /**
     *
     */
    private void audioDBChanged() {
        if (mMediaRecorder != null) {
            int db = mMediaRecorder.getMaxAmplitude() / 600;
            switch (db / 5) {
                case 0:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_1);
                    break;
                case 1:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_2);
                    break;
                case 2:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_3);
                    break;
                case 3:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_4);
                    break;
                case 4:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_5);
                    break;
                case 5:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_6);
                    break;
                case 6:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_7);
                    break;
                default:
                    mStateIV.setImageResource(R.drawable.rc_ic_volume_8);
            }
        }

    }

    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if (Build.VERSION.SDK_INT < 8) {
            Log.d(TAG, "muteAudioFocus Android 2.1 and below can not stop music");
        } else {
            if (bMute) {
                audioManager.requestAudioFocus(mAfChangeListener, 3, 2);
            } else {
                audioManager.abandonAudioFocus(mAfChangeListener);
                mAfChangeListener = null;
            }

        }
    }

    class TimerState extends AudioState {
        TimerState() {
        }

        void handleMessage(AudioStateMessage msg) {
            Log.d(TAG, getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch (msg.what) {
                case 3:
                    setCancelView();
                    mCurAudioState = cancelState;
                case 4:
                default:
                    break;
                case 5:
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            stopRec();
                            sendAudioFile();
                            destroyView();
                        }
                    }, 500L);
                    mCurAudioState = idleState;
                    idleState.enter();
                    break;
                case 6:
                    stopRec();
                    destroyView();
                    deleteAudioFile();
                    mCurAudioState = idleState;
                    idleState.enter();
                    break;
                case 7:
                    int counter = ((Integer) msg.obj).intValue();
                    if (counter > 0) {
                        Message message = Message.obtain();
                        message.what = 8;
                        message.obj = Integer.valueOf(counter - 1);
                        mHandler.sendMessageDelayed(message, 1000L);
                        setTimeoutView(counter);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                stopRec();
                                sendAudioFile();
                                destroyView();
                            }
                        }, 500L);
                        mCurAudioState = idleState;
                    }
            }

        }
    }

    class CancelState extends AudioState {
        CancelState() {
        }

        void handleMessage(AudioStateMessage msg) {
            Log.d(TAG, getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch (msg.what) {
                case 1:
                case 2:
                case 3:
                default:
                    break;
                case 4:
                    setRecordingView();
                    mCurAudioState = recordState;
                    sendEmptyMessage(2);
                    break;
                case 5:
                case 6:
                    stopRec();
                    destroyView();
                    deleteAudioFile();
                    mCurAudioState = idleState;
                    idleState.enter();
                    break;
                case 7:
                    int counter = ((Integer) msg.obj).intValue();
                    if (counter > 0) {
                        Message message = Message.obtain();
                        message.what = 8;
                        message.obj = Integer.valueOf(counter - 1);
                        mHandler.sendMessageDelayed(message, 1000L);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                stopRec();
                                sendAudioFile();
                                destroyView();
                            }
                        }, 500L);
                        mCurAudioState = idleState;
                        idleState.enter();
                    }
            }

        }
    }

    class RecordState extends AudioState {
        RecordState() {
        }

        void handleMessage(AudioStateMessage msg) {
            Log.d(TAG, getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch (msg.what) {
                case 2:
                    audioDBChanged();
                    mHandler.sendEmptyMessageDelayed(2, 150L);
                    break;
                case 3:
                    setCancelView();
                    mCurAudioState = cancelState;
                case 4:
                default:
                    break;
                case 5:
                    final boolean checked = checkAudioTimeLength();
                    boolean activityFinished = false;
                    if (msg.obj != null) {
                        activityFinished = ((Boolean) msg.obj).booleanValue();
                    }

                    if (checked && !activityFinished) {
                        mStateIV.setImageResource(R.drawable.rc_ic_volume_wraning);
                        mStateTV.setText(R.string.rc_voice_short);
                        mHandler.removeMessages(2);
                    }

                    if (!activityFinished && mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                stopRec();
                                if (!checked) {
                                    sendAudioFile();
                                }

                                destroyView();
                                mCurAudioState = idleState;
                            }
                        }, 500L);
                    } else {
                        stopRec();
                        if (!checked && activityFinished) {
                            sendAudioFile();
                        }

                        destroyView();
                        mCurAudioState = idleState;
                    }
                    break;
                case 6:
                    stopRec();
                    destroyView();
                    deleteAudioFile();
                    mCurAudioState = idleState;
                    idleState.enter();
                    break;
                case 7:
                    int counter = ((Integer) msg.obj).intValue();
                    setTimeoutView(counter);
                    mCurAudioState = timerState;
                    if (counter > 0) {
                        Message message = Message.obtain();
                        message.what = 8;
                        message.obj = Integer.valueOf(counter - 1);
                        mHandler.sendMessageDelayed(message, 1000L);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                stopRec();
                                sendAudioFile();
                                destroyView();
                            }
                        }, 500L);
                        mCurAudioState = idleState;
                    }
                    break;
            }

        }
    }

    class IdleState extends AudioState {
        public IdleState() {
            Log.d(TAG, "IdleState");
        }

        void enter() {
            super.enter();
            if (mHandler != null) {
                mHandler.removeMessages(7);
                mHandler.removeMessages(8);
                mHandler.removeMessages(2);
            }
        }

        void handleMessage(AudioStateMessage msg) {
            Log.d(TAG, "IdleState handleMessage : " + msg.what);
            switch (msg.what) {
                case 1:
                    initView(mRootView);
                    setRecordingView();
                    startRec();
                    smStartRecTime = SystemClock.elapsedRealtime();
                    mCurAudioState = recordState;
                    sendEmptyMessage(2);
                default:
            }
        }
    }

    public interface StateListener{
        public void onComplete(Uri path);
    }
}
