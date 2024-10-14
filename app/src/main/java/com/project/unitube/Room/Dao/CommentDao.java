package com.project.unitube.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.project.unitube.entities.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    // get comments list by video ID
    @Query("SELECT * FROM comment WHERE videoId = :videoId")
    List<Comment> getCommentsByVideoID(int videoId);

    @Query("SELECT * FROM comment WHERE id = :id")
    Comment getCommentByID(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(Comment... comments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllComments(List<Comment> comments);

    @Update
    void updateComment(Comment... comments);

    @Delete
    void deleteComment(Comment... comments);
}