package com.project.unitube;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class DarkModeHelper {
    private boolean isDarkMode;

    public DarkModeHelper(Context context) {
        this.isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public void initializeDarkModeButtons(ImageButton toggleButton, ImageButton addButton, ImageButton homeButton, NavigationView navigationView) {
        setInitialToggleIcon(toggleButton, addButton, homeButton, navigationView.getHeaderView(0).findViewById(R.id.logo_image));
        toggleButton.setOnClickListener(view -> toggleDarkMode(toggleButton, addButton, homeButton, navigationView.getHeaderView(0).findViewById(R.id.logo_image)));
    }

    private void toggleDarkMode(ImageButton toggleButton, ImageButton addButton, ImageButton homeButton, ImageView logoImageView) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            updateIcons(toggleButton, addButton, homeButton, logoImageView, false);
            isDarkMode = false;
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            updateIcons(toggleButton, addButton, homeButton, logoImageView, true);
            isDarkMode = true;
        }
    }

    private void setInitialToggleIcon(ImageButton toggleButton, ImageButton addButton, ImageButton homeButton, ImageView logoImageView) {
        updateIcons(toggleButton, addButton, homeButton, logoImageView, isDarkMode);
    }

    private void updateIcons(ImageButton toggleButton, ImageButton addButton, ImageButton homeButton, ImageView logoImageView, boolean isDarkMode) {
        if (isDarkMode) {
            toggleButton.setImageResource(R.drawable.icon_dark_mode);
            addButton.setImageResource(R.drawable.icon_add_video_dark);
            homeButton.setImageResource(R.drawable.icon_home_dark);
            logoImageView.setImageResource(R.drawable.logo_image_dark_mode);
        } else {
            toggleButton.setImageResource(R.drawable.icon_light_mode);
            addButton.setImageResource(R.drawable.icon_add_video_light);
            homeButton.setImageResource(R.drawable.icon_home_light);
            logoImageView.setImageResource(R.drawable.logo_image_light_mode);
        }
    }
}
