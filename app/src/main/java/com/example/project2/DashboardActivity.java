package com.example.project2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Get the current user from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Extract the username from the email
        String username = "Guest"; // Default value in case the user is null or email is missing
        if (currentUser != null && currentUser.getEmail() != null) {
            String email = currentUser.getEmail();
            username = email.substring(0, email.indexOf('@')); // Extract the part before '@'
        }

        // Set the username in the TextView
        TextView usernameTitle = findViewById(R.id.username_title);
        usernameTitle.setText(username);
    }
}
