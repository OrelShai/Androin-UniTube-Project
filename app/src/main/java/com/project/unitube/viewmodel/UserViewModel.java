package com.project.unitube.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.Unitube;
import com.project.unitube.entities.User;
import com.project.unitube.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    private MutableLiveData<User> userMutableLiveData;

    public UserViewModel() {
        userRepository = new UserRepository(Unitube.context);
        userMutableLiveData = new MutableLiveData<>();
    }

    public void insertUser(User user) {
        userRepository.insertUser(user);
    }


    public MutableLiveData<User> getUserMutableLiveData() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;
    }
}
