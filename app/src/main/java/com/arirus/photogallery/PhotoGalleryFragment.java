package com.arirus.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whd910421 on 16/10/11.
 */



public class PhotoGalleryFragment extends Fragment {
    private final static String TAG = "PhotoGralleryFragemnet";
//    public static final int GET_BITMAP = 0;
    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private Handler mHandler ;
    private static int mLastPos ;
    private LruCache<String, Drawable> mDrawableLruCache;


    public static PhotoGalleryFragment newInstance()
    {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
        mDrawableLruCache = new LruCache<>(20);
        mHandler = new Handler();
//        {
//            @Override
//            public void handleMessage(Message msg) {
//                arirusLog.get().ShowLog(TAG, "HandleMessage");
//                if (msg.what == GET_BITMAP && isAdded())
//                {
//                    arirusLog.get().ShowLog(TAG, "HandleMessage isAdded");
//                    tempData Target = (tempData) msg.obj;
//                    PhotoHolder target = Target.tempHolder;
//                    Bitmap bitmap = Target.tempBitmap;
//                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                    target.bindDrawable(drawable);
//                }
//            }
//        };

        mThumbnailDownloader = new ThumbnailDownloader<>(mHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
                GalleryItem item = mItems.get(target.getPosition());
                mDrawableLruCache.put(item.getUrl_s(), drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_photo_gallery_recycle_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                arirusLog.get().ShowLog(TAG, "findFirstCompletelyVisibleItemPosition" + String.valueOf(gridLayoutManager.findFirstCompletelyVisibleItemPosition()));

                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() >= FlickrFetchr.getInstance().getCurPage()*50 -20)
                {
                    new FetchItemsTask().execute();
                }
                mLastPos = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            }
        });
        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        arirusLog.get().ShowLog(TAG, "onDestroyView");
        mThumbnailDownloader.clearQuene();
//        mHandler.removeMessages(GET_BITMAP);
    }

    ///////////////////////////////RecyclerView 相关////////////////////////////////////
    public class PhotoHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable)
        {
            mImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdpter extends RecyclerView.Adapter<PhotoHolder>
    {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdpter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindDrawable(getResources().getDrawable(R.drawable.drawable_defult));
            if ( mDrawableLruCache.get(galleryItem.getUrl_s()) != null)
                holder.bindDrawable(mDrawableLruCache.get(galleryItem.getUrl_s()));
            else
                mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl_s());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }


    }
    private void setupAdapter()
    {
        if (isAdded())
        {
            mRecyclerView.setAdapter(new PhotoAdpter(mItems));
            mRecyclerView.scrollToPosition(mLastPos);
//            arirusLog.get().ShowLog(TAG, String.valueOf(mLastPos));
        }
    }
    ///////////////////////////////RecyclerView 相关////////////////////////////////////
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>
    {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return FlickrFetchr.getInstance().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            for ( GalleryItem item: items)
            {
                mItems.add(item);
            }
            setupAdapter();
        }

    }

}
