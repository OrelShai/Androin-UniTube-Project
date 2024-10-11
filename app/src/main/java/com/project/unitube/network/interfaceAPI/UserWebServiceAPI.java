package com.project.unitube.network.interfaceAPI;

import com.project.unitube.entities.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserWebServiceAPI {

    // Create a new user
    @Multipart
    @POST("api/users/")
    Call<Void> createUser(
            @Part("userName") RequestBody userName,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profilePicture
    );
    // Fetch user details by userName
    @GET("api/users/{id}")
    Call<User> getUserByUsername(@Path("id") String userName);

    // Update a user
    @Multipart
    @PUT("api/users/{id}")
    Call<User> updateUser(
            @Path("id") String userName,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profilePicture
    );

    // Delete a user by userName
    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") String userName);

    // Log in the user
    @FormUrlEncoded
    @POST("api/tokens/")
    Call<String> processLogin(@Field("userName") String userName, @Field("password") String password);

}