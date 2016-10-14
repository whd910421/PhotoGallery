package com.arirus.photogallery;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whd910421 on 16/10/11.
 */

public class PhotoGalleryFragment extends Fragment {
    private final static String TAG = "PhotoGralleryFragemnet";

    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private static int mLastPos ;

    public static PhotoGalleryFragment newInstance()
    {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
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

    ///////////////////////////////RecyclerView 相关////////////////////////////////////
    private class PhotoHolder extends RecyclerView.ViewHolder
    {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item)
        {
            mTitleTextView.setText(item.toString());
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
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
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
            arirusLog.get().ShowLog(TAG, String.valueOf(mLastPos));
        }
    }
    ///////////////////////////////RecyclerView 相关////////////////////////////////////
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>
    {
        private int index ;

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            index = this.hashCode();
            return FlickrFetchr.getInstance().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
//            arirusLog.get().ShowLog(TAG, "hashCode"+ String.valueOf(index));
            for ( GalleryItem item: items)
            {
                mItems.add(item);
            }
            setupAdapter();
        }

    }

}
