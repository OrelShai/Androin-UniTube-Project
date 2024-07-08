package com.project.unitube;

import static android.content.ContentValues.TAG;

import static com.project.unitube.Videos.videosList;

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
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class AddVideoScreen extends Activity {
    private EditText videoTitle;
    private EditText videoDescription;
    private Button uploadVideoCoverButton;
    private Button uploadVideoButton;
    private Button addVideoButton;
    private ImageView uploadVideoCoverImage;
    private Uri selectedVideoUri;
    private Uri selectedCoverPhotoUri;

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

        // Initialize Buttons
        uploadVideoCoverButton = findViewById(R.id.uploadVideoCoverButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);
        addVideoButton = findViewById(R.id.AddVideoButton);

        // Initialize ImageViews
        uploadVideoCoverImage = findViewById(R.id.uploadVideoCoverImage);
        ImageView uploadVideoImage = findViewById(R.id.uploadVideoImage);
    }

    private void setUpListeners() {
        // Set click listener for uploadVideoButton
        uploadVideoButton.setOnClickListener(view -> {
            showVideoPickerOptions();
        });

        // Set click listener for uploadVideoCoverButton
        uploadVideoCoverButton.setOnClickListener(view -> {
            showImagePickerOptions();
        });

        // Initialize Bottom Navigation
        addVideoButton.setOnClickListener(view -> {
            if (validateFields()) {
                // Create and add new video
                createAndAddVideo();
            }
        });
    }

    /**
     * Displays a dialog to allow the user to choose between picking
     * a video from the gallery or recording a new video with the camera.
     */
    private void showVideoPickerOptions() {
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

    /**
     * Initiates an intent to capture a video using the camera.
     */
    private void captureVideoFromCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_REQUEST);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initiates an intent to pick a video from the gallery.
     */
    private void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    /**
     * Displays an AlertDialog to allow the user to choose between picking
     * an image from the gallery or taking a new photo with the camera.
     */
    private void showImagePickerOptions() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Photo"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickImageFromGallery();
                            break;
                        case 1:
                            //if (checkCameraPermission()) {
                            captureImageFromCamera();
                            //}
                            break;
                    }
                });
        builder.show();
    }

    /**
     * Initiates an intent to pick an image from the gallery.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Initiates an intent to capture an image using the camera.
     */
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the image
            File photoFile = createImageFile();
            if (photoFile != null) {
                selectedCoverPhotoUri = FileProvider.getUriForFile(this, "com.project.unitube.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedCoverPhotoUri);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );
            return image;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Handles the result of video picking or capturing process.
     *
     * @param requestCode The request code identifying the intent
     * @param resultCode  The result code indicating success or failure
     * @param data        The data returned from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST && data != null && data.getData() != null) {
                // Handle picked video from gallery
                Toast.makeText(this, "Video picked from gallery", Toast.LENGTH_SHORT).show();
                selectedVideoUri = data.getData(); // Store the video URI
            } else if (requestCode == CAPTURE_VIDEO_REQUEST && data != null && data.getData() != null) {
                // Handle captured video from camera
                Toast.makeText(this, "Video captured from camera", Toast.LENGTH_SHORT).show();
                selectedVideoUri = data.getData(); // Store the video URI
            }

            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Handle picked image from gallery
                selectedCoverPhotoUri = data.getData(); // Store the cover photo URI
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedCoverPhotoUri);
                    uploadVideoCoverImage.setImageBitmap(bitmap);
                    uploadVideoCoverImage.setTag(selectedCoverPhotoUri.toString()); // Save the URI of the selected image
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Handle captured image from camera
                try {
                    // Load the image as Bitmap and display in the ImageView
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedCoverPhotoUri);
                    uploadVideoCoverImage.setImageBitmap(imageBitmap);
                    uploadVideoCoverImage.setTag(selectedCoverPhotoUri.toString()); // Save the URI of the selected image
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createAndAddVideo() {
        try {
            // Create a new Video object using the URIs and form data
            Video newVideoObject = new Video(
                    videoTitle.getText().toString(),
                    videoDescription.getText().toString(),
                    selectedVideoUri.toString(),
                    selectedCoverPhotoUri.toString(),
                    RegisterScreen.currentUser,
                    getVideoDuration(selectedVideoUri.toString())
            );
            videosList.add(newVideoObject); // Add the video to the list
            Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();

            // back to the main activity
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error uploading video", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        String Title = videoTitle.getText().toString();
        if (TextUtils.isEmpty(Title)) {
            videoTitle.setError("video title is required");
            isValid = false;
        }
        String description = videoDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            videoDescription.setError("video description is required");
            isValid = false;
        }
        if (selectedVideoUri == null) {
            Toast.makeText(this, "Video is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (selectedCoverPhotoUri == null) {
            Toast.makeText(this, "Cover photo is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    public String getVideoDuration(String videoUriString) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // Convert the string URI to a Uri object
            Uri videoUri = Uri.parse(videoUriString);

            // Set the data source to the video URI
            retriever.setDataSource(this, videoUri);

            // Retrieve the duration in milliseconds
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationInMillis = Long.parseLong(time);

            // Convert duration to a formatted string (HH:MM:SS or MM:SS)
            String formattedDuration = formatDuration(durationInMillis);

            return formattedDuration;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VideoDurationError", "Error retrieving video duration", e);
            return "00:00"; // Return a default value if there is an error
        } finally {
            retriever.release();
        }
    }

    // Helper method to format the duration in milliseconds to HH:MM:SS or MM:SS
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





}




