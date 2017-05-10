package com.barswipe.dragger2;

import javax.inject.Inject;

/**
 * Created by Soli on 2017/5/10.
 *
 * https://github.com/tianwenju/LearningTest/blob/master/readme.md
 * http://blog.csdn.net/zjbpku/article/details/42109891
 */

public class Computer {
    @Inject
    Display display;
    @Inject
    keyboard keyboard;
    @Inject
    Master master;
    public Computer() {
        // DaggerComputerComponent.create().inject(this);
        DaggerComputerComponent.builder().computerModule(new ComputerModule("中国","联想")).build().inject(this);
        makeComputer(keyboard, display, master);
    }
    private void makeComputer(keyboard keyboard, Display display, Master master) {
        System.out.println("制造完成");
    }
    public static void main(String[] args) {
        new Computer();
    }
}