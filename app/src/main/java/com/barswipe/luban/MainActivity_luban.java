package com.barswipe.luban;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;
import com.barswipe.luban.library.Luban;
import com.barswipe.luban.library.OnCompressListener;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;

public class MainActivity_luban extends AppCompatActivity {

    private TextView fileSize;
    private TextView imageSize;
    private TextView thumbFileSize;
    private TextView thumbImageSize;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_luban);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fileSize = (TextView) findViewById(R.id.file_size);
        imageSize = (TextView) findViewById(R.id.image_size);
        thumbFileSize = (TextView) findViewById(R.id.thumb_file_size);
        thumbImageSize = (TextView) findViewById(R.id.thumb_image_size);
        image = (ImageView) findViewById(R.id.image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RxView.clicks(fab)
                .throttleFirst(500, TimeUnit.MICROSECONDS)
                .compose(RxPermissions.getInstance(this).ensure(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        PhotoPicker.builder()
                                .setPhotoCount(1)
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .start(MainActivity_luban.this, PhotoPicker.REQUEST_CODE);
                    }
                });
    }

    /**
     * 压缩单张图片 Listener 方式
     */
    private void compressWithLs(File file) {
        Luban.get(this)
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity_luban.this, "I'm start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(File file) {
                        Glide.with(MainActivity_luban.this).load(file).into(image);

                        thumbFileSize.setText(file.length() / 1024 + "k");
                        thumbImageSize.setText(Luban.get(getApplicationContext()).getImageSize(file.getPath())[0] + " * " + Luban.get(getApplicationContext()).getImageSize(file.getPath())[1]);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    /**
     * 压缩单张图片 RxJava 方式
     */
    private void compressWithRx(File file) {
        Luban.get(this)
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> throwable.printStackTrace())
                .onErrorResumeNext(Observable.empty())
                .subscribe(file1 -> {
                    Glide.with(MainActivity_luban.this).load(file).into(image);

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    MainActivity_luban.this.sendBroadcast(intent);

                    thumbFileSize.setText(file.length() / 1024 + "k");
                    thumbImageSize.setText(Luban.get(getApplicationContext()).getImageSize(file.getPath())[0] + " * " + Luban.get(getApplicationContext()).getImageSize(file.getPath())[1]);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                File imgFile = new File(photos.get(0));
                fileSize.setText(imgFile.length() / 1024 + "k");
                imageSize.setText(Luban.get(this).getImageSize(imgFile.getPath())[0] + " * " + Luban.get(this).getImageSize(imgFile.getPath())[1]);

                compressWithLs(new File(photos.get(0)));
            }
        }
    }
}
