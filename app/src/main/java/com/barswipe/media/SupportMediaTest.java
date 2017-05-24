package com.barswipe.media;

import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.barswipe.media.google.utils.MediaMetaHelper;
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

        RxView.clicks(findViewById(R.id.btnEffic))
                .subscribe(o -> {
                    int statest = mMediaController.getPlaybackState().getState();
                    switch (statest) {
                        case PlaybackStateCompat.STATE_NONE:
                            mMediaController.getTransportControls().playFromMediaId("sds", MediaMetaHelper.getdata());
                            break;
                        case PlaybackStateCompat.STATE_PLAYING:
                            mMediaController.getTransportControls().pause();
                            break;
                        case PlaybackStateCompat.STATE_PAUSED:
                            mMediaController.getTransportControls().play();
                            break;
                    }
                });
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
