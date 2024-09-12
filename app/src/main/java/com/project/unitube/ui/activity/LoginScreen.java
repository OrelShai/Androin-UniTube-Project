package com.project.unitube.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.project.unitube.R;
import com.project.unitube.utils.manager.UserManager;
import com.project.unitube.entities.User;
import com.project.unitube.viewmodel.UserViewModel;

public class LoginScreen extends AppCompatActivity {
    private EditText UserNameLoginTextBox;
    private EditText passwordLoginTextBox;
    private Button loginButton;
    private TextView alreadyHaveAccount;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize the UserViewModel
        userViewModel = new UserViewModel();

        // Initialize UI components
        initializeUIComponents();

        // Set up listeners for buttons
        setUpListeners();
    }

    private void initializeUIComponents() {
        UserNameLoginTextBox = findViewById(R.id.UserNameLoginTextBox);
        passwordLoginTextBox = findViewById(R.id.passwordLoginTextBox);
        loginButton = findViewById(R.id.loginButton);
        alreadyHaveAccount = findViewById(R.id.DoNotHaveAnAccount);
    }

    private void setUpListeners() {
        loginButton.setOnClickListener(v -> {
            String username = UserNameLoginTextBox.getText().toString();
            String password = passwordLoginTextBox.getText().toString();


            // Call the login method from UserViewModel and observe the result
            userViewModel.loginUser(username, password).observe( this, user -> {
                if (user != null) {
                    // Login successful
                    UserManager.getInstance().setCurrentUser(user);
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Login failed
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        alreadyHaveAccount.setOnClickListener(v -> {
            Intent i  = new Intent(this, RegisterScreen.class);
            startActivity(i);
        });
    }

}