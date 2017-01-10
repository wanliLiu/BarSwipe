package com.barswipe.draweePhotoView.lib;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Soli on 2016/8/17.
 */
public class FrescoUtil {

    private static final String IMAGE_PIPELINE_CACHE_DIR = "fresco_image_cache";

    private static ImagePipelineConfig sImagePipelineConfig;

    /**
     * @param ctx
     */
    public static void Init(Context ctx) {
        Fresco.initialize(ctx, getImagePipelineConfig(ctx));
    }

    /**
     * Creates config using android http stack as network backend.
     */
    private static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            configureCaches(configBuilder, context);
            configureLoggingListeners(configBuilder);
            configureOptions(configBuilder);
            sImagePipelineConfig = configBuilder.build();

            NoOpMemoryTrimmableRegistry.getInstance().registerMemoryTrimmable(new MemoryTrimmable() {
                @Override
                public void trim(MemoryTrimType trimType) {
                    final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                    if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                            || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                            || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                            ) {
                        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                    }
                }
            });
        }
        return sImagePipelineConfig;
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        configBuilder.setBitmapMemoryCacheParamsSupplier(
                new Supplier<MemoryCacheParams>() {
                    public MemoryCacheParams get() {
                        return bitmapCacheParams;
                    }
                })
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder(context)
                                .setBaseDirectoryPath(context.getCacheDir())
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)
                                .build());
    }

    /**
     * @param configBuilder
     */
    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        configBuilder.setRequestListeners(requestListeners);
    }

    /**
     * @param configBuilder
     */
    private static void configureOptions(ImagePipelineConfig.Builder configBuilder) {
//        configBuilder.setDownsampleEnabled(true);
        configBuilder.setCacheKeyFactory(new MyCacheKeyFactory());
    }

    /**
     * 查找一个bitmap是否被缓存
     * 存在memory 或 diskcache，就算有
     *
     * @param url
     */
    public static boolean isUrlExistInCache(String url) {
        if (!TextUtils.isEmpty(url)) {
            boolean inMemoryCache = Fresco.getImagePipeline().isInBitmapMemoryCache(Uri.parse(url));
            if (inMemoryCache) {//如果内存中有，就返回
                return true;
            } else {//内存中没有，查询磁盘缓存是否有
                File file = getFrescoCacheFile(url);
                return (file != null && file.exists()) ? true : false;
            }
        }
        return false;
    }

    /**
     * 获取fresco 缓存的文件
     *
     * @param loadUri
     * @return
     */
    public static File getFrescoCacheFile(String loadUri) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(loadUri));
        imageRequestBuilder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.DISK_CACHE);
        CacheKey cacheKey = MyCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequestBuilder.build(), null);
        BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
        if (resource != null) {
            return ((FileBinaryResource) resource).getFile();
        }
        return null;
    }
}
