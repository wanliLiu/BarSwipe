package com.barswipe.dragger2;

import dagger.Component;

/**
 * Created by Soli on 2017/5/11.
 */

@HouseScope
@Component(modules = {HouseModule.class})
public interface HouseComponent {
    void inject(Tom tom);

    void inject(Jason jason);
}
