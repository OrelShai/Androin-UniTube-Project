package com.project.unitube.utils;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.project.unitube.R;

public class VideoController {
    private Context context;
    private VideoView videoView;
    private ImageButton playPauseButton;
    private boolean isPlaying = true;
    private Handler handler;
    private GestureDetector gestureDetector;

    public VideoController(Context context, VideoView videoView, ImageButton playPauseButton) {
        this.context = context;
        this.videoView = videoView;
        this.playPauseButton = playPauseButton;
        handler = new Handler();
        initialize();
    }

    private void initialize() {
        playPauseButton.setOnClickListener(v -> togglePlayPause());
        setupGestureDetector();
        scheduleHide();
    }

    private void togglePlayPause() {
        if (isPlaying) {
            videoView.pause();
            playPauseButton.setImageResource(R.drawable.ic_play);
        } else {
            videoView.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);
        }
        isPlaying = !isPlaying;
        scheduleHide();
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (playPauseButton.getVisibility() == View.VISIBLE) {
                    playPauseButton.setVisibility(View.GONE);
                } else {
                    playPauseButton.setVisibility(View.VISIBLE);
                    scheduleHide();
                }
                return true;
            }
        });
    }

    public void onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
    }

    private void scheduleHide() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> playPauseButton.setVisibility(View.GONE), 3000); // Hide after 3 seconds
    }
}