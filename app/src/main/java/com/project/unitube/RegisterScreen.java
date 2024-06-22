package com.project.unitube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.project.unitube.LoginScreen;
import com.project.unitube.R;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * MainActivity handles the registration process for new users.
 * It includes fields for first name, last name, username, and password,
 * and allows the user to upload a profile photo either by selecting from
 * the gallery or taking a new photo using the camera.
 * This activity also manages permission requests for camera and storage.
 */
public class RegisterScreen extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;


    private ImageView profileImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText reEnterPasswordEditText;
    private EditText userNameEditText;

    // List of users and the current logged-in user
    public static List<User> usersList = new LinkedList<>();
    public static User currentUser;


    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle).
     */
    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Initialize UI components
        initializeUIComponents();

        // Set up listeners for buttons
        setUpListeners();
    }

    /**
     * Initializes the UI components.
     * Binds the XML views to the corresponding Java objects.
     */
    private void initializeUIComponents() {
        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        firstNameEditText = findViewById(R.id.SignUpFirstNameEditText);
        lastNameEditText = findViewById(R.id.SignUpLastNameEditText);
        passwordEditText = findViewById(R.id.SignUpPasswordEditText);
        reEnterPasswordEditText = findViewById(R.id.SignUpReEnterPasswordEditText);
        userNameEditText = findViewById(R.id.SignUpUserNameEditText);
        profileImageView = findViewById(R.id.profileImageView);

        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        // Set default profile image
        profileImageView.setImageResource(R.drawable.default_profile_image);
    }

    /**
     * Sets up listeners for the buttons in the activity.
     * Handles actions for signing up and uploading a photo.
     */
    private void setUpListeners() {
        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        alreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
        });

        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        uploadPhotoButton.setOnClickListener(v -> showImagePickerOptions());

        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> {
            if (validateFields()) {
                User user = new User(
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        userNameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        profileImageView.getTag() != null ? profileImageView.getTag().toString() : null);

                // Add the user to the list and set as current user
                usersList.add(user);

                // Show "Sign up successful" toast and move to sign-in page
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginScreen.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Displays an AlertDialog to allow the user to choose between picking
     * an image from the gallery or taking a new photo with the camera.
     */
    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Photo"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickImageFromGallery();
                            break;
                        case 1:
                            if (checkCameraPermission()) {
                                captureImageFromCamera();
                            }
                            break;
                    }
                });
        builder.show();
    }


    /**
     * Initiates an intent to pick an image from the gallery.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Checks if the app has the necessary permissions to use the camera and storage.
     * If not, requests these permissions.
     *
     * @return true if the permissions are granted, false otherwise.
     */
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Show an explanation to the user why you need the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                showPermissionExplanationDialog();
            } else {
                // Request the permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
            }
            return false;
        } else {
            return true; // Permission already granted
        }
    }

    /**
     * Initiates an intent to capture an image using the camera.
     */
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
        }
    }

    /**
     * Displays a dialog explaining why camera and storage permissions are needed.
     * Provides an option to request these permissions again.
     */
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Camera Permission Needed")
                .setMessage("This app needs the Camera and Storage permissions to take and save photos. Please grant them.")
                .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Handles the result of the image picking or capturing process.
     * Sets the selected or captured image as the profile photo.
     *
     * @param requestCode The request code identifying the intent
     * @param resultCode The result code indicating success or failure
     * @param data The data returned from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profileImageView.setImageBitmap(bitmap);
                    profileImageView.setTag(imageUri.toString()); // Save the URI of the selected image
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileImageView.setImageBitmap(imageBitmap);
                // Save the URI or file path of the captured image if needed
            }
        }
    }

    /**
     * Validates the input fields for the registration process.
     * Checks if fields are empty or contain invalid characters.
     *
     * @return true if all fields are valid, false otherwise.
     */
    private boolean validateFields() {
        boolean isValid = true;

        // Check if first name is empty or contains numbers
        String firstName = firstNameEditText.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            isValid = false;
        } else if (firstName.matches(".*\\d.*")) {
            firstNameEditText.setError("First name should contain letters only");
            isValid = false;
        }

        // Check if last name is empty or contains numbers
        String lastName = lastNameEditText.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            isValid = false;
        } else if (lastName.matches(".*\\d.*")) {
            lastNameEditText.setError("Last name should contain letters only");
            isValid = false;
        }

        // Check if password is empty, contains non-alphanumeric characters, or less than 8 characters.
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            isValid = false;
        } else if (!password.matches("[a-zA-Z0-9]+")) {
            passwordEditText.setError("Password should contain only letters and numbers");
        } else if (password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters");
            isValid = false;
        }

        // Check if re-enter password is empty or do not match password
        String reEnterPassword = reEnterPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(reEnterPassword)) {
            reEnterPasswordEditText.setError("Re-entering password is required");
            isValid = false;
        }   else if (!reEnterPassword.equals(password)) {
            reEnterPasswordEditText.setError("Passwords do not match");
            isValid = false;
        }

        // Check if userName is empty or contains non-alphanumeric characters
        String userName = userNameEditText.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError("Username is required");
            isValid = false;
        } else if (!userName.matches("[a-zA-Z0-9]+")) {
            userNameEditText.setError("Username should contain letters and numbers only");
            isValid = false;
        } else if (isUsernameTaken(userName)) {
            userNameEditText.setError("Username is already taken");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Checks if the given username is already taken by another user.
     *
     * @param username The username to check
     * @return true if the username is taken, false otherwise.
     */
    private Boolean isUsernameTaken(String username) {
        // Iterate through the usersList and find the user
        for (User user : usersList) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false; // User not found
    }

    /**
     * Handles the result of permission requests.
     * If camera permission is granted, allows capturing an image.
     * If denied, shows a dialog to navigate to app settings.
     *
     * @param requestCode The request code identifying the permission request
     * @param permissions The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with capturing image
                captureImageFromCamera();
            } else {
                // Permission denied
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
                if (!showRationale) {
                    // User selected "Don't ask again"
                    showSettingsDialog();
                } else {
                    // Permission denied without "Don't ask again"
                    Toast.makeText(this, "Camera and storage permissions are required to take a photo", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * Displays a dialog to inform the user that permissions are needed and
     * provides an option to navigate to the app's settings page.
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs camera and storage permissions. You can grant them in app settings.");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            dialog.cancel();
            openAppSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    /**
     * Opens the app's settings page to allow the user to grant the required permissions.
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}