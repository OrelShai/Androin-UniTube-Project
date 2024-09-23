package com.project.unitube.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
//import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.utils.helper.DarkModeHelper;
import com.project.unitube.utils.manager.DataManager;
import com.project.unitube.utils.helper.NavigationHelper;
import com.project.unitube.R;
import com.project.unitube.utils.manager.UserManager;
import com.project.unitube.ui.adapter.VideoAdapter;
import com.project.unitube.entities.User;
import com.project.unitube.entities.Video;
import com.project.unitube.entities.Videos;
import com.project.unitube.viewmodel.CommentViewModel;
import com.project.unitube.viewmodel.UserViewModel;
import com.project.unitube.viewmodel.VideoViewModel;

import static com.project.unitube.utils.VideoInteractionHandler.updateDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_VIDEO_REQUEST = 1; // Request code for adding a video

    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int CAPTURE_IMAGE_REQUEST = 3;

    private Uri editDialogSelectedPhotoUri;
    private ImageView editDialogprofileImageView;

    private DrawerLayout drawerLayout;
    private NavigationHelper navigationHelper;
    private DarkModeHelper darkModeHelper;
    private DataManager dataManager;
    private VideoAdapter videoAdapter;

    private UserViewModel userViewModel;
    private VideoViewModel videoViewModel;
    private CommentViewModel commentViewModel;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DataManager
        dataManager = new DataManager(this);

        // Initialize the UI components. Binds the XML views to the corresponding Java objects.
        initializeUIComponents();

        // Initialize the ViewModels
        initializeViewModels();

        // Set up listeners for the buttons in the activity.
        setUpListeners();

        // Initialize VideosToShow with all videos
        initializeVideosToShow();
    }

    private void initializeViewModels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
