package com.project.unitube;

import android.app.Application;
import android.content.Context;

public class Unitube extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
