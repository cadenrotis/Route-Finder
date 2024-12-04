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

import com.example.project2.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for authenticating users when logging in or signing up.
 */
public class AuthenticateActivity extends AppCompatActivity {

    /**
     * Variables for the elements in the activity_login.xml layout.
     */
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private RadioButton radioLogin;
    private RadioButton radioSignup;
    private TextView loginLabel;

    /**
     * Initializes the activity and sets up the button click listeners.
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in. If they are, then skip authentication process
        FirebaseUser currentUser = FirebaseUtil.getAuth().getCurrentUser();
        if (currentUser != null) {
            navigateToDashboard();
            return;
        }

        setContentView(R.layout.activity_login);

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

    /**
     * Switches the UI to the login state when the login radio button is clicked.
     */
    private void switchToLogin() {
        radioSignup.setChecked(false);
        loginLabel.setText("Login");
        loginButton.setText("Login");
    }

    /**
     * Switches the UI to the signup state when the signup radio button is clicked.
     */
    private void switchToSignup() {
        radioLogin.setChecked(false);
        loginLabel.setText("Sign Up");
        loginButton.setText("Sign Up");
    }

    /**
     * Handles the authentication process based on the selected radio button.
     */
    private void handleAuth() {
        String email = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Check if email and password are empty
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

    /**
     * Logs in the user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    private void loginUser(String email, String password) {
        FirebaseUtil.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        navigateToDashboard();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Signs up the user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    private void signupUser(String email, String password) {
        FirebaseUtil.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        navigateToDashboard();
                    } else {
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Navigates to the dashboard activity (the activity_dashboard.xml layout).
     */
    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
