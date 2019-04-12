package com.example.chars.photocollection.modle;

import android.net.Uri;
import android.util.Log;

import com.example.chars.photocollection.modle.data.PhotoItem;
import com.example.chars.photocollection.modle.data.PhotoResult;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    public static final String API_KEY = "e8d9c7dc1ed04cdc64a2c9628b54003b";
    public static final String FETCH_RECENT_METHOD = "flickr.photos.getRecent";
    public static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest").buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    private String buildUrl(String method, String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);
        if (method.equals(SEARCH_METHOD))
            uriBuilder.appendQueryParameter("text", query);
        return uriBuilder.build().toString();
    }

    private List<PhotoItem> downloadPhotoItem(String url) {
        List<PhotoItem> items = new ArrayList<>();
        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received Json: " + jsonString);
//            JSONObject jsonBody = new JSONObject(jsonString);
//            parseItems(items, jsonBody);
//            parseItemsWithGson(jsonString, items);
            items = parseItemsWithGson(jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items");
        }
//        catch (JSONException e) {
//            Log.e(TAG,"Failed to parse Json.");
//        }
        Log.i(TAG, "Items after : " + items);
        return items;
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ":with" + urlSpec);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<PhotoItem> fecthRecentPhotos() {
        String url = buildUrl(FETCH_RECENT_METHOD, null);
        return downloadPhotoItem(url);
    }

    public List<PhotoItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadPhotoItem(url);
    }

    public void parseItems(List<PhotoItem> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            PhotoItem item = new PhotoItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s"))
                continue;

            item.setUrl(photoJsonObject.getString("url_s"));
            item.setOwner(photoJsonObject.getString("owner"));
            items.add(item);
        }
    }

    public List<PhotoItem> parseItemsWithGson(String json) {
        List<PhotoItem> items = new ArrayList<>();
        PhotoResult result = new Gson().fromJson(json, PhotoResult.class);
        List<PhotoResult.PhotosBean.PhotoBean> photolist = result.photos.photo;
        for (int i = 0; i < photolist.size(); i++) {
            PhotoItem item = new PhotoItem();
            item.setCaption(photolist.get(i).title);
            item.setOwner(photolist.get(i).owner);
            item.setUrl(photolist.get(i).url_s);
            item.setId(photolist.get(i).id);
            items.add(item);
        }
        return items;
    }

    public void parseItemsWithGson(String json, List<PhotoItem> items) throws IOException {
        Log.i(TAG, "parseItemWhitGson.");
        JsonReader reader = new JsonReader(new StringReader(json));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String propertyName = reader.nextName();
                if (propertyName.equals("photos")) {
                    readPhotos(reader, items);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } finally {
            reader.close();
        }
    }

    private void readPhotos(JsonReader reader, List<PhotoItem> items) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String propertyName = reader.nextName();
            if (propertyName.equals("photo")) {
                readPhoto(reader, items);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readPhoto(JsonReader reader, List<PhotoItem> items) throws IOException {
        reader.beginArray();
        String id = null, url_s = null, owner = null, title = null;
        while (reader.hasNext()) {
            PhotoItem item = new PhotoItem();
            reader.beginObject();
            while (reader.hasNext()) {
                String propertyName = reader.nextName();
                switch (propertyName) {
                    case "id":
                        id = reader.nextString();
                        break;
                    case "url_s":
                        url_s = reader.nextString();
                        break;
                    case "owner":
                        owner = reader.nextString();
                        break;
                    case "title":
                        title = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            item.setId(id);
            item.setUrl(url_s);
            item.setOwner(owner);
            item.setCaption(title);
            items.add(item);
            reader.endObject();
        }
        reader.endArray();
    }

}
