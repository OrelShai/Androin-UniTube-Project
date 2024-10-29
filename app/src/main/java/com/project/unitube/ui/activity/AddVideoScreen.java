package com.project.unitube.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.project.unitube.R;
import com.project.unitube.entities.Videos;
import com.project.unitube.utils.helper.VideoUploadRequest;
import com.project.unitube.utils.manager.UserManager;
import com.project.unitube.entities.Video;
import com.project.unitube.viewmodel.VideoViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddVideoScreen extends AppCompatActivity {
    private EditText videoTitle;
    private EditText videoDescription;
    private TextView videoUri;
    private Button uploadThumbnailButton;
    private Button uploadVideoButton;
    private Button addVideoButton;
    private ImageView thumbnailImageView;

    private Uri selectedThumbnailUri;
    private Uri selectedVideoUri;

    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int PICK_VIDEO_REQUEST = 101;
    private static final int CAPTURE_VIDEO_REQUEST = 102;

    // Permission Request Codes
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 202;


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
        uploadThumbnailButton = findViewById(R.id.uploadVideoCoverButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);
        addVideoButton = findViewById(R.id.AddVideoButton);

        // Initialize ImageViews
        thumbnailImageView = findViewById(R.id.uploadVideoCoverImage);
    }

    private void setUpListeners() {
        // Set click listener for uploadVideoButton
        uploadVideoButton.setOnClickListener(view -> showVideoPickerOptions());

        // Set click listener for uploadThumbnailButton
        uploadThumbnailButton.setOnClickListener(view -> showThumbnailPickerOptions());

        // Initialize Bottom Navigation
        addVideoButton.setOnClickListener(view -> {
            if (validateFields()) {
                // Create and add new video
                createAndAddVideo();
            }
        });
    }

    // Permissions Methods
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureThumbnailFromCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showThumbnailPickerOptions(); // or other storage operations
            } else {
                Toast.makeText(this, "Storage permission is required to access files.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createAndAddVideo() {
        try {

            Uri selectedVideoUri = getSelectedVideoUri();
            Uri selectedThumbnailUri = getSelectedThumbnailUri();
            String duration = getVideoDuration(selectedVideoUri);

            // Log selected thumbnail URI
            Log.d("createAndAddVideo", "Selected thumbnail URI: " + selectedThumbnailUri);

            VideoViewModel videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
            videoViewModel.getHighestVideoId().observe(this, highestId -> {
                if (highestId != null) {

                    // Create a VideoUploadRequest with the next video ID
                    int nextVideoId = highestId + 1;
                    String profilePicture = UserManager.getInstance().getCurrentUser() != null
                            ? UserManager.getInstance().getCurrentUser().getProfilePicture()
                            : "default_profile_picture";
                    VideoUploadRequest request = new VideoUploadRequest(
                            nextVideoId,
                            videoTitle.getText().toString(),
                            videoDescription.getText().toString(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()),
                            duration,
                            profilePicture
                    );

                    // Convert URIs to File objects
                    File videoFile = new File(selectedVideoUri.getPath());
                    File thumbnailFile = new File(selectedThumbnailUri.getPath());

                    // Show the progress dialog before starting the upload process
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Uploading video...");
                    progressDialog.setCancelable(true); // enable dismiss by tapping outside of the dialog
                    progressDialog.show();

                    // Call the uploadVideo method of VideoViewModel
                    videoViewModel.uploadVideo(UserManager.getInstance().getCurrentUser().getUserName(), request, videoFile, thumbnailFile)
                            .observe(this, uploadedVideo -> {
                                if (uploadedVideo != null) {
                                    // Log success
                                    Log.d("createAndAddVideo", "Video uploaded successfully with ID: " + uploadedVideo.getId());
                                    Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    // Log failure
                                    Log.e("createAndAddVideo", "Error uploading video");
                                    Toast.makeText(this, "Error uploading video", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Log failure to fetch highest video ID
                    Log.e("createAndAddVideo", "Error fetching highest video ID");
                    Toast.makeText(this, "Error fetching highest video ID", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            // Log exception
            Log.e("createAndAddVideo", "IOException occurred", e);
            Toast.makeText(this, "Error uploading video", Toast.LENGTH_SHORT).show();
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

        if (getSelectedThumbnailUri() == null) {
            Toast.makeText(this, "Cover photo is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    public void showThumbnailPickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Photo"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (checkStoragePermission()) {
                                pickThumbnailFromGallery();
                            } else {
                                requestStoragePermission();
                            }
                            break;
                        case 1:
                            if (checkCameraPermission()) {
                                captureThumbnailFromCamera();
                            } else {
                                requestCameraPermission();
                            }
                            break;
                    }
                });
        builder.show();
    }

    private void pickThumbnailFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureThumbnailFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                selectedThumbnailUri = FileProvider.getUriForFile(this, "com.project.unitube.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedThumbnailUri);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No camera application found", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST || requestCode == CAPTURE_IMAGE_REQUEST) {
                handleThumbnailActivityResult(requestCode, resultCode, data);
            }
            if (requestCode == PICK_VIDEO_REQUEST || requestCode == CAPTURE_VIDEO_REQUEST) {
                handleVideoActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void handleThumbnailActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedThumbnailUri = data.getData();
            thumbnailImageView.setImageURI(selectedThumbnailUri);
            thumbnailImageView.setTag(selectedThumbnailUri.toString());
            saveImageFromUri(selectedThumbnailUri);
        } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
            if (selectedThumbnailUri != null) {
                thumbnailImageView.setImageURI(selectedThumbnailUri);
                thumbnailImageView.setTag(selectedThumbnailUri.toString());
                saveImageFromUri(selectedThumbnailUri);
            } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File saveUriToFile(Uri uri, String filePrefix, String fileExtension) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File file = createTempFile(filePrefix, fileExtension);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

    private File createTempFile(String prefix, String suffix) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(prefix, suffix, storageDir);
    }

    private void saveImageFromUri(Uri uri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_thumbnail.jpg";  // Unique filename
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            this.selectedThumbnailUri = Uri.parse(imageFile.getAbsolutePath());
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleVideoActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == PICK_VIDEO_REQUEST && data != null && data.getData() != null) {
                    selectedVideoUri = data.getData();
                    // Save the video to a file
                    File videoFile = saveUriToFile(selectedVideoUri, "VID_", ".mp4");
                    selectedVideoUri = Uri.fromFile(videoFile);
                    this.videoUri.setText(selectedVideoUri.toString());
                } else if (requestCode == CAPTURE_VIDEO_REQUEST) {
                    selectedVideoUri = data.getData();
                    File videoFile = saveUriToFile(selectedVideoUri, "VID_", ".mp4");
                    selectedVideoUri = Uri.fromFile(videoFile);
                    if (selectedVideoUri != null) {
                        this.videoUri.setText(selectedVideoUri.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showVideoPickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Video Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Video"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (checkStoragePermission()) {
                                pickVideoFromGallery();
                            } else {
                                requestStoragePermission();
                            }
                            break;
                        case 1:
                            if (checkCameraPermission()) {
                                captureVideoFromCamera();
                            } else {
                                requestCameraPermission();
                            }
                            break;
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

    public Uri getSelectedThumbnailUri() {
        return selectedThumbnailUri;
    }

    public Uri getSelectedVideoUri() {
        return selectedVideoUri;
    }

}

