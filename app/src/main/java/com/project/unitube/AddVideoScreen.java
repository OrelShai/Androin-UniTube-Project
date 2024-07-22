package com.project.unitube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddVideoScreen extends AppCompatActivity {
    private EditText videoTitle;
    private EditText videoDescription;
    private TextView videoUri;
    private Button uploadVideoCoverButton;
    private Button uploadVideoButton;
    private Button addVideoButton;
    private ImageView uploadVideoCoverImage;

    private Uri selectedPhotoUri;
    private Uri selectedVideoUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int REQUEST_CODE = 100;
    private static final int PICK_VIDEO_REQUEST = 101;
    private static final int CAPTURE_VIDEO_REQUEST = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_screen);

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
        uploadVideoButton.setOnClickListener(view -> showVideoPickerOptions());

        // Set click listener for uploadVideoCoverButton
        uploadVideoCoverButton.setOnClickListener(view -> showImagePickerOptions());

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
        handleVideoActivityResult(requestCode, resultCode, data);
        handlePhotoActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = getSelectedPhotoUri();
            if (photoUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    uploadVideoCoverImage.setImageBitmap(bitmap);
                    uploadVideoCoverImage.setTag(photoUri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Uri videoUri = getSelectedVideoUri();
            if (videoUri != null) {
                this.videoUri.setText(videoUri.toString());
            }
        }
    }

    private void createAndAddVideo() {
        try {
            Uri selectedVideoUri = getSelectedVideoUri();
            Uri selectedCoverPhotoUri = getSelectedPhotoUri();

            // Create a new Video object using the URIs and form data
            Video newVideoObject = new Video(
                    videoTitle.getText().toString(),
                    videoDescription.getText().toString(),
                    selectedVideoUri.toString(),
                    selectedCoverPhotoUri.toString(),
                    RegisterScreen.currentUser,
                    getVideoDuration(selectedVideoUri)
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
        if (getSelectedVideoUri() == null) {
            Toast.makeText(this, "Video is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (getSelectedPhotoUri() == null) {
            Toast.makeText(this, "Cover photo is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }


    public void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Photo"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickImageFromGallery();
                            break;
                        case 1:
                            captureImageFromCamera();
                            break;
                    }
                });
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.project.unitube.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                selectedPhotoUri = photoUri; // Store the selected photo URI
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void handlePhotoActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Handle picking image from gallery
                selectedPhotoUri = data.getData();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Handle capturing image from camera
                // The selected photo URI is already set in captureImageFromCamera()
            }
        }
    }


    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO"}, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your operation
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showVideoPickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Video Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Video"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                pickVideoFromGallery();
                                break;
                            case 1:
                                captureVideoFromCamera();
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void pickVideoFromGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void captureVideoFromCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_REQUEST);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    public String getVideoDuration(Uri videoUri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationInMillis = Long.parseLong(time);
            return formatDuration(durationInMillis);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VideoDurationError", "Error retrieving video duration", e);
            return "00:00";
        } finally {
            retriever.release();
        }
    }

    private String formatDuration(long durationInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationInMillis));

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void handleVideoActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST && data != null && data.getData() != null) {
                // Handle picked video from gallery
                Toast.makeText(this, "Video picked from gallery", Toast.LENGTH_SHORT).show();
                selectedVideoUri = data.getData(); // Store the video URI
            } else if (requestCode == CAPTURE_VIDEO_REQUEST && data != null && data.getData() != null) {
                // Handle captured video from camera
                Toast.makeText(this, "Video captured from camera", Toast.LENGTH_SHORT).show();
                selectedVideoUri = data.getData(); // Store the video URI
            }
        }
    }

    public Uri getSelectedPhotoUri() {
        return selectedPhotoUri;
    }

    public Uri getSelectedVideoUri() {
        return selectedVideoUri;
    }


}
