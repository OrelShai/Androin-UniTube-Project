package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.User;
import com.project.unitube.entities.Video;
import com.project.unitube.network.VideoService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VideoRepository {
    private final VideoDao videoDao;
//    private final VideoService videoService;
    private final Executor executor;

    public VideoRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        videoDao = db.videoDao();
//        videoService = new VideoService(); // Assuming VideoService is already implemented
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Video>> getAllVideos() {
        return videoDao.getAllVideos();
    }
    public Video getVideoByID(int id) {
        return videoDao.getVideoByID(id);
    }

    public void insertVideo(Video video) {
        executor.execute(() -> videoDao.insertVideo(video));
    }

    public void updateVideo(Video video) {
        executor.execute(() -> videoDao.updateVideo(video));
    }

    public void deleteVideo(Video video) {
        executor.execute(() -> videoDao.deleteVideo(video));
    }

//    public void fetchVideosFromNetwork() {
//        executor.execute(() -> {
//            List<Video> videos = videoService.getVideos(); // Assuming this method is synchronous
//            videoDao.insertVideos(videos);
//        });
//    }
}