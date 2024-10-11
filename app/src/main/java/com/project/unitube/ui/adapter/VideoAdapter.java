package com.project.unitube.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.project.unitube.R;
import com.project.unitube.Unitube;
import com.project.unitube.entities.Video;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.ui.activity.UserPageActivity;
import com.project.unitube.ui.activity.VideoPlayActivity;
import com.project.unitube.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final Context context;
    private List<Video> videos = new ArrayList<>();

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

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
        Video video = videos.get(position);

        setTextViews(holder, video);
        String thumbnailUrl = video.getThumbnailUrl();
        if (!thumbnailUrl.startsWith("https://")) {
            thumbnailUrl = RetrofitClient.getBaseUrl() + thumbnailUrl;
        }
        Log.d("loadThumbnail", "Loading '" + video.getTitle() + "' thumbnail: " + video.getThumbnailUrl());
        loadThumbnail(holder.videoThumbnail, thumbnailUrl);
        loadProfilePicture(holder.uploaderProfileImage, video.getProfilePicture());
        setClickListeners(holder, video);
    }

    private void setTextViews(VideoViewHolder holder, Video video) {
        holder.videoTitle.setText(video.getTitle());
        holder.videoUploader.setText(video.getUploader());
        holder.videoUploadDate.setText(video.getUploadDate());
        holder.videoDuration.setText(video.getDuration());
    }

    private void loadThumbnail(ImageView imageView, String thumbnailUrl) {
        Glide.with(context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.placeholder_thumbnail)
                .error(R.drawable.error_thumbnail)
                .into(imageView);
    }

    private void loadProfilePicture(ImageView imageView, String profilePicture) {
        Glide.with(context)
                .load(profilePicture)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.error_profile)
                .circleCrop()
                .into(imageView);
    }

    private void setClickListeners(VideoViewHolder holder, Video video) {
        holder.itemView.setOnClickListener(v -> openVideoPlayActivity(video.getId()));
        holder.uploaderProfileImage.setOnClickListener(v -> openUserPageActivity(video.getUploader()));
    }

    private void openVideoPlayActivity(int videoId) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("VIDEO_ID", videoId);
        context.startActivity(intent);
    }

    private void openUserPageActivity(String username) {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.getUserByUsername(username).observe((LifecycleOwner) context, user -> {
            if (user != null) {
                Intent intent = new Intent(context, UserPageActivity.class);
                intent.putExtra("USER", user);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return videos.size(); // Using the filtered list size
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