package com.soli.jnistudy;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by soli on 16/04/2017.
 */

public class JniTestActivity extends AppCompatActivity {

    private RxPermissions permissions;
    private final String TAG = "JniTest";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_jnitest);

        TextView stringFrom = (TextView) findViewById(R.id.stringFrom);
        stringFrom.setText(JniTest.getStringFromJni());

        permissions = new RxPermissions(this);
        permissions.setLogging(true);

        newRxpermissionTest();
        studyRxjava2();
    }

    /**
     *
     */
    private void newRxpermissionTest() {

        RxView.clicks(findViewById(R.id.rxPermissionTest))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Toast.makeText(JniTestActivity.this, "执行成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        Toast.makeText(JniTestActivity.this, "执行成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(JniTestActivity.this, "执行成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(JniTestActivity.this, "执行成功", Toast.LENGTH_LONG).show();
                    }
                });


//        RxView.clicks(findViewById(R.id.rxPermissionTest))
//                .compose(permissions.ensure(Manifest.permission.RECORD_AUDIO))
//                .subscribe(isPass -> {
//                    if (isPass)
//                        Toast.makeText(this, "执行成功", Toast.LENGTH_LONG).show();
//                    else
//                        Toast.makeText(this, "执行失败", Toast.LENGTH_LONG).show();
//                });

//
//        RxView.clicks(findViewById(R.id.rxPermissionTest))
//                .subscribe(v -> permissions.request(Manifest.permission.RECORD_AUDIO)
//                        .subscribe(aBoolean -> {
//                            if (aBoolean)
//                                Toast.makeText(this, "执行成功", Toast.LENGTH_LONG).show();
//                            else
//                                Toast.makeText(this, "执行失败", Toast.LENGTH_LONG).show();
//                        }));
    }

    /**
     *
     */
    private void studyRxjava2() {
//
        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            if (!e.isDisposed()) {
                e.onNext(1);
                e.onNext(2);
                e.onComplete();
                e.onNext(2);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "complete");
            }
        });

        Flowable.just("Hello world")
                .subscribe(str -> Log.e(TAG, str));

        Flowable.range(1,10)
                .observeOn(Schedulers.computation())
                .map(v -> v*v)
                .blockingSubscribe();
    }
}
