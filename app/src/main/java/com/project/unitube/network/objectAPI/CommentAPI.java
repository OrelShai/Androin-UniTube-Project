package com.project.unitube.network.objectAPI;

import android.util.Log;

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

    Retrofit retrofit;
    CommentWebServiceAPI commentWebServiceAPI;

    public CommentAPI() {
        retrofit = RetrofitClient.getClient();
        commentWebServiceAPI = retrofit.create(CommentWebServiceAPI.class);
    }


    // Method to fetch comments for a specific video ID
    public MutableLiveData<List<Comment>> getComments(int videoId) {
        MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();

        Call<List<Comment>> call = commentWebServiceAPI.getComments(videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    commentsLiveData.postValue(response.body());
                } else {
                    Log.e("CommentAPI", "Failed to fetch comments: " + response.code());
                    commentsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("CommentAPI", "Error: " + t.getMessage());
                commentsLiveData.postValue(null);
            }
        });

        return commentsLiveData;
    }

    public MutableLiveData<String> createComment(int videoId, Comment comment) {
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();

        Call<Void> call = commentWebServiceAPI.createComment(videoId, comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue("Success");
                } else {
                    resultLiveData.postValue("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultLiveData.postValue("failure: " + t.getMessage());
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<String> updateComment(String commentId, Comment comment) {
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();

        Call<Void> call = commentWebServiceAPI.updateComment(commentId, comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue("Success");
                } else {
                    resultLiveData.postValue("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultLiveData.postValue("failure: " + t.getMessage());
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<String> deleteComment(String commentId) {
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();

        Call<Void> call = commentWebServiceAPI.deleteComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue("Success");
                } else {
                    resultLiveData.postValue("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultLiveData.postValue("failure: " + t.getMessage());
            }
        });
        return resultLiveData;
    }
}
