package com.example.chars.photocollection;

import android.app.Application;

import com.example.chars.photocollection.network.api.GetRequest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoCollection extends Application {

    private static PhotoCollection instance;
    public static PhotoCollection getInstance() {
        return instance;
    }

    private static OkHttpClient sOkHttpClient;
    private static GsonConverterFactory sGsonConverterFactory;
    private static RxJava2CallAdapterFactory sRxJava2CallAdapterFactory;
    private static GetRequest api;

    public static final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/rest/";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.addInterceptor(interceptor);
        client.connectTimeout(1, TimeUnit.MINUTES);
        client.writeTimeout(1, TimeUnit.MINUTES);
        client.readTimeout(1, TimeUnit.MINUTES);
        sOkHttpClient = client.build();

        sGsonConverterFactory = GsonConverterFactory.create();
        sRxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();

        api = new Retrofit.Builder().baseUrl("https://api.flickr.com/")
                .addCallAdapterFactory(sRxJava2CallAdapterFactory)
                .addConverterFactory(sGsonConverterFactory)
                .client(sOkHttpClient)
                .build()
                .create(GetRequest.class);
    }

    public OkHttpClient getOkHttpClient() {
        return sOkHttpClient;
    }

    public GsonConverterFactory getGsonConverterFactory() {
        return sGsonConverterFactory;
    }

    public RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        return sRxJava2CallAdapterFactory;
    }

    public GetRequest getApi() {
        return api;
    }
}
