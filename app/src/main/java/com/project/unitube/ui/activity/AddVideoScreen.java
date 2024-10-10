package com.project.unitube.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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

import androidx.appcompat.app.AppCompatActivity;
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
    private Button uploadVideoCoverButton;
    private Button uploadVideoButton;
    private Button addVideoButton;
    private ImageView uploadVideoCoverImage;

    private Uri selectedPhotoUri;
    private Uri selectedVideoUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
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

    private void createAndAddVideo() {
        try {
            // Log start of the method
            Log.d("createAndAddVideo", "Start creating and adding video");

            Uri selectedVideoUri = getSelectedVideoUri();
            Uri selectedCoverPhotoUri = getSelectedPhotoUri();
            String duration = getVideoDuration(selectedVideoUri);

            // Log selected URIs and duration
            Log.d("createAndAddVideo", "Selected video URI: " + selectedVideoUri);
            Log.d("createAndAddVideo", "Selected cover photo URI: " + selectedCoverPhotoUri);
            Log.d("createAndAddVideo", "Video duration: " + duration);

            VideoViewModel videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
            videoViewModel.getHighestVideoId().observe(this, highestId -> {
                if (highestId != null) {
                    // Log highest video ID
                    Log.d("createAndAddVideo", "Fetched highest video ID: " + highestId);

                    // Create a VideoUploadRequest with the next video ID
                    int nextVideoId = highestId + 1;
                    VideoUploadRequest request = new VideoUploadRequest(
                            nextVideoId,
                            videoTitle.getText().toString(),
                            videoDescription.getText().toString(),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()),
                            duration,
                            UserManager.getInstance().getCurrentUser().getProfilePicture()
                    );

                    // Log the video upload request details
                    Log.d("createAndAddVideo", "Video upload request created with ID: " + nextVideoId);

                    // Convert URIs to File objects
                    File videoFile = new File(selectedVideoUri.getPath());
                    File thumbnailFile = new File(selectedCoverPhotoUri.getPath());

                    // Log file paths
                    Log.d("createAndAddVideo", "Video file path: " + videoFile.getPath());
                    Log.d("createAndAddVideo", "Thumbnail file path: " + thumbnailFile.getPath());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST || requestCode == CAPTURE_IMAGE_REQUEST) {
                handlePhotoActivityResult(requestCode, resultCode, data);
            }
            if (requestCode == PICK_VIDEO_REQUEST || requestCode == CAPTURE_VIDEO_REQUEST) {
                handleVideoActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void handlePhotoActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                    selectedPhotoUri = data.getData();
                    // Save the image to a file
                    File imageFile = saveUriToFile(selectedPhotoUri, "IMG_", ".jpg");
                    selectedPhotoUri = Uri.fromFile(imageFile);
                    // show the selected image
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoUri);
                    uploadVideoCoverImage.setImageBitmap(bitmap);
                    uploadVideoCoverImage.setTag(selectedPhotoUri.toString());
                } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                    // The selectedPhotoUri is already set in captureImageFromCamera()
                    if (selectedPhotoUri != null) {
                        // show the selected image
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoUri);
                        uploadVideoCoverImage.setImageBitmap(bitmap);
                        uploadVideoCoverImage.setTag(selectedPhotoUri.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public Uri getSelectedPhotoUri() {
        return selectedPhotoUri;
    }

    public Uri getSelectedVideoUri() {
        return selectedVideoUri;
    }

}