//        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
//        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
    }

    private void initializeUIComponents() {
        if (updateDate) {
            updateDate = false;
            onResume();
        }
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
                findViewById(R.id.button_toggle_mode)
        );

        // Initialize the auth button (Sign In/Sign Out)
        initLoginSignOutButton();

        // Initialize search functionality
        initializeSearchFunctionality();
    }

    private void initLoginSignOutButton() {
        // Find the LinearLayout and its components
        LinearLayout authLinearLayout = findViewById(R.id.log_in_out_button_layout);
        TextView authText = findViewById(R.id.text_log_in_out);
        ImageView authIcon = findViewById(R.id.icon_log_in_out);
        UserManager userManager = UserManager.getInstance();

        // Check if there is a logged-in user
        if (userManager.getCurrentUser() == null) {
            // No user logged in, set to "Sign In"
            authText.setText(getString(R.string.sign_in));
            authIcon.setImageResource(R.drawable.ic_login); // Change icon if needed

            authLinearLayout.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            });
        } else {
            // User is logged in, set to "Sign Out"
            authText.setText(getString(R.string.sign_out));
            authIcon.setImageResource(R.drawable.ic_logout); // Change icon if needed

            authLinearLayout.setOnClickListener(view -> {
                // Handle sign out and go to LoginScreen
                Toast.makeText(this, "User signed out", Toast.LENGTH_SHORT).show();
                userManager.setCurrentUser(null);

                // Navigate to LoginScreen after sign out
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            });

            // Update user greeting and profile picture
            updateGreetingUser();
        }
    }

    private void InitDeleteAndEditAccountButtons() {
        LinearLayout deleteAccountButton = findViewById(R.id.delete_user_button_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView editUserButton = headerView.findViewById(R.id.edit_user_button); // Access from headerView

        User currentUser = UserManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Make buttons visible
            deleteAccountButton.setVisibility(View.VISIBLE);
            editUserButton.setVisibility(View.VISIBLE);

            // Set up listener for delete account button
            deleteAccountButton.setOnClickListener(view -> {
                // Delete the user account
                userViewModel.deleteUser(currentUser.getUserName());
                UserManager.getInstance().setCurrentUser(null);

                // Notify the user and navigate to LoginScreen
                Toast.makeText(this, "User account deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            });

            // Set up listener for edit user button
            editUserButton.setOnClickListener(view -> showEditUserDialog());
        } else {
            // Hide buttons if no user is logged in
            deleteAccountButton.setVisibility(View.GONE);
            editUserButton.setVisibility(View.GONE);
        }
    }

    private void showEditUserDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        // Reference the EditText fields
        EditText firstNameEditText = dialogView.findViewById(R.id.edit_first_name);
        EditText lastNameEditText = dialogView.findViewById(R.id.edit_last_name);
        EditText passwordEditText = dialogView.findViewById(R.id.edit_password);
        editDialogprofileImageView = dialogView.findViewById(R.id.edit_profile_picture);
        Button changeProfilePictureButton = dialogView.findViewById(R.id.change_profile_picture_button);

        // Get the current user and set the EditText fields
        User user = UserManager.getInstance().getCurrentUser();
        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        passwordEditText.setText(user.getPassword());

        // Load current profile picture (if any)
        if (user.getProfilePicture() != null) {
            Uri profilePhotoUri = Uri.parse(user.getProfilePicture());
            editDialogprofileImageView.setImageURI(profilePhotoUri);
        } else {
            editDialogprofileImageView.setImageResource(R.drawable.default_profile_image);
        }

        // Set up the profile picture change listener
        changeProfilePictureButton.setOnClickListener(view -> {
            // Call UploadPhotoHelper to choose or take a new profile picture
            showImagePickerOptions();
        });



        // Set up the "Save" button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Get updated values from EditTexts
                    String updatedFirstName = firstNameEditText.getText().toString();
                    String updatedLastName = lastNameEditText.getText().toString();
                    String updatedPassword = passwordEditText.getText().toString();

                    // Validate the input and update the user data
                    if (!updatedFirstName.isEmpty() && !updatedLastName.isEmpty() && !updatedPassword.isEmpty()) {
                        // Update the user object
                        user.setFirstName(updatedFirstName);
                        user.setLastName(updatedLastName);
                        user.setPassword(updatedPassword);

                        // Update the user profile picture (if changed)
                        String updatedProfilePictureUri = getEditDialogSelectedPhotoUri().toString();
                        user.setProfilePicture(updatedProfilePictureUri);

                        userViewModel.updateUser(user);
                        updateGreetingUser();
                        updateProfilePhotoPresent();
                    } else {
                        // Show a message if validation fails
                        Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set up the "Cancel" button
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Simply dismiss the dialog
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
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
            if (UserManager.getInstance().getCurrentUser() != null) {
                // Navigate to add video screen
                Intent intent = new Intent(MainActivity.this, AddVideoScreen.class);
                startActivityForResult(intent, ADD_VIDEO_REQUEST); // Start AddVideoScreen with request code
            } else {
                Toast.makeText(this, "Log in first", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            }
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
            String lowerCaseQuery = query.toLowerCase();
            for (Video video : Videos.videosList) {
                if (video.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        video.getDescription().toLowerCase().contains(lowerCaseQuery) ||
                        video.getUser().getUserName().toLowerCase().contains(lowerCaseQuery)) {
                    Videos.videosToShow.add(video);
                }
            }
        }
        videoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreetingUser();
        updateProfilePhotoPresent();
        initLoginSignOutButton();
        initializeVideosToShow(); // Ensure the videos list is updated
        videoAdapter.notifyDataSetChanged(); // Refresh the adapter
        InitDeleteAndEditAccountButtons();
    }

    private void updateProfilePhotoPresent() {
        ImageView currentUserProfilePic = findViewById(R.id.logo);
        User currentUser = UserManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            Uri profilePhotoUri = Uri.parse(currentUser.getProfilePicture());

            if (profilePhotoUri != null) {

                currentUserProfilePic.setImageURI(profilePhotoUri);
                Glide.with(this)
                        .load(profilePhotoUri)
                        .circleCrop()
                        .placeholder(R.drawable.default_profile_image) // Placeholder in case of loading issues
                        .into(currentUserProfilePic);
            } else {
                currentUserProfilePic.setImageResource(R.drawable.default_profile_image);
                Glide.with(this)
                        .load(profilePhotoUri)  // This line seems redundant as profilePhotoUri is null here.
                        .circleCrop()
                        .placeholder(R.drawable.default_profile_image) // Placeholder in case of loading issues
                        .into(currentUserProfilePic);
            }
        } else {
            currentUserProfilePic.setImageResource(R.drawable.unitube_logo);
            currentUserProfilePic.setBackground(null); // Remove background for default logo
            currentUserProfilePic.setScaleType(ImageView.ScaleType.FIT_XY); // Reverting scale type for logo
        }
    }

    private void updateGreetingUser() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView greetingText = headerView.findViewById(R.id.user_greeting);

        User currentUser = UserManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is signed in
            String welcome = getString(R.string.welcome);
            greetingText.setText(welcome + " " + currentUser.getFirstName());

        } else {
            if (greetingText != null) {
                greetingText.setText(R.string.welcome_to_unitube);
            }
        }
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_VIDEO_REQUEST && resultCode == RESULT_OK) {
            // Refresh the video list when a new video is added
            initializeVideosToShow();
            videoAdapter.notifyDataSetChanged();
        }
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            editDialogprofileImageView.setImageURI(editDialogSelectedPhotoUri);
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            editDialogSelectedPhotoUri = data.getData(); // Get the URI from the result
            saveImageFromUri(editDialogSelectedPhotoUri); // Call the method to save the image
            // Update the profile image view
            editDialogprofileImageView.setImageURI(editDialogSelectedPhotoUri);
        }
    }

    public void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Photo"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickImageFromGallery();
                            break;
                        case 1:
                            captureImageFromCamera();
                            break;
                    }
                });
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.project.unitube.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                editDialogSelectedPhotoUri = photoUri; // Store the selected photo URI
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void saveImageFromUri(Uri uri) {
        try {
            File imageFile = createImageFile();
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            editDialogSelectedPhotoUri = Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getEditDialogSelectedPhotoUri() {
        return editDialogSelectedPhotoUri;
    }
}