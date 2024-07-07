package com.project.unitube;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

public class NavigationHelper implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private DrawerLayout drawerLayout;
    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private DataManager dataManager;
    private static final String TAG = "NavigationHelper";

    public NavigationHelper(Context context, DrawerLayout drawerLayout, RecyclerView videoRecyclerView) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.videoRecyclerView = videoRecyclerView;
        this.dataManager = new DataManager(context);
    }

    public void initializeNavigation(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
        setupRecyclerView();
        setupSignOutButton(navigationView);
    }

    private void setupRecyclerView() {
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<Video> videoList = dataManager.getVideoList();
        if (videoList.isEmpty()) {
            Log.d(TAG, "No videos found.");
        } else {
            Log.d(TAG, "Videos found: " + videoList.size());
        }
        videoAdapter = new VideoAdapter(context);
        videoRecyclerView.setAdapter(videoAdapter);
    }

    private void setupSignOutButton(NavigationView navigationView) {
        View signOutView = navigationView.getHeaderView(0).findViewById(R.id.icon_auth);
        if (signOutView != null) {
            signOutView.setOnClickListener(v -> {
                Log.d(TAG, "Sign Out selected");
                // Handle sign out action
            });
        }
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
}
