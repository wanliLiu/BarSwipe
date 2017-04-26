package com.barswipe.volume;

/**
 * Created by Soli on 2017/1/19.
 */

public abstract class AudioState {
    public AudioState() {
    }

    void enter() {
    }

    abstract void handleMessage(AudioStateMessage var1);
}