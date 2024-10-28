package com.project.unitube.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Video;
import com.project.unitube.network.objectAPI.VideoAPI;
import com.project.unitube.utils.helper.VideoUploadRequest;

import java.io.File;
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

    public LiveData<Video> getVideoByID(int userId, int id) {
        //Log.d("VideoRepository", "Fetching video with UserID: " + userId + " and VideoID: " + id);
        return videoAPI.getVideoByID(userId, id);
    }

    public LiveData<Video> uploadVideo(String userName, VideoUploadRequest request, File videoFile, File thumbnailFile) {
        Log.d("uploadVideo", "VideoRepository- Uploading video for user: " + userName);
        Log.d("uploadVideo", "VideoRepository- Video upload request: " + request);
        Log.d("uploadVideo", "VideoRepository- Video file path: " + videoFile.getPath());
        Log.d("uploadVideo", "VideoRepository- Thumbnail file path: " + thumbnailFile.getPath());
        return videoAPI.uploadVideo(userName, request, videoFile, thumbnailFile);
    }

    public LiveData<Video> toggleLike(int videoId, String userName) {
        return videoAPI.toggleLike(videoId, userName);
    }

    public LiveData<Video> toggleDislike(int videoId, String userName) {
        return videoAPI.toggleDislike(videoId, userName);
    }

    public LiveData<Video> incrementVideoViews(int videoId, String userName) {
        return videoAPI.incrementVideoViews(videoId, userName);
    }

    public LiveData<Video> editVideo(String userId, int videoId, String newTitle, String newDescription) {
        return videoAPI.editVideo(userId, videoId, newTitle, newDescription);
    }

    public LiveData<Boolean> deleteVideo(String userName, int videoId) {
        return videoAPI.deleteVideo(userName, videoId);
    }

    public LiveData<List<Video>> getUserVideos(String username) {
        return videoAPI.getUserVideos(username);
    }

    public LiveData<Integer> getHighestVideoId() {
        return videoAPI.getHighestVideoId();
    }

    public LiveData<List<Video>> getRecommendedVideos(String username, int videoId){
        return videoAPI.getRecommendedVideos(username, videoId);
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