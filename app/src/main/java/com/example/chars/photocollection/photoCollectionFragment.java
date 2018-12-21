package com.example.chars.photocollection;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chars.photocollection.data.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class photoCollectionFragment extends Fragment {
    private static final String TAG = "PhotoCollectionFragment";
    private RecyclerView photoRecyclerView;
    private List<PhotoItem> items = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;

//    @NonNull
//    @Override
//    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
//        return new AsyncLoaderPhoto(getActivity());
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<List<PhotoItem>> loader, List<PhotoItem> photoitems) {
//        items = photoitems;
//        setupAdapter();
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader loader) {
//        items = null;
//    }

    private class FetchItemTask extends AsyncTask<Void, Void, List<PhotoItem>> {

        @Override
        protected List<PhotoItem> doInBackground(Void... voids) {
//            try {
//                String result = new FlickrFetchr().getUrlString("https://www.baidu.com");
//                Log.i(TAG,"Fetched contents of URL: " + result);
//            } catch (IOException e) {
//                Log.e(TAG,"Failed to fecth URL.");
//            }
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<PhotoItem> photoItems) {
            super.onPostExecute(photoItems);
            items = photoItems;
            setupAdapter();
        }
    }

    public static photoCollectionFragment newInstance() {
        return new photoCollectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask().execute();
//        LoaderManager.getInstance(this).initLoader(1,null,this).forceLoad();
        Handler responseHandler = new Handler();
        thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        thumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindPhoto(drawable);
            }
        });
        thumbnailDownloader.start();
        thumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread started.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_container,container,false);
        photoRecyclerView = view.findViewById(R.id.fragment_photo_collection);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thumbnailDownloader.quit();
        Log.i(TAG,"Background thread destroyed.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thumbnailDownloader.clearQueue();
    }

    private void setupAdapter() {
        if (isAdded())
            photoRecyclerView.setAdapter(new PhotoAdapter(items));
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.fragment_photo_collection_image_view);
        }

        public void bindPhoto(Drawable drawable) {
            itemImage.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<PhotoItem> photoItems;

        public PhotoAdapter(List<PhotoItem> items) {
            photoItems = items;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.photo_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            PhotoItem item = photoItems.get(i);
            Drawable placeHolder = getResources().getDrawable(R.drawable.ic_launcher_background);
            photoHolder.bindPhoto(placeHolder);
            thumbnailDownloader.queueThumbnail(photoHolder, item.getUrl());
        }

        @Override
        public int getItemCount() {
            return photoItems.size();
        }
    }
}






