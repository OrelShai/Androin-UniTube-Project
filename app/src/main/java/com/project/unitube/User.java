package com.project.unitube;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String userName;
    private final String profilePicture;
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

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

}