package com.barswipe.dragger2;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Soli on 2017/5/11.
 */
@Module
public class HouseModule {

    /**
     * 指定房子的使用范围
     *
     * @return
     */
    @HouseScope
    @Provides
    public House providerHouse() {
        return new House();
    }
}
