package com.project.unitube.entities;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.project.unitube.utils.converter.UriConverter;

@Entity
public class Comment {
    private static int nextId = 1;
    @PrimaryKey
    private  int id;
    private String userName;
    @TypeConverters(UriConverter.class)
    private Uri profilePicture;
    private String commentText;

    public Comment(String userName, Uri profilePicture, String commentText) {
        this.id = nextId++;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.commentText = commentText;
    }

    public static void setNextId(int nextId) {
        Comment.nextId = nextId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
