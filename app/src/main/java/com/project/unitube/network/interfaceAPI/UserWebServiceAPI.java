package com.project.unitube.network.interfaceAPI;

import com.project.unitube.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Query;

public interface UserWebServiceAPI {

    @GET("user")
    Call<User> getUser(@Query("userName") String username);

    @POST("user")
    Call<Void> createUser(@Body User user);

    @GET("checkIfUserExists")
    Call<Boolean> isUsernameTaken(@Query("userName") String username);
}