package com.barswipe.fram;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;

import com.barswipe.R;
import com.jakewharton.rxbinding.view.RxView;

import rx.functions.Action1;

public class CustomBehaviorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_behavior);

        RxView.clicks(findViewById(R.id.depentent))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ViewCompat.offsetTopAndBottom(findViewById(R.id.depentent), 15);
                    }
                });
    }
}
