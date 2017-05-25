package com.barswipe.media.google.utils;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;

/**
 * Created by Soli on 2017/5/24.
 */

public class MediaMetaHelper {

    /**
     * @return
     */
    public static MediaMetadataCompat test() {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf("Jazz in Paris".hashCode()))
//                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 103)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Jazz in Paris")
                .build();
    }

    /**
     * @return
     */
    public static Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf("Jazz in Paris".hashCode()));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3");
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Jazz in Paris");
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "我是我");
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "我是描述");
        return bundle;
    }

    /**
     * @param bundle
     * @return
     */
    public static MediaMetadataCompat getMeta(Bundle bundle) {
        if (bundle == null)
            return new MediaMetadataCompat.Builder().build();
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, bundle.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, bundle.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, bundle.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION))
                .build();
    }
}
