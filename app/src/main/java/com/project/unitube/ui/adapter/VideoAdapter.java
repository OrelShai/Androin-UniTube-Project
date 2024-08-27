package com.project.unitube.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.unitube.R;
import com.project.unitube.entities.Video;
import com.project.unitube.entities.Videos;
import com.project.unitube.ui.activity.VideoPlayActivity;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final Context context;

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
        Video video = Videos.videosToShow.get(position); // Using the filtered list

        holder.videoTitle.setText(video.getTitle());
        holder.videoUploader.setText(video.getUser().getUserName());
        holder.videoUploadDate.setText(video.getUploadDate());
        holder.videoDuration.setText(video.getDuration());

        // Load video thumbnail
        int thumbnailResourceId = context.getResources().getIdentifier(video.getThumbnailUrl(), "drawable", context.getPackageName());
        if (thumbnailResourceId != 0) {
            holder.videoThumbnail.setImageResource(thumbnailResourceId);
        } else {
            holder.videoThumbnail.setImageURI(Uri.parse(video.getThumbnailUrl()));
        }

        // Load uploader profile image
        int profileImageResourceId = context.getResources().getIdentifier(video.getUser().getProfilePicture(), "drawable", context.getPackageName());
        if (profileImageResourceId != 0) {
            holder.uploaderProfileImage.setImageResource(profileImageResourceId);
        } else {
            holder.uploaderProfileImage.setImageURI(Uri.parse(video.getUser().getProfilePicture())); // Fallback profile image
        }



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
        return Videos.videosToShow.size(); // Using the filtered list size
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUploader;
        TextView videoUploadDate;
        TextView videoDuration;
        ImageView uploaderProfileImage;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUploader = itemView.findViewById(R.id.videoUploader);
            videoUploadDate = itemView.findViewById(R.id.videoUploadDate);
            videoDuration = itemView.findViewById(R.id.videoDuration);
            uploaderProfileImage = itemView.findViewById(R.id.uploaderProfileImage);
        }
    }
}