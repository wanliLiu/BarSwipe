package com.barswipe.volume.wave;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.barswipe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shuyu on 2016/11/15.
 * 声音波形，录制与播放
 */
public class MainActivity_wave extends AppCompatActivity {

    @BindView(R.id.main_frameLayout)
    FrameLayout mainFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wave);
        ButterKnife.bind(this);

        MainFragment newFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frameLayout, newFragment);
//        transaction.addToBackStack(null);
        transaction.commit();

    }

}
