package com.barswipe.volume.wave;

import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.barswipe.R;
import com.barswipe.volume.pcm.pcm2amr.ToastUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by Soli on 2017/4/10.
 */

public class StudyAudioRecord extends AppCompatActivity {

    private EditText quality,kbps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_study_record);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkVolume);
        checkBox.setChecked(AudioConfig.recordFormatIsMp3);
        if (!AudioConfig.recordFormatIsMp3)
            checkBox.setText("录制amr");
        else
            checkBox.setText("录制mp3");
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked)
                checkBox.setText("录制amr");
            else
                checkBox.setText("录制mp3");
            AudioConfig.recordFormatIsMp3 = isChecked;

        });

        final CheckBox checkChannel = (CheckBox) findViewById(R.id.checkChannel);
        checkChannel.setChecked(FansSoundFile.channelConfig == AudioFormat.CHANNEL_IN_MONO);
        if (FansSoundFile.channelConfig == AudioFormat.CHANNEL_IN_MONO)
            checkChannel.setText("单通道");
        else
            checkChannel.setText("双通道");
        checkChannel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                checkChannel.setText("单通道");
                FansSoundFile.channelConfig= AudioFormat.CHANNEL_IN_MONO;
                FansMp3EncodeThread.DEFAULT_LAME_IN_CHANNEL = 1;
            }
            else{
                checkChannel.setText("双通道");
                FansSoundFile.channelConfig= AudioFormat.CHANNEL_IN_STEREO;
                FansMp3EncodeThread.DEFAULT_LAME_IN_CHANNEL = 2;
            }


        });

        final TextView textView = (TextView) findViewById(R.id.recordTime);
        textView.setText(("能录制的最大时间：" + AudioConfig._totalTimeSec));
        textView.setOnClickListener(v -> {
            textView.setText("能录制的最大时间：" + (AudioConfig._totalTimeSec += 10));
        });


        quality = (EditText) findViewById(R.id.quality);
        kbps = (EditText)findViewById(R.id.kbps);

        quality.setText(FansMp3EncodeThread.DEFAULT_LAME_MP3_QUALITY + "");
        quality.addTextChangedListener(new texchangelistener(0));
        kbps.setText(FansMp3EncodeThread.DEFAULT_LAME_MP3_BIT_RATE + "");
        kbps.addTextChangedListener(new texchangelistener(1));

        byteBufferTest();
    }

    /**
     *
     */
    private class texchangelistener implements TextWatcher{

        private int type;
        public texchangelistener(int text){
            type = text;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            try {
                if (type == 0){
                    FansMp3EncodeThread.DEFAULT_LAME_MP3_QUALITY = Integer.valueOf(quality.getText().toString());
                }else{
                    FansMp3EncodeThread.DEFAULT_LAME_MP3_BIT_RATE = Integer.valueOf(kbps.getText().toString());
                }
            }catch (Exception e){
                e.printStackTrace();
                ToastUtil.showLong(StudyAudioRecord.this,"输入有问题");
            }

        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.testOne:
                startActivity(new Intent(this, AcitivtyWaveTest.class));
                break;
            case R.id.testTwo:
                startActivity(new Intent(this, ActivityWaveDisplayTest.class));
                break;
            case R.id.testTwoDemo:
                startActivity(new Intent(this, AcitivtyWaveTestRecycler.class));
                break;
        }
    }


    /**
     *
     */
    private void byteBufferTest() {
        short[] tesk = new short[]{1,2,3,4,5,6,7,8,9};
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer shot = buffer.asShortBuffer();
        shot.put(tesk);
        for (int i = 0; i < shot.position();i++){
            Log.e("Buffe数据：", "" + shot.get(i));
        }

        /**
         * android 7.0上这个弄出来 大小端的方式没有了
         */
        ShortBuffer tes = shot.asReadOnlyBuffer();
        for (int i = 0; i < tes.position();i++){
            Log.e("Buffe数据tes：", "" + tes.get(i));
        }
    }
}
