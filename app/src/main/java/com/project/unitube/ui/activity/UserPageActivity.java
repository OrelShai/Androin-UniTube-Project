package com.project.unitube.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.unitube.R;
import com.project.unitube.entities.User;
import com.project.unitube.network.RetroFit.RetrofitClient;
import com.project.unitube.ui.adapter.VideoAdapter;
import com.project.unitube.utils.manager.UserManager;

public class UserPageActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView fullNameTextView;
    private TextView usernameTextView;
    private Button homeButton;
    private RecyclerView videosRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // initialize views
        initializeUIComponents();

        // Load user details (replace with actual user data)
        loadUserDetails();

        // Set up RecyclerView
        setupRecyclerView();
    }

    private void initializeUIComponents() {
        // Initialize views
        profileImageView = findViewById(R.id.profileImageView);
        fullNameTextView = findViewById(R.id.fullNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        homeButton = findViewById(R.id.backToHomePageButton);
        videosRecyclerView = findViewById(R.id.userPageVideosRecyclerView);

        // Set on click listener for home button
        homeButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserDetails() {
        // Load user if intent contains user
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER")) {
            User user = (User) intent.getSerializableExtra("USER");
            if (user != null) {
                // Load user profile image
                String baseUrl = RetrofitClient.getBaseUrl();
                String ProfilePic = baseUrl + user.getProfilePicture();
                Glide.with(this)
                        .load(ProfilePic)
                        .circleCrop()
                        .placeholder(R.drawable.default_profile_image) // Placeholder in case of loading issues
                        .into(profileImageView);

//               // FAKE PROFILE IMAGES WILL BE SHOWN AFTER FETCHING FAKE USER FROM DATA BASE
//                int profileImageResourceId = getResources().getIdentifier(user.getProfilePicture(), "drawable", getPackageName());
//                if (profileImageResourceId != 0) {
//                    profileImageView.setImageResource(profileImageResourceId);
//                } else {
//                    profileImageView.setImageURI(Uri.parse(user.getProfilePicture())); // Fallback profile image
//                }
                // Set user details
                String fullName = user.getFirstName() + " " + user.getLastName();
                fullNameTextView.setText(fullName);
                usernameTextView.setText(user.getUserName());
            }
        }
    }

    private void setupRecyclerView() {
        // Initialize RecyclerView
        RecyclerView videoRecyclerView = findViewById(R.id.userPageVideosRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter for the RecyclerView using the global videos list
        VideoAdapter videoAdapter = new VideoAdapter(this);
        videosRecyclerView.setAdapter(videoAdapter);
    }
}