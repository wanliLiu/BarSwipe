package com.barswipe.volume;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by chenjiahuan on 16/5/8.
 */
public class MediaPlayerUtil {

    private static int last;

    public static void startPlaying(String filePath) {
        try {
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setPlaybackParams();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();//异步缓冲
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.e("MediaPlayerUtil", "onBufferingUpdate+" + percent);
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    mediaPlayer.seekTo(12746);
                    mediaPlayer.start();//开始或恢复播放
                    Log.e("MediaPlayerUtil", "onPrepared");
//                    Timer timer = new Timer();
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            int temp = mediaPlayer.getCurrentPosition();
//                            Log.e("MediaPlayerUtil", "playback_position+" + (temp - last));
//                            last = temp;
//                        }
//                    }, 10, 83);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e("MediaPlayerUtil", "onCompletion");
                }
            });
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.e("MediaPlayerUtil", "onSeekComplete");
//                    mediaPlayer.start();//开始或恢复播放
                }
            });
//            mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
//                @Override
//                public void onTimedText(MediaPlayer mp, TimedText text) {
//                    Log.e("MediaPlayerUtil", "onTimedText+" + text.getText());
//                }
//            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("MediaPlayerUtil", "onError+");
                    return false;
                }
            });

        } catch (Exception e) {
        }
    }

}
