package com.example.chars.photocollection.network.api;

import com.example.chars.photocollection.network.json.PhotoRecent;
import com.example.chars.photocollection.network.json.PhotoSizes;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import io.reactivex.Observable;

public interface GetRequest {

    @GET("services/rest")
    Call<PhotoRecent> requestq(@QueryMap Map<String, String> options,
                               @Query("method") String m,
                               @Query("text") String q);

    @GET("services/rest")
    Call<PhotoRecent> request(@QueryMap Map<String, String> options,
                              @Query("method") String m);

    @GET("services/rest")
    Observable<PhotoRecent> requestRxQ(@QueryMap Map<String, String> options,
                                       @Query("method") String m,
                                       @Query("text") String q);

    @GET("services/rest/")
    Flowable<PhotoRecent> requestRx(@QueryMap Map<String, String> options,
                                      @Query("method") String m);

    @GET("flickr.photos.getRecent")
    Observable<PhotoRecent> getRecentPhoto(@QueryMap Map<String, String> options);

    @GET("flickr.photos.search")
    Observable<PhotoRecent> searchPhoto(@QueryMap Map<String, String> options,
                                        @Query("text") String q);

    @GET("flickr.photos.getSizes")
    Observable<PhotoRecent> getPhotoSizes(@QueryMap Map<String, String> options);

    @GET("services/rest")
    Flowable<PhotoSizes> requestRxSizes(@QueryMap Map<String, String> options,
                                        @Query("method") String m,
                                        @Query("photo_id") String id);
    @GET("services/rest")
    Observable<PhotoSizes> requestRxSizesOb(@QueryMap Map<String, String> options,
                                        @Query("method") String m,
                                        @Query("photo_id") String id);
}
