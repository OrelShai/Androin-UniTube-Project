package com.project.unitube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.Room.Database.AppDB;
import com.project.unitube.entities.User;
//import com.project.unitube.network.UserService;

import java.util.List;
//import java.util.concurrent.Executor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
//    private final UserService userService;
    private final Executor executor;

    public UserRepository(Context context) {
        AppDB db = AppDB.getInstance(context);
        userDao = db.userDao();
//        userService = new UserService(); // Assuming UserService is already implemented
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserByID(String userName) {
        return userDao.getUserByID(userName);
    }

    public void insertUser(User user) {
        executor.execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        executor.execute(() -> userDao.updateUser(user));
    }

    public void deleteUser(User user) {
        executor.execute(() -> userDao.deleteUser(user));
    }

//    public void fetchUsersFromNetwork() {
//        executor.execute(() -> {
//            List<User> users = userService.getUsers(); // Assuming this method is synchronous
//            userDao.insertUsers(users);
//        });
//    }
}