package com.project.unitube.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.project.unitube.entities.Video;

import java.util.List;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM video")
    List<Video> getAllVideos();

    @Query("SELECT * FROM video WHERE id = :id")
    Video getVideoByID(int id);

    @Query("SELECT * FROM video WHERE uploader = :uploader")
    List<Video> getVideosByUploader(String uploader);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideo(Video... videos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllVideos(List<Video> videos);

    @Update
    void updateVideo(Video... videos);

    @Delete
    void deleteVideo(Video... videos);

    @Query("DELETE FROM video")
    void deleteAllVideos();
}
