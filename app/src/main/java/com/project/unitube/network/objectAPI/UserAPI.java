package com.project.unitube.network.objectAPI;

import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.entities.User;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.network.interfaceAPI.UserWebServiceAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserAPI {
    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    private UserDao userDao;
    Retrofit retrofit;
    UserWebServiceAPI userWebServiceAPI;

    public UserAPI(UserDao userDao) {
        this.userDao = userDao;
        retrofit = RetrofitClient.getClient();
        userWebServiceAPI = retrofit.create(UserWebServiceAPI.class);
    }


    public MutableLiveData<User> getUser(String username) {
        Call<User> call = userWebServiceAPI.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                new Thread(() -> {
                    currentUser.postValue(response.body());
                }).start();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
        return currentUser;
    }


    public MutableLiveData<User> loginUser(String username, String password) {
        Call<User> call = userWebServiceAPI.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user != null && user.getPassword().equals(password)) {
                    // Store the user locally using Room
                        currentUser.postValue(user);
                } else {
                    currentUser.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                currentUser.postValue(null);
            }
        });
        return currentUser;
    }


    public void createUser(User user) {
        Call<Void> call = userWebServiceAPI.createUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                userDao.insertUser(user);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

}
