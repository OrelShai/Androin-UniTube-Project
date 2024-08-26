package com.project.unitube.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.entities.Video;
import com.project.unitube.repository.VideoRepository;

import java.util.List;

/**
 * ViewModel class to manage Video data.
 */
public class VideoViewModel extends ViewModel {
    private VideoRepository videoRepository;
    private LiveData<List<Video>> videos;

    /**
     * Constructor to initialize VideoRepository and fetch all videos.
     */
    public VideoViewModel(Context context) {
        videoRepository = new VideoRepository(context);
        videos = videoRepository.getAllVideos();
    }

    /**
     * Returns the LiveData object containing the list of videos.
     *
     * @return LiveData object containing the list of videos
     */
    public LiveData<List<Video>> getVideos() {
        return videos;
    }

    public LiveData<List<Video>> reloadVideos() {
        videos = videoRepository.getAllVideos();
        return videos;
    }

    public LiveData<Video> getVideoByID(int id) {
        return videoRepository.getVideoByID(id);
    }

    /**
     * Inserts a new video.
     *
     * @param video the video to be inserted
     */
    public void insertVideo(Video video) {
        videoRepository.insertVideo(video);
    }

    /**
     * Deletes an existing video.
     *
     * @param video the video to be deleted
     */
    public void deleteVideo(Video video) {
        videoRepository.deleteVideo(video);
    }

    /**
     * Updates an existing video.
     *
     * @param video the video to be updated
     */
    public void updateVideo(Video video) {
        videoRepository.updateVideo(video);
    }

}
