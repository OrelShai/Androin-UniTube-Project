package com.project.unitube.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.project.unitube.R;
import com.project.unitube.utils.manager.UserManager;
import com.project.unitube.entities.User;
import com.project.unitube.viewmodel.UserViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RegisterScreen handles the registration process for new users.
 * It includes fields for first name, last name, username, and password,
 * and allows the user to upload a profile photo either by selecting from
 * the gallery or taking a new photo using the camera.
 */
public class RegisterScreen extends AppCompatActivity  {

    private ImageView profileImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText reEnterPasswordEditText;
    private EditText userNameEditText;
    private Button uploadPhotoButton;
    private Button signUpButton;
    private Uri selectedPhotoUri;
    private UserViewModel userViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;


    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Initialize UI components
        initializeUIComponents();

        // Initialize the UserViewModel
        userViewModel = new UserViewModel();

        // Set up listeners for buttons
        setUpListeners();
    }

    /**
     * Initializes the UI components.
     * Binds the XML views to the corresponding Java objects.
     */
    private void initializeUIComponents() {
        firstNameEditText = findViewById(R.id.SignUpFirstNameEditText);
        lastNameEditText = findViewById(R.id.SignUpLastNameEditText);
        passwordEditText = findViewById(R.id.SignUpPasswordEditText);
        reEnterPasswordEditText = findViewById(R.id.SignUpReEnterPasswordEditText);
        userNameEditText = findViewById(R.id.SignUpUserNameEditText);
        profileImageView = findViewById(R.id.profileImageView);

        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        signUpButton = findViewById(R.id.signUpButton);

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

        uploadPhotoButton.setOnClickListener(v -> showImagePickerOptions());

        signUpButton.setOnClickListener(v -> {
            if (validateFields()) {
                User user = new User(
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        userNameEditText.getText().toString(),
                        profileImageView.getTag() != null ? profileImageView.getTag().toString() : "default_profile_image");

                userViewModel.createUser(user, selectedPhotoUri).observe(this, result -> {
                    if (result.equals("success")) {
                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result.equals("409")) {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
        } else if (!reEnterPassword.equals(password)) {
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
        for (User user : UserManager.getInstance().getUsers()) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false; // User not found
    }

    /**
     * Handles the result of the image picking or capturing process.
     * Sets the selected or captured image as the profile photo.
     *
     * @param requestCode The request code identifying the intent
     * @param resultCode  The result code indicating success or failure
     * @param data        The data returned from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleActivityResult(requestCode, resultCode, data);

        // Update profileImageView with the selected/captured photo
        if (resultCode == RESULT_OK) {
            Uri photoUri = getSelectedPhotoUri();
            if (photoUri != null) {
                try {
                    profileImageView.setImageURI(photoUri);
                    profileImageView.setTag(photoUri.toString()); // Set tag to store URI
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to set image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedPhotoUri = data.getData();
                saveImageFromUri(selectedPhotoUri);
                // Handle picking image from gallery
            }
                // if (requestCode == CAPTURE_IMAGE_REQUEST)
                // Handle capturing image from camera
                // The selected photo URI is already set in captureImageFromCamera()
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
                selectedPhotoUri = photoUri; // Store the selected photo URI
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
            selectedPhotoUri = Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getSelectedPhotoUri() {
        return selectedPhotoUri;
    }

}


