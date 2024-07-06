package com.project.unitube;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView uploaderProfileImageView;
    private TextView uploaderNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        // Initialize views
        videoView = findViewById(R.id.video_view);
        titleTextView = findViewById(R.id.video_title);
        descriptionTextView = findViewById(R.id.video_description);
        uploaderProfileImageView = findViewById(R.id.uploaderProfileImage);
        uploaderNameTextView = findViewById(R.id.uploaderName);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIDEO")) {
            Video video = (Video) intent.getSerializableExtra("VIDEO");
            if (video != null) {
                loadVideo(video);
            }
        }
    }

    private void loadVideo(Video video) {
        // Set the video title and description
        titleTextView.setText(video.getTitle());
        descriptionTextView.setText(video.getDescription());

        // Set the uploader name and last name
        uploaderNameTextView.setText(video.getUser().getFirstName() +" "+ video.getUser().getLastName());

        // Load uploader profile image
        int profileImageResourceId = getResources().getIdentifier(video.getUser().getProfilePicture(), "drawable", getPackageName());
        if (profileImageResourceId != 0) {
            uploaderProfileImageView.setImageResource(profileImageResourceId);
        } else {
            uploaderProfileImageView.setImageResource(R.drawable.placeholder_profile); // Fallback profile image
        }

        // Construct the Uri for the video in the raw folder
        int videoResourceId = getResources().getIdentifier(video.getUrl(), "raw", getPackageName());
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);

        // Set the video URI and start the video
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}
