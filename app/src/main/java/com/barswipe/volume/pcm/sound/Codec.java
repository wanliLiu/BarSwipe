package com.barswipe.volume.pcm.sound;

import android.media.AmrInputStream;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.kvh.media.amr.AmrEncoder;

/**
 * Created by changbinhe on 14/11/22.
 * 用amr-codec编码相比系统的，就90s来看，要大很多，系统的不到100k,而amr-codec在141k左右
 */
public class Codec implements Runnable, Supporter.PcmConsumer, Supporter.OnOffSwitcher {
    private static final String TAG = "Codec";
    private static final boolean DEBUG = true;

    private List<short[]> pcmFrames;
    private boolean isRunning;

    private Supporter.AmrConsumer amrConsumer;
    private Thread runningThread;
    final private Object waitingObject;

    private boolean isUseSystEncode = false;

    public Codec() {
        pcmFrames = Collections.synchronizedList(new LinkedList<short[]>());
        waitingObject = new Object();
    }

    /**
     * 是否用系统的arm编码
     *
     * @param useSystEncode
     */
    public void setUseSystEncode(boolean useSystEncode) {
        isUseSystEncode = useSystEncode;
    }

    @Override
    public void onPcmFeed(short[] buffer, int length) {
        //would crash if not 160
        if (length != 160)
            return;
        short[] tempArray = new short[length];
        System.arraycopy(buffer, 0, tempArray, 0, length);

        if (DEBUG)
            Log.i(TAG, "onPcmFeed :" + length);
        pcmFrames.add(tempArray);

        if (DEBUG)
            Log.i(TAG, "onPcmFeed pcmFrames :" + pcmFrames.size());
        synchronized (waitingObject) {
            waitingObject.notify();
        }
    }

    public void setAmrConsumer(Supporter.AmrConsumer amrConsumer) {
        this.amrConsumer = amrConsumer;
    }

    @Override
    public void start() {
        if (DEBUG)
            Log.i(TAG, "try to start");
        if (isRunning) {

            Log.i(TAG, "already started");
            return;
        }
        if (DEBUG)
            Log.i(TAG, "start succeed");
        isRunning = true;

        if (!isUseSystEncode)
            AmrEncoder.init(0);

        //start
        runningThread = new Thread(this);
        runningThread.start();
    }

    @Override
    public void stop() {
        if (DEBUG)
            Log.i(TAG, "stop clean up");
        if (!isRunning) {
            Log.i(TAG, "not running");
            return;
        }

        isRunning = false;

        //todo need sync?
        //finish all
        while (pcmFrames.size() > 0) {
            short[] buffer = pcmFrames.remove(0);
            byte[] encodedData = new byte[buffer.length];
            int encodedLength = encodeData(buffer, encodedData);

            if (DEBUG)
                Log.i(TAG, "clean up encode: length" + encodedLength);
            if (encodedLength > 0) {
                amrConsumer.onAmrFeed(encodedData, encodedLength);
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            synchronized (waitingObject) {
                if (pcmFrames.size() == 0) {
                    try {
                        if (DEBUG)
                            Log.i(TAG, "wait: " + pcmFrames.size());
                        waitingObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    short[] buffer = pcmFrames.remove(0);
                    byte[] encodedData = new byte[buffer.length];
                    int encodedLength = encodeData(buffer, encodedData);
                    //Log.i(TAG, "encode: length" + encodedLength);
                    if (encodedLength > 0) {
                        amrConsumer.onAmrFeed(encodedData, encodedLength);
                    }
                }
            }
        }

        encodeDown();
    }

    /**
     *
     */
    private void encodeDown() {
        if (isUseSystEncode) {

        } else {
            AmrEncoder.exit();
        }
    }

    /**
     * @param pcmData
     * @param armData
     * @return
     */
    private int encodeData(short[] pcmData, byte[] armData) {

        if (isUseSystEncode) {
            try {
                InputStream stream = new ByteArrayInputStream(BytesTransUtil.getInstance().Shorts2Bytes(pcmData));
                AmrInputStream ais = new AmrInputStream(stream);
                int length = ais.read(armData);
                ais.close();
                return length;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Codec", "系统arm编码失败，尝试amr-codec编码");
            }
        }

        return AmrEncoder.encode(AmrEncoder.Mode.MR122.ordinal(), pcmData, armData);
    }
}
