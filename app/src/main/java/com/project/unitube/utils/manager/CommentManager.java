package com.project.unitube.utils.manager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.project.unitube.R;
import com.project.unitube.Unitube;
import com.project.unitube.entities.Comment;
import com.project.unitube.entities.User;
import com.project.unitube.entities.Video;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.ui.adapter.CommentAdapter;
import com.project.unitube.viewmodel.CommentViewModel;
import com.project.unitube.viewmodel.UserViewModel;

import java.util.List;

public class CommentManager {
    private Context context;
    private Video currentVideo;
    private EditText commentEditText;
    private ImageButton uploadCommentButton;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;
    private TextView commentCountTextView;
    private ImageView userProfileImageView;
    private CommentViewModel commentViewModel;

    public CommentManager(Context context, Video currentVideo, EditText commentEditText, ImageButton uploadCommentButton,
                          CommentAdapter commentAdapter, List<Comment> comments, TextView commentCountTextView, ImageView userProfileImageView) {
        this.context = context;
        this.currentVideo = currentVideo;
        this.commentEditText = commentEditText;
        this.uploadCommentButton = uploadCommentButton;
        this.commentAdapter = commentAdapter;
        this.comments = comments;
        this.commentCountTextView = commentCountTextView;
        this.userProfileImageView = userProfileImageView;
        this.commentViewModel = new CommentViewModel();

        initialize();
    }

    private void initialize() {
        User currentUser = UserManager.getInstance().getCurrentUser();

        // Set the user's profile image if logged in, otherwise set a placeholder image
        if (currentUser != null) {
            // Construct the full profile picture URL
            String baseUrl = RetrofitClient.getBaseUrl();
            String profilePhotoUrl = baseUrl + currentUser.getProfilePicture();  // Combine base URL and path

            Glide.with(context)
                    .load(profilePhotoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.default_profile) // Placeholder in case of loading issues
                    .into(userProfileImageView);

        } else {
            userProfileImageView.setImageResource(R.drawable.ic_profile_placeholder); // Default image when not logged in
        }

        uploadCommentButton.setOnClickListener(v -> {
            if (currentUser == null) {
                // User is not logged in, show notification
                Toast.makeText(context, "You must be logged in to add a comment.", Toast.LENGTH_SHORT).show();
            } else {
                // User is logged in, add the comment
                addComment();
            }
        });
    }

    private void addComment() {
        String commentText = commentEditText.getText().toString().trim();
        User currentUser = UserManager.getInstance().getCurrentUser();

        if (!commentText.isEmpty()) {
            Comment newComment = new Comment(
                    currentVideo.getId(),
                    currentUser.getUserName(),
                    currentUser.getProfilePicture(),
                    commentText
            );

            // Add the comment to the database
            commentViewModel.createComment(newComment).observe((LifecycleOwner) context, result -> {
                if (result.equals("Success")) {
                    Toast.makeText(context, "Comment added successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to add comment. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

            // fetch all comments including the new one and update the UI
            commentViewModel.getCommentsForVideo(currentVideo.getId()).observe((LifecycleOwner) context, comments -> {
                this.comments.clear();
                this.comments.addAll(comments);
                commentAdapter.notifyDataSetChanged();

                for (Comment comment : comments) {
                    Log.d("newCommentManager", "New_comment_id : " + comment.getId() + " new_comment_text: " + comment.getCommentText());
                }
            });

            commentEditText.setText("");

            // Update the comment count
            commentCountTextView.setText("(" + comments.size() + ")");
        } else {
            Toast.makeText(context, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
