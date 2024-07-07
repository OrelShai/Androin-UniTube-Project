package com.project.unitube;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView uploaderProfileImageView;
    private TextView uploaderNameTextView;
    private LinearLayout likeButton;
    private LinearLayout dislikeButton;
    private TextView likeCountTextView;
    private TextView dislikeCountTextView;
    private Video currentVideo;

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
        likeButton = findViewById(R.id.button_like);
        dislikeButton = findViewById(R.id.button_dislike);
        likeCountTextView = findViewById(R.id.like_count);
        dislikeCountTextView = findViewById(R.id.dislike_count);

        // Load video if intent contains video ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIDEO_ID")) {
            int videoId = intent.getIntExtra("VIDEO_ID", -1);
            currentVideo = getVideoById(videoId);
            if (currentVideo != null) {
                loadVideo(currentVideo);
                updateLikeDislikeCounts();
            }
        }

        // Set up like button listener
        likeButton.setOnClickListener(v -> {
            if (RegisterScreen.currentUser == null) {
                showToast("You cannot like a video if you are not logged in.");
            } else if (currentVideo != null) {
                currentVideo.addLike(RegisterScreen.currentUser.getUserName());
                updateLikeDislikeCounts();
            }
        });

        // Set up dislike button listener
        dislikeButton.setOnClickListener(v -> {
            if (RegisterScreen.currentUser == null) {
                showToast("You cannot dislike a video if you are not logged in.");
            } else if (currentVideo != null) {
                currentVideo.addDislike(RegisterScreen.currentUser.getUserName());
                updateLikeDislikeCounts();
            }
        });

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
                showToast("Download...");
            }
        });

        // Initialize share button
        View shareButton = findViewById(R.id.button_share);
        ImageView shareIcon = shareButton.findViewById(R.id.button_icon);
        TextView shareText = shareButton.findViewById(R.id.button_text);

        // Set icon and text for share button
        shareIcon.setImageResource(R.drawable.ic_share); // Ensure you have an appropriate icon in the drawable folder
        shareText.setText("Share");

        // Set click listener for the share button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom toast message for 2 seconds
                showToast("Share...");
            }
        });

        // Initialize more button
        View moreButton = findViewById(R.id.button_more);
        ImageView moreIcon = moreButton.findViewById(R.id.button_icon);
        TextView moreText = moreButton.findViewById(R.id.button_text);

        // Set icon and text for more button
        moreIcon.setImageResource(R.drawable.ic_more); // Ensure you have an appropriate icon in the drawable folder
        moreText.setText("More");

        // Set click listener for the more button to show the popup menu
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void updateLikeDislikeCounts() {
        // Update like and dislike counts
        likeCountTextView = likeButton.findViewById(R.id.like_count);
        dislikeCountTextView = dislikeButton.findViewById(R.id.dislike_count);

        likeCountTextView.setText(String.valueOf(currentVideo.getLikes()));
        dislikeCountTextView.setText(String.valueOf(currentVideo.getDislikes()));
    }

    private void showPopupMenu(View anchorView) {
        // Inflate the popup menu layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.editor_video_menu, null);

        // Create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        // Initialize edit and delete buttons
        View editButton = popupView.findViewById(R.id.button_edit);
        ImageView editIcon = editButton.findViewById(R.id.button_icon);
        TextView editText = editButton.findViewById(R.id.button_text);
        editIcon.setImageResource(R.drawable.ic_edit);
        editIcon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(17), dpToPx(17)));
        editIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        editText.setText("    Edit");

        View deleteButton = popupView.findViewById(R.id.button_delete);
        ImageView deleteIcon = deleteButton.findViewById(R.id.button_icon);
        TextView deleteText = deleteButton.findViewById(R.id.button_text);
        deleteIcon.setImageResource(R.drawable.ic_delete);
        deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(17), dpToPx(17)));
        deleteIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        deleteText.setText("  Delete");

        // Set click listeners for edit and delete buttons
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit action
                showToast("Edit...");
                popupWindow.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete action
                showToast("Delete...");
                popupWindow.dismiss();
            }
        });

        // Show the popup window
        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void showToast(String message) {
        // Show custom toast message
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_layout_root));

        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);

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
        }, 2000); // 2000 milliseconds = 2 seconds
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

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null; // or handle the case when video is not found
    }
}
