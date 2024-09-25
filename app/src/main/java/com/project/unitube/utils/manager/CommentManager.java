package com.project.unitube.utils.manager;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.unitube.R;
import com.project.unitube.entities.Comment;
import com.project.unitube.entities.User;
import com.project.unitube.entities.Video;
import com.project.unitube.ui.adapter.CommentAdapter;

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

        initialize();
    }

    private void initialize() {
        User currentUser = UserManager.getInstance().getCurrentUser();

        // Set the user's profile image if logged in, otherwise set a placeholder image
        if (currentUser != null) {
            String profilePictureName = currentUser.getProfilePicture();
            int profileImageResourceId = context.getResources().getIdentifier(profilePictureName, "drawable", context.getPackageName());
            if (profileImageResourceId != 0) {
                userProfileImageView.setImageResource(profileImageResourceId);
            } else {
                userProfileImageView.setImageURI(Uri.parse(currentUser.getProfilePicture())); // Fallback profile image
            }
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
                    currentUser.getUserName(),
                    Uri.parse(currentUser.getProfilePicture()),
                    commentText
            );

            comments.add(newComment);
            commentAdapter.notifyItemInserted(comments.size() - 1);
            commentAdapter.notifyItemRangeChanged(comments.size() - 1, comments.size());
            commentEditText.setText("");

            // Update the comment count
            commentCountTextView.setText("(" + comments.size() + ")");
        } else {
            Toast.makeText(context, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
