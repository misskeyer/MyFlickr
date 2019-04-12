package com.example.chars.photocollection.ui;

import com.example.chars.photocollection.modle.data.PhotoResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GetRequest {

    @GET("services/rest")
    Call<PhotoResult> requestq(@QueryMap Map<String, String> options,
                              @Query("method") String m,
                              @Query("text") String q);

    @GET("services/rest")
    Call<PhotoResult> request(@QueryMap Map<String, String> options,
                              @Query("method") String m);

}
