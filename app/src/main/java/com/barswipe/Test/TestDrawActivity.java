package com.barswipe.Test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.BaseActivity;
import com.barswipe.R;

/**
 * Created by soli on 6/26/16.
 */
public class TestDrawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_draw);
    }

    public void sayHello(View v) {
        TextView textView = (TextView) findViewById(R.id.textView);
        EditText editText = (EditText) findViewById(R.id.editText);
        textView.setText("Hello, " + editText.getText().toString() + "!");
        Toast.makeText(this,editText.getText().toString(),Toast.LENGTH_SHORT).show();
        Log.e("内容",editText.getText().toString());
    }
}
