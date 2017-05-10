package com.barswipe.dragger2;

import dagger.Component;

/**
 * Created by Soli on 2017/5/10.
 */

@Component(modules = {ComputerModule.class})//指明要在那些Module里寻找依赖
public interface ComputerComponent {
    //注意：下面这三个方法，返回值必须是从上面指定的依赖库SaladModule.class中取得的对象
    //注意：而方法名不一致也行，但是方便阅读，建议一致，因为它主要是根据返回值类型来找依赖的
    //★注意：下面这三个方法也可以不写，但是如果要写，就按照这个格式来
    //但是当Component要被别的Component依赖时，
    //这里就必须写这个方法，不写代表不向别的Component暴露此依赖
    Display sprovideDisplay();

    keyboard ProvidekeyBoard();

    Master provideMaster();
    //注意：下面的这个方法，表示要将以上的三个依赖注入到某个类中
    //这里我们把上面的三个依赖注入到Computer中
    //因为我们要做沙拉
    void inject(Computer computer);
}
