package com.project.unitube;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatDelegate;


public class DarkModeHelper {
    private boolean isDarkMode;

    public DarkModeHelper(Context context) {
        this.isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public void initializeDarkModeButtons(ImageButton toggleButton) {
        toggleButton.setOnClickListener(view -> toggleDarkMode(toggleButton));
    }

    private void toggleDarkMode(ImageButton toggleButton) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            toggleButton.setImageResource(R.drawable.ic_dark_mode);
            isDarkMode = false;
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            toggleButton.setImageResource(R.drawable.ic_light_mode);
            isDarkMode = true;
        }
    }
}