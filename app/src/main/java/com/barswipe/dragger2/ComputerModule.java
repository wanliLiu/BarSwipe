package com.barswipe.dragger2;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Soli on 2017/5/10.
 */

@Module
public class ComputerModule {

    private String from;

    private String taste;

    @Provides
    public Display providerDisplay() {
        return new Display();
    }

    @Provides
    public String providerString() {
        return from;
    }

    @Named("taste")
    @Provides
    public String providerStringtaste() {
        return taste;
    }

    public ComputerModule() {
    }

    public ComputerModule(String from, String taste) {
        this.from = from;
        this.taste = taste;
    }

    @Provides
    public keyboard providerkeyboardFrom(String from) {
        return new keyboard(from);
    }
}
