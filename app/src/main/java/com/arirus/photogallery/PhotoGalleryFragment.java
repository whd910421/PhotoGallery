package com.arirus.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
//    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private Handler mHandler ;
    private static int mLastPos ;


    public static PhotoGalleryFragment newInstance()
    {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arirusLog.get().ShowLog(TAG, "QueryTextSubmit",query);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arirusLog.get().ShowLog(TAG,"QueryTextChange ", newText);
                return false;
            }
        });
    }

    private void updateItems()
    {
        new FetchItemsTask().execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
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

//        mThumbnailDownloader = new ThumbnailDownloader<>(mHandler);
//        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
//            @Override
//            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
//                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
//                target.bindDrawable(drawable);
//            }
//        });
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mThumbnailDownloader.quit();
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
//                arirusLog.get().ShowLog(TAG, "findFirstCompletelyVisibleItemPosition" + String.valueOf(gridLayoutManager.findFirstCompletelyVisibleItemPosition()));

                arirusLog.get().ShowLog("请不请求下一页?",String.valueOf(gridLayoutManager.findFirstCompletelyVisibleItemPosition()),String.valueOf(FlickrFetchr.getInstance().getCurPage()*50 -20));
                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() >= FlickrFetchr.getInstance().getCurPage()*50 -20)
                {   arirusLog.get().ShowLog("请求下一页");
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
//        mThumbnailDownloader.clearQuene();
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

        public void bindGalleryItem(GalleryItem item)
        {
            Picasso.with(getActivity()).load(item.getUrl_s()).placeholder(R.drawable.drawable_defult).into(mImageView);
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
            holder.bindGalleryItem(galleryItem);
//            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl_s());
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
            return FlickrFetchr.getInstance().searchPhotos("wow");
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
