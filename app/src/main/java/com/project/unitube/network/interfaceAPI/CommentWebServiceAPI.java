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

    @GET("comments/{videoId}")
    Call<List<Comment>> getComments(@Path("videoId") int videoId);

    @POST("comments/{videoId}")
    Call<Void> createComment(@Path("videoId") int videoId, @Body Comment comment);

    @PUT("comments/{commentId}")
    Call<Void> updateComment(@Path("commentId") int commentId, @Body Comment comment);

    @DELETE("comments/{commentId}")
    Call<Void> deleteComment(@Path("commentId") int commentId);
}
