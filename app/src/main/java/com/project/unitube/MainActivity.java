package com.project.unitube;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private DrawerLayout drawerLayout;
    private static final String TAG = "MainActivity";
    private boolean isDarkMode = false;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up action_menu button to open the drawer
        findViewById(R.id.action_menu).setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Initialize RecyclerView
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DataManager
        dataManager = new DataManager(this);

        // Initialize Adapter with video list from DataManager
        List<Video> videoList = dataManager.getVideoList();
        if (videoList.isEmpty()) {
            Log.d(TAG, "No videos found.");
        } else {
            Log.d(TAG, "Videos found: " + videoList.size());
        }
        videoAdapter = new VideoAdapter(this, videoList);
        videoRecyclerView.setAdapter(videoAdapter);

        // Set up sign out button listener
        View signOutView = findViewById(R.id.icon_sign_out);
        if (signOutView != null) {
            signOutView.setOnClickListener(v -> {
                Log.d(TAG, "Sign Out selected");
                // Handle sign out action
            });
        }

        // Initialize Bottom Navigation
        findViewById(R.id.button_add_video).setOnClickListener(view -> {
            Log.d(TAG, "Add Video selected");
            // Handle add video action
        });

        findViewById(R.id.button_toggle_mode).setOnClickListener(view -> {
            Log.d(TAG, "Toggle Mode selected");
            // Handle toggle mode action
            toggleDarkMode();
        });

        // Set the initial icon based on the current mode
        setInitialToggleIcon();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_your_account) {
            Log.d(TAG, "Your Account selected");
            // Handle your account action
        } else if (id == R.id.nav_home) {
            Log.d(TAG, "Home selected");
            // Handle home action
        } else if (id == R.id.nav_history) {
            Log.d(TAG, "History selected");
            // Handle history action
        } else if (id == R.id.nav_playlists) {
            Log.d(TAG, "Playlists selected");
            // Handle playlists action
        } else if (id == R.id.nav_switch_account) {
            Log.d(TAG, "Switch Account selected");
            // Handle switch account action
        } else if (id == R.id.nav_setting) {
            Log.d(TAG, "Setting selected");
            // Handle setting action
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void toggleDarkMode() {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            ((ImageButton) findViewById(R.id.button_toggle_mode)).setImageResource(R.drawable.icon_light_mode);
            ((ImageButton) findViewById(R.id.button_add_video)).setImageResource(R.drawable.icon_add_video_light);
            ((ImageButton) findViewById(R.id.button_home)).setImageResource(R.drawable.icon_home_light);
            isDarkMode = false;
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            ((ImageButton) findViewById(R.id.button_toggle_mode)).setImageResource(R.drawable.icon_dark_mode);
            ((ImageButton) findViewById(R.id.button_add_video)).setImageResource(R.drawable.icon_add_video_dark);
            ((ImageButton) findViewById(R.id.button_home)).setImageResource(R.drawable.icon_home_dark);
            isDarkMode = true;
        }
    }

    private void setInitialToggleIcon() {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            ((ImageButton) findViewById(R.id.button_toggle_mode)).setImageResource(R.drawable.icon_dark_mode);
            ((ImageButton) findViewById(R.id.button_add_video)).setImageResource(R.drawable.icon_add_video_dark);
            ((ImageButton) findViewById(R.id.button_home)).setImageResource(R.drawable.icon_home_dark);
            isDarkMode = true;
        } else {
            ((ImageButton) findViewById(R.id.button_toggle_mode)).setImageResource(R.drawable.icon_light_mode);
            ((ImageButton) findViewById(R.id.button_add_video)).setImageResource(R.drawable.icon_add_video_light);
            ((ImageButton) findViewById(R.id.button_home)).setImageResource(R.drawable.icon_home_light);
            isDarkMode = false;
        }
    }
}
