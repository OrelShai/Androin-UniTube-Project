package com.project.unitube.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.project.unitube.R;
import com.project.unitube.entities.Video;
import com.project.unitube.network.RetroFit.RetrofitClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        uploaderNameTextView.setText(video.getUploader());

        setProfilePicture(video.getProfilePicture());
        setVideoView(video.getUrl());
    }

    public void setProfilePicture(String profilePicture){
        //Log.d("ProfilePicture", "Setting profile picture. URL: " + profilePicture);
        Glide.with(context)
                .load(profilePicture)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.error_profile)
                .circleCrop()
                .into(uploaderProfileImageView);
    }

    public void setVideoView(String videoPath) {
        String TAG = "VideoPlayer";
        Log.d(TAG, "Setting video with path: " + videoPath);

        // Normalize the video path
        videoPath = videoPath.replace("\\", "/");
        if (videoPath.startsWith("/")) {
            videoPath = videoPath.substring(1);
        }

        // Construct the full URL
        String API_URL = RetrofitClient.getBaseUrl();
        String fullVideoUrl = API_URL + videoPath;

        Log.d(TAG, "Full video URL (before encoding): " + fullVideoUrl);

        // Encode the URL properly
        String encodedUrl = Uri.encode(fullVideoUrl, ":/?#[]@!$&'()*+,;=");

        Log.d(TAG, "Full video URL (after encoding): " + encodedUrl);

        try {
            // Create Uri object from the full URL
            Uri videoUri = Uri.parse(fullVideoUrl);

            // Set the video URI to the VideoView
            videoView.setVideoURI(videoUri);

            // Set up event listeners
            videoView.setOnPreparedListener(mp -> {
                Log.d(TAG, "Video prepared, starting playback");
                videoView.start();
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Error playing video. Error code: " + what + ", Extra code: " + extra);
                return false;
            });

            // Request focus and start loading the video
            videoView.requestFocus();

            Log.d(TAG, "Video loading initiated");
        } catch (Exception e) {
            Log.e(TAG, "Exception in setVideoView: ", e);
        }
    }
}