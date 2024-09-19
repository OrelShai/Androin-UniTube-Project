package com.project.unitube;

import com.project.unitube.entities.User;

public interface CallBack  {
        void onSuccess(String token);
        void onFail(String message);
}
