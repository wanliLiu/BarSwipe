package com.barswipe.volume;

/**
 * Created by Soli on 2017/1/19.
 */

public class AudioStateMessage {
    public int what;
    public Object obj;

    public AudioStateMessage() {
    }

    public static AudioStateMessage obtain() {
        return new AudioStateMessage();
    }
}
