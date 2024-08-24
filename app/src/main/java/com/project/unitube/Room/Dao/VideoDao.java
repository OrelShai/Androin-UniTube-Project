package com.project.unitube.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.project.unitube.entities.Video;

import java.util.List;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM video")
    LiveData<List<Video>> getAllVideos();

    @Query("SELECT * FROM video WHERE id = :id")
    Video getVideoByID(int id);

    @Insert
    void insertVideo(Video... videos);

    @Update
    void updateVideo(Video... videos);

    @Delete
    void deleteVideo(Video... videos);
}
