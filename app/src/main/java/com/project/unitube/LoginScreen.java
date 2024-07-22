package com.project.unitube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class LoginScreen extends Activity {
    private EditText UserNameLoginTextBox;
    private EditText passwordLoginTextBox;
    private Button loginButton;
    private TextView alreadyHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

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


            // Search for the user in the UserLinkedList
            User foundUser = findUser(username, password);

            if (foundUser != null) {
                // Set the currentUser reference to the found user
                UserManager.getInstance().setCurrentUser(foundUser);
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                // back to the main activity
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        alreadyHaveAccount.setOnClickListener(v -> {
            Intent i  = new Intent(this, RegisterScreen.class);
            startActivity(i);
        });
    }

    private User findUser(String username, String password) {
        // Iterate through the usersList and find the user
        List<User> users = UserManager.getInstance().getUsers();
        for (User user : users) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // User not found
    }
}