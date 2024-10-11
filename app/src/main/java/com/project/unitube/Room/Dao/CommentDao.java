package com.project.unitube.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.project.unitube.entities.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("SELECT * FROM comment")
    LiveData<List<Comment>> getAllComments();

    @Query("SELECT * FROM comment WHERE id = :id")
    Comment getCommentByID(int id);

    @Insert
    void insertComment(Comment... comments);

    @Insert
    void insertAllComments(List<Comment> comments);

    @Update
    void updateComment(Comment... comments);

    @Delete
    void deleteComment(Comment... comments);
}