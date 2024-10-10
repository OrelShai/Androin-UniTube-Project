package com.project.unitube.repository;


import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.User;
import com.project.unitube.network.objectAPI.UserAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private UserAPI userAPI;
    private final UserDao userDao;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userDao = db.userDao();
        userAPI = new UserAPI();
    }

    public MutableLiveData<String> createUser(User user, Uri photoUri) {
        return userAPI.createUser(user, photoUri);
    }

    public MutableLiveData<User> getUserByUsername(String username) {
        return userAPI.getUserByUsername(username);
    }

    public MutableLiveData<User> loginUser(String username, String password) {
        return userAPI.loginUser(username, password);
    }

    public MutableLiveData<String> deleteUser(String userName) {
        return userAPI.deleteUser(userName);
    }

    public MutableLiveData<String> updateUser(User user, Uri photoUri) {
        return userAPI.updateUser(user, photoUri);
    }
}