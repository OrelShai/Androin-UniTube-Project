package com.project.unitube.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
public class Video implements Serializable {
    @PrimaryKey
    private final int id;
    private static int nextId = 1;


    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private User user;
    private int likes;
    private int dislikes;
    private String uploadDate;
    private final String duration;
    private List<String> likesList;
    private List<String> dislikesList;
    private List<Comment> comments;

    // Constructor with thumbnailUrl
    public Video(String title, String description, String url, String thumbnailUrl, User user, String duration) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : generateThumbnail(url, 7); // Use provided thumbnail or generate
        this.user = user;
        this.likes = 0;
        this.dislikes = 0;
        this.uploadDate = getCurrentDate();
        this.duration = duration;
        this.likesList = new ArrayList<>();
        this.dislikesList = new ArrayList<>();
        this.comments = new ArrayList<>(); // Initialize the comments list
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDuration() {
        return duration;
    }

    public List<String> getLikesList() {
        return likesList;
    }

    public List<String> getDislikesList() {
        return dislikesList;
    }

    // Methods to handle likes and dislikes
    public void addLike(String userName) {
        if (!likesList.contains(userName)) {
            likesList.add(userName);
            likes++;
            removeDislike(userName); // Remove dislike if present
        }
    }

    public void removeLike(String userName) {
        if (likesList.contains(userName)) {
            likesList.remove(userName);
            likes--;
        }
    }

    public void addDislike(String userName) {
        if (!dislikesList.contains(userName)) {
            dislikesList.add(userName);
            dislikes++;
            removeLike(userName); // Remove like if present
        }
    }

    public void removeDislike(String userName) {
        if (dislikesList.contains(userName)) {
            dislikesList.remove(userName);
            dislikes--;
        }
    }

    public boolean hasUserLiked(String userName) {
        return likesList.contains(userName);
    }

    public boolean hasUserDisliked(String userName) {
        return dislikesList.contains(userName);
    }

    // Helper method to generate a thumbnail at a specific time
    private String generateThumbnail(String url, int seconds) {
        // Placeholder logic to generate a thumbnail from the video at the given time
        // This should be replaced with actual logic to capture a frame from the video
        return "default_thumbnail"; // Example thumbnail URL
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }


    public boolean isLikedBy(String userName) {
        return likesList.contains(userName);
    }

    public boolean isDislikedBy(String userName) {
        return dislikesList.contains(userName);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}