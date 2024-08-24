package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.project.unitube.Room.Dao.CommentDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Comment;
import com.project.unitube.entities.Video;
//import com.project.unitube.network.CommentService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommentRepository {
    private final CommentDao commentDao;
//    private final CommentService commentService;
    private final Executor executor;

    public CommentRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        commentDao = db.commentDao();
//        commentService = new CommentService(); // Assuming CommentService is already implemented
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Comment>> getAllComments() {
        return commentDao.getAllComments();
    }

    public Comment getCommentByID(int id) {
        return commentDao.getCommentByID(id);
    }

    public void insertComment(Comment comment) {
        executor.execute(() -> commentDao.insertComment(comment));
    }

    public void updateComment(Comment comment) {
        executor.execute(() -> commentDao.updateComment(comment));
    }

    public void deleteComment(Comment comment) {
        executor.execute(() -> commentDao.deleteComment(comment));
    }

//    public void fetchCommentsFromNetwork() {
//        executor.execute(() -> {
//            List<Comment> comments = commentService.getComments(); // Assuming this method is synchronous
//            commentDao.insertComments(comments);
//        });
//    }
}