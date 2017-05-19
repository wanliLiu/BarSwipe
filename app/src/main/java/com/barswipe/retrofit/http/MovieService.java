package com.barswipe.retrofit.http;


import com.barswipe.retrofit.entity.HttpResult;
import com.barswipe.retrofit.entity.MovieEntity;
import com.barswipe.retrofit.entity.Subject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liukun on 16/3/9.
 */
public interface MovieService {

    @GET("top250")
    Call<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);

    @GET("top250")
    Observable<MovieEntity> getTopMovie12(@Query("start") int start, @Query("count") int count);

//    @GET("top250")
//    Observable<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);

    @GET("top250")
    Observable<HttpResult<List<Subject>>> getTopMovieRxJava(@Query("start") int start, @Query("count") int count);
}
