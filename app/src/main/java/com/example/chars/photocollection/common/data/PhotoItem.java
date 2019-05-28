package com.example.chars.photocollection.common.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class PhotoItem {
    private String caption;
    private String id;
    private String url;
    private String thumbnailUrl;
    private String owner;
    private int originWidth;
    private int originHeight;

    public boolean loadPhotoSuccess = false;
    public boolean hasFadedIn = false;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getOriginWidth() {
        return originWidth;
    }

    public void setOriginWidth(int originWidth) {
        this.originWidth = originWidth;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public void setOriginHeight(int originHeight) {
        this.originHeight = originHeight;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCaption() {
        return caption;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return caption;
    }


    public int[] getRegularSize(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        float screenRatio = (float) (1.0 * screenWidth / screenHeight);
        float imageRatio = (float) (1.0 * originWidth / originHeight);

        Log.i("PhotoItem", "screenRatio: " + screenRatio + " imageRatio: " + imageRatio);
        if (imageRatio > screenRatio) {
            int h = Math.min(screenHeight / 2, originHeight);
            int w = (int) (1.0 * originWidth / originHeight * h);
            return new int[] {w, h};
        } else {
            int w = Math.min(screenWidth / 2, originWidth);
            int h = (int) (1.0 * originHeight / originWidth * w);
            return new int[] {w, h};
        }
    }
}
