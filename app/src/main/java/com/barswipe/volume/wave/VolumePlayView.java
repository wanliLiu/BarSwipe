package com.barswipe.volume.wave;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by Soli on 2017/3/28.
 */

public class VolumePlayView extends FrameLayout implements View.OnClickListener, onAudioDetailPlayListener {

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

    private FansAudioPlayer player;

    private onActionTypeListener listener;

    public VolumePlayView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VolumePlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VolumePlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
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
     * 开始播放
     */
    private void startPlay() {
        if (player == null) {
            player = new FansAudioPlayer();
            player.setOnAudioPlayListener(this);
        }
        player.play(volumPath);
    }

    /**
     * 设置音频的时间
     *
     * @param durationStr
     */
    public void setVolumeDuration(String durationStr) {
        audioDuration.setText(durationStr);
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

    @Override
    public void onAudioPlayComplete() {
        wavePlay.onAudioPlayComplete();
        audioPlay.setImageResource(R.mipmap.icon_play);
    }

    @Override
    public void onAudioPlayProgress(double timeMs) {
        wavePlay.updateData();
    }

    @Override
    public void onStartPrepare() {
        audioPlay.setImageResource(R.mipmap.icon_play_stop);
    }

    @Override
    public void onStartPlay() {
        audioPlay.setImageResource(R.mipmap.icon_play_stop);
    }

    @Override
    public void onError(int extra) {

    }

    @Override
    public void onPause() {
        audioPlay.setImageResource(R.mipmap.icon_play);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (player != null) {
            player.release();
        }
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
