package com.project.unitube.ui.activity;

import android.Manifest;
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

import com.project.unitube.R;
import com.project.unitube.entities.Video;
import com.project.unitube.entities.Videos;
import com.project.unitube.ui.adapter.CommentAdapter;
import com.project.unitube.ui.adapter.VideoAdapter;
import com.project.unitube.utils.VideoController;
import com.project.unitube.utils.VideoInteractionHandler;
import com.project.unitube.utils.VideoLoader;
import com.project.unitube.utils.manager.CommentManager;
import com.project.unitube.utils.manager.VideoContentManager;

public class VideoPlayActivity extends AppCompatActivity implements CommentAdapter.CommentAdapterListener {

    private static final String TAG = "VideoPlayActivity";
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;

    private VideoView videoView;
    private TextView titleTextView, descriptionTextView, uploaderNameTextView, likeCountTextView,
            dislikeCountTextView, commentCountTextView, timeIndicator;
    private ImageView uploaderProfileImageView, userProfileImageView;
    private LinearLayout likeButton, dislikeButton;
    private RecyclerView commentsRecyclerView, recommendedVideosRecyclerView;
    private EditText commentEditText;
    private ImageButton uploadCommentButton, playPauseButton;
    private View progressPlayed, progressIndicator;

    private Video currentVideo;
    private VideoContentManager videoContentManager;
    private CommentManager commentManager;
    private VideoController videoController;

    private final Handler handler = new Handler();
    private Runnable updateProgressAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        initializeUIComponents();
        requestStoragePermission();
        initializeManagers();
        loadVideoFromIntent();
    }

    private void initializeUIComponents() {
        // Initialize all UI components
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

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }

    private void initializeManagers() {
        videoContentManager = new VideoContentManager(this, this);
        videoController = new VideoController(this, videoView, playPauseButton);
    }

    private void loadVideoFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("VIDEO_ID")) {
            int videoId = intent.getIntExtra("VIDEO_ID", -1);
            currentVideo = getVideoById(videoId);

            if (currentVideo != null) {
                loadVideo();
                updateVideoData();
                initializeVideoInteraction();
                initializeCommentManager();
                initializeRecommendedVideos();
                startProgressUpdates();
            }
        }
    }

    private void loadVideo() {
        VideoLoader videoLoader = new VideoLoader(this, videoView, titleTextView, descriptionTextView,
                uploaderNameTextView, uploaderProfileImageView);
        videoLoader.loadVideo(currentVideo);

        // Start playing the video automatically
        videoView.start();
    }

    private void updateVideoData() {
        likeCountTextView.setText(String.valueOf(currentVideo.getLikesList().size()));
        dislikeCountTextView.setText(String.valueOf(currentVideo.getDislikesList().size()));
        commentCountTextView.setText("(" + currentVideo.getComments().size() + ")");

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter commentAdapter = new CommentAdapter(this, currentVideo.getComments(), this);
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    private void initializeVideoInteraction() {
        new VideoInteractionHandler(this, currentVideo.getId(), likeButton, dislikeButton,
                likeCountTextView, dislikeCountTextView);
    }

    private void initializeCommentManager() {
        commentManager = new CommentManager(this, currentVideo, commentEditText, uploadCommentButton,
                (CommentAdapter) commentsRecyclerView.getAdapter(), currentVideo.getComments(),
                commentCountTextView, userProfileImageView);
    }

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

    private void startProgressUpdates() {
        updateProgressAction = new Runnable() {
            @Override
            public void run() {
                updateProgress();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateProgressAction);
    }

    private Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null;
    }

    @Override
    public void onCommentDeleted(int newCommentCount) {
        commentCountTextView.setText("(" + newCommentCount + ")");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        videoController.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @SuppressLint("SetTextI18n")
    private void updateProgress() {
        int currentPosition = videoView.getCurrentPosition();
        int duration = videoView.getDuration();

        if (duration > 0) {
            timeIndicator.setText(formatTime(currentPosition) + " / " + formatTime(duration));

            float progress = (float) currentPosition / duration;

            progressPlayed.post(() -> {
                int totalWidth = ((View) progressPlayed.getParent()).getWidth();
                int progressWidth = (int) (progress * totalWidth);

                ViewGroup.LayoutParams params = progressPlayed.getLayoutParams();
                params.width = progressWidth;
                progressPlayed.setLayoutParams(params);

                progressIndicator.setTranslationX(progressWidth - ((float) progressIndicator.getWidth() / 2));
            });
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateProgressAction);
    }

    public void updateVideoDetails(Video video) {
        titleTextView.setText(video.getTitle());
        descriptionTextView.setText(video.getDescription());
    }
}