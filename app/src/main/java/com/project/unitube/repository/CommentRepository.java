package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.CommentDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Comment;

import java.util.LinkedList;
import java.util.List;

public class CommentRepository {
    private final CommentDao commentDao;
    private CommentListData commentListData;

    public CommentRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        commentDao = db.commentDao();
        commentListData = new CommentListData();
    }

    public List<Comment> getAllComments() {
        return commentDao.getAllComments();
    }

    public Comment getCommentByID(int id) {
        return commentDao.getCommentByID(id);
    }

    public void insertComment(Comment comment) {
        commentDao.insertComment(comment);
    }

    public void updateComment(Comment comment) {
        commentDao.updateComment(comment);
    }

    public void deleteComment(Comment comment) {
        commentDao.deleteComment(comment);
    }

    class CommentListData extends MutableLiveData<List<Comment>> {
        public CommentListData() {
            super();
            setValue(new LinkedList<Comment>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                commentListData.postValue(commentDao.getAllComments());
            }).start();
        }

        public LiveData<List<Comment>> getAll() {
            return commentListData;
        }
    }

}