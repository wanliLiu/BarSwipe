package com.barswipe.volume.wave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by Soli on 2017/4/10.
 */

public class StudyAudioRecord extends AppCompatActivity {

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
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    checkBox.setText("录制amr");
                else
                    checkBox.setText("录制mp3");
                AudioConfig.recordFormatIsMp3 = isChecked;

            }
        });

        final TextView textView = (TextView) findViewById(R.id.recordTime);
        textView.setText(("能录制的最大时间：" + AudioConfig._totalTimeSec));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("能录制的最大时间：" + (AudioConfig._totalTimeSec += 10));
            }
        });

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
}
