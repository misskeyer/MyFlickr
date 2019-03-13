package com.example.chars.photocollection;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.chars.photocollection.data.PhotoItem;
import com.example.chars.photocollection.data.PhotoResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoCollectionFragment extends VisibleFragment {
    private static final String TAG = "PhotoCollectionFragment";
    private RecyclerView photoRecyclerView;
    private List<PhotoItem> items = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private RequestPhotoCallback callback;

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

    public static PhotoCollectionFragment newInstance() {
        return new PhotoCollectionFragment();
    }

    public interface RequestPhotoCallback {
        void requestSuccess(PhotoCollectionFragment fragment, List<PhotoResult.PhotosBean.PhotoBean> list);

        void requestFailed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        BitmapUtils.verifyPermission(getActivity());
        updateItems();
//        LoaderManager.getInstance(this).initLoader(1,null,this).forceLoad();
//        Handler responseHandler = new Handler();
//        thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        thumbnailDownloader.setThumbnailDownloadListener(
//                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
//                    @Override
//                    public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
//                        Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
//                        target.bindPhoto(drawable);
//                    }
//                });
//        thumbnailDownloader.start();
//        thumbnailDownloader.getLooper();
//        Log.i(TAG, "Background thread started.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_container, container, false);
        photoRecyclerView = view.findViewById(R.id.fragment_photo_collection);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_collection, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit" + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateItems();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange" + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity()))
            toggleItem.setTitle(R.string.stop_polling);
        else
            toggleItem.setTitle(R.string.start_polling);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        thumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        thumbnailDownloader.clearQueue();
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        callback = new RequestCallback();
//        new FetchItemTask(query).execute();
        requestItem(query);
    }

    private void setupAdapter() {
        if (isAdded()) {
            photoRecyclerView.setAdapter(new PhotoAdapter(items));
//            photoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                    switch (newState) {
//                        case RecyclerView.SCROLL_STATE_IDLE:
//                            GridLayoutManager gridLayoutManager = (GridLayoutManager) photoRecyclerView.getLayoutManager();
//                            PhotoAdapter photoAdapter = (PhotoAdapter) photoRecyclerView.getAdapter();
//
//                            if (gridLayoutManager != null) {
//                                int startPosi = gridLayoutManager.findLastVisibleItemPosition() + 1;
//                                int upperLimit = Math.min(startPosi + 10, photoAdapter.getItemCount());
//                                for (int i = startPosi; i < upperLimit; i++) {
//                                    Log.i(TAG, "onScrollstateChanged.");
//                                    thumbnailDownloader.preloadImage(photoAdapter.getPhotoItem(i).getUrl());
//                                }
//
//                                startPosi = gridLayoutManager.findFirstVisibleItemPosition() - 1;
//                                int lowerLimit = Math.max(startPosi - 10, 0);
//                                for (int i = startPosi; i > lowerLimit; i--) {
//                                    thumbnailDownloader.preloadImage(photoAdapter.getPhotoItem(i).getUrl());
//                                }
//                            }
//                            break;
//                        case RecyclerView.SCROLL_STATE_DRAGGING:
//                            thumbnailDownloader.clearPreloadQueue();
//                            Log.i(TAG, "onScrolled.");
//
//                            break;
//                    }
//                }
//
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                }
//            });
        }
    }

    public void init(List<PhotoItem> items) {
        this.items = items;
        setupAdapter();
    }

    private class FetchItemTask extends AsyncTask<Void, Void, List<PhotoItem>> {
        private String query;

        public FetchItemTask(String message) {
            query = message;
        }

        @Override
        protected List<PhotoItem> doInBackground(Void... voids) {
            if (query == null) {
                return new FlickrFetchr().fecthRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(query);
            }
        }

        @Override
        protected void onPostExecute(List<PhotoItem> photoItems) {
            super.onPostExecute(photoItems);
            items = photoItems;
            setupAdapter();
        }
    }

    public void requestItem(String query) {
        Map<String, String> options = new HashMap<>();
        Call<PhotoResult> call = null;

        options.put("api_key", FlickrFetchr.API_KEY);
        options.put("format", "json");
        options.put("nojsoncallback", "1");
        options.put("extras", "url_s");

        GetRequest api = new Retrofit.Builder().baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetRequest.class);

        if (query != null) {
            call = api.requestq(options, FlickrFetchr.SEARCH_METHOD, query);
        } else {
            call = api.request(options, FlickrFetchr.FETCH_RECENT_METHOD);
        }

        call.enqueue(new Callback<PhotoResult>() {
            @Override
            public void onResponse(Call<PhotoResult> call, Response<PhotoResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.requestSuccess(PhotoCollectionFragment.this,
                            response.body().photos.photo);
                }
            }

            @Override
            public void onFailure(Call<PhotoResult> call, Throwable t) {
                Log.i(TAG, "connect failed");
            }
        });
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private PhotoItem photoItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.fragment_photo_collection_image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = PhotoPageActivity
                            .newIntent(getActivity(), photoItem.getPhotoPageUri());
                    startActivity(i);
                }
            });
        }

        public void bindPhotoItem(PhotoItem item) {
            photoItem = item;
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
            String url = item.getUrl();
            Glide.with(PhotoCollectionFragment.this)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(photoHolder.itemImage);
            photoHolder.bindPhotoItem(item);

//            Bitmap bitmap = thumbnailDownloader.getCachedImage(item.getUrl());
//            if (bitmap == null) {
//                bitmap = BitmapUtils.getBitmapFromLocal(item.getUrl());
//                if (bitmap == null){
//                    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
//                    photoHolder.bindPhoto(drawable);
//                    photoHolder.bindPhotoItem(item);
//                    thumbnailDownloader.queueThumbnail(photoHolder, item.getUrl());
//                    Log.i(TAG,"Loaded image from net");
//                } else {
//                    photoHolder.bindPhoto(new BitmapDrawable(getResources(), bitmap));
//                    Log.i(TAG,"Loaded image from disk");
//                }
//            }else {
//                photoHolder.bindPhoto(new BitmapDrawable(getResources(), bitmap));
//                Log.i(TAG,"Loaded image from cache");
//            }
//            preloadAdjacentPhotos(i);
        }

        private void preloadAdjacentPhotos(int position) {
            final int photoBufferSize = 10;
            int startIndex = Math.max(position - photoBufferSize, 0);
            int endIndex = Math.min(photoBufferSize + position, photoItems.size() - 1);
            for (int i = startIndex; i < endIndex; i++) {
                if (i == position)
                    continue;
                String url = photoItems.get(i).getUrl();
                thumbnailDownloader.preloadImage(url);
            }
        }

        @Override
        public int getItemCount() {
            return photoItems.size();
        }

        public PhotoItem getPhotoItem(int position) {
            return photoItems.get(position);
        }
    }
}





