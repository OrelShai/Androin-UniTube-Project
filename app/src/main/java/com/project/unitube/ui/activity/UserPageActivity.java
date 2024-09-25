package com.project.unitube.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project.unitube.R;
import com.project.unitube.entities.User;
import com.project.unitube.utils.manager.UserManager;

public class UserPageActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView fullNameTextView;
    private TextView usernameTextView;
    private RecyclerView videosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Initialize views
        profileImageView = findViewById(R.id.profileImageView);
        fullNameTextView = findViewById(R.id.fullNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        videosRecyclerView = findViewById(R.id.videosRecyclerView);

        // Load user details (replace with actual user data)
        loadUserDetails();

        // Set up RecyclerView
        setupRecyclerView();
    }

    private void loadUserDetails() {
        // Load user if intent contains user
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER")) {
            User user = (User) intent.getSerializableExtra("USER");
            if (user != null) {
                // Load user profile image

                int profileImageResourceId = getResources().getIdentifier(user.getProfilePicture(), "drawable", getPackageName());
                if (profileImageResourceId != 0) {
                    profileImageView.setImageResource(profileImageResourceId);
                } else {
                    profileImageView.setImageURI(Uri.parse(user.getProfilePicture())); // Fallback profile image
                }
                // Set user details
                String fullName = user.getFirstName() + " " + user.getLastName();
                fullNameTextView.setText(fullName);
                usernameTextView.setText(user.getUserName());
            }
        }
    }

    private void setupRecyclerView() {
    }


}