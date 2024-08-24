package com.project.unitube.webServices;

import androidx.lifecycle.MutableLiveData;

import com.project.unitube.R;
import com.project.unitube.entities.Video;
import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.unitube;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public VideoAPI(MutableLiveData<List<Video>> postListData, VideoDao dao) {

        retrofit = new Retrofit.Builder()
                .baseUrl(unitube.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get() {
        Call<List<Video>> call = webServiceAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {

                List<Video> videos = response.body();
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
            }
        });
    }
}
