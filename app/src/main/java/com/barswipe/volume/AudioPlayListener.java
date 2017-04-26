package com.barswipe.volume;

import android.net.Uri;

/**
 * Created by Soli on 2017/1/19.
 */

public interface AudioPlayListener {
    void onStart(Uri var1);

    void onStop(Uri var1);

    void onComplete(Uri var1);
}
