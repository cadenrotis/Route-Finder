package com.example.project2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Retrieve the username from the Intent
        String username = getIntent().getStringExtra("USERNAME");

        // Set the username in the TextView
        TextView usernameTitle = findViewById(R.id.username_title);
        usernameTitle.setText(username);
    }
}
