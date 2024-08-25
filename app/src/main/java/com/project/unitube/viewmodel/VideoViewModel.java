package com.project.unitube.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.entities.Video;

import java.util.List;

public class VideoViewModel {
    private MutableLiveData<List<Video>> VideoMutableLiveData;

    public MutableLiveData<List<Video>> getVideoMutableLiveData() {
        if (VideoMutableLiveData == null) {
            VideoMutableLiveData = new MutableLiveData<>();
        }
        return VideoMutableLiveData;
    }

}
