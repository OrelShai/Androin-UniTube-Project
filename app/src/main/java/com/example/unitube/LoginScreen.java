package com.example.unitube;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class LoginScreen extends Activity {
    private EditText UserNameLoginTextBox;
    private EditText passwordLoginTextBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        UserNameLoginTextBox = findViewById(R.id.UserNameLoginTextBox);
        passwordLoginTextBox = findViewById(R.id.passwordLoginTextBox);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = UserNameLoginTextBox.getText().toString();
            String password = passwordLoginTextBox.getText().toString();

            // Search for the user in the UserLinkedList
            User foundUser = findUser(username, password);

            if (foundUser != null) {
                // Set the currentUser reference to the found user
                MainActivity.currentUser = foundUser;
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                // Navigate to another activity, e.g., user's dashboard
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        TextView alreadyHaveAccount = findViewById(R.id.DoNotHaveAnAccount);
        alreadyHaveAccount.setOnClickListener(v -> {
            Intent i  = new Intent(this, MainActivity.class);
            startActivity(i);
        });
    }


    private User findUser(String username, String password) {
        // Iterate through the usersList and find the user
        List<User> usersList = MainActivity.usersList;
        for (User user : usersList) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // User not found
    }
}