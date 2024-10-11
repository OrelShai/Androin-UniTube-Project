package com.project.unitube.utils.helper;

public class VideoUploadRequest {
    private int id;
    private String title;
    private String description;
    private String uploadDate;
    private String duration;
    private String profilePicture;

    // Constructor
    public VideoUploadRequest(int id, String title, String description, String uploadDate, String duration, String profilePicture) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadDate;
        this.duration = duration;
        this.profilePicture = profilePicture;
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

    public String getUploadDate() {
        return uploadDate;
    }

    public String getDuration() {
        return duration;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
