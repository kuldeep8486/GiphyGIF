package com.demo;

import com.demo.pojo.GiphyMainPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
 
 
public interface ApiInterface {
    @GET("v1/gifs/trending")
    Call<GiphyMainPojo> getGiphyImages(@Query("api_key") String apiKey, @Query("limit") String limit);
 
    /*@GET("movie/{id}")
    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);*/
}