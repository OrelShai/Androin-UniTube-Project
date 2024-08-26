package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Video;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    private final VideoDao videoDao;
    private VideoListData videoListData;

    public VideoRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        videoDao = db.videoDao();
        videoListData = new VideoListData();
    }

    public List<Video> getAllVideos() {
        return videoDao.getAllVideos();
    }

    public Video getVideoByID(int id) {
        return videoDao.getVideoByID(id);
    }

    public void insertVideo(Video video) {
        videoDao.insertVideo(video);
    }

    public void updateVideo(Video video) {
        videoDao.updateVideo(video);
    }

    public void deleteVideo(Video video) {
        videoDao.deleteVideo(video);
    }

    class VideoListData extends MutableLiveData<List<Video>> {
        public VideoListData() {
            super();
            setValue(new LinkedList<Video>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                videoListData.postValue(videoDao.getAllVideos());
            }).start();
        }
    }
    public LiveData<List<Video>> getAll() {
        return videoListData;
    }


}