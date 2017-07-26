package com.barswipe.net;

/**
 * Created by Soli on 2017/6/1.
 */

public interface RequestCallBack {

    /**
     * 网络请求错误回调
     *
     * @param errorCode
     */
    void onError(int errorCode);

    /**
     * 网络请求成功
     *
     * @param result
     */
    void onComplete(String result);

    /**
     * 网络请求进度
     *
     * @param progress
     */
    void onProgress(int progress);

    /**
     * 取消网络请求
     *
     * @param var1
     */
    void onCanceled(Object var1);
}
