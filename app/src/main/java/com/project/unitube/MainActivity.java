package com.project.unitube;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationHelper navigationHelper;
    private DarkModeHelper darkModeHelper;
    private static final String TAG = "MainActivity";
    private DataManager dataManager;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DataManager
        dataManager = new DataManager(this);

        // Initialize the UI components. Binds the XML views to the corresponding Java objects.
        initializeUIComponents();

        // Set up listeners for the buttons in the activity.
        setUpListeners();

        // Initialize VideosToShow with all videos
        initializeVideosToShow();
    }

    private void initializeUIComponents() {
        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Initialize RecyclerView
        RecyclerView videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter for the RecyclerView using the global videos list
        videoAdapter = new VideoAdapter(this);
        videoRecyclerView.setAdapter(videoAdapter);

        // Initialize NavigationHelper
        navigationHelper = new NavigationHelper(this, drawerLayout, videoRecyclerView);
        navigationHelper.initializeNavigation(navigationView);

        // Initialize DarkModeHelper
        darkModeHelper = new DarkModeHelper(this);

        // Initialize dark mode buttons
        darkModeHelper.initializeDarkModeButtons(
                findViewById(R.id.button_toggle_mode),
                findViewById(R.id.button_add_video),
                findViewById(R.id.button_home),
                navigationView
        );

        // Initialize the auth button (Sign In/Sign Out)
        initializeAuthButton();

        // Initialize search functionality
        initializeSearchFunctionality();
    }

    private void initializeAuthButton() {
        // Find the LinearLayout and its components
        LinearLayout authLinearLayout = findViewById(R.id.authLinearLayout);
        TextView authText = findViewById(R.id.text_auth);
        ImageView authIcon = findViewById(R.id.icon_auth);

        // Check if there is a logged-in user
        if (RegisterScreen.currentUser == null) {
            // No user logged in, set to "Sign In"
            authText.setText("Sign In");
            authIcon.setImageResource(R.drawable.icon_sign_in); // Change icon if needed

            authLinearLayout.setOnClickListener(view -> {
                // Go to LoginScreen activity
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            });
        } else {
            // User is logged in, set to "Sign Out"
            authText.setText("Sign Out");
            authIcon.setImageResource(R.drawable.icon_sign_out); // Change icon if needed

            authLinearLayout.setOnClickListener(view -> {
                // Handle sign out and go to LoginScreen
                Toast.makeText(this, "User signed out", Toast.LENGTH_SHORT).show();
                RegisterScreen.currentUser = null;

                // Navigate to LoginScreen after sign out
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            });
        }
    }

    private void setUpListeners() {
        // Set up action_menu button to open the drawer
        findViewById(R.id.action_menu).setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Initialize Bottom Navigation
        findViewById(R.id.button_add_video).setOnClickListener(view -> {
            Log.d(TAG, "Add Video selected");
            // Handle add video action
        });
    }

    private void initializeVideosToShow() {
        Videos.videosToShow.clear();
        Videos.videosToShow.addAll(Videos.videosList);
    }

    private void initializeSearchFunctionality() {
        EditText searchBox = findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVideos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void filterVideos(String query) {
        Videos.videosToShow.clear();
        if (query.isEmpty()) {
            Videos.videosToShow.addAll(Videos.videosList);
        } else {
            for (Video video : Videos.videosList) {
                if (video.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        video.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                        video.getUser().getUserName().toLowerCase().contains(query.toLowerCase())) {
                    Videos.videosToShow.add(video);
                }
            }
        }
        videoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
