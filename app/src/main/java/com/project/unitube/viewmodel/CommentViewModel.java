package com.project.unitube.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.unitube.Unitube;
import com.project.unitube.entities.Comment;
import com.project.unitube.entities.User;
import com.project.unitube.repository.CommentRepository;
import com.project.unitube.repository.UserRepository;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private CommentRepository commentRepository;

    public CommentViewModel() {
        commentRepository = new CommentRepository(Unitube.context);
    }

    public MutableLiveData<List<Comment>> getCommentsForVideo(int videoId) {
        return commentRepository.getCommentsForVideo(videoId);
    }


    public MutableLiveData<String> createComment(Comment comment) {
        return commentRepository.createComment(comment);
    }

//    public MutableLiveData<String> updateComment(Comment comment) {
//        return commentRepository.updateComment(comment);
//    }

}
