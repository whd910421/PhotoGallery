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
//                handleRequest(target); 这样写其实还是在主线程上调用堆栈信息如下:
//            FATAL EXCEPTION: main
//            Process: com.arirus.photogallery, PID: 7580
//            android.os.NetworkOnMainThreadException
//            at android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork(StrictMode.java:1273)
//            at java.net.InetAddress.lookupHostByName(InetAddress.java:431)
//            at java.net.InetAddress.getAllByNameImpl(InetAddress.java:252)
//            at java.net.InetAddress.getAllByName(InetAddress.java:215)
//            at com.android.okhttp.internal.Network$1.resolveInetAddresses(Network.java:29)
//            at com.android.okhttp.internal.http.RouteSelector.resetNextInetSocketAddress(RouteSelector.java:188)
//            at com.android.okhttp.internal.http.RouteSelector.nextProxy(RouteSelector.java:157)
//            at com.android.okhttp.internal.http.RouteSelector.next(RouteSelector.java:100)
//            at com.android.okhttp.internal.http.HttpEngine.createNextConnection(HttpEngine.java:358)
//            at com.android.okhttp.internal.http.HttpEngine.nextConnection(HttpEngine.java:341)
//            at com.android.okhttp.internal.http.HttpEngine.connect(HttpEngine.java:331)
//            at com.android.okhttp.internal.http.HttpEngine.sendRequest(HttpEngine.java:249)
//            at com.android.okhttp.internal.huc.HttpURLConnectionImpl.execute(HttpURLConnectionImpl.java:437)
//            at com.android.okhttp.internal.huc.HttpURLConnectionImpl.getResponse(HttpURLConnectionImpl.java:388)
//            at com.android.okhttp.internal.huc.HttpURLConnectionImpl.getInputStream(HttpURLConnectionImpl.java:231)
//            at com.android.okhttp.internal.huc.DelegatingHttpsURLConnection.getInputStream(DelegatingHttpsURLConnection.java:210)
//            at com.android.okhttp.internal.huc.HttpsURLConnectionImpl.getInputStream(HttpsURLConnectionImpl.java)

//@           at com.arirus.photogallery.FlickrFetchr.getUrlBytes(FlickrFetchr.java:48)
//@           at com.arirus.photogallery.ThumbnailDownloader.handleRequest(ThumbnailDownloader.java:90)
//@           at com.arirus.photogallery.ThumbnailDownloader.queueThumbnail(ThumbnailDownloader.java:59)
//@           at com.arirus.photogallery.PhotoGalleryFragment$PhotoAdpter.onBindViewHolder(PhotoGalleryFragment.java:138)
//@           at com.arirus.photogallery.PhotoGalleryFragment$PhotoAdpter.onBindViewHolder(PhotoGalleryFragment.java:118)

//            at android.support.v7.widget.RecyclerView$Adapter.onBindViewHolder(RecyclerView.java:5822)
//            at android.support.v7.widget.RecyclerView$Adapter.bindViewHolder(RecyclerView.java:5855)
//            at android.support.v7.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:5091)
//            at android.support.v7.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:4967)
//            at android.support.v7.widget.LinearLayoutManager$LayoutState.next(LinearLayoutManager.java:2029)
//            at android.support.v7.widget.GridLayoutManager.layoutChunk(GridLayoutManager.java:541)
//            at android.support.v7.widget.LinearLayoutManager.fill(LinearLayoutManager.java:1377)
//            at android.support.v7.widget.LinearLayoutManager.onLayoutChildren(LinearLayoutManager.java:578)
//            at android.support.v7.widget.GridLayoutManager.onLayoutChildren(GridLayoutManager.java:170)
//            at android.support.v7.widget.RecyclerView.dispatchLayoutStep2(RecyclerView.java:3315)
//            at android.support.v7.widget.RecyclerView.dispatchLayout(RecyclerView.java:3124)
//            at android.support.v7.widget.RecyclerView.onLayout(RecyclerView.java:3568)
//            at android.view.View.layout(View.java:16647)
//            at android.view.ViewGroup.layout(ViewGroup.java:5438)
//            at android.widget.FrameLayout.layoutChildren(FrameLayout.java:336)
//            at android.widget.FrameLayout.onLayout(FrameLayout.java:273)
//            at android.view.View.layout(View.java:16647)
//            at android.view.ViewGroup.layout(ViewGroup.java:5438)
//            at android.widget.FrameLayout.layoutChildren(FrameLayout.java:336)
//            at android.widget.FrameLayout.onLayout(FrameLayout.java:273)
//            at android.view.View.layout(View.java:16647)
//            at android.view.ViewGroup.layout(ViewGroup.java:5438)
//            at android.support.v7.widget.ActionBarOverlayLayout.onLayout(ActionBarOverlayLayout.java:433)
//            at android.view.View.layout(View.java:16647)
//            at android.view.ViewGroup.layout(ViewGroup.java:5438)
//            at android.widget.FrameLayout.layoutChildren(FrameLayout.java:336)
//            at android.widget.FrameLayout.onLayout(FrameLayout.java:273)
//            at android.view.View.layout(View
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
                    T target = (T)msg.obj;
                    arirusLog.get().ShowLog(TAG, "Got a request for URL " + mRequestMap.get(target));
                    handleRequest(target);
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
//            mResponseHandler.obtainMessage(PhotoGalleryFragment.GET_BITMAP, new tempData((PhotoGalleryFragment.PhotoHolder)target, bitmap) ).sendToTarget();
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
