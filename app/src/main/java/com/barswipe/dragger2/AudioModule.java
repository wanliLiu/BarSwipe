package com.barswipe.dragger2;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Soli on 2017/5/11.
 */

@Module
public class AudioModule {
    @Provides
    public Audio providerAudio() {
        return new Audio();
    }

}
