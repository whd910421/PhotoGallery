package com.arirus.photogallery;

import android.os.HandlerThread;

/**
 * Created by whd910421 on 16/10/14.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    private Boolean mHasQuit = false;

    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url)
    {
        arirusLog.get().ShowLog(TAG, "Got a Url:"+ url);
    }


}
