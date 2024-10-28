package com.project.unitube.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.entities.Video;
import com.project.unitube.repository.VideoRepository;
import com.project.unitube.utils.helper.VideoUploadRequest;

import java.io.File;
import java.util.List;
import android.util.Log;


/**
 * ViewModel class to manage Video data.
 */
public class VideoViewModel extends ViewModel {
    private VideoRepository videoRepository;
    private LiveData<List<Video>> videos;

    public VideoViewModel() {
        videoRepository = new VideoRepository();
        videos = videoRepository.getAllVideos();
    }

    public LiveData<List<Video>> getVideos() {
        return videos;
    }

    public LiveData<List<Video>> reloadVideos() {
        videos = videoRepository.getAllVideos();
        return videos;
    }

    public LiveData<Video> getVideoByID(int userId, int id) {
        return videoRepository.getVideoByID(userId, id);
    }

    public LiveData<Video> toggleLike(int videoId, String userName) {
        return videoRepository.toggleLike(videoId, userName);
    }

    public LiveData<Video> toggleDislike(int videoId, String userName) {
        return videoRepository.toggleDislike(videoId, userName);
    }

    public LiveData<Video> incrementVideoViews(int videoId, String userName) {
        return videoRepository.incrementVideoViews(videoId, userName);
    }

    public LiveData<Video> editVideo(String userId, int videoId, String newTitle, String newDescription) {
        return videoRepository.editVideo(userId, videoId, newTitle, newDescription);
    }

    public LiveData<Boolean> deleteVideo(String userName, int videoId) {
        return videoRepository.deleteVideo(userName, videoId);
    }

    public LiveData<List<Video>> getUserVideos(String username) {
        return videoRepository.getUserVideos(username);
    }

    public LiveData<Video> uploadVideo(String userName, VideoUploadRequest request, File videoFile, File thumbnailFile) {
        return videoRepository.uploadVideo(userName, request, videoFile, thumbnailFile);
    }

    public LiveData<Integer> getHighestVideoId() {
        return videoRepository.getHighestVideoId();
    }

    public LiveData<List<Video>> getRecommendedVideos(String username, int videoId) {
        return videoRepository.getRecommendedVideos(username, videoId);
    }
}
