package com.project.unitube.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.project.unitube.entities.Video;

public class VideoLoader {

    private final Context context;
    private final VideoView videoView;
    private final TextView titleTextView;
    private final TextView descriptionTextView;
    private final TextView uploaderNameTextView;
    private final ImageView uploaderProfileImageView;

    public VideoLoader(Context context, VideoView videoView, TextView titleTextView, TextView descriptionTextView,
                       TextView uploaderNameTextView, ImageView uploaderProfileImageView) {
        this.context = context;
        this.videoView = videoView;
        this.titleTextView = titleTextView;
        this.descriptionTextView = descriptionTextView;
        this.uploaderNameTextView = uploaderNameTextView;
        this.uploaderProfileImageView = uploaderProfileImageView;
    }

    public void loadVideo(Video video) {
        titleTextView.setText(video.getTitle());
        descriptionTextView.setText(video.getDescription());
        uploaderNameTextView.setText(video.getUser().getFirstName() + " " + video.getUser().getLastName());
        int profileImageResourceId = context.getResources().getIdentifier(video.getUser().getProfilePicture(), "drawable", context.getPackageName());
        if (profileImageResourceId != 0) {
            uploaderProfileImageView.setImageResource(profileImageResourceId);
        } else {
            uploaderProfileImageView.setImageURI(Uri.parse(video.getUser().getProfilePicture()));
        }
        Uri videoUri;
        int videoResourceId = context.getResources().getIdentifier(video.getUrl(), "raw", context.getPackageName());
        if (videoResourceId != 0) {
            videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + videoResourceId);
        } else {
            videoUri = Uri.parse(video.getUrl());
        }
        videoView.setVideoURI(videoUri);


        videoView.setOnPreparedListener(mp -> videoView.start());
    }

}