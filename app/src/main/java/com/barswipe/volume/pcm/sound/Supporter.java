package com.barswipe.volume.pcm.sound;

import java.io.File;

/**
 * Created by changbinhe on 14/11/22.
 */
public class Supporter {
    public static final int SLICE_SECOND = 90;

    /**
     * pcm数据产生
     */
    public static interface PcmConsumer {
        public void onPcmFeed(short[] buffer, int length);
    }

    /**
     * 编码后数据产生
     */
    public static interface AmrConsumer {
        public void onAmrFeed(byte[] buffer, int length);
    }

    public static interface FileConsumer {
        public void onFileFeed(File file);
    }

    public static interface OnOffSwitcher {
        public void start();

        public void stop();
    }
}
