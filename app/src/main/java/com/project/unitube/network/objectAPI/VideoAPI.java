package com.project.unitube.network.objectAPI;

import static com.project.unitube.Unitube.context;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.Video;
import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.network.interfaceAPI.VideoWebServiceAPI;
import com.project.unitube.utils.helper.EditVideoRequest;
import com.project.unitube.utils.helper.VideoUploadRequest;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoAPI {
    private MutableLiveData<List<Video>> videoListData;
    VideoWebServiceAPI videoWebServiceAPI;
    private VideoDao videoDao;

    public VideoAPI(MutableLiveData<List<Video>> VideoListData) {
        this.videoListData = VideoListData;
        Retrofit retrofit = RetrofitClient.getClient();
        videoWebServiceAPI = retrofit.create(VideoWebServiceAPI.class);

        // Initialize Room DAO
        AppDB db = AppDB.getInstance(context);
        this.videoDao = db.videoDao();  // Initialize VideoDao for Room operations
    }

    public MutableLiveData<List<Video>> getAllVideos() {
        // First, check local cache (Room) in new thread
        new Thread(() -> {
            List<Video> localVideos = videoDao.getAllVideos();
            if (!localVideos.isEmpty()) {
                videoListData.postValue(localVideos);  // Return cached videos
            }
        }).start();

        Call<List<Video>> call = videoWebServiceAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update Room with the new list of videos in new thread
                    new Thread(() -> {
                        List<Video> videos = response.body();
                        videoDao.insertAllVideos(videos);
                    }).start();

                    // Update LiveData with the new list of videos
                    videoListData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
            }
        });
        return videoListData;
    }

    public MutableLiveData<Video> getVideoByID(int userId, int id) {
        MutableLiveData<Video> videoData = new MutableLiveData<>();

        // First, check Room cache in new thread
        new Thread(() -> {
            Video localVideo = videoDao.getVideoByID(id);
            if (localVideo != null) {
                videoData.postValue(localVideo);  // Return cached video
            }
        }).start();

        // Then, fetch from the server
        Call<Video> call = videoWebServiceAPI.getVideoById(userId, id);
        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Video video = response.body();

                    // Update Room with the fetched video in new thread
                    new Thread(() -> {
                        videoDao.insertVideo(video);
                    }).start();

                    // Update LiveData with the new video data
                    videoData.postValue(video);
                } else {
                    // Log the failure case if response is not successful or body is null
                    Log.w("VideoAPI", "Failed to fetch video. Response Code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                // Log the error in case the call fails
                Log.e("VideoAPI", "Error fetching video: " + t.getMessage(), t);
            }
        });

        return videoData;
    }


    public LiveData<List<Video>> getUserVideos(String username) {
        MutableLiveData<List<Video>> videosLiveData = new MutableLiveData<>();

        // First, check Room cache in new thread
        new Thread(() -> {
            List<Video> localVideos = videoDao.getVideosByUploader(username);
            if (!localVideos.isEmpty()) {
                videosLiveData.postValue(localVideos);  // Return cached videos
            }
        }).start();

        videoWebServiceAPI.getUserVideos(username).enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videosLiveData.postValue(response.body());

                    // Update Room with the fetched videos in new thread
                    new Thread(() -> {
                        videoDao.insertAllVideos(response.body());
                    }).start();
                } else {
                    videosLiveData.postValue(null);
                    // Log error here
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                videosLiveData.postValue(null);
                // Log error here
            }
        });

        return videosLiveData;
    }

    public LiveData<Video> uploadVideo(String userName, VideoUploadRequest request, File videoFile, File thumbnailFile) {
        MutableLiveData<Video> videoLiveData = new MutableLiveData<>();

        Log.d("uploadVideo", "VideoAPI- Starting upload video for user: " + userName);

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(request.getId()));
        Log.d("uploadVideo", "VideoAPI- Created idBody: " + request.getId());

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), request.getTitle());
        Log.d("uploadVideo", "VideoAPI- Created titleBody: " + request.getTitle());

        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), request.getDescription());
        Log.d("uploadVideo", "VideoAPI- Created descriptionBody: " + request.getDescription());

        RequestBody uploadDateBody = RequestBody.create(MediaType.parse("text/plain"), request.getUploadDate());
        Log.d("uploadVideo", "VideoAPI- Created uploadDateBody: " + request.getUploadDate());

        RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), request.getDuration());
        Log.d("uploadVideo", "VideoAPI- Created durationBody: " + request.getDuration());

        RequestBody profilePictureBody = RequestBody.create(MediaType.parse("text/plain"), request.getProfilePicture());
        Log.d("uploadVideo", "VideoAPI- Created profilePictureBody: " + request.getProfilePicture());

        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        Log.d("uploadVideo", "VideoAPI- Created videoRequestBody for file: " + videoFile.getName());

        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("url", videoFile.getName(), videoRequestBody);
        Log.d("uploadVideo", "VideoAPI- Created videoPart: " + videoFile.getName());

        RequestBody thumbnailRequestBody = RequestBody.create(MediaType.parse("image/*"), thumbnailFile);
        Log.d("uploadVideo", "VideoAPI- Created thumbnailRequestBody for file: " + thumbnailFile.getName());

        MultipartBody.Part thumbnailPart = MultipartBody.Part.createFormData("thumbnailUrl", thumbnailFile.getName(), thumbnailRequestBody);
        Log.d("uploadVideo", "VideoAPI- Created thumbnailPart: " + thumbnailFile.getName());

        Log.d("uploadVideo", "VideoAPI- Sending upload request to server...");

        videoWebServiceAPI.uploadVideo(
                userName,
                idBody,
                titleBody,
                descriptionBody,
                uploadDateBody,
                durationBody,
                profilePictureBody,
                videoPart,
                thumbnailPart
        ).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("uploadVideo", "VideoAPI- Video upload successful for user: " + userName);
                    Log.d("uploadVideo", "VideoAPI- Received video ID: " + response.body().getId());
                    Video video = response.body();
                    // Insert the newly added video into Room in a new thread
                    new Thread(() -> {
                        videoDao.insertVideo(video);
                    }).start();

                    videoLiveData.postValue(video);
                } else {
                    Log.d("uploadVideo", "VideoAPI- Video upload failed for user: " + userName + " - Response not successful. Response code: " + response.code());
                    videoLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.d("uploadVideo", "VideoAPI- Video upload failed for user: " + userName + " - Error: " + t.getMessage());
                videoLiveData.postValue(null);
            }
        });

        return videoLiveData;
    }


    public LiveData<Boolean> deleteVideo(String userName, int videoId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        videoWebServiceAPI.deleteVideo(userName, videoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.postValue(response.isSuccessful());

                if (response.isSuccessful()) {
                    // Delete the video from Room in a new thread
                    new Thread(() -> {
                        Video video = videoDao.getVideoByID(videoId);
                        videoDao.deleteVideo(video);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.postValue(false);
            }
        });
        return result;
    }

    public LiveData<Video> toggleLike(int videoId, String userName) {
        MutableLiveData<Video> videoData = new MutableLiveData<>();

        // Create a request body with userName
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("userName", userName);

        videoWebServiceAPI.toggleLike(videoId, requestBody).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videoData.postValue(response.body());
                } else {
                    videoData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                videoData.postValue(null);
            }
        });
        return videoData;
    }

    public LiveData<Video> toggleDislike(int videoId, String userName) {
        MutableLiveData<Video> videoData = new MutableLiveData<>();

        // Create a request body with userName
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("userName", userName);

        videoWebServiceAPI.toggleDislike(videoId, requestBody).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videoData.postValue(response.body());
                } else {
                    videoData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                videoData.postValue(null);
            }
        });
        return videoData;
    }

    public LiveData<Video> incrementVideoViews(int videoId) {
        MutableLiveData<Video> videoData = new MutableLiveData<>();
        videoWebServiceAPI.incrementVideoViews(videoId).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videoData.postValue(response.body());
                } else {
                    videoData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                videoData.postValue(null);
            }
        });
        return videoData;
    }

    public LiveData<Video> editVideo(String userName, int videoId, String newTitle, String newDescription) {
        MutableLiveData<Video> videoData = new MutableLiveData<>();
        EditVideoRequest request = new EditVideoRequest(newTitle, newDescription);

        videoWebServiceAPI.editVideo(userName, videoId, request).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Video video = response.body();

                    // Update Room with the edited video in a new thread
                    new Thread(() -> {
                        videoDao.updateVideo(video);
                    }).start();

                    videoData.postValue(video);
                } else {
                    videoData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                videoData.postValue(null);
            }
        });
        return videoData;
    }

    public LiveData<Integer> getHighestVideoId() {
        MutableLiveData<Integer> highestIdLiveData = new MutableLiveData<>();

        videoWebServiceAPI.getHighestVideoId().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int highestId = response.body().get("highestId").getAsInt();
                    highestIdLiveData.postValue(highestId); // Use postValue() instead of setValue()
                } else {
                    highestIdLiveData.postValue(null); // Use postValue() instead of setValue()
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                highestIdLiveData.postValue(null); // Use postValue() instead of setValue()
            }
        });

        return highestIdLiveData;
    }
}
