package com.naman14.timber.lastfmapi;

import com.naman14.timber.lastfmapi.models.ScrobbleInfo;
import com.naman14.timber.lastfmapi.models.UserLoginInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by christoph on 17.07.16.
 */
public interface LastFmUserRestService {


    @POST
    @FormUrlEncoded
    Call<UserLoginInfo> getUserLoginInfo(@Field("method") String method, @Field("format") String format, @Field("api_key") String apikey, @Field("api_sig") String apisig, @Field("username") String username, @Field("password") String password);

    @POST
    @FormUrlEncoded
    Call<ScrobbleInfo> getScrobbleInfo(@Field("method") String method, @Field("api_key") String apikey, @Field("api_sig") String apisig, @Field("sk") String token, @Field("artist") String artist, @Field("track") String track, @Field("timestamp") long timestamp);

}
