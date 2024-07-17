package com.project.unitube;

import android.net.Uri;

public class Comment {
    private static int nextId = 1;

    private int id;
    private String userName;
    private Uri profilePicture;
    private String commentText;

    public Comment(String userName, Uri profilePicture, String commentText) {
        this.id = nextId++;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.commentText = commentText;
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
