package com.project.unitube.network.interfaceAPI;

import com.project.unitube.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface UserWebServiceAPI {

    @GET("user")
    Call<User> getUser();

    @GET("users")
    Call<List<User>> getAllUsers();

    @POST("user")
    Call<Void> createUser(@Body User user);
}