package com.barswipe.media.google.utils;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;

/**
 * Created by Soli on 2017/5/24.
 */

public class MediaMetaHelper {

    /**
     *
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
     *
     * @return
     */
    public static Bundle getdata() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaConstants.MediaMetaDataKey, test());
        return bundle;
    }
}
