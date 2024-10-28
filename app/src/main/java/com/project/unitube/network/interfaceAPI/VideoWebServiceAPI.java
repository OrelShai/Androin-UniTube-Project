package com.project.unitube.network.interfaceAPI;
import com.google.gson.JsonObject;
import com.project.unitube.entities.Video;
import com.project.unitube.utils.helper.EditVideoRequest;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @POST("api/videos/{id}/increment")
    Call<Video> incrementVideoViews(@Path("id") int videoId, @Body JsonObject requestBody);

    @PUT("api/users/{userName}/videos/{videoId}")
    Call<Video> editVideo(
            @Path("userName") String userName,
            @Path("videoId") int videoId,
            @Body EditVideoRequest request
    );

    @DELETE("api/users/{userName}/videos/{videoId}")
    Call<Void> deleteVideo(@Path("userName") String userName, @Path("videoId") int videoId);

    @GET("api/users/{username}/videos")
    Call<List<Video>> getUserVideos(@Path("username") String username);

    @Multipart
    @POST("api/users/{userName}/videos")
    Call<Video> uploadVideo(
            @Path("userName") String userName,
            @Part("id") RequestBody videoId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("uploadDate") RequestBody uploadDate,
            @Part("duration") RequestBody duration,
            @Part("profilePicture") RequestBody profilePicture,
            @Part MultipartBody.Part url,
            @Part MultipartBody.Part thumbnailUrl
    );

    @GET("api/videos/highest-id")
    Call<JsonObject> getHighestVideoId();

    @GET("api/videos/{videoId}/recommendations/{username}/")
    Call<List<Video>> getRecommendedVideos(@Path("username") String username, @Path("videoId") int videoId);
}
