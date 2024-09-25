package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Video;
import com.project.unitube.network.objectAPI.VideoAPI;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    private final VideoDao videoDao;
    private VideoListData videoListData;
    private VideoAPI videoAPI;

    public VideoRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        videoDao = db.videoDao();
        videoListData = new VideoListData();
        videoAPI = new VideoAPI(videoListData, videoDao);
    }

    public LiveData<List<Video>> getAllVideos() {
        return videoAPI.getAllVideos();
    }

    public LiveData<Video> getVideoByID(int id) {
        return videoAPI.getVideoByID(id);
    }

    public void insertVideo(Video video) {
        videoAPI.createVideo(video);
    }

    public void updateVideo(Video video) {
        videoAPI.createVideo(video);
    }

    public void deleteVideo(Video video) {
        videoAPI.deleteVideo(video.getUser().getUserName(), video.getId());
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