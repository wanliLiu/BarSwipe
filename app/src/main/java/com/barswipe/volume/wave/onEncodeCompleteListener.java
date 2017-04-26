package com.barswipe.volume.wave;

/**
 * Created by soli on 22/03/2017.
 */

public interface onEncodeCompleteListener {
    /**
     * 编码完成 返回相关后面需要用到测参数
     *
     * @param filepath 音乐文件保存到本地地址
     * @param waveData 波形数据，逗号分开
     */
    public void onEncodeComplete(String filepath, String waveData);
}
