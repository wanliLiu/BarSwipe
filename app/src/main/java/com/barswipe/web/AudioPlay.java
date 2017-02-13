package com.barswipe.web;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import okhttp3.RequestBody;
import okhttp3.ws.WebSocket;

/**
 * Created by chenjiahuan on 16/5/8.
 */
public class AudioPlay {

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private boolean isRecording = false;

    private WebSocket webSocket;

    // 设置音频采样率，44100是目前的标准
    private int sampleRateInHz = 8000;//44100
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeRecord = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    private byte[] inbytes;
    private int bufferSizeTrack = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

    protected LinkedList<byte[]> sendTemp;

    private int bufferSize = 160;
    private int currentBuffer = -1;
    private ByteArrayOutputStream bufferStream0 = new ByteArrayOutputStream();
    private ByteArrayOutputStream bufferStream1 = new ByteArrayOutputStream();

    /**
     * @param socket
     * @return
     */
    public AudioPlay initRecord(WebSocket socket) {
        webSocket = socket;
        sendTemp = new LinkedList<>();
        inbytes = new byte[bufferSizeRecord];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeRecord);
        return this;
    }

    /**
     * @param
     * @return
     */
    public AudioPlay initTrack() {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeTrack, AudioTrack.MODE_STREAM);
        audioTrack.play();
        new Thread(new playTHread()).start();
        return this;
    }

    /**
     *
     */
    public void startRecording() {
        isRecording = true;
        audioRecord.startRecording();
        new Thread(new readRecordDataThread()).start();
    }

    /**
     *
     */
    public void stopRecording() {
        isRecording = false;
//        audioRecord.stop();
    }

    public void destory() {

        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        if (audioTrack != null) {
            try {
                audioTrack.stop();
                audioTrack.release();
                audioTrack = null;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param data
     */
    public void playAudioData(byte[] data) {
        if (audioTrack != null && data != null && data.length > 0) {
            byte[] bytes_pkg = null;
            bytes_pkg = data.clone();
            audioTrack.write(bytes_pkg, 0, bytes_pkg.length);
            Log.e("audioTrack", "音频有数据过来");
        }
    }

    public synchronized void onPlaying(byte[] data) {
        switch (currentBuffer) {
            case 0:
                bufferStream1.write(data, 0, data.length);
                if (bufferStream1.size() > bufferSize) {
                    if (bufferStream0.size() <= 0)
                        currentBuffer = 1;
                }
                break;
            case -1:
            case 1:
                bufferStream0.write(data, 0, data.length);
                if (bufferStream0.size() > bufferSize) {
                    if (bufferStream1.size() <= 0)
                        currentBuffer = 0;
                }
                break;
        }
    }

    private class playTHread implements Runnable {

        /**
         * @param bufferStream
         */
        private void runPay(ByteArrayOutputStream bufferStream) {
            int dataSize = bufferStream.size();
            byte[] dataArray = bufferStream.toByteArray();
            int count = dataSize / bufferSize;
            int offset = dataSize % bufferSize == 0 ? 0 : 1;
            for (int i = 0; i < count + offset; i++) {
                int len;
                if ((i + 1) * bufferSize > dataSize) {
                    len = dataSize - i * bufferSize;
                } else {
                    len = bufferSize;
                }
                audioTrack.write(dataArray, i * bufferSize, len);
            }

            bufferStream.reset();
        }

        @Override
        public void run() {
            while (true) {

                if (currentBuffer == -1)
                    continue;

                if (currentBuffer == 0)
                    runPay(bufferStream0);
                else if (currentBuffer == 1)
                    runPay(bufferStream1);
            }
        }
    }


    private class readRecordDataThread implements Runnable {
        @Override
        public void run() {
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            byte[] audiodata;
            while (isRecording) {
                audioRecord.read(inbytes, 0, bufferSizeRecord);
                audiodata = inbytes.clone();
                if (sendTemp.size() >= 2) {
                    if (webSocket != null) {
                        byte[] senddata = sendTemp.removeFirst();
                        int offset = senddata.length % bufferSize > 0 ? 1 : 0;
                        //将一个buffer拆分成几份小数据包 MAX_DATA_LENGTH 为包的最大byte数
                        for (int i = 0; i < senddata.length / bufferSize + offset; i++) {
                            int length = bufferSize;
                            if ((i + 1) * bufferSize > senddata.length) {
                                length = senddata.length - i * bufferSize;
                            }
                            try {
                                RequestBody response = RequestBody.create(WebSocket.BINARY, senddata, i * bufferSize, length);//文本格式发送消息
                                webSocket.sendMessage(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                sendTemp.add(audiodata);
            }

            try {
                if (audioRecord != null) {
                    audioRecord.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
