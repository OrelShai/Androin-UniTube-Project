package com.example.unitube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText reEnterPasswordEditText;
    private EditText userNameEditText;
//work
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        alreadyHaveAccount.setOnClickListener(v -> {
            Intent i  = new Intent(this, LoginScreen.class);
            startActivity(i);
        });

        // Initialize EditText fields
        firstNameEditText = findViewById(R.id.SignUpFirstNameEditText);
        lastNameEditText = findViewById(R.id.SignUpLastNameEditText);
        passwordEditText = findViewById(R.id.SignUpPasswordEditText);
        reEnterPasswordEditText = findViewById(R.id.SignUpReEnterPasswordEditText);
        userNameEditText = findViewById(R.id.SignUpUserNameEditText);

        // Initialize ImageView
        profileImageView = findViewById(R.id.profileImageView);
        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);

        // Initialize Button
        Button signUpButton = findViewById(R.id.signUpButton);

        // Set click listener for upload photo button
        uploadPhotoButton.setOnClickListener(v -> openImageChooser());

        // Set click listener for sign up button
        signUpButton.setOnClickListener(v -> {
            if (validateFields()) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

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
        }

        return isValid;
    }
}