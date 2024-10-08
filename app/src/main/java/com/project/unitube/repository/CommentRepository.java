package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Comment;
import com.project.unitube.network.objectAPI.CommentAPI;

import java.util.LinkedList;
import java.util.List;

public class CommentRepository {
//    private final CommentDao commentDao;
    private final CommentAPI commentAPI;
    private CommentListData commentListData;

    public CommentRepository(Context context) {
//        AppDB db = AppDB.getInstance(context);
//        commentDao = db.commentDao();
        commentAPI = new CommentAPI();
        commentListData = new CommentListData();
    }

    public MutableLiveData<String> createComment(Comment comment) {
        return commentAPI.createComment(comment.getVideoId(), comment);
    }

    public MutableLiveData<List<Comment>> getCommentsForVideo(int videoId) {
        return commentAPI.getComments(videoId);
    }

    public MutableLiveData<String> updateComment(Comment comment) {
        return commentAPI.updateComment(comment.getId(), comment);
    }

    public MutableLiveData<String> deleteComment(String commentId) {
        return commentAPI.deleteComment(commentId);
    }


    class CommentListData extends MutableLiveData<List<Comment>> {
        public CommentListData() {
            super();
            setValue(new LinkedList<Comment>());
        }

        @Override
        protected void onActive() {
            super.onActive();

//            new Thread(() -> {
//                commentListData.postValue(commentDao.getAllComments());
//            }).start();
        }

        public LiveData<List<Comment>> getAll() {
            return commentListData;
        }
    }

}