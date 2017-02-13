package com.barswipe.web;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

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

    private int MAX_DATA_LENGTH = 160;

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

    class readRecordDataThread implements Runnable {
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
                        int offset = senddata.length % MAX_DATA_LENGTH > 0 ? 1 : 0;
                        //将一个buffer拆分成几份小数据包 MAX_DATA_LENGTH 为包的最大byte数
                        for (int i = 0; i < senddata.length / MAX_DATA_LENGTH + offset; i++) {
                            int length = MAX_DATA_LENGTH;
                            if ((i + 1) * MAX_DATA_LENGTH > senddata.length) {
                                length = senddata.length - i * MAX_DATA_LENGTH;
                            }
                            try {
                                RequestBody response = RequestBody.create(WebSocket.BINARY, senddata, i* MAX_DATA_LENGTH, length);//文本格式发送消息
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
