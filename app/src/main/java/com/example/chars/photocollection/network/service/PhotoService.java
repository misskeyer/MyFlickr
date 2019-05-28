package com.example.chars.photocollection.network.service;

import com.example.chars.photocollection.BuildConfig;
import com.example.chars.photocollection.PhotoCollection;
import com.example.chars.photocollection.common.data.PhotoItem;
import com.example.chars.photocollection.network.SchedulerTransformer;
import com.example.chars.photocollection.network.api.GetRequest;
import com.example.chars.photocollection.network.json.PhotoRecent;
import com.example.chars.photocollection.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoService {

    private GetRequest api;
    private CompositeDisposable mCompositeDisposable;
    private Map<String, String> options = new HashMap<>();

    public PhotoService(OkHttpClient client,
                        GsonConverterFactory gsonConverterFactory,
                        RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                        CompositeDisposable disposable) {
        api = new Retrofit.Builder().baseUrl(PhotoCollection.FLICKR_API_BASE_URL)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build()
                .create(GetRequest.class);
        mCompositeDisposable = disposable;

        options.put("api_key", BuildConfig.FLICKR_API_KEY);
        options.put("format", "json");
        options.put("nojsoncallback", "1");
    }

    public PhotoService() {
        mCompositeDisposable = new CompositeDisposable();

        options.put("api_key", BuildConfig.FLICKR_API_KEY);
        options.put("format", "json");
        options.put("nojsoncallback", "1");
    }


    public void requestRecentPhotos(List<PhotoItem> List) {
        Disposable disposable = PhotoCollection.getInstance().getApi()
                .getRecentPhoto(options)
                .compose(SchedulerTransformer.create())
                .subscribe(new Consumer<PhotoRecent>() {
                    @Override
                    public void accept(PhotoRecent photoRecent) throws Exception {
                        List<PhotoRecent.PhotosBean.PhotoBean> list = photoRecent.photos.photo;
                        for (int i = 0; i < list.size(); i++) {
                            PhotoItem item = new PhotoItem();
                            item.setCaption(list.get(i).title);
                            item.setOwner(list.get(i).owner);
                            item.setId(list.get(i).id);
                            List.add(item);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.log("RxConnectionError");
                        LogUtils.log("Throwable : " + throwable);
                    }
                });
    }
}
