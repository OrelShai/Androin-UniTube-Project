package com.project.unitube.network.interfaceAPI;
import com.google.gson.JsonObject;
import com.project.unitube.entities.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VideoWebServiceAPI {
    @GET("api/videos")
    Call<List<Video>> getVideos();

    @GET("api/users/{userId}/videos/{videoId}")
    Call<Video> getVideoById(@Path("userId") int userId, @Path("videoId") int videoId);

    @POST("api/videos/{id}/like")
    Call<Video> toggleLike(@Path("id") int videoId, @Body JsonObject body);

    @POST("api/videos/{id}/dislike")
    Call<Video> toggleDislike(@Path("id") int videoId, @Body JsonObject body);

    @GET("videos/user/{user_name}")
    Call<List<Video>> getUserVideos(@Path("user_name") String userName);

    @PUT("videos/{user_name}/{videoId}")
    Call<Void> editVideo(@Path("user_name") String userName, @Path("videoId") int videoId, @Body Video video);

    @POST("videos")
    Call<Void> createVideo(@Body Video video);

    @DELETE("videos/{user_name}/{videoId}")
    Call<Void> deleteVideo(@Path("user_name") String userName, @Path("videoId") int videoId);
}
