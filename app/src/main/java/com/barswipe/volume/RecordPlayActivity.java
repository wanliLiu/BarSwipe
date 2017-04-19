package com.barswipe.volume;

import android.app.Activity;
import android.app.Service;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.barswipe.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * <p>
 * 文件名称 ：RecordPlayActivity.java
 * <p>
 * 内容摘要 ：
 * <p>
 * 作者 ：吴辰彪 创建时间 ：2012-7-25 下午01:35:48 描述 :边录音边播放的例子
 */
public class RecordPlayActivity extends Activity implements OnClickListener {

    private static final String TAG = "RecordPlayActivity";
    /**
     * 按钮
     */
    private Button bt_exit;
    /**
     * AudioRecord 写入缓冲区大小
     */
    protected int m_in_buf_size;
    /**
     * 录制音频对象
     */
    private AudioRecord m_in_rec;
    /**
     * 录入的字节数组
     */
    private byte[] m_in_bytes;
    /**
     * 存放录入字节数组的大小
     */
    private LinkedList<byte[]> m_in_q;
    /**
     * AudioTrack 播放缓冲大小
     */
    private int m_out_buf_size;
    /**
     * 播放音频对象
     */
    private AudioTrack m_out_trk;
    /**
     * 播放的字节数组
     */
    private byte[] m_out_bytes;
    /**
     * 录制音频线程
     */
    private Thread record;
    /**
     * 播放音频线程
     */
    private Thread play;
    /**
     * 让线程停止的标志
     */
    private boolean flag = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_record);
        this.setTitle("音频回路");

        Log.d("dfdfd", "333333333333");

        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
    }

    private void init() {
        bt_exit = (Button) findViewById(R.id.bt_yinpinhuilu_testing_exit);
        Log.i(TAG, "bt_exit====" + bt_exit);

        // AudioRecord 得到录制最小缓冲区的大小
        m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        // 实例化播放音频对象
        m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);
        // 实例化一个字节数组，长度为最小缓冲区的长度
        m_in_bytes = new byte[m_in_buf_size];
        // 实例化一个链表，用来存放字节组数
        m_in_q = new LinkedList<byte[]>();

        // AudioTrack 得到播放最小缓冲区的大小
        m_out_buf_size = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        // 实例化播放音频对象
        m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
                AudioTrack.MODE_STREAM);
        // 实例化一个长度为播放最小缓冲大小的字节数组
        m_out_bytes = new byte[m_out_buf_size];
    }

    /**
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.starplay: {
                init();
                record = new Thread(new recordSound());
                play = new Thread(new playRecord());
                // 启动录制线程
                record.start();
                // 启动播放线程
                play.start();
            }
            break;
            case R.id.bt_yinpinhuilu_testing_exit:
                flag = false;
                m_in_rec.stop();
                m_in_rec = null;
                m_out_trk.stop();
                m_out_trk = null;
                this.finish();

            case R.id.btn_startAudioTrack:
                startAudioTrack();
                break;
            case R.id.btn_endRecord:
                isRecording = false;
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                break;
            case R.id.btn_startRecord:
                initPCMFile();
                initAudioRecord();
                startAudioRecord();
                break;
            default:
                break;
        }
    }

    /**
     * <p>
     * 文件名称 ：RecordPlayActivity.java
     * <p>
     * 内容摘要 ：
     * <p>
     * 作者 ：吴辰彪 创建时间 ：2012-7-25 下午01:57:09 描述 :录音线程
     */
    class recordSound implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "........recordSound run()......");
            byte[] bytes_pkg;
            // 开始录音
            m_in_rec.startRecording();

            while (flag) {
                m_in_rec.read(m_in_bytes, 0, m_in_buf_size);
                bytes_pkg = m_in_bytes.clone();
                Log.i(TAG, "........recordSound bytes_pkg==" + bytes_pkg.length);
                if (m_in_q.size() >= 2) {
                    m_in_q.removeFirst();
                }
                m_in_q.add(bytes_pkg);
            }
        }

    }

    /**
     * <p>
     * 文件名称 ：RecordPlayActivity.java
     * <p>
     * 内容摘要 ：
     * <p>
     * 作者 ：吴辰彪 创建时间 ：2012-7-25 下午01:57:34 描述 :播放线程
     */
    class playRecord implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.i(TAG, "........playRecord run()......");
            byte[] bytes_pkg = null;
            // 开始播放
            m_out_trk.play();

            while (flag) {
                try {
                    m_out_bytes = m_in_q.getFirst();
                    bytes_pkg = m_out_bytes.clone();
                    m_out_trk.write(bytes_pkg, 0, bytes_pkg.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    int frequency;
    int channelConfig;
    int audioEncording;
    File file;
    boolean isRecording = false;
    AudioRecord audioRecord;
    AudioManager audio;
    int bufferSize;


    private void initPCMFile() {
        file = new File(Environment.getExternalStorageDirectory() + "/raw.pcm");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * audioRecord初始化
     *
     * @param
     */
    private void initAudioRecord() {
        // 8000,11025, 16000,44100等
        frequency = 16000;
        // 双声道AudioFormat.CHANNEL_IN_STEREO，单声道AudioFormat.CHANNEL_IN_MONO
        channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        // 16bit或者默认的8bit
        audioEncording = AudioFormat.ENCODING_PCM_16BIT;

        // 三个参数为：frequency频率、channelConfig音频编码、audioFormat声道编码
        bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfig,
                audioEncording);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                channelConfig, audioEncording, bufferSize * 3);
    }

    /**
     * audioRecord录制
     */
    private void startAudioRecord() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                DataOutputStream dos;
                OutputStream os;
                try {

                    os = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    dos = new DataOutputStream(bos);

                    short[] buffer = new short[bufferSize];

                    // 创建一个新的AudioRecord对象来录制音频

                    audioRecord.startRecording();

                    isRecording = true;
                    while (isRecording) {
                        int bufferReadResult = audioRecord.read(buffer, 0,
                                bufferSize);
                        for (int i = 0; i < bufferReadResult; i++) {
                            dos.writeShort(buffer[i]);
                        }
                    }
                    dos.flush();
                    dos.close();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * 拦截声音按键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:// 增大声音
                /**
                 * 第一个参数除了通话音量还有STREAM_ALARM 警报 STREAM_MUSIC 音乐回放即媒体音量 STREAM_RING
                 * 铃声 STREAM_SYSTEM 系统 等等
                 */
                /**
                 * 第二个参数有 ADJUST_LOWER 降低音量 ADJUST_RAISE 升高音量 ADJUST_SAME
                 * 保持不变,这个主要用于向用户展示当前的音量
                 */
                /**
                 * 第三个参数有 FLAG_PLAY_SOUND 调整音量时播放声音 FLAG_SHOW_UI
                 * 调整时显示音量条,就是按音量键出现的那个 等等
                 */
                audio.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:// 减小声音
                audio.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * AudioTrack播放
     */
    private void startAudioTrack() {
        if (file == null) {
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    //用于存储音轨的short数组
                    int audioLength = (int) file.length() / 2;
                    short[] audio = new short[audioLength];
                    InputStream is = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    DataInputStream dis = new DataInputStream(bis);

                    int i = 0;
                    while (dis.available() > 0) {
                        audio[i] = dis.readShort();
                        i++;
                    }
                    //关闭输入流
                    dis.close();
                    /**
                     * 参数1 选用的是通话声音
                     * 参数6 AudioTrack.MODE_STATIC和AudioTrack.MODE_STREAM
                     */
                    AudioTrack audioTrack = new AudioTrack(
                            AudioManager.STREAM_VOICE_CALL, frequency,
                            channelConfig, audioEncording, audioLength,
                            AudioTrack.MODE_STREAM);

                    audioTrack.play();
                    audioTrack.write(audio, 0, audio.length);
                    audioTrack.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    protected void onStop() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        super.onStop();
    }
}