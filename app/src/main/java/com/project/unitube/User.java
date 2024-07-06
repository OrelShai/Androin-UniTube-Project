package com.project.unitube;

import java.io.Serializable;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private String password;
    private String userName;
    private String profilePicture;

    // Constructor
    public User(String firstName, String lastName, String password, String userName, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userName = userName;
        this.profilePicture = (profilePicture != null && !profilePicture.isEmpty()) ? profilePicture : "placeholder_profile";
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        this.profilePicture = (profilePicture != null && !profilePicture.isEmpty()) ? profilePicture : "placeholder_profile";
    }
}
