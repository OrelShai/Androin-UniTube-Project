package com.project.unitube.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.project.unitube.Room.converter.CommentListConverter;
import com.project.unitube.Room.converter.StringListConverter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
public class Video implements Serializable {
    private static int nextId = 1;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private String uploader;
    private int likes;
    private int dislikes;
    private String uploadDate;
    private String duration;
    private String profilePicture;
    @TypeConverters(StringListConverter.class)
    private List<String> likesList;
    @TypeConverters(StringListConverter.class)
    private List<String> dislikesList;
    @TypeConverters(CommentListConverter.class)
    private List<Comment> comments;

    public Video() {}

    public Video(String title, String description, String url, String thumbnailUrl,
                 String uploader, String duration, String profilePicture) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : "default_thumbnail";
        // Use provided thumbnail or generate
        this.uploader = uploader;
        this.likes = 0;
        this.dislikes = 0;
        this.uploadDate = getCurrentDate();;
        this.duration = duration;
        this.profilePicture = profilePicture;
        this.likesList = new ArrayList<>();
        this.dislikesList = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUploader() {
        return uploader;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getDuration() {
        return duration;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public List<String> getLikesList() {
        return likesList;
    }

    public List<String> getDislikesList() {
        return dislikesList;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }


    // Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setLikesList(List<String> likesList) {
        this.likesList = likesList;
    }

    public void setDislikesList(List<String> dislikesList) {
        this.dislikesList = dislikesList;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
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
}