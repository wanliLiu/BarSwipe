package com.barswipe.volume.wave;

/**
 * Created by soli on 22/03/2017.
 */

public interface onEncodeCompleteListener {
    /**
     * 编码完成 返回文件地址
     * @param filepath
     */
    public void onEncodeComplete(String filepath);
}
