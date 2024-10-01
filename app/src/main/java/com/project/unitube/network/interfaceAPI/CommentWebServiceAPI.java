package com.project.unitube.network.interfaceAPI;

import com.project.unitube.entities.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentWebServiceAPI {

    @GET("api/comments/{id}")
    Call<List<Comment>> getComments(@Path("id") int videoId);

    @POST("api/comments/{id}")
    Call<Void> createComment(@Path("id") int videoID, @Body Comment comment);

    @PUT("api/comments/{id}")
    Call<Void> updateComment(@Path("id") String commentId, @Body Comment comment);

    @DELETE("api/comments/{id}")
    Call<Void> deleteComment(@Path("id") String commentId);
}
