package com.project.unitube.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.unitube.R;
import com.project.unitube.entities.User;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.ui.adapter.VideoAdapter;
import com.project.unitube.viewmodel.VideoViewModel;

public class UserPageActivity extends AppCompatActivity {

    private static final String TAG = "UserPageActivity";

    private ImageView profileImageView;
    private TextView fullNameTextView;
    private TextView usernameTextView;
    private Button homeButton;
    private RecyclerView videosRecyclerView;
    private User user;
    private VideoAdapter videoAdapter;
    private VideoViewModel videoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Log.d(TAG, "onCreate: UserPageActivity started");

        // initialize views
        initializeUIComponents();

        // Load user details (replace with actual user data)
        loadUserDetails();

        // Set up RecyclerView
        setupRecyclerView();
    }

    private void initializeUIComponents() {
        Log.d(TAG, "initializeUIComponents: Initializing UI components");
        // Initialize views
        profileImageView = findViewById(R.id.profileImageView);
        fullNameTextView = findViewById(R.id.fullNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        homeButton = findViewById(R.id.backToHomePageButton);
        videosRecyclerView = findViewById(R.id.userPageVideosRecyclerView);

        // Set on click listener for home button
        homeButton.setOnClickListener(v -> {
            Log.d(TAG, "Home button clicked. Finishing activity.");
            finish();
        });
        Log.d(TAG, "initializeUIComponents: UI components initialized");
    }

    private void loadUserDetails() {
        Log.d(TAG, "loadUserDetails: Loading user details");
        // Load user if intent contains user
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER")) {
            this.user = (User) intent.getSerializableExtra("USER");
            if (user != null) {
                Log.d(TAG, "loadUserDetails: User data received. Username: " + user.getUserName());
                // Load user profile image
                String baseUrl = RetrofitClient.getBaseUrl();
                String ProfilePic = baseUrl + user.getProfilePicture();
                Log.d(TAG, "loadUserDetails: Loading profile picture from: " + ProfilePic);
                Glide.with(this)
                        .load(ProfilePic)
                        .circleCrop()
                        .placeholder(R.drawable.default_profile)
                        .into(profileImageView);
                // Set user details
                String fullName = user.getFirstName() + " " + user.getLastName();
                fullNameTextView.setText(fullName);
                usernameTextView.setText(user.getUserName());
                Log.d(TAG, "loadUserDetails: User details set. Full Name: " + fullName);
            } else {
                Log.e(TAG, "loadUserDetails: User object is null");
            }
        } else {
            Log.e(TAG, "loadUserDetails: No user data in intent");
        }
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Setting up RecyclerView");

        // Initialize RecyclerView
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize VideoAdapter
        videoAdapter = new VideoAdapter(this);
        videosRecyclerView.setAdapter(videoAdapter);

        // Initialize VideoViewModel
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        // Fetch user videos
        String username = user.getUserName(); // Assuming you have a 'user' object
        videoViewModel.getUserVideos(username).observe(this, videos -> {
            if (videos != null) {
                Log.d(TAG, "setupRecyclerView: Received " + videos.size() + " videos for user " + username);
                videoAdapter.setVideos(videos);
            } else {
                Log.e(TAG, "setupRecyclerView: Failed to fetch videos for user " + username);
                Toast.makeText(this, "Failed to load user videos", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(TAG, "setupRecyclerView: RecyclerView setup complete");
    }
}