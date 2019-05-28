package com.example.chars.photocollection.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;

import com.example.chars.photocollection.PhotoCollection;
import com.example.chars.photocollection.network.json.PhotoSizes;
import com.example.chars.photocollection.common.basic.VisibleFragment;
import com.example.chars.photocollection.network.api.GetRequest;
import com.example.chars.photocollection.main.adapter.PhotoAdapter;
import com.example.chars.photocollection.main.holder.PhotoHolder;
import com.example.chars.photocollection.network.service.PhotoService;
import com.example.chars.photocollection.util.BitmapUtils;
import com.example.chars.photocollection.network.FlickrFetchr;
import com.example.chars.photocollection.background.PollService;
import com.example.chars.photocollection.util.QueryPreferences;
import com.example.chars.photocollection.R;
import com.example.chars.photocollection.network.ThumbnailDownloader;
import com.example.chars.photocollection.common.data.PhotoItem;
import com.example.chars.photocollection.network.json.PhotoRecent;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoCollectionFragment extends VisibleFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "PhotoCollectionFragment";
    private RecyclerView photoRecyclerView;
    private List<PhotoItem> items = new ArrayList<>();
    private List<PhotoItem> Items = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private RequestPhotoCallback callback;
    private SwipeRefreshLayout mRefreshLayout;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private PhotoService mPhotoService;

    public static PhotoCollectionFragment newInstance() {
        return new PhotoCollectionFragment();
    }

    public interface RequestPhotoCallback {
        void requestSuccess(PhotoCollectionFragment fragment, List<PhotoRecent.PhotosBean.PhotoBean> list);

        void requestFailed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        BitmapUtils.verifyPermission(getActivity());
        updateItems();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_container, container, false);
        photoRecyclerView = view.findViewById(R.id.recyclerview);
        mRefreshLayout = view.findViewById(R.id.swipe_refresh);

        photoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        setupAdapter();
        mRefreshLayout.setOnRefreshListener(this);
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
    public void onRefresh() {
        updateItems();
        mRefreshLayout.setRefreshing(false);
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
//        requestItem(query);
        requestItemsWithRxJava(query);
//        getPhotoSizeRx();
//        requestItemsWithRx2Java(query);
        Log.i("ItemsSize", String.valueOf(Items.size()));
        for (int i = 0; i < Items.size(); i++) {
            Log.i("ItemsSize", "Id:" + Items.get(i).getId());
            Log.i("ItemsSize", "OriginWidth:" + String.valueOf(Items.get(i).getOriginWidth()));
            Log.i("ItemsSize", "OriginHeight:" + String.valueOf(Items.get(i).getOriginHeight()));
        }
        init(Items);
    }

    private void setupAdapter() {
        if (isAdded()) {
            for (int i = 0; i < Items.size(); i++) {
                Log.i("ItemsSizeAdapter", "Id:" + Items.get(i).getId());
                Log.i("ItemsSizeAdapter", "OriginWidth:" + String.valueOf(Items.get(i).getOriginWidth()));
                Log.i("ItemsSizeAdapter", "OriginHeight:" + String.valueOf(Items.get(i).getOriginHeight()));
            }
            photoRecyclerView.setAdapter(new PhotoAdapter(getActivity(), items));
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
        Call<PhotoRecent> call = null;

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

        call.enqueue(new Callback<PhotoRecent>() {
            @Override
            public void onResponse(Call<PhotoRecent> call, Response<PhotoRecent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.requestSuccess(PhotoCollectionFragment.this,
                            response.body().photos.photo);
                }
            }

            @Override
            public void onFailure(Call<PhotoRecent> call, Throwable t) {
                Log.i(TAG, "connect failed");
            }
        });
    }

    private void requestItemsWithRxJava(String query) {
        Map<String, String> options = new HashMap<>();
        Disposable disposable;

        options.put("api_key", FlickrFetchr.API_KEY);
        options.put("format", "json");
        options.put("nojsoncallback", "1");
        options.put("extras", "url_s");

        if (query != null) {
            disposable = PhotoCollection.getInstance().getApi()
                    .requestRxQ(options, FlickrFetchr.SEARCH_METHOD, query)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PhotoRecent>() {
                        @Override
                        public void accept(PhotoRecent photoRecent) throws Exception {
                            parsePhotoResult(photoRecent);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i(TAG, "RxConnectionErrorQ");
                            Log.i(TAG, "throwable e:" + throwable);
                        }
                    });
            mCompositeDisposable.add(disposable);
        } else {
            disposable = PhotoCollection.getInstance().getApi()
                    .requestRx(options, FlickrFetchr.FETCH_RECENT_METHOD)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PhotoRecent>() {
                        @Override
                        public void accept(PhotoRecent photoRecent) throws Exception {
                            parsePhotoResult(photoRecent);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i(TAG, "RxConnectionError");
                            Log.i(TAG, "throwable e:" + throwable);
                        }
                    });
            mCompositeDisposable.add(disposable);
        }
    }

    private void getPhotoSizeRx() {
        Disposable disposable;
        Log.i("PhotoRecentSize", String.valueOf(items.size()));

        for (int i = 0; i < (items.size()); i++) {
            Map<String, String> Options = new HashMap<>();
            Options.put("api_key", FlickrFetchr.API_KEY);
            Options.put("format", "json");
            Options.put("nojsoncallback", "1");

            PhotoItem photoItem = items.get(i);
            disposable = PhotoCollection.getInstance().getApi()
                    .requestRxSizes(Options, FlickrFetchr.GET_SIZES, photoItem.getId())
                    .map(new Function<PhotoSizes, PhotoItem>() {
                        @Override
                        public PhotoItem apply(PhotoSizes photoSizes) throws Exception {
                            List<PhotoSizes.SizesBean.SizeBean> listSize = photoSizes.getSizes().getSize();
                            int camp = listSize.size();
                            Log.i(TAG, "ListSize " + camp);
                            for (int i = 0; i < camp; i++) {
                                if (listSize.get(i).getLabel().equals("Original")) {
                                    photoItem.setUrl(listSize.get(i).getSource());
                                    photoItem.setOriginWidth(listSize.get(i).getWidth());
                                    photoItem.setOriginHeight(listSize.get(i).getHeight());
                                }
                                if (listSize.get(i).getLabel().equals("Thumbnail"))
                                    photoItem.setThumbnailUrl(listSize.get(i).getSource());
                            }
                            return photoItem;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PhotoItem>() {
                        @Override
                        public void accept(PhotoItem photoItem) throws Exception {
                            Items.add(photoItem);
                            Log.i("Item", String.valueOf(Items.size()));
                            Log.i(TAG, "Id: " + photoItem.getId());
                            Log.i(TAG, "OriginWidth: " + String.valueOf(photoItem.getOriginWidth()));
                            Log.i(TAG, "OriginHeight: " + String.valueOf(photoItem.getOriginHeight()));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i(TAG, "RxConnectionSizeError");
                            Log.i(TAG, "throwable e:" + throwable);
                        }
                    });
            Log.i("ItemB", String.valueOf(Items.size()));
            mCompositeDisposable.add(disposable);
        }
    }

    private void requestItemsWithRx2Java(String query) {
        Map<String, String> options = new HashMap<>();
        Disposable disposable;

        options.put("api_key", FlickrFetchr.API_KEY);
        options.put("format", "json");
        options.put("nojsoncallback", "1");
        options.put("extras", "url_s");

        if (query != null) {
            PhotoCollection.getInstance().getApi()
                    .requestRxQ(options, FlickrFetchr.SEARCH_METHOD, query)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PhotoRecent>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(PhotoRecent photoRecent) {
                            parsePhotoResult(photoRecent);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "RxConnectionErrorQ");
                            Log.i(TAG, "throwable e:" + e);
                        }

                        @Override
                        public void onComplete() {
                            Log.i(TAG, "RxConnectionCompleteQ");
                        }
                    });
            getPhotoSizeRx();
        } else {
            disposable = PhotoCollection.getInstance().getApi()
                    .requestRx(options, FlickrFetchr.FETCH_RECENT_METHOD)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<PhotoRecent, Publisher<PhotoItem>>() {
                        @Override
                        public Publisher<PhotoItem> apply(PhotoRecent photoRecent) throws Exception {
                            List<PhotoRecent.PhotosBean.PhotoBean> list = photoRecent.photos.photo;
                            List<PhotoItem> items = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                PhotoItem item = new PhotoItem();
                                item.setCaption(list.get(i).title);
                                item.setOwner(list.get(i).owner);
                                item.setId(list.get(i).id);
                                items.add(item);
                            }
                            Log.i(TAG, "ListPhotoItem: " + items.size());
                            PhotoItem[] Pi = new PhotoItem[items.size()];
                            for (int i = 0; i < items.size(); i++) {
                                Pi[i] = items.get(i);
                            }
                            for (int i = 0; i < 10; i++) {
                                Log.i(TAG, "Pi[" + i + "] :" + Pi[i].getId());
                            }
                            return Flowable.fromArray(Pi);
                        }
                    })
                    .flatMap(new Function<PhotoItem, Publisher<PhotoSizes>>() {
                        @Override
                        public Publisher<PhotoSizes> apply(PhotoItem photoItem) throws Exception {
                            Map<String, String> Options = new HashMap<>();
                            Options.put("api_key", FlickrFetchr.API_KEY);
                            Options.put("format", "json");
                            Options.put("nojsoncallback", "1");
                            return PhotoCollection.getInstance().getApi()
                                    .requestRxSizes(Options, FlickrFetchr.GET_SIZES, photoItem.getId());
                        }
                    })
                    .map(new Function<PhotoSizes, PhotoItem>() {
                        @Override
                        public PhotoItem apply(PhotoSizes photoSizes) throws Exception {
                            PhotoItem photoItem = new PhotoItem();
                            List<PhotoSizes.SizesBean.SizeBean> listSize = photoSizes.getSizes().getSize();
                            int camp = listSize.size();
                            Log.i(TAG, "ListSize " + camp);
                            for (int i = 0; i < camp; i++) {
                                if (listSize.get(i).getLabel().equals("Original")) {
                                    photoItem.setOriginWidth(listSize.get(i).getWidth());
                                    photoItem.setOriginHeight(listSize.get(i).getHeight());
                                }
                                if (listSize.get(i).getLabel().equals("Thumbnail"))
                                    photoItem.setThumbnailUrl(listSize.get(i).getSource());
                                if (listSize.get(i).getLabel().equals("Medium 800"))
                                    photoItem.setUrl(listSize.get(i).getSource());
                            }
                            return photoItem;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PhotoItem>() {
                        @Override
                        public void accept(PhotoItem photoItem) throws Exception {
                            Items.add(photoItem);
                            Log.i("Item", String.valueOf(Items.size()));
                            Log.i(TAG, "Id: " + photoItem.getId());
                            Log.i(TAG, "Url: " + photoItem.getUrl());
                            Log.i(TAG, "OriginWidth: " + String.valueOf(photoItem.getOriginWidth()));
                            Log.i(TAG, "OriginHeight: " + String.valueOf(photoItem.getOriginHeight()));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i(TAG, "RxConnectionSizeError");
                            Log.i(TAG, "throwable e:" + throwable);
                        }
                    });
            mCompositeDisposable.add(disposable);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCompositeDisposable.clear();
        Items.clear();
    }

    private void parsePhotoResult(PhotoRecent photoRecent) {
        List<PhotoRecent.PhotosBean.PhotoBean> list = photoRecent.photos.photo;
        List<PhotoItem> items = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PhotoItem item = new PhotoItem();
            item.setCaption(list.get(i).title);
            item.setOwner(list.get(i).owner);
            item.setId(list.get(i).id);
            items.add(item);
        }
        init(items);
    }
}






