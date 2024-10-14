package com.project.unitube.network.objectAPI;

import static com.project.unitube.Unitube.context;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.CommentDao;
import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.Room.Database.AppDB;
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

    private final Retrofit retrofit;
    private final CommentWebServiceAPI commentWebServiceAPI;
    private final CommentDao commentDao;  // Room DAO


    public CommentAPI() {
        this.retrofit = RetrofitClient.getClient();
        this.commentWebServiceAPI = retrofit.create(CommentWebServiceAPI.class);

        // Initialize Room DAO
        AppDB db = AppDB.getInstance(context);
        this.commentDao = db.commentDao();  // Initialize CommentDao for Room operations
    }


    // Method to fetch comments for a specific video ID
    public MutableLiveData<List<Comment>> getComments(int videoId) {
        MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();

        // First, check local cache (Room) in new thread
        new Thread(() -> {
            List<Comment> localComments = commentDao.getCommentsByVideoID(videoId);
            if (localComments != null && !localComments.isEmpty()) {
                commentsLiveData.postValue(localComments);
            }
        }).start();

        Call<List<Comment>> call = commentWebServiceAPI.getComments(videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    List<Comment> comments = response.body();
                    commentsLiveData.postValue(comments);

                    // Update Room with fetched comments
                    new Thread(() -> commentDao.insertAllComments(comments)).start();  // Insert comments in background

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

        Call<Comment> call = commentWebServiceAPI.createComment(videoId, comment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue("Success");

                    Comment createdComment = response.body();  // Get the returned comment from the server
                    // Insert the created comment into Room
                    new Thread(() -> commentDao.insertComment(createdComment)).start();
                } else {
                    resultLiveData.postValue("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
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

                    // Update the comment in Room
                    new Thread(() -> commentDao.updateComment(comment)).start();
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

                    // Remove the comment from Room
                    new Thread(() -> {
                        Comment comment = commentDao.getCommentByID(commentId); // Fetch the comment by ID
                        if (comment != null) {
                            commentDao.deleteComment(comment);
                        }
                    }).start();
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
