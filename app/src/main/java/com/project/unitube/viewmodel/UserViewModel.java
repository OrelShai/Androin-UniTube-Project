package com.project.unitube.viewmodel;

import android.net.Uri;

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

    public MutableLiveData<String> createUser(User user, Uri photoUri) {
        return userRepository.createUser(user, photoUri);
    }

    public LiveData<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public MutableLiveData<User> loginUser(String username, String password) {
        return userRepository.loginUser(username, password);
    }

    public MutableLiveData<String> deleteUser(String userName) {
        return userRepository.deleteUser(userName);
    }

    public MutableLiveData<String> updateUser(User user, Uri photoUri) {
        return userRepository.updateUser(user, photoUri);
    }
}
