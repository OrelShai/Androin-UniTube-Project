package com.project.unitube.network.objectAPI;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.project.unitube.CallBack;
import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.entities.User;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.network.interfaceAPI.UserWebServiceAPI;
import com.project.unitube.utils.manager.UserManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserAPI {
    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    Retrofit retrofit;
    UserWebServiceAPI userWebServiceAPI;
    CallBack callBack;

    public UserAPI( ) {
        retrofit = RetrofitClient.getClient();
        userWebServiceAPI = retrofit.create(UserWebServiceAPI.class);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
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

/*
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
*/

    public MutableLiveData<User> loginUser(String username, String password) {
        Call<String> call = userWebServiceAPI.processLogin(username, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String token = response.body();
                    // Store the user locally using Room
                    UserManager.token = token;
                    currentUser = getUser(username);
                } else {
                    currentUser.postValue(null);  // Set null in case of failure
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                currentUser.postValue(null);  // Set null in case of failure
            }
        });
        return currentUser;
    }


    public void createUser(User user) {
        Call<Void> call = userWebServiceAPI.createUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //userDao.insertUser(user);
                if (response.isSuccessful()) {
                    callBack.onSuccess("");
                } else if (response.code() == 409) {
                    callBack.onFail("Username already exists");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void deleteUser(String userName) {
        Call<Void> call = userWebServiceAPI.deleteUser(userName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //userDao.insertUser(user);
                if (response.isSuccessful()) {
                    Log.d("UserAPI", "User deleted successfully");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserAPI", "Error deleting user: " + t.getMessage());
            }
        });
    }

    public void updateUser(User user) {
        Call<Void> call = userWebServiceAPI.updateUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //userDao.insertUser(user);
                if (response.isSuccessful()) {
                    UserManager.getInstance().setCurrentUser(user);
                    Log.d("UserAPI", "User updated successfully");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserAPI", "Error updating user: " + t.getMessage());
            }
        });
    }
}
