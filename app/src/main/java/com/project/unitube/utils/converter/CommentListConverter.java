package com.project.unitube.utils.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.unitube.entities.Comment;

import java.lang.reflect.Type;
import java.util.List;

public class CommentListConverter {
    @TypeConverter
    public static String fromCommentList(List<Comment> list) {
        if (list == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Comment> toCommentList(String string) {
        if (string == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Comment>>() {}.getType();
        return gson.fromJson(string, type);
    }
}