package com.project.unitube;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class AddVideoScreen extends Activity {
    private EditText videoTitle;
    private EditText videoDescription;
    private TextView videoUri;
    private Button uploadVideoCoverButton;
    private Button uploadVideoButton;
    private Button addVideoButton;
    private ImageView uploadVideoCoverImage;

    private UploadPhotoHandler uploadPhotoHandler;
    private UploadVideoHandler uploadVideoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_screen);

        // Initialize handlers
        uploadPhotoHandler = new UploadPhotoHandler(this);
        uploadVideoHandler = new UploadVideoHandler(this);

        // Initialize UI components
        initializeUIComponents();

        // Set up listeners for buttons
        setUpListeners();
    }

    private void initializeUIComponents() {
        // Initialize EditTexts
        videoTitle = findViewById(R.id.videoTitle);
        videoDescription = findViewById(R.id.videoDescription);
        videoUri = findViewById(R.id.videoUri);

        // Initialize Buttons
        uploadVideoCoverButton = findViewById(R.id.uploadVideoCoverButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);
        addVideoButton = findViewById(R.id.AddVideoButton);

        // Initialize ImageViews
        uploadVideoCoverImage = findViewById(R.id.uploadVideoCoverImage);
    }

    private void setUpListeners() {
        // Set click listener for uploadVideoButton
        uploadVideoButton.setOnClickListener(view -> uploadVideoHandler.showVideoPickerOptions());

        // Set click listener for uploadVideoCoverButton
        uploadVideoCoverButton.setOnClickListener(view -> uploadPhotoHandler.showImagePickerOptions());

        // Initialize Bottom Navigation
        addVideoButton.setOnClickListener(view -> {
            if (validateFields()) {
                // Create and add new video
                createAndAddVideo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Call handleActivityResult for both handlers with proper arguments
        uploadPhotoHandler.handleActivityResult(requestCode, resultCode, data);
        uploadVideoHandler.handleActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = uploadPhotoHandler.getSelectedPhotoUri();
            if (photoUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    uploadVideoCoverImage.setImageBitmap(bitmap);
                    uploadVideoCoverImage.setTag(photoUri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Uri videoUri = uploadVideoHandler.getSelectedVideoUri();
            if (videoUri != null) {
                this.videoUri.setText(videoUri.toString());
            }
        }
    }

    private void createAndAddVideo() {
        try {
            Uri selectedVideoUri = uploadVideoHandler.getSelectedVideoUri();
            Uri selectedCoverPhotoUri = uploadPhotoHandler.getSelectedPhotoUri();

            // Create a new Video object using the URIs and form data
            Video newVideoObject = new Video(
                    videoTitle.getText().toString(),
                    videoDescription.getText().toString(),
                    selectedVideoUri.toString(),
                    selectedCoverPhotoUri.toString(),
                    RegisterScreen.currentUser,
                    uploadVideoHandler.getVideoDuration(selectedVideoUri)
            );
            Videos.videosList.add(newVideoObject); // Add the video to the list
            Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();

            // Log all videos in the list
            logAllVideos();

            // Set result to OK and finish the activity
            setResult(RESULT_OK);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error uploading video", Toast.LENGTH_SHORT).show();
        }
    }

    private void logAllVideos() {
        for (Video video : Videos.videosList) {
            Log.d("VideoList", "ID: " + video.getId() + ", Title: " + video.getTitle() +
                    ", Description: " + video.getDescription() + ", URL: " + video.getUrl() +
                    ", Thumbnail: " + video.getThumbnailUrl() + ", User: " + video.getUser().getUserName() +
                    ", Duration: " + video.getDuration());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        String title = videoTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            videoTitle.setError("Video title is required");
            isValid = false;
        }
        String description = videoDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            videoDescription.setError("Video description is required");
            isValid = false;
        }
        if (uploadVideoHandler.getSelectedVideoUri() == null) {
            Toast.makeText(this, "Video is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (uploadPhotoHandler.getSelectedPhotoUri() == null) {
            Toast.makeText(this, "Cover photo is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }
}
