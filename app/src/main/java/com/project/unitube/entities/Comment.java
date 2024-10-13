package com.project.unitube.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Comment {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;  // MongoDB ObjectId
    private int videoId;  // Stores the video ID this comment belongs to
    @SerializedName("name")
    private String userName;
    private String profilePicture;
    @SerializedName("text")
    private String commentText;

    public Comment(int videoId, String userName, String profilePicture, String commentText) {
        this.videoId = videoId;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.commentText = commentText;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
