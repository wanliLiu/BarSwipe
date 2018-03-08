package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.barswipe.R;
import com.barswipe.media.google.utils.MediaMetaHelper;

/**
 * Created by Soli on 2017/3/28.
 */

public class MediaVolumePlayView extends FrameLayout implements View.OnClickListener {

    /**
     * 右边操作区域类型
     */
    public static final int actionType_delete = 0;//删除
    public static final int actionType_record = 1;//重录

    private String[] actionStr = new String[]{"删除", "重录"};
    private int[] actionIcon = new int[]{R.mipmap.icon_delete_voice, R.mipmap.icon_re_record};

    private int currentAction = -1;

    private String volumPath;

    private WavePlayView wavePlay;
    private View actionViewTop, actionViewBottom;
    private TextView audioDuration, actionText;
    private ImageView audioPlay;

    private MediaControllerCompat mMediaController;
    private playbackCallback callback;

    private onActionTypeListener listener;

    public MediaVolumePlayView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MediaVolumePlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaVolumePlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * @param ctx
     */
    private void init(Context ctx) {
        View.inflate(ctx, R.layout.item_volume_play, this);
        wavePlay = (WavePlayView) findViewById(R.id.wavePlay);
        actionViewTop = findViewById(R.id.actionViewTop);
        actionViewBottom = findViewById(R.id.actionViewBottom);

        audioPlay = (ImageView) findViewById(R.id.audioPlay);
        audioPlay.setOnClickListener(this);

        audioDuration = (TextView) findViewById(R.id.audioDuration);
        actionText = (TextView) findViewById(R.id.actionText);
        actionText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.audioPlay:
                if (!TextUtils.isEmpty(volumPath))
                    startPlay();
                break;
            case R.id.actionText:
                if (listener != null && currentAction != -1)
                    listener.onActionType(currentAction);
                break;
        }
    }

    /**
     * @param type
     */
    public void showActionView(int type) {
        if (type > actionStr.length)
            throw new IllegalArgumentException("操作类型未定义");

        currentAction = type;
        actionText.setText(actionStr[currentAction]);
        Drawable drawable = getContext().getResources().getDrawable(actionIcon[currentAction]);
        actionText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        actionViewTop.setVisibility(INVISIBLE);
        actionViewBottom.setVisibility(VISIBLE);
    }

    /**
     * 显示删除视图
     */
    public void showViewInDelete() {
        showActionView(actionType_delete);
    }

    /**
     * 显示重录按钮
     */
    public void showViewRerecord() {
        showActionView(actionType_record);
    }

    /**
     * 获取媒体控制器
     *
     * @return
     */
    private MediaControllerCompat getMediaController() {
        if (mMediaController == null) {
//            mMediaController = ((AppCompatActivity) getContext()).getSupportMediaController();
            callback = new playbackCallback();
        }

        if (mMediaController == null)
            throw new IllegalArgumentException("MediaControllerCompat cannot be null");

        return mMediaController;
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        MediaMetaHelper.decidePlayStatus(getMediaController(), MediaMetaHelper.getData(volumPath), callback);
    }

    /**
     * 设置音频的时间
     *
     * @param durationStr
     */
    public void setVolumeDuration(String durationStr) {
        audioDuration.setText(durationStr);
        audioDuration.setTag(audioDuration.getText().toString());
    }

    /**
     * 设置播放需要的数据
     *
     * @param path     可以是网络url,也可以是本地文件地址
     * @param waveData 音频数据
     */
    public void setData(String path, String waveData) {
        this.volumPath = path;
        wavePlay.setWaveData(waveData);
    }

    /**
     * 播放器回调
     */
    private class playbackCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_STOPPED:
                    wavePlay.onAudioPlayComplete();
                    audioPlay.setImageResource(R.mipmap.icon_play);
                    getHandler().post(() -> audioDuration.setText(audioDuration.getTag().toString()));
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    audioPlay.setImageResource(R.mipmap.icon_play);
                    break;
                case PlaybackStateCompat.STATE_BUFFERING:
                case PlaybackStateCompat.STATE_PLAYING:
                    audioPlay.setImageResource(R.mipmap.icon_play_stop);
                    break;
            }
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            wavePlay.updateData();
            getHandler().post(() -> {
                int timeSec = (int) (Double.valueOf(event) / 1000.f);
                audioDuration.setText(timeSec + "''");
            });
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMediaController != null)
            mMediaController.unregisterCallback(callback);
    }

    /**
     * @param mlistener
     */
    public void setonActionTypeListener(onActionTypeListener mlistener) {
        listener = mlistener;
    }

    /**
     * 右边视图点击回调
     */
    public interface onActionTypeListener {
        /**
         * @param type
         * @link actionType_delete
         * @link actionType_record
         */
        public void onActionType(int type);
    }
}
