package com.arirus.photogallery;

import android.net.Uri;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whd910421 on 16/10/11.
 */

public class FlickrFetchr {
    private final static String TAG = "FlickrFetchr";
    private final static String API_KEY = "60499a8b051cafb279a31b89fbf2a91d";
    private final static String PASS_WORD = "8d8371230308e704";

    public int getCurPage() {
        return curPage;
    }
    private static int curPage = 0;
    private static FlickrFetchr mFlickrFetchr;

    public static FlickrFetchr getInstance()
    {
        if (mFlickrFetchr == null)
            mFlickrFetchr = new FlickrFetchr();
        return mFlickrFetchr;
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException
    {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() +
                                        ": with "+
                                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0 ,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException
    {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems()
    {
        List<GalleryItem> items = new ArrayList<>() ;
        curPage ++;
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", String.valueOf(curPage) )
                    .appendQueryParameter("per_page", "50")
                    .build().toString();
            String jsonString = getUrlString(url);
            arirusLog.get().ShowLog(TAG, "JSON串是:", jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }
        catch (JSONException je)
        {
            arirusLog.get().ShowLog(TAG, "JSON is Failed to parsed");
        }
        catch (IOException ioe)
        {
            arirusLog.get().ShowLog(TAG, "抓取item失败!", ioe.toString());
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException
    {
        Gson gson = new Gson();
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++)
        {
            GalleryItem item = gson.fromJson(photoJsonArray.getJSONObject(i).toString(), GalleryItem.class);
            items.add(item);
        }
    }
}
