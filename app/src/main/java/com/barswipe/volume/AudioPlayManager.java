package com.barswipe.volume;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by Soli on 2017/1/19.
 */

public class AudioPlayManager implements SensorEventListener {
    private static final String TAG = "AudioPlayManager";
    private MediaPlayer _mediaPlayer;
    private AudioPlayListener _playListener;
    private Uri _playingUri;
    private Sensor _sensor;
    private SensorManager _sensorManager;
    private AudioManager _audioManager;
    private PowerManager _powerManager;
    private PowerManager.WakeLock _wakeLock;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private Context context;

    public AudioPlayManager() {
    }

    public static AudioPlayManager getInstance() {
        return AudioPlayManager.SingletonHolder.sInstance;
    }

    @TargetApi(11)
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (_sensor != null && _mediaPlayer != null) {
            if (_mediaPlayer.isPlaying()) {
                if ((double) range > 0.0D) {
                    _audioManager.setMode(AudioManager.MODE_NORMAL);
                    _audioManager.setSpeakerphoneOn(true);
                    final int positions = _mediaPlayer.getCurrentPosition();

                    try {
                        _mediaPlayer.reset();
                        _mediaPlayer.setAudioStreamType(STREAM_MUSIC);
                        _mediaPlayer.setVolume(1.0F, 1.0F);
                        _mediaPlayer.setDataSource(context, _playingUri);
                        _mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mp) {
                                mp.seekTo(positions);
                            }
                        });
                        _mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                            public void onSeekComplete(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        _mediaPlayer.prepareAsync();
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                    setScreenOn();
                } else {
                    setScreenOff();
                    if (Build.VERSION.SDK_INT >= 11) {
                        _audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    } else {
                        _audioManager.setMode(AudioManager.MODE_IN_CALL);
                    }

                    _audioManager.setSpeakerphoneOn(false);
                    replay();
                }
            } else if ((double) range > 0.0D) {
                _audioManager.setMode(AudioManager.MODE_NORMAL);
                _audioManager.setSpeakerphoneOn(true);
                setScreenOn();
            }

        }
    }

    @TargetApi(21)
    private void setScreenOff() {
        if (_wakeLock == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                _wakeLock = _powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "AudioPlayManager");
            } else {
                Log.e(TAG, "Does not support on level " + Build.VERSION.SDK_INT);
            }
        }

        if (_wakeLock != null) {
            _wakeLock.acquire();
        }
    }

    /**
     *
     */
    private void setScreenOn() {
        if (_wakeLock != null) {
            _wakeLock.setReferenceCounted(false);
            _wakeLock.release();
            _wakeLock = null;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void replay() {
        try {
            _mediaPlayer.reset();
            _mediaPlayer.setAudioStreamType(0);
            _mediaPlayer.setVolume(1.0F, 1.0F);
            _mediaPlayer.setDataSource(context, _playingUri);
            _mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            _mediaPlayer.prepareAsync();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    /**
     *
     * @param context
     * @param audioUri
     * @param playListener
     */
    public void startPlay(Context context, Uri audioUri, AudioPlayListener playListener) {
        if (context != null && audioUri != null) {
            this.context = context;
            if (_playListener != null && _playingUri != null) {
                _playListener.onStop(_playingUri);
            }

            resetMediaPlayer();
            afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    Log.d(TAG, "OnAudioFocusChangeListener " + focusChange);
                    if (_audioManager != null && focusChange == -1) {
                        _audioManager.abandonAudioFocus(afChangeListener);
                        afChangeListener = null;
                        resetMediaPlayer();
                    }

                }
            };

            try {
                _powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                _audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (!_audioManager.isWiredHeadsetOn()) {
                    _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                    _sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    _sensorManager.registerListener(this, _sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }

                muteAudioFocus(_audioManager, true);
                _playListener = playListener;
                _playingUri = audioUri;
                _mediaPlayer = new MediaPlayer();
                _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        if (_playListener != null) {
                            _playListener.onComplete(_playingUri);
                            _playListener = null;
                        }

                        reset();
                    }
                });
                _mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        reset();
                        return true;
                    }
                });
                _mediaPlayer.setDataSource(context, audioUri);
                _mediaPlayer.setAudioStreamType(STREAM_MUSIC);
                _mediaPlayer.prepare();
                _mediaPlayer.start();
                if (_playListener != null) {
                    _playListener.onStart(_playingUri);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
                if (_playListener != null) {
                    _playListener.onStop(audioUri);
                    _playListener = null;
                }

                reset();
            }

        } else {
            Log.e(TAG, "startPlay context or audioUri is null.");
        }
    }

    public void setPlayListener(AudioPlayListener listener) {
        _playListener = listener;
    }

    /**
     *
     */
    public void stopPlay() {
        if (_playListener != null && _playingUri != null) {
            _playListener.onStop(_playingUri);
        }

        reset();
    }

    /**
     *
     */
    private void reset() {
        resetMediaPlayer();
        resetAudioPlayManager();
    }

    private void resetAudioPlayManager() {
        if (_audioManager != null) {
            muteAudioFocus(_audioManager, false);
        }

        if (_sensorManager != null) {
            _sensorManager.unregisterListener(this);
        }

        _sensorManager = null;
        _sensor = null;
        _powerManager = null;
        _audioManager = null;
        _wakeLock = null;
        _playListener = null;
        _playingUri = null;
    }

    /**
     *
     */
    private void resetMediaPlayer() {
        if (_mediaPlayer != null) {
            try {
                _mediaPlayer.stop();
                _mediaPlayer.reset();
                _mediaPlayer.release();
                _mediaPlayer = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    public Uri getPlayingUri() {
        return _playingUri;
    }

    @TargetApi(8)
    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if (Build.VERSION.SDK_INT < 8) {
            Log.d(TAG, "muteAudioFocus Android 2.1 and below can not stop music");
        } else {
            if (bMute) {
                audioManager.requestAudioFocus(afChangeListener, STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            } else {
                audioManager.abandonAudioFocus(afChangeListener);
                afChangeListener = null;
            }

        }
    }

    static class SingletonHolder {
        static AudioPlayManager sInstance = new AudioPlayManager();

        SingletonHolder() {
        }
    }
}
