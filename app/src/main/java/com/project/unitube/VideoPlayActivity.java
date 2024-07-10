package com.project.unitube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoPlayActivity extends AppCompatActivity implements CommentAdapter.CommentAdapterListener {

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
    private VideoContentManager videoContentManager;
    private TextView commentCountTextView;
    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private ImageButton uploadCommentButton;
    private ImageView userProfileImageView;
    private CommentManager commentManager;
    private RecyclerView recommendedVideosRecyclerView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        // Initialize views
        initializeViews();

        // Initialize VideoContentManager
        videoContentManager = new VideoContentManager(this, this);

        // Load video if intent contains video ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIDEO_ID")) {
            int videoId = intent.getIntExtra("VIDEO_ID", -1);
            currentVideo = getVideoById(videoId);
            if (currentVideo != null) {
                // Load video using VideoLoader
                VideoLoader videoLoader = new VideoLoader(this, videoView, titleTextView, descriptionTextView,
                        uploaderNameTextView, uploaderProfileImageView);
                videoLoader.loadVideo(currentVideo);

                // Update the changed data display
                videoDataUpdate(currentVideo);

                // Handle user interactions using VideoInteractionHandler
                new VideoInteractionHandler(this, videoId, likeButton, dislikeButton, likeCountTextView, dislikeCountTextView);

                // Initialize CommentManager
                commentManager = new CommentManager(this, currentVideo, commentEditText, uploadCommentButton,
                        (CommentAdapter) commentsRecyclerView.getAdapter(), currentVideo.getComments(), commentCountTextView, userProfileImageView);

                // Initialize the recommended videos RecyclerView
                initializeRecommendedVideos();
            }
        }
    }

    private void initializeViews() {
        videoView = findViewById(R.id.video_view);
        titleTextView = findViewById(R.id.video_title);
        descriptionTextView = findViewById(R.id.video_description);
        uploaderProfileImageView = findViewById(R.id.uploaderProfileImage);
        uploaderNameTextView = findViewById(R.id.uploaderName);
        likeButton = findViewById(R.id.button_like);
        dislikeButton = findViewById(R.id.button_dislike);
        likeCountTextView = findViewById(R.id.like_count);
        dislikeCountTextView = findViewById(R.id.dislike_count);
        commentCountTextView = findViewById(R.id.comment_count);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentEditText = findViewById(R.id.comment_edit_text);
        uploadCommentButton = findViewById(R.id.upload_comment_button);
        userProfileImageView = findViewById(R.id.comment_user_profile_image);
        recommendedVideosRecyclerView = findViewById(R.id.recommended_videos_recycler_view);
    }

    private void initializeRecommendedVideos() {
        recommendedVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        VideoAdapter videoAdapter = new VideoAdapter(this);
        recommendedVideosRecyclerView.setAdapter(videoAdapter);
    }

    /**
     * Retrieves the Video object with the specified ID from the list of videos.
     *
     * @param videoId The ID of the video to retrieve.
     * @return The Video object with the matching ID, or null if not found.
     */
    private Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null; // Handle the case when video is not found
    }

    /**
     * Updates the UI with the current video's dynamic data such as likes, dislikes, and comments count.
     *
     * @param currentVideo The video object containing the data to be displayed.
     */
    private void videoDataUpdate(Video currentVideo) {
        // Set initial like and dislike counts
        likeCountTextView.setText(String.valueOf(currentVideo.getLikesList().size()));
        dislikeCountTextView.setText(String.valueOf(currentVideo.getDislikesList().size()));

        // Set the text of the TextView to display the count in parentheses
        commentCountTextView.setText("(" + currentVideo.getComments().size() + ")");

        // Set up the RecyclerView for comments
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter commentAdapter = new CommentAdapter(this, currentVideo.getComments(), this);
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void onCommentDeleted(int newCommentCount) {
        // Update the comment count text view
        commentCountTextView.setText("(" + newCommentCount + ")");
    }
}
