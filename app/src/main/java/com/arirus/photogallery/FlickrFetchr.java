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
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri    ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
                                            .buildUpon()
                                            .appendQueryParameter("api_key", API_KEY)
                                            .appendQueryParameter("format", "json")
                                            .appendQueryParameter("nojsoncallback", "1")
                                            .appendQueryParameter("extras", "url_s")
                                            .build();

    public int getCurPage() {
        return curPage;
    }
    private static int curPage = 0;

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

    public List<GalleryItem> downloadGalleryItems(String url)
    {
        List<GalleryItem> items = new ArrayList<>() ;
        curPage ++;
        try {
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

    private String buildUrl(String method, String query)
    {
        Uri.Builder builder = ENDPOINT.buildUpon()
                                .appendQueryParameter("method",method);
        if (method.equals(SEARCH_METHOD))
        {
            builder.appendQueryParameter("text",query);
        }

        return builder.build().toString();
    }

    public List<GalleryItem> fetchRecentPhotos()
    {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        arirusLog.get().ShowLog(TAG, "fetchRecentPhotos");
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query)
    {
        arirusLog.get().ShowLog(TAG, "fetchRecentPhotos","keyword is ", query);
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
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
