/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.danxx.mdplayer.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.application.Settings;
import com.danxx.mdplayer.module.RecentMediaStorage;
import com.danxx.mdplayer.widget.media.CustomMediaController;
import com.danxx.mdplayer.widget.media.IjkVideoView;
import com.danxx.mdplayer.widget.media.MeasureHelper;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class VideoActivity extends AppCompatActivity implements TracksFragment.ITrackHolder {
    private static final String TAG = "VideoActivity";

    private String mVideoPath;
    private Uri    mVideoUri;

//    private AndroidMediaController mMediaController;
    private CustomMediaController customMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;

    private Settings mSettings;
    private boolean mBackPressed;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mSettings = new Settings(this);

        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");

        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    String scheme = mVideoUri.getScheme();
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown ccheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        mVideoPath = mVideoUri.getPath();
                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }

        // init UI
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
//        mMediaController = new AndroidMediaController(this, false);
//        mMediaController.setSupportActionBar(actionBar);
        customMediaController = new CustomMediaController(this, false);
        customMediaController.setSupportActionBar(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mToastTextView = (TextView) findViewById(R.id.toast_text_view);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(customMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (mVideoPath != null)
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null)
            mVideoView.setVideoURI(mVideoUri);
        else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle_ratio) {
            int aspectRatio = mVideoView.toggleAspectRatio();
            String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
            mToastTextView.setText(aspectRatioText);
            customMediaController.showOnce(mToastTextView);
            return true;
        }
        /**隐藏重播选项**/
//        else if (id == R.id.action_toggle_player) {
//            int player = mVideoView.togglePlayer();
//            String playerText = IjkVideoView.getPlayerText(this, player);
//            mToastTextView.setText(playerText);
//            customMediaController.showOnce(mToastTextView);
//            return true;
//        }
        /**隐藏render选项**/
//        else if (id == R.id.action_toggle_render) {
//            int render = mVideoView.toggleRender();
//            String renderText = IjkVideoView.getRenderText(this, render);
//            mToastTextView.setText(renderText);
//            customMediaController.showOnce(mToastTextView);
//            return true;
//        }
        else if (id == R.id.action_show_info) {
            mVideoView.showMediaInfo();
        }
        /**隐藏tracks选项**/
//        else if (id == R.id.action_show_tracks) {
//            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
//                Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
//                if (f != null) {
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.remove(f);
//                    transaction.commit();
//                }
//                mDrawerLayout.closeDrawer(mRightDrawer);
//            } else {
//                Fragment f = TracksFragment.newInstance();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.right_drawer, f);
//                transaction.commit();
//                mDrawerLayout.openDrawer(mRightDrawer);
//            }
//        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        if (mVideoView == null)
            return null;

        return mVideoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (mVideoView == null)
            return -1;

        return mVideoView.getSelectedTrack(trackType);
    }
}
