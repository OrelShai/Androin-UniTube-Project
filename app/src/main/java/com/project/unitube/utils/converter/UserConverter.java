package com.project.unitube.utils.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.project.unitube.entities.User;

public class UserConverter {
    @TypeConverter
    public static String fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new Gson().toJson(user);
    }

    @TypeConverter
    public static User toUser(String userString) {
        if (userString == null) {
            return null;
        }
        return new Gson().fromJson(userString, User.class);
    }
}