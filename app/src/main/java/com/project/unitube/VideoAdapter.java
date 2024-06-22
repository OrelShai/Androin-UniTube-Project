package com.project.unitube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
    private List<Video> videoList;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
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

        holder.moreOptionsButton.setOnClickListener(v -> {
            // Handle more options logic here
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
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
