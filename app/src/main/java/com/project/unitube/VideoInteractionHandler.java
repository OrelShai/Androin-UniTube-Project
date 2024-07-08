package com.project.unitube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class VideoInteractionHandler {

    private Context context;
    private int videoId;
    private LinearLayout likeButton;
    private LinearLayout dislikeButton;
    private TextView likeCountTextView;
    private TextView dislikeCountTextView;
    private ImageView likeIcon;
    private ImageView dislikeIcon;

    public VideoInteractionHandler(Context context, int videoId, LinearLayout likeButton, LinearLayout dislikeButton,
                                   TextView likeCountTextView, TextView dislikeCountTextView) {
        this.context = context;
        this.videoId = videoId;
        this.likeButton = likeButton;
        this.dislikeButton = dislikeButton;
        this.likeCountTextView = likeCountTextView;
        this.dislikeCountTextView = dislikeCountTextView;
        this.likeIcon = likeButton.findViewById(R.id.icon_like);
        this.dislikeIcon = dislikeButton.findViewById(R.id.icon_dislike);

        setupInteractionListeners();
        setupOtherButtons();
        updateButtonIcons();
    }

    private Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null;
    }

    private void setupInteractionListeners() {
        likeButton.setOnClickListener(v -> {
            Video currentVideo = getVideoById(videoId);
            if (RegisterScreen.currentUser == null) {
                showToast("You cannot like a video if you are not logged in.");
            } else if (currentVideo != null) {
                currentVideo.addLike(RegisterScreen.currentUser.getUserName());
                updateLikeDislikeCounts(currentVideo);
                saveVideoState(currentVideo); // Save state after updating
            }
        });

        dislikeButton.setOnClickListener(v -> {
            Video currentVideo = getVideoById(videoId);
            if (RegisterScreen.currentUser == null) {
                showToast("You cannot dislike a video if you are not logged in.");
            } else if (currentVideo != null) {
                currentVideo.addDislike(RegisterScreen.currentUser.getUserName());
                updateLikeDislikeCounts(currentVideo);
                saveVideoState(currentVideo); // Save state after updating
            }
        });
    }

    private void setupOtherButtons() {
        // Initialize download button
        View downloadButton = ((VideoPlayActivity) context).findViewById(R.id.button_download);
        ImageView downloadIcon = downloadButton.findViewById(R.id.button_icon);
        TextView downloadText = downloadButton.findViewById(R.id.button_text);

        // Set icon and text for download button
        downloadIcon.setImageResource(R.drawable.ic_download); // Ensure you have an appropriate icon in the drawable folder
        downloadText.setText("Download");

        // Set click listener for the download button
        downloadButton.setOnClickListener(v -> {
            // Show standard toast message
            Toast.makeText(context, "Download.", Toast.LENGTH_SHORT).show();
        });

        // Initialize share button
        View shareButton = ((VideoPlayActivity) context).findViewById(R.id.button_share);
        ImageView shareIcon = shareButton.findViewById(R.id.button_icon);
        TextView shareText = shareButton.findViewById(R.id.button_text);

        // Set icon and text for share button
        shareIcon.setImageResource(R.drawable.ic_share); // Ensure you have an appropriate icon in the drawable folder
        shareText.setText("Share");

        // Set click listener for the share button
        shareButton.setOnClickListener(v -> {
            // Show standard toast message
            showToast("Share.");
        });

        // Initialize more button
        View moreButton = ((VideoPlayActivity) context).findViewById(R.id.button_more);
        ImageView moreIcon = moreButton.findViewById(R.id.button_icon);
        TextView moreText = moreButton.findViewById(R.id.button_text);

        // Set icon and text for more button
        moreIcon.setImageResource(R.drawable.ic_more); // Ensure you have an appropriate icon in the drawable folder
        moreText.setText("More");

        // Set click listener for the more button to show the popup menu
        moreButton.setOnClickListener(v -> showPopupMenu(v));
    }

    private void updateButtonIcons() {
        Video currentVideo = getVideoById(videoId);
        if (currentVideo != null && RegisterScreen.currentUser != null) {
            boolean isLiked = currentVideo.getLikesList().contains(RegisterScreen.currentUser.getUserName());
            boolean isDisliked = currentVideo.getDislikesList().contains(RegisterScreen.currentUser.getUserName());

            // Assuming you have appropriate icons for liked, unliked, disliked, and undisliked states
            int likeIconRes = isLiked ? R.drawable.ic_liked : R.drawable.ic_like;
            int dislikeIconRes = isDisliked ? R.drawable.ic_disliked : R.drawable.ic_dislike;

            likeIcon.setImageResource(likeIconRes);
            dislikeIcon.setImageResource(dislikeIconRes);
        }
    }

    private void updateLikeDislikeCounts(Video currentVideo) {
        likeCountTextView.setText(String.valueOf(currentVideo.getLikesList().size()));
        dislikeCountTextView.setText(String.valueOf(currentVideo.getDislikesList().size()));
        updateButtonIcons(); // Update button icons
    }

    private void showPopupMenu(View anchorView) {
        // Inflate the popup menu layout
        LayoutInflater inflater = LayoutInflater.from(context);
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
        editButton.setOnClickListener(v -> {
            // Handle edit action
            showToast("Edit.");
            popupWindow.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            // Handle delete action
            showToast("Delete.");
            popupWindow.dismiss();
        });

        // Show the popup window
        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void saveVideoState(Video video) {
        // Save the video state to the global list or a persistent storage
        for (int i = 0; i < Videos.videosList.size(); i++) {
            if (Videos.videosList.get(i).getId() == video.getId()) {
                Videos.videosList.set(i, video);
                break;
            }
        }
        // Optionally save to persistent storage if needed (e.g., database, shared preferences)
    }
}
