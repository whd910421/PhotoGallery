package com.arirus.photogallery;

import android.graphics.Bitmap;

/**
 * Created by whd910421 on 16/10/17.
 */

public class tempData
{
    PhotoGalleryFragment.PhotoHolder tempHolder;
    Bitmap tempBitmap;

    public tempData(PhotoGalleryFragment.PhotoHolder tempHolder, Bitmap tempBitmap) {
        this.tempHolder = tempHolder;
        this.tempBitmap = tempBitmap;
    }
}