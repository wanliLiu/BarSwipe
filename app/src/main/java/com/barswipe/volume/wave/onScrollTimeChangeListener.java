package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/3/23.
 */

public interface onScrollTimeChangeListener {

    /**
     * Will be called by the SoundFile class periodically
     * with values between 0.0 and 1.0.  Return true to continue
     * loading the file or recording the audio, and false to cancel or stop recording.
     */
    public void onScrollTimeChange(double fractionComplete, String time);
}
