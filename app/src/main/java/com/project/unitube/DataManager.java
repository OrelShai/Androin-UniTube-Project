package com.project.unitube;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataManager {
    private List<User> userList;

    public DataManager(Context context) {
        userList = new ArrayList<>();
        Videos.videosList = new ArrayList<>();
        parseUsers(context);
        parseVideos(context);
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Video> getVideoList() {
        return Videos.videosList;
    }

    private void parseUsers(Context context) {
        try {
            InputStream is = context.getAssets().open("profiles.json");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "";
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String firstName = jsonObject.getString("firstName");
                String lastName = jsonObject.getString("lastName");
                String password = jsonObject.getString("password");
                String userName = jsonObject.getString("userName");
                String profilePicture = jsonObject.optString("profilePicture", "placeholder_profile");

                User user = new User(firstName, lastName, password, userName, profilePicture);

                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseVideos(Context context) {
        try {
            InputStream is = context.getAssets().open("videos.json");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "";
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String description = jsonObject.getString("description");
                String url = jsonObject.getString("url");
                String thumbnailUrl = jsonObject.optString("thumbnailUrl", null);
                String uploader = jsonObject.getString("uploader");

                User videoUploader = getUserByUserName(uploader);
                String duration = getVideoDuration(context, url);

                Video video = new Video(title, description, url, thumbnailUrl, videoUploader, duration);

                Videos.videosList.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getVideoDuration(Context context, String url) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // Set the data source to the video file in the raw resource directory
            int rawResourceId = context.getResources().getIdentifier(url, "raw", context.getPackageName());
            retriever.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + rawResourceId));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillis = Long.parseLong(time);
            return String.format("%d:%02d", timeInMillis / 60000, (timeInMillis % 60000) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return "0:00"; // Default duration in case of error
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private User getUserByUserName(String userName) {
        for (User user : userList) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null; // or throw an exception if user is not found
    }

    public Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null;
    }
}
