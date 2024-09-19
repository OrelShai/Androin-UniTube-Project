package com.project.unitube.network.interfaceAPI;

import com.project.unitube.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserWebServiceAPI {

    // Log in the user
    @FormUrlEncoded
    @POST("api/tokens")
    Call<String> processLogin(@Field("userName") String userName, @Field("password") String password);

    // Fetch user details by userName
    @GET("api/users/{userName}")
    Call<User> getUser(@Path("userName") String userName);

    // Create a new user
    @POST("api/users/user")
    Call<Void> createUser(@Body User user);


    // Delete a user by userName
    @DELETE("api/users/{userName}")
    Call<Void> deleteUser(@Path("userName") String userName);
}