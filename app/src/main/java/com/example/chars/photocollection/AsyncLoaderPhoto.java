package com.example.chars.photocollection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class AsyncLoaderPhoto extends AsyncTaskLoader {

    public AsyncLoaderPhoto(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        return null;
//        return new FlickrFetchr().fetchItems();
    }

}
