package com.arirus.photogallery;

import android.net.Uri;

/**
 * Created by whd910421 on 16/10/11.
 */

public class GalleryItem {
    private String title;
    private String id;
    private String url_s;
    private String owner;

    @Override
    public String toString()
    {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_s() {
        return url_s;
    }

    public void setUrl_s(String url_s) {
        this.url_s = url_s;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Uri getPhotoUri()
    {
        return Uri.parse("http://www.flickr.com/photos/").buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
    }
}
