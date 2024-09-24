package com.project.unitube.network.interfaceAPI;

import com.project.unitube.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserWebServiceAPI {

    // Create a new user
    @POST("api/users/")
    Call<Void> createUser(@Body User user);

    // Fetch user details by userName
    @GET("api/users/{id}")
    Call<User> getUser(@Path("id") String userName);

    // Update a user
    @PUT("api/users/{id}")
    Call<Void> updateUser(@Body User user);

    // Delete a user by userName
    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") String userName, @Header("Authorization") String token);

    // Log in the user
    @FormUrlEncoded
    @POST("api/tokens/")
    Call<String> processLogin(@Field("userName") String userName, @Field("password") String password);

}