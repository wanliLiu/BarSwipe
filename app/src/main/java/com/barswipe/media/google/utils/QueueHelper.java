/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barswipe.media.google.utils;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to help on queue related tasks.
 */
public class QueueHelper {

    /**
     * @param item
     * @return
     */
    public static MediaMetadataCompat converTo(MediaSessionCompat.QueueItem item) {
        if (item == null)
            return new MediaMetadataCompat.Builder().build();

        MediaDescriptionCompat desc = item.getDescription();
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, desc.getMediaId())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, desc.getMediaUri().toString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, desc.getTitle().toString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, desc.getSubtitle().toString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, desc.getDescription().toString())
                .build();
    }

    /**
     * @param metadata
     * @return
     */
    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(MediaMetadataCompat metadata) {
        int count = 0;
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        // We don't expect queues to change after created, so we use the item index as the
        // queueId. Any other number unique in the queue would work.
        MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(metadata.getDescription(), count++);
        queue.add(item);
        return queue;
    }

    /**
     * @param queue
     * @param mediaId
     * @return
     */
    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           String mediaId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * @param queue
     * @param queueId
     * @return
     */
    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           long queueId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (queueId == item.getQueueId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * @param index
     * @param queue
     * @return
     */
    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }

}
