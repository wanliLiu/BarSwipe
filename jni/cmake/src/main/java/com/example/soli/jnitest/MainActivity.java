package com.example.soli.jnitest;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.soli.jnitest.databinding.TestDataBinding;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private TestDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnProtocol.setOnClickListener(this);
        binding.btnCodec.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);
        binding.btnFormat.setOnClickListener(this);
        binding.setFirstName("我是来自特使");

        stringFromJNI();
        testJni.getString();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_protocol:
                binding.tvInfo.setText(urlprotocolinfo());
                break;
            case R.id.btn_format:
                binding.tvInfo.setText(avformatinfo());
                break;
            case R.id.btn_codec:
                binding.tvInfo.setText(avcodecinfo());
                break;
            case R.id.btn_filter:
                binding.tvInfo.setText(avfilterinfo());
                break;
            default:
                break;
        }
    }

    public native String urlprotocolinfo();

    public native String avformatinfo();

    public native String avcodecinfo();

    public native String avfilterinfo();

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
