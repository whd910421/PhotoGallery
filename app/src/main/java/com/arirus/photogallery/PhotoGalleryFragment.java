package com.arirus.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by whd910421 on 16/10/11.
 */

public class PhotoGalleryFragment extends Fragment {
    private final static String TAG = "PhotoGralleryFragemnet";

    private RecyclerView mRecyclerView;

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

        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
//            try {
//                String result = new FlickrFetchr().getUrlString("http://www.3dmgame.com");
//                arirusLog.get().ShowLog(TAG,"成功获取", result);
//            }catch (IOException ioe)
//            {
//                arirusLog.get().ShowLog(TAG, "获取失败" , ioe.toString());
//            }
            new FlickrFetchr().fetchItems();
            return null;
        }
    }

}
