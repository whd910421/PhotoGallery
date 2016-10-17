package com.arirus.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by whd910421 on 16/10/14.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Boolean mHasQuit = false;
    private Handler mRequestHandler = null;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler = null;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>
    {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener)
    {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader( Handler responseHandler ) {
        super(TAG);
        mResponseHandler =responseHandler;
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url)
    {
        arirusLog.get().ShowLog(TAG, "Got a Url:"+ url);
        if (url == null)
            mRequestMap.remove(target);
        else
        {
            mRequestMap.put(target,url);
            if (mRequestHandler!=null)
                mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
            else
                arirusLog.get().ShowLog(TAG,"HAndler is empty");
        }
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD)
                {
                    T Target = (T)msg.obj;
                    arirusLog.get().ShowLog(TAG, "Got a request for URL " + mRequestMap.get(Target));
                    handleRequest(Target);
                }
            }
        };
    }

    private void handleRequest(final T target)
    {
        try {
            final String url = mRequestMap.get(target);
            if (url == null)
            {
                return;
            }

            byte[] bitmapBytes = FlickrFetchr.getInstance().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            arirusLog.get().ShowLog(TAG, "BitMap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(target) != url || mHasQuit)
                    {return;}
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe)
        {
            arirusLog.get().ShowLog(TAG, "Error Downloading image", ioe.toString());
        }
    }

    public void clearQuene()
    {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
