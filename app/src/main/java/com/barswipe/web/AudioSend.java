package com.barswipe.web;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.LinkedList;

import okhttp3.RequestBody;
import okhttp3.ws.WebSocket;

/**
 * Created by Soli on 2017/2/13.
 */

public class AudioSend extends Thread {
    protected AudioRecord m_in_rec;
    protected int m_in_buf_size;
    protected byte[] m_in_bytes;
    protected boolean m_keep_running;
    protected LinkedList<byte[]> m_in_q;

    private WebSocket webSocket;

    public AudioSend(WebSocket socket) {
        webSocket = socket;
    }

    public void run() {
        try {
            byte[] bytes_pkg;
            m_in_rec.startRecording();
            while (m_keep_running) {
                m_in_rec.read(m_in_bytes, 0, m_in_buf_size);
                bytes_pkg = m_in_bytes.clone();
                if (m_in_q.size() >= 2) {
                    if (webSocket != null) {
                        try {
                            RequestBody response = RequestBody.create(WebSocket.BINARY, m_in_q.removeFirst(), 0, m_in_q.removeFirst().length);//文本格式发送消息
                            webSocket.sendMessage(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                m_in_q.add(bytes_pkg);
            }

            m_in_rec.stop();
            m_in_rec = null;
            m_in_bytes = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                m_in_buf_size);

        m_in_bytes = new byte[m_in_buf_size];

        m_keep_running = true;
        m_in_q = new LinkedList<>();
    }

    public void free() {
        m_keep_running = false;
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Log.d("sleep exceptions...\n", "");
        }
    }
}