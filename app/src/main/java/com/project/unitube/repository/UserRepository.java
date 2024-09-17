package com.project.unitube.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.User;
import com.project.unitube.network.objectAPI.UserAPI;

public class UserRepository {
    private UserAPI userAPI;
    private final UserDao userDao;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userDao = db.userDao();
        userAPI = new UserAPI();
    }

    public void insertUser(User user) {
        userAPI.createUser(user);
    }

    public MutableLiveData<User> getUser(String username) {
        return userAPI.getUser(username);
    }

    public MutableLiveData<User> loginUser(String username, String password) {
        return userAPI.loginUser(username, password);
    }

    public MutableLiveData<Boolean> isUsernameTaken(String username) {
        return userAPI.isUsernameTaken(username);
    }
}