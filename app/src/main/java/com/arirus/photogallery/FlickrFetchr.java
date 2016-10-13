package com.arirus.photogallery;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

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

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
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
        JSONObject photosJsonObject = gson.fromJson(jsonBody.toString(), jsonBody.getJSONObject("photos").getClass());
        JSONArray photoJsonArray = gson.fromJson(photosJsonObject.toString(), photosJsonObject.getJSONArray("photo").getClass());

        for (int i = 0; i < photoJsonArray.length(); i++)
        {
            GalleryItem item = gson.fromJson(photoJsonArray.getJSONObject(i).toString(), GalleryItem.class);
            items.add(item);
        }
//        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
//        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
//
//        for (int i = 0; i < photoJsonArray.length(); i++)
//        {
//            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
//
//            GalleryItem item = new GalleryItem();
//            item.setId(photoJsonObject.getString("id"));
//            item.setCaption(photoJsonObject.getString("title"));
//
//            if (!photoJsonObject.has("url_s"))
//            {
//                continue;
//            }
//
//            item.setUrl(photoJsonObject.getString("url_s"));
//            items.add(item);
//        }
    }
}
