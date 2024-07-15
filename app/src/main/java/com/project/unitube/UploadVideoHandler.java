package com.project.unitube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UploadVideoHandler {
    private final Activity activity;
    private Uri selectedVideoUri;

    private static final int PICK_VIDEO_REQUEST = 101;
    private static final int CAPTURE_VIDEO_REQUEST = 102;

    public UploadVideoHandler(Activity activity) {
        this.activity = activity;
    }

    public void showVideoPickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void captureVideoFromCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_REQUEST);
        } else {
            Toast.makeText(activity, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST && data != null && data.getData() != null) {
                // Handle picked video from gallery
                Toast.makeText(activity, "Video picked from gallery", Toast.LENGTH_SHORT).show();
                selectedVideoUri = data.getData(); // Store the video URI
            } else if (requestCode == CAPTURE_VIDEO_REQUEST && data != null && data.getData() != null) {
                // Handle captured video from camera
                Toast.makeText(activity, "Video captured from camera", Toast.LENGTH_SHORT).show();
                selectedVideoUri = data.getData(); // Store the video URI
            }
        }
    }

    public Uri getSelectedVideoUri() {
        return selectedVideoUri;
    }

    public String getVideoDuration(Uri videoUri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(activity, videoUri);
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
}
