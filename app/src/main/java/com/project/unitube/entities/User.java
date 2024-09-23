package com.project.unitube.entities;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.project.unitube.utils.converter.UriConverter;

import java.io.Serializable;

@Entity
public class User implements Serializable {
    private String firstName;
    private String lastName;
    private  String password;
    @PrimaryKey
    @NonNull
    private final String userName;
    private String profilePicture;

    // Constructor
    public User(String firstName, String lastName, String password, @NonNull String userName, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userName = userName;
        this.profilePicture = profilePicture;
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

    @NonNull
    public String getUserName() {
        return userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}