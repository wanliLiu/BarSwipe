package com.barswipe.draweePhotoView.lib;

import android.net.Uri;

import com.barswipe.util.FileUtil;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;

/**
 * Created by Soli on 2016/8/24.
 */
public class MyCacheKeyFactory extends DefaultCacheKeyFactory {

    private static MyCacheKeyFactory sInstance = null;

    protected MyCacheKeyFactory() {
    }

    public static synchronized MyCacheKeyFactory getInstance() {
        if (sInstance == null) {
            sInstance = new MyCacheKeyFactory();
        }
        return sInstance;
    }

    @Override
    protected Uri getCacheKeySourceUri(Uri sourceUri) {
        return Uri.parse(FileUtil.MD5(sourceUri.toString()));
    }
}
