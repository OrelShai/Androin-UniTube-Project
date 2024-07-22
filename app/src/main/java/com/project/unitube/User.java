package com.project.unitube;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private String password;
    private String userName;
    private String profilePicture;
    private Uri profilePictureUri;

    // Constructor
    public User(String firstName, String lastName, String password, String userName, String profilePicture, Uri profilePictureUri) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.profilePictureUri = profilePictureUri;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = (profilePicture != null && !profilePicture.isEmpty()) ? profilePicture : "placeholder_profile";
    }
}