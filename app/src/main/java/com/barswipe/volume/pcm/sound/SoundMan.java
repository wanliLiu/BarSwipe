package com.barswipe.volume.pcm.sound;

import android.os.Handler;
import android.util.Log;

import io.kvh.media.amr.AmrDecoder;

/**
 * Created by changbinhe on 14/11/22.
 */
public class SoundMan implements Supporter.OnOffSwitcher {
    private static final String TAG = "Filer";
    private static final boolean DEBUG = true;

    private boolean isUseSystEncode = false;

    private static final class SingletonHolder {
        public static final SoundMan INSTANCE = new SoundMan();
    }

    public static SoundMan getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private boolean isRunning;
    private boolean initialized;
    private Handler handler;

    public SoundMan() {
        handler = new Handler();
    }

    @Override
    public void start() {
        if (DEBUG)
            Log.i(TAG, "try to start");
        if (isRunning) {
            Log.i(TAG, "already started");
            return;
        }

        if (!initialized) {
            if (DEBUG)
                Log.i(TAG, "try init");
            init();
            initialized = true;
            if (DEBUG)
                Log.i(TAG, "init succeed");
        }
        isRunning = true;

        codec.setUseSystEncode(isUseSystEncode);

        recorder.start();
        codec.start();
        filer.start();
        uploader.start();
        if (DEBUG)
            Log.i(TAG, "start succeed");

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(sliceRunnable, Supporter.SLICE_SECOND * 1000);
    }

    Runnable sliceRunnable = new Runnable() {
        @Override
        public void run() {
            stop();
            Log.e(TAG,"录制结束---" + Supporter.SLICE_SECOND );
//            filer.nextSlice();
//            handler.postDelayed(sliceRunnable, Supporter.SLICE_SECOND * 1000);
        }
    };

    @Override
    public void stop() {
        if (!isRunning)
            return;
        isRunning = false;
        handler.removeCallbacksAndMessages(null);

        //new slice
        recorder.stop();
        codec.stop();
        filer.stop();
    }

    private void init() {
        AmrDecoder.init();

        recorder = new Recorder();
        codec = new Codec();

        filer = new Filer();
        uploader = new Uploader();

        recorder.setPcmConsumer(codec);
        codec.setAmrConsumer(filer);
        filer.setFileConsumer(uploader);
    }

    /**
     * @param useSystEncode
     */
    public SoundMan setUseSystEncode(boolean useSystEncode) {
        isUseSystEncode = useSystEncode;
        return this;
    }

    private Recorder recorder;//产生pcm数据
    private Codec codec;//编码pcm数据
    private Filer filer;//保存编码后的amr数据
    private Uploader uploader;

}
