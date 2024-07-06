package com.project.unitube;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

        // Initialize download button
        View downloadButton = findViewById(R.id.button_download);
        ImageView downloadIcon = downloadButton.findViewById(R.id.button_icon);
        TextView downloadText = downloadButton.findViewById(R.id.button_text);

        // Set icon and text for download button
        downloadIcon.setImageResource(R.drawable.ic_download); // Ensure you have an appropriate icon in the drawable folder
        downloadText.setText("Download");

        // Set click listener for the download button
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom toast message for 2 seconds
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView toastText = layout.findViewById(R.id.toast_text);
                toastText.setText("Download...");

                final Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                // Handler to remove the toast after 2 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1500); // 1500 milliseconds = 1.5 seconds
            }
        });

        // Load video if intent contains video data
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
        uploaderNameTextView.setText(video.getUser().getFirstName() + " " + video.getUser().getLastName());

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
