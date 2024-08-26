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
    private MutableLiveData<User> currentUser;

    private MutableLiveData<List<User>> users;

    private UserDao userDao;
    Retrofit retrofit;
    UserWebServiceAPI userWebServiceAPI;

    public UserAPI(UserDao userDao) {
        this.userDao = userDao;
        retrofit = RetrofitClient.getClient();
        userWebServiceAPI = retrofit.create(UserWebServiceAPI.class);
    }

    public void getUser() {
        Call<User> call = userWebServiceAPI.getUser();
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
    }

    public void getAllUsers() {
        Call<List<User>> call = userWebServiceAPI.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                new Thread(() -> {
                    userDao.deleteAllUsers();
                    userDao.insertAllUsers(response.body());
                    users.postValue(userDao.getAllUsers());
                }).start();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });
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
