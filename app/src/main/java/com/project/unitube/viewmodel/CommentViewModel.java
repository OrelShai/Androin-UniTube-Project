package com.project.unitube.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.entities.Comment;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<Comment>> commentMutableLiveData;

    public MutableLiveData<List<Comment>> getCommentMutableLiveData() {
        if (commentMutableLiveData == null) {
            commentMutableLiveData = new MutableLiveData<>();
        }
        return commentMutableLiveData;
    }
}
