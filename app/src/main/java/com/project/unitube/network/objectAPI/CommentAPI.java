package com.project.unitube.network.objectAPI;

import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.CommentDao;
import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.entities.Comment;
import com.project.unitube.entities.Video;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.network.interfaceAPI.CommentWebServiceAPI;
import com.project.unitube.network.interfaceAPI.VideoWebServiceAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentAPI {

    private MutableLiveData<List<Comment>> commentListData;
    private CommentDao commentDao;
    Retrofit retrofit;
    CommentWebServiceAPI commentWebServiceAPI;

    public CommentAPI(MutableLiveData<List<Comment>> commentListData, CommentDao commentDao) {
        this.commentListData = commentListData;
        this.commentDao = commentDao;
        retrofit = RetrofitClient.getClient();
        commentWebServiceAPI = retrofit.create(CommentWebServiceAPI.class);
    }

    public void getComments(int videoId) {
        Call<List<Comment>> call = commentWebServiceAPI.getComments(videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                new Thread(() -> {
                    commentDao.deleteAllComments();
                    commentDao.insertAllComments(response.body());
                    commentListData.postValue(commentDao.getAllComments());
                }).start();
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
            }
        });
    }

    public void createComment(int videoId, Comment comment) {
        Call<Void> call = commentWebServiceAPI.createComment(videoId, comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                commentDao.insertComment(comment);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void updateComment(int commentId, Comment comment) {
        Call<Void> call = commentWebServiceAPI.updateComment(commentId, comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                commentDao.updateComment(comment);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void deleteComment(int commentId) {
        Call<Void> call = commentWebServiceAPI.deleteComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Comment comment = commentDao.getCommentByID(commentId);
                commentDao.deleteComment(comment);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}
