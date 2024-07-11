package com.project.unitube;

import static com.project.unitube.Videos.videosList;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import  com.project.unitube.Videos;


import com.project.unitube.Comment;

public class DataManager {
    private List<User> userList;
    private List<FakeProfileForComments> fakeProfilesForComments;

    public void logCommentsForAllVideos() {
        for (Video video : Videos.videosList) {
            Log.d("DataManager", "Video: " + video.getTitle());
            for (Comment comment : video.getComments()) {
                Log.d("DataManager", "Comment by " + comment.getUserName() + ": " + comment.getCommentText());
            }
        }
    }

    // Constructor initializes lists and calls parsing methods
    public DataManager(Context context) {
        userList = new ArrayList<>();
        fakeProfilesForComments = new ArrayList<>();
        Videos.videosList = new ArrayList<>();
        parseUsers(context);
        parseVideos(context);
        parseComments(context);
        logCommentsForAllVideos();  // Add this line to log comments after parsing
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Video> getVideoList() {
        return Videos.videosList;
    }

    // Parses users from profiles.json
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

    // Parses videos from videos.json
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

    // Parses comments and links them to the corresponding videos
    private void parseComments(Context context) {
        try {
            // Parse random comments profiles
            InputStream profilesStream = context.getAssets().open("randomCommentsProfiles.json");
            Scanner profilesScanner = new Scanner(profilesStream).useDelimiter("\\A");
            String profilesJsonString = profilesScanner.hasNext() ? profilesScanner.next() : "";
            JSONArray profilesJsonArray = new JSONArray(profilesJsonString);

            for (int i = 0; i < profilesJsonArray.length(); i++) {
                JSONObject profileObject = profilesJsonArray.getJSONObject(i);
                String name = profileObject.getString("name");
                String profilePicture = profileObject.getString("profilePicture");

                FakeProfileForComments profile = new FakeProfileForComments(name, profilePicture);
                fakeProfilesForComments.add(profile);
            }

            // Parse comments and assign them to videos
            InputStream commentsStream = context.getAssets().open("comments.json");
            Scanner commentsScanner = new Scanner(commentsStream).useDelimiter("\\A");
            String commentsJsonString = commentsScanner.hasNext() ? commentsScanner.next() : "";
            JSONArray commentsJsonArray = new JSONArray(commentsJsonString);

            for (int i = 0; i < commentsJsonArray.length(); i++) {
                JSONObject videoCommentsObject = commentsJsonArray.getJSONObject(i);
                String videoTitle = videoCommentsObject.getString("title");
                JSONArray commentsArray = videoCommentsObject.getJSONArray("comments");

                Video video = getVideoByTitle(videoTitle);
                if (video != null) {
                    for (int j = 0; j < commentsArray.length(); j++) {
                        JSONObject commentObject = commentsArray.getJSONObject(j);
                        String name = commentObject.getString("name");
                        String text = commentObject.getString("text");

                        String profilePicture = getProfilePictureByName(name);
                        Comment comment = new Comment(name, profilePicture, text);
                        video.addComment(comment);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retrieves the profile picture URL for a given name
    private String getProfilePictureByName(String name) {
        for (FakeProfileForComments profile : fakeProfilesForComments) {
            if (profile.getName().equals(name)) {
                return profile.getProfilePicture();
            }
        }
        return "default_profile_picture"; // Default profile picture if not found
    }

    // Retrieves the video duration for a given URL
    private String getVideoDuration(Context context, String url) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
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

    // Retrieves a User object by its username
    private User getUserByUserName(String userName) {
        for (User user : userList) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null; // or throw an exception if user is not found
    }

    // Retrieves a Video object by its title
    private Video getVideoByTitle(String title) {
        for (Video video : Videos.videosList) {
            if (video.getTitle().equals(title)) {
                return video;
            }
        }
        return null;
    }

    // Retrieves a Video object by its ID
    public Video getVideoById(int videoId) {
        for (Video video : Videos.videosList) {
            if (video.getId() == videoId) {
                return video;
            }
        }
        return null;
    }

    // Internal class to represent fake profiles used for comments
    private class FakeProfileForComments {
        private String name;
        private String profilePicture;

        public FakeProfileForComments(String name, String profilePicture) {
            this.name = name;
            this.profilePicture = profilePicture;
        }

        public String getName() {
            return name;
        }

        public String getProfilePicture() {
            return profilePicture;
        }
    }
}
