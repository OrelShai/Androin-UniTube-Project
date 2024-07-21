package com.project.unitube;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * VideoLoader is responsible for loading and displaying video details including the video itself,
 * title, description, uploader name, and uploader profile picture.
 */
public class VideoLoader {

    private Context context;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView uploaderNameTextView;
    private ImageView uploaderProfileImageView;

    /**
     * Constructs a VideoLoader with the specified context and UI components.
     *
     * @param context                 the context from which the VideoLoader is instantiated
     * @param videoView               the VideoView to display the video
     * @param titleTextView           the TextView to display the video's title
     * @param descriptionTextView     the TextView to display the video's description
     * @param uploaderNameTextView    the TextView to display the uploader name
     * @param uploaderProfileImageView the ImageView to display the uploader profile picture
     */
    public VideoLoader(Context context, VideoView videoView, TextView titleTextView, TextView descriptionTextView,
                       TextView uploaderNameTextView, ImageView uploaderProfileImageView) {
        this.context = context;
        this.videoView = videoView;
        this.titleTextView = titleTextView;
        this.descriptionTextView = descriptionTextView;
        this.uploaderNameTextView = uploaderNameTextView;
        this.uploaderProfileImageView = uploaderProfileImageView;
    }

    /**
     * Loads the video details into the corresponding UI components.
     *
     * @param video the Video object containing the details to be loaded
     */
    public void loadVideo(Video video) {
        // Set the video's title
        titleTextView.setText(video.getTitle());

        // Set the video's description
        descriptionTextView.setText(video.getDescription());

        // Set the uploader name
        uploaderNameTextView.setText(video.getUser().getFirstName() + " " + video.getUser().getLastName());

        // Set the uploader profile picture
        int profileImageResourceId = context.getResources().getIdentifier(video.getUser().getProfilePicture(), "drawable", context.getPackageName());
        if (profileImageResourceId != 0) {
            uploaderProfileImageView.setImageResource(profileImageResourceId);
        } else {
            uploaderProfileImageView.setImageResource(R.drawable.ic_profile_placeholder);
        }

        // Prepare the video URI
        Uri videoUri;
        int videoResourceId = context.getResources().getIdentifier(video.getUrl(), "raw", context.getPackageName());
        if (videoResourceId != 0) {
            videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + videoResourceId);
        } else {
            videoUri = Uri.parse(video.getUrl());
        }

        // Set the video URI to the VideoView
        videoView.setVideoURI(videoUri);

        // Start the video when it is prepared
        videoView.setOnPreparedListener(mp -> {
            videoView.start();
        });
    }
}