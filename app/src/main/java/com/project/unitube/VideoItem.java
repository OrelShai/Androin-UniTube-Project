package com.project.unitube;

public class VideoItem {
    private String title;
    private String uploader;
    private String uploadDate;
    private String url;
    private String duration;
    private String thumbnailUrl;

    public VideoItem(String title, String uploader, String uploadDate, String url, String duration, String thumbnailUrl) {
        this.title = title;
        this.uploader = uploader;
        this.uploadDate = uploadDate;
        this.url = url;
        this.duration = duration;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUploader() {
        return uploader;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getUrl() {
        return url;
    }

    public String getDuration() {
        return duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}