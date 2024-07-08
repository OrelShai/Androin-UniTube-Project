package com.project.unitube;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoLoader {

    private Context context;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView uploaderNameTextView;
    private ImageView uploaderProfileImageView;

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
        // Set the video title and description
        titleTextView.setText(video.getTitle());
        descriptionTextView.setText(video.getDescription());

        // Set the uploader name and last name
        uploaderNameTextView.setText(video.getUser().getFirstName() + " " + video.getUser().getLastName());

        // Load uploader profile image
        int profileImageResourceId = context.getResources().getIdentifier(video.getUser().getProfilePicture(), "drawable", context.getPackageName());
        if (profileImageResourceId != 0) {
            uploaderProfileImageView.setImageResource(profileImageResourceId);
        } else {
            uploaderProfileImageView.setImageResource(R.drawable.placeholder_profile); // Fallback profile image
        }

        // Construct the Uri for the video in the raw folder
        int videoResourceId = context.getResources().getIdentifier(video.getUrl(), "raw", context.getPackageName());
        Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + videoResourceId);

        // Set the video URI and start the video
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}
