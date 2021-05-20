package com.barswipe.media;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.barswipe.volume.wave.AcitivtyWaveTestRecycler;
import com.jakewharton.rxbinding2.view.RxView;

/**
 * Created by Soli on 2017/5/22.
 */

public class SupportMediaTest extends BaseActivity {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

//    private MediaBrowserCompat mMediaBrowserCompat;
//    private MediaControllerCompat mMediaControllerCompat;
//
//    private Button mPlayPauseToggleButton;

//    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
//
//        @Override
//        public void onConnected() {
//            super.onConnected();
//            try {
//                mMediaControllerCompat = new MediaControllerCompat(SupportMediaTest.this, mMediaBrowserCompat.getSessionToken());
//                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
//                setSupportMediaController(mMediaControllerCompat);
//                getSupportMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);
//
//            } catch (RemoteException e) {
//
//            }
//        }
//    };
//
//    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {
//
//        @Override
//        public void onPlaybackStateChanged(PlaybackStateCompat state) {
//            super.onPlaybackStateChanged(state);
//            if (state == null) {
//                return;
//            }
//
//            switch (state.getState()) {
//                case PlaybackStateCompat.STATE_PLAYING: {
//                    mCurrentState = STATE_PLAYING;
//                    break;
//                }
//                case PlaybackStateCompat.STATE_PAUSED: {
//                    mCurrentState = STATE_PAUSED;
//                    break;
//                }
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_media);

//        mPlayPauseToggleButton = (Button) findViewById(R.id.button);
//
//        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
//                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());
//
//        mMediaBrowserCompat.connect();
//
//        mPlayPauseToggleButton.setOnClickListener(view -> {
//            if (mCurrentState == STATE_PAUSED) {
//                getSupportMediaController().getTransportControls().play();
//                mCurrentState = STATE_PLAYING;
//            } else {
//                if (getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    getSupportMediaController().getTransportControls().pause();
//                }
//
//                mCurrentState = STATE_PAUSED;
//            }
//        });

//        RxView.clicks(findViewById(R.id.btnEffic))
//                .subscribe(o -> MediaMetaHelper.decidePlayStatus(getSupportMediaController(), MediaMetaHelper.getData(),null));
        RxView.clicks(findViewById(R.id.notify1))
                .subscribe(o -> showNotification());
        RxView.clicks(findViewById(R.id.notify2))
                .subscribe(o -> showNotification1());
        RxView.clicks(findViewById(R.id.notify3))
                .subscribe(o -> showNotification2());
    }

    /**
     *
     */
    private void showNotification2() {
        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher);

//启动一个线程，定时更新进度条
        new Thread(
                () -> {
                    int incr;
                    // Do the "lengthy" operation 20 times
                    for (incr = 0; incr <= 100; incr += 5) {
                        // Sets the progress indicator to a max value, the
                        // current completion percentage, and "determinate"
                        // state
                        mBuilder.setProgress(100, incr, false);
                        // Displays the progress bar for the first time.
                        mNotificationManager.notify(0, mBuilder.build());
                        // Sleeps the thread, simulating an operation
                        // that takes time
                        try {
                            // Sleep for 5 seconds
                            Thread.sleep(1 * 1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    // 下载完成，更新信息
                    mBuilder.setContentText("Download complete")
                            // 移除进度条
                            .setProgress(0, 0, false);
                    mNotificationManager.notify(0, mBuilder.build());
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    /**
     *
     */
    private void showNotification1() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event tracker")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[]{"skkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk", "skkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk", "skkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk", "skkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk", "skkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk", "skkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"};
// Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
// Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }
// Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, mBuilder.build());
    }

    /**
     *
     */
    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
//设置点击通知跳转的activity
        Intent resultIntent = new Intent(this, AcitivtyWaveTestRecycler.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//创建一个任务栈，用于处理在通知页面，返回时现实的页面
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SupportMediaTest.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setFullScreenIntent(resultPendingIntent, true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final Notification notification = mBuilder.build();

//这通知的其他属性，比如：声音和振动
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(1, notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
//            getSupportMediaController().getTransportControls().pause();
//        }
//
//        mMediaBrowserCompat.disconnect();
    }
}
