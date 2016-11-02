package com.unam.alex.pumaride.retrofit;

import com.unam.alex.pumaride.models.Match;
import com.unam.alex.pumaride.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by alex on 30/10/16.
 */
public interface WebServices {
    @GET("matches/{user}")
    Call<List<Match>> listMatches(@Path("user") String user);
    @POST("pumaride/registro/")
    Call<User> createUser(@Body User user);
    @FormUrlEncoded
    @POST("pumaride/login/")
    Call<User> loginUser(@Field("email") String email, @Field("password") String password);
    @GET("pumaride/users/me")
    Call<User> getUserMe(@Header("Authorization") String authorization);
    @FormUrlEncoded
    @POST("resetpassword")
    Call<User> resetPassword(@Field("email") String email);
}
