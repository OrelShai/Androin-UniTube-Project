package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.User;
import com.project.unitube.network.objectAPI.UserAPI;

import java.util.LinkedList;
import java.util.List;

public class UserRepository {
    private UserAPI userAPI;
    private final UserDao userDao;
    private UserListData userListData;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userDao = db.userDao();
        userListData = new UserListData();
        userAPI = new UserAPI(userDao);
    }

    public List<User> getAllVideos() {
        return userDao.getAllUsers();
    }

    public User getVideoByID(String userName) {
        return userDao.getUserByID(userName);
    }

    public void insertUser(User user) {
        userAPI.createUser(user);
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public void deleteUser(User user) {
        userDao.deleteUser(user);
    }

    class UserListData extends MutableLiveData<List<User>> {
        public UserListData() {
            super();
            setValue(new LinkedList<User>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                userListData.postValue(userDao.getAllUsers());
            }).start();
        }

        public LiveData<List<User>> getAll() {
            return userListData;
        }
    }

}