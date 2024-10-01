package com.project.unitube.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import com.project.unitube.ui.adapter.CommentAdapter;
import com.project.unitube.utils.manager.CommentManager;
import com.project.unitube.R;
import com.project.unitube.ui.adapter.VideoAdapter;
import com.project.unitube.utils.manager.VideoContentManager;
import com.project.unitube.utils.VideoController;
import com.project.unitube.utils.VideoInteractionHandler;
import com.project.unitube.utils.VideoLoader;
import com.project.unitube.entities.Video;
import com.project.unitube.entities.Videos;

/**
 * VideoPlayActivity handles the playback of a video, as well as displaying video details,
 * user interactions (likes, dislikes, comments), and recommended videos.
 */
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
    private VideoController videoController;
    private ImageButton playPauseButton;
    private TextView timeIndicator;
    private View progressPlayed;
    private View progressIndicator;

    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;

    private static final String TAG = "VideoPlayActivity";

    private final Handler handler = new Handler();
    private Runnable updateProgressAction;

    /**
     * Initializes the activity, sets up the views, and loads the video if provided via intent.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                          this Bundle contains the data it most recently supplied.
     */
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        // Initialize views
        initializeUIComponents();

        // Initialize VideoContentManager
        videoContentManager = new VideoContentManager(this, this);

        // Initialize VideoController
        videoController = new VideoController(this, videoView, playPauseButton);

        // Request permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }

        // Load video if intent contains video ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIDEO_ID")) {
            int videoId = intent.getIntExtra("VIDEO_ID", -1);
            currentVideo = getVideoById(videoId);

            Log.d("My VideoPlayActivity", "my Video id: " + currentVideo.getId());
            Log.d("My VideoPlayActivity", "my Video url: " + currentVideo.getUrl());

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

                // Update progress bar and time indicator
                updateProgressAction = new Runnable() {
                    @Override
                    public void run() {
                        updateProgress();
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(updateProgressAction);
            }
        }
    }

    /**
     * Initializes the views by finding them by their IDs.
     */
    private void initializeUIComponents() {
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
        playPauseButton = findViewById(R.id.play_pause_button);
        timeIndicator = findViewById(R.id.time_indicator);
        progressPlayed = findViewById(R.id.progress_played);
        progressIndicator = findViewById(R.id.progress_indicator);
    }

    /**
     * Initializes the recommended videos list by filtering out the current video.
     */
    private void initializeRecommendedVideos() {
        Videos.videosToShow.clear();
        for (Video video : Videos.videosList) {
            if (video.getId() != currentVideo.getId()) {
                Videos.videosToShow.add(video);
            }
        }
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

    /**
     * Called when a comment is deleted to update the comment count.
     *
     * @param newCommentCount The new comment count after deletion.
     */
    @Override
    public void onCommentDeleted(int newCommentCount) {
        // Update the comment count text view
        commentCountTextView.setText("(" + newCommentCount + ")");
    }

    /**
     * Handles touch events for the video controller.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        videoController.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Updates the progress bar and time indicator based on the video's current position.
     */
    @SuppressLint("SetTextI18n")
    private void updateProgress() {
        int currentPosition = videoView.getCurrentPosition();
        int duration = videoView.getDuration();

        if (duration > 0) {
            // Update time indicator
            timeIndicator.setText(formatTime(currentPosition) + " / " + formatTime(duration));

            // Calculate progress as a fraction
            float progress = (float) currentPosition / duration;

            // Log the calculated progress for debugging
            Log.d(TAG, "Current position: " + currentPosition);
            Log.d(TAG, "Duration: " + duration);
            Log.d(TAG, "Calculated progress: " + progress);

            // Ensure progressPlayed has a valid width
            progressPlayed.post(new Runnable() {
                @Override
                public void run() {
                    // Calculate the new width of the progress bar based on the progress fraction
                    int totalWidth = ((View) progressPlayed.getParent()).getWidth();
                    int progressWidth = (int) (progress * totalWidth);

                    // Log the calculated progress width for debugging
                    Log.d(TAG, "Total width: " + totalWidth);
                    Log.d(TAG, "Progress width: " + progressWidth);

                    // Update progress bar width
                    ViewGroup.LayoutParams params = progressPlayed.getLayoutParams();
                    params.width = progressWidth;
                    progressPlayed.setLayoutParams(params);

                    // Update progress indicator position
                    progressIndicator.setTranslationX(progressWidth - ((float) progressIndicator.getWidth() / 2));
                }
            });
        }
    }

    /**
     * Formats time in milliseconds to a string format.
     *
     * @param millis Time in milliseconds.
     * @return Formatted time string.
     */
    @SuppressLint("DefaultLocale")
    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        int hours = millis / (1000 * 60 * 60);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Removes the update progress action callback when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateProgressAction);
    }

    /**
     * Updates the video details in the UI.
     *
     * @param video The video object containing the details to be displayed.
     */
    public void updateVideoDetails(Video video) {
        titleTextView.setText(video.getTitle());
        descriptionTextView.setText(video.getDescription());
    }

}