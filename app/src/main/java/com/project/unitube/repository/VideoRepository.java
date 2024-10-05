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
    private VideoListData videoListData;
    private VideoAPI videoAPI;

    public VideoRepository() {
        videoListData = new VideoListData();
        videoAPI = new VideoAPI(videoListData);
    }

    public LiveData<List<Video>> getAllVideos() {
        return videoListData;
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
        videoAPI.deleteVideo(video.getUploader(), video.getId());
    }

    class VideoListData extends MutableLiveData<List<Video>> {
        public VideoListData() {
            super();
            setValue(new LinkedList<Video>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            VideoAPI videoAPI = new VideoAPI(this);
            videoAPI.getAllVideos();
            /*new Thread(() -> {
                videoListData.postValue(videoDao.getAllVideos());
            }).start();*/
        }
    }
}