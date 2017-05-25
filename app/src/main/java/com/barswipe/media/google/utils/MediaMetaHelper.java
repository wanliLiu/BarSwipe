package com.barswipe.media.google.utils;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

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
     *
     * @param controller
     * @param bundle
     */
    public static void decidePlayStatus(MediaControllerCompat controller, Bundle bundle) {
        if (controller == null)
            return;

        MediaMetadataCompat current = controller.getMetadata();
        int state = controller.getPlaybackState().getState();
        if (current != null && state != PlaybackStateCompat.STATE_NONE && state != PlaybackStateCompat.STATE_STOPPED &&
                current.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).equals(bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                controller.getTransportControls().pause();
            } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                controller.getTransportControls().play();
            }
        } else {
            if (state == PlaybackStateCompat.STATE_PLAYING ||
                    state == PlaybackStateCompat.STATE_BUFFERING) {
                controller.getTransportControls().stop();
            }
            controller.getTransportControls().playFromMediaId(bundle.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), bundle);
        }
    }


    /**
     * @return
     */
    public static Bundle getData(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(url.hashCode()));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, url);
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "自己录制的音频");
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "录制标题");
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "录制内容");
        return bundle;
    }

    /**
     * @return
     */
    public static Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf("http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3".hashCode()));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3");
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "直接播放网络的适配");
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
