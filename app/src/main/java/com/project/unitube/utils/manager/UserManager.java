package com.project.unitube.utils.manager;

import com.project.unitube.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private List<User> users;
    private User currentUser;
    public static String token;

    private UserManager() {
        users = new ArrayList<>();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}


