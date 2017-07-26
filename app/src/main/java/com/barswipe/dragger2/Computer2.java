package com.barswipe.dragger2;

import javax.inject.Inject;

/**
 * Created by Soli on 2017/5/11.
 */

public class Computer2 {
    @Inject
    Audio audio;
    @Inject
    keyboard keyboard;
    @Inject
    Display display;
    @Inject
    Master master;

    public Computer2() {
        ComputerComponent component =  DaggerComputerComponent.builder().computerModule(new ComputerModule("中国","台达")).build();
        DaggerAudioComponent.builder().computerComponent(component).audioModule(new AudioModule()).build().inject(this);

    }

    public static void main(String[] args) {
        new Computer2();
    }
}
