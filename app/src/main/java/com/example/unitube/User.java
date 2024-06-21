package com.example.unitube;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String profilePictureUri; // New field for profile picture URI


    // Constructor
    public User(String firstName, String lastName, String username, String password, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.profilePictureUri = profilePictureUri;
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

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }
    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }
}
