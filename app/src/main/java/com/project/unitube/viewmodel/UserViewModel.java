package com.project.unitube.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.Unitube;
import com.project.unitube.entities.User;
import com.project.unitube.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;


    public UserViewModel() {
        userRepository = new UserRepository(Unitube.context);
    }

    public  MutableLiveData<String> createUser(User user) {
        return userRepository.createUser(user);
    }

    public MutableLiveData<User> getUser(String username) {
        return userRepository.getUser(username);
    }

    public MutableLiveData<User> loginUser(String username, String password) {
        return userRepository.loginUser(username, password);
    }

    public MutableLiveData<String> deleteUser(String userName, String token) {
        return userRepository.deleteUser(userName, token);
    }

    public MutableLiveData<String> updateUser(User user) {
        return userRepository.updateUser(user);
    }
}
