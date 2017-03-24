package com.barswipe.volume.wave;

/**
 * Created by Soli on 2017/3/24.
 */

public interface onWaveEditListener {

    /**
     * 编辑过程中是否可以操作，如果选中的额区域全是，就不能编辑
     */
    public void onActionCando(boolean isCan);
}
