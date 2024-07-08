package com.project.unitube;

public class Comment {
    private User user;
    private String commentText;

    // Constructor
    public Comment(User user, String commentText) {
        this.user = user;
        this.commentText = commentText;
    }

    // Getters
    public User getUser() {
        return user;
    }

    public String getCommentText() {
        return commentText;
    }

    // Method to get user's full name
    public String getUserFullName() {
        return user.getFullName();
    }

    // Method to get user's profile picture
    public String getUserProfilePicture() {
        return user.getProfilePicture();
    }
}
