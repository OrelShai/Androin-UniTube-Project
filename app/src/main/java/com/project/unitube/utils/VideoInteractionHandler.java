package com.project.unitube.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.project.unitube.R;
import com.project.unitube.entities.Videos;
import com.project.unitube.utils.manager.UserManager;
import com.project.unitube.entities.User;
import com.project.unitube.entities.Video;
import com.project.unitube.ui.activity.VideoPlayActivity;
import com.project.unitube.viewmodel.VideoViewModel;

public class VideoInteractionHandler {

    private Context context;
    private int videoId;
    private LinearLayout likeButton;
    private LinearLayout dislikeButton;
    private TextView likeCountTextView;
    private TextView dislikeCountTextView;
    private ImageView likeIcon;
    private ImageView dislikeIcon;
    private Video currentVideo;
    private VideoViewModel videoViewModel;
    private LifecycleOwner lifecycleOwner;

    public static boolean updateDate = false;

    public VideoInteractionHandler(Context context, LifecycleOwner lifecycleOwner, int videoId,
                                   LinearLayout likeButton, LinearLayout dislikeButton,
                                   TextView likeCountTextView, TextView dislikeCountTextView,
                                   VideoViewModel videoViewModel) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.videoId = videoId;
        this.likeButton = likeButton;
        this.dislikeButton = dislikeButton;
        this.likeCountTextView = likeCountTextView;
        this.dislikeCountTextView = dislikeCountTextView;
        this.likeIcon = likeButton.findViewById(R.id.icon_like);
        this.dislikeIcon = dislikeButton.findViewById(R.id.icon_dislike);
        this.videoViewModel = videoViewModel;

        fetchVideoDetails();
        setupInteractionListeners();
        setupOtherButtons();
    }

    private void fetchVideoDetails() {
        videoViewModel.getVideoByID(-1, videoId).observe(lifecycleOwner, video -> {
            if (video != null) {
                currentVideo = video;
                updateButtonIcons();
                updateLikeDislikeCounts();
            } else {
                // Handle the case where video is null (error case)
                Toast.makeText(context, "Error: Unable to fetch video details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupInteractionListeners() {
        User currentUser = UserManager.getInstance().getCurrentUser();

        likeButton.setOnClickListener(v -> {
            if (currentUser == null) {
                showToast("You cannot like a video if you are not logged in.");
            } else if (currentVideo != null) {
                String userName = currentUser.getUserName();
                Log.d("VideoInteraction", "Attempting to toggle like for video ID: " + currentVideo.getId() + " by user: " + userName);
                toggleLike(userName);
            }
        });

        dislikeButton.setOnClickListener(v -> {
            if (currentUser == null) {
                showToast("You cannot dislike a video if you are not logged in.");
            } else if (currentVideo != null) {
                String userName = currentUser.getUserName();
                Log.d("VideoInteraction", "Attempting to toggle like for video ID: " + currentVideo.getId() + " by user: " + userName);
                toggleDislike(userName);
            }
        });
    }

    private void toggleLike(String userName) {
        videoViewModel.toggleLike(currentVideo.getId(), userName).observe(lifecycleOwner, result -> {
            if (result != null) {
                currentVideo = result;
                updateButtonIcons();
                updateLikeDislikeCounts();
            } else {
                showToast("Failed to update like status.");
            }
        });
    }

    private void toggleDislike(String userName) {
        videoViewModel.toggleDislike(currentVideo.getId(), userName).observe(lifecycleOwner, result -> {
            if (result != null) {
                currentVideo = result;
                updateButtonIcons();
                updateLikeDislikeCounts();
            } else {
                showToast("Failed to update dislike status.");
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
        moreIcon.setImageResource(R.drawable.ic_more_vertical); // Ensure you have an appropriate icon in the drawable folder
        moreText.setText("More");

        // Set click listener for the more button to show the popup menu
        moreButton.setOnClickListener(v -> showPopupMenu(v));
    }

    private void updateButtonIcons() {
        User currentUser = UserManager.getInstance().getCurrentUser();

        if (currentVideo != null && currentUser != null) {
            boolean isLiked = currentVideo.getLikesList().contains(currentUser.getUserName());
            boolean isDisliked = currentVideo.getDislikesList().contains(currentUser.getUserName());

            // Assuming you have appropriate icons for liked, unliked, disliked, and undisliked states
            int likeIconRes = isLiked ? R.drawable.ic_liked : R.drawable.ic_like;
            int dislikeIconRes = isDisliked ? R.drawable.ic_disliked : R.drawable.ic_dislike;

            likeIcon.setImageResource(likeIconRes);
            dislikeIcon.setImageResource(dislikeIconRes);
        }
    }

    private void updateLikeDislikeCounts() {
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
        editText.setText("    Edit");

        View deleteButton = popupView.findViewById(R.id.button_delete);
        ImageView deleteIcon = deleteButton.findViewById(R.id.button_icon);
        TextView deleteText = deleteButton.findViewById(R.id.button_text);
        deleteIcon.setImageResource(R.drawable.ic_delete);
        deleteText.setText("  Delete");

        // Set click listeners for edit and delete buttons
        editButton.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                showEditDialog();
                popupWindow.dismiss();
            } else {
                showLoginErrorMessage();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                showDeleteDialog();
                popupWindow.dismiss();
            } else {
                showLoginErrorMessage();
            }
        });

        // Show the popup window
        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Video");

        // Set up the input views
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_video, null);
        EditText editTitle = dialogView.findViewById(R.id.edit_video_title);
        EditText editDescription = dialogView.findViewById(R.id.edit_video_description);
        editTitle.setText(currentVideo.getTitle());
        editDescription.setText(currentVideo.getDescription());
        builder.setView(dialogView);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = editTitle.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();
            if (!newTitle.isEmpty() && !newDescription.isEmpty()) {
                currentVideo.setTitle(newTitle);
                currentVideo.setDescription(newDescription);
                saveVideoState(currentVideo); // Save updated state

                // Refresh UI elements if needed
                if (context instanceof VideoPlayActivity) {
                    ((VideoPlayActivity) context).updateVideoDetails(currentVideo);
                }
            } else {
                Toast.makeText(context, "Title and description cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Video");
        builder.setMessage("Are you sure you want to delete this video?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            Videos.videosList.remove(currentVideo);
            saveVideoState(currentVideo); // Update the state

            // Optionally, refresh the UI or navigate away from the current activity
            if (context instanceof VideoPlayActivity) {
                ((VideoPlayActivity) context).finish(); // Close the activity
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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
        updateDate = true;
        // Optionally save to persistent storage if needed (e.g., database, shared preferences)
    }

    private boolean isUserLoggedIn() {
        return UserManager.getInstance().getCurrentUser() != null;
    }

    private void showLoginErrorMessage() {
        showToast("You must be logged in to perform this action.");
    }

}
