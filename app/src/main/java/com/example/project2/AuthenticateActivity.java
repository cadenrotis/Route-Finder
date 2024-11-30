package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticateActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private RadioButton radioLogin;
    private RadioButton radioSignup;
    private TextView loginLabel;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize elements in view
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        radioLogin = findViewById(R.id.radio_login);
        radioSignup = findViewById(R.id.radio_signup);
        loginLabel = findViewById(R.id.login_label);

        // Set up button click listeners
        radioLogin.setOnClickListener(v -> switchToLogin());
        radioSignup.setOnClickListener(v -> switchToSignup());
        loginButton.setOnClickListener(v -> handleAuth());
    }

    // Switch to the login screen when the login radio button is clicked
    private void switchToLogin() {
        radioSignup.setChecked(false);
        loginLabel.setText("Login");
        loginButton.setText("Login");
    }

    // Switch to the signup screen when the signup radio button is clicked
    private void switchToSignup() {
        radioLogin.setChecked(false);
        loginLabel.setText("Sign Up");
        loginButton.setText("Sign Up");
    }

    // Handle the login or signup button click
    private void handleAuth() {
        String email = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (radioLogin.isChecked()) {
            loginUser(email, password);
        } else if (radioSignup.isChecked()) {
            signupUser(email, password);
        }
    }

    // Login user using Firebase Auth
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Welcome, " + (user != null ? user.getEmail() : ""), Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        // Login failed
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Signup user using Firebase Auth
    private void signupUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Signup successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Signup successful! Welcome, " + (user != null ? user.getEmail() : ""), Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        // Signup failed
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Navigate to the dashboard activity when user successfully logs in or signs up
    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
