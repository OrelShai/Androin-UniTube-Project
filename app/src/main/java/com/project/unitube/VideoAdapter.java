package com.project.unitube;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;

    public VideoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = Videos.videosList.get(position); // Using the global videos list
        holder.videoTitle.setText(video.getTitle());
        holder.videoUploader.setText(video.getUser().getUserName());
        holder.videoUploadDate.setText(video.getUploadDate());
        holder.videoDuration.setText(video.getDuration());

        // Load video thumbnail
        int thumbnailResourceId = context.getResources().getIdentifier(video.getThumbnailUrl(), "drawable", context.getPackageName());
        if (thumbnailResourceId != 0) {
            holder.videoThumbnail.setImageResource(thumbnailResourceId);
        } else {
            holder.videoThumbnail.setImageResource(R.drawable.placeholder_image); // Fallback image
        }

        // Load uploader profile image
        int profileImageResourceId = context.getResources().getIdentifier(video.getUser().getProfilePicture(), "drawable", context.getPackageName());
        if (profileImageResourceId != 0) {
            holder.uploaderProfileImage.setImageResource(profileImageResourceId);
        } else {
            holder.uploaderProfileImage.setImageResource(R.drawable.placeholder_profile); // Fallback profile image
        }

        // Handle more options button logic
        holder.moreOptionsButton.setOnClickListener(v -> {
            // Handle more options logic here
        });

        // Set click listener on the whole item view to open VideoPlayActivity
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to start VideoPlayActivity
            Intent intent = new Intent(context, VideoPlayActivity.class);
            // Pass the video ID to the activity
            intent.putExtra("VIDEO_ID", video.getId());
            // Start the activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Videos.videosList.size(); // Using the global videos list size
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUploader;
        TextView videoUploadDate;
        TextView videoDuration;
        ImageView uploaderProfileImage;
        ImageButton moreOptionsButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUploader = itemView.findViewById(R.id.videoUploader);
            videoUploadDate = itemView.findViewById(R.id.videoUploadDate);
            videoDuration = itemView.findViewById(R.id.videoDuration);
            uploaderProfileImage = itemView.findViewById(R.id.uploaderProfileImage);
            moreOptionsButton = itemView.findViewById(R.id.moreOptionsButton);
        }
    }
}
