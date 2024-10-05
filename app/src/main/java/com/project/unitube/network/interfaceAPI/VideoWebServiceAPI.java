package com.project.unitube.network.interfaceAPI;
import com.project.unitube.entities.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VideoWebServiceAPI {
    @GET("api/videos")
    Call<List<Video>> getVideos();

    @GET("videos/{videoId}")
    Call<Video> getVideoById(@Path("videoId") int videoId);

    @GET("videos/user/{user_name}")
    Call<List<Video>> getUserVideos(@Path("user_name") String userName);

    @PUT("videos/{user_name}/{videoId}")
    Call<Void> editVideo(@Path("user_name") String userName, @Path("videoId") int videoId, @Body Video video);

    @POST("videos")
    Call<Void> createVideo(@Body Video video);

    @DELETE("videos/{user_name}/{videoId}")
    Call<Void> deleteVideo(@Path("user_name") String userName, @Path("videoId") int videoId);
}
