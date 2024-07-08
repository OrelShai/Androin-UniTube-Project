package com.project.unitube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView uploaderProfileImageView;
    private TextView uploaderNameTextView;
    private LinearLayout likeButton;
    private LinearLayout dislikeButton;
    private TextView likeCountTextView;
    private TextView dislikeCountTextView;
    private Video currentVideo;
    private VideoContentManager videoContentManager;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        // Initialize views
        initializeViews();

        // Initialize VideoContentManager
        videoContentManager = new VideoContentManager(this, this);

        // Load video if intent contains video ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIDEO_ID")) {
            int videoId = intent.getIntExtra("VIDEO_ID", -1);
            currentVideo = getVideoById(videoId);
            if (currentVideo != null) {
                // Load video using VideoLoader
                VideoLoader videoLoader = new VideoLoader(this, videoView, titleTextView, descriptionTextView,
                        uploaderNameTextView, uploaderProfileImageView);
                videoLoader.loadVideo(currentVideo);

                // Set initial like and dislike counts
                likeCountTextView.setText(String.valueOf(currentVideo.getLikesList().size()));
                dislikeCountTextView.setText(String.valueOf(currentVideo.getDislikesList().size()));

                // Handle user interactions using VideoInteractionHandler
                new VideoInteractionHandler(this, videoId, likeButton, dislikeButton, likeCountTextView, dislikeCountTextView);
            }
        }
    }

    private void initializeViews() {
        videoView = findViewById(R.id.video_view);
        titleTextView = findViewById(R.id.video_title);
        descriptionTextView = findViewById(R.id.video_description);
        uploaderProfileImageView = findViewById(R.id.uploaderProfileImage);
        uploaderNameTextView = findViewById(R.id.uploaderName);
        likeButton = findViewById(R.id.button_like);
        dislikeButton = findViewById(R.id.button_dislike);
        likeCountTextView = findViewById(R.id.like_count);
        dislikeCountTextView = findViewById(R.id.dislike_count);
    }

    // Implement getVideoById method
    private Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null; // or handle the case when video is not found
    }
}