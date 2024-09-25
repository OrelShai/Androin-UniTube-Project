package com.project.unitube.utils.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.unitube.R;

public class VideoContentManager {

    private Context context;
    private Activity activity;
    private LinearLayout videoContentLayout;
    private LinearLayout expandedCommentsLayout;

    public VideoContentManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        initializeLayouts();
        setupClickListeners();
    }

    private void initializeLayouts() {
        videoContentLayout = activity.findViewById(R.id.video_content_layout);
        expandedCommentsLayout = activity.findViewById(R.id.expanded_comments_layout);
    }

    private void setupClickListeners() {
        // Click listener for comment layout to expand comments
        View commentLayout = activity.findViewById(R.id.comment_layout);
        commentLayout.setOnClickListener(v -> showExpandedComments());

        // Click listener for close button in expanded comments layout
        ImageView closeButton = expandedCommentsLayout.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> showVideoContent());
    }

    private void showExpandedComments() {
        videoContentLayout.setVisibility(View.GONE);
        expandedCommentsLayout.setVisibility(View.VISIBLE);
    }

    private void showVideoContent() {
        expandedCommentsLayout.setVisibility(View.GONE);
        videoContentLayout.setVisibility(View.VISIBLE);
    }
}