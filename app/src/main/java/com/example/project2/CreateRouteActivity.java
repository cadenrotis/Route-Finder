package com.example.project2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.model.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for allowing users to create a new route
 */
public class CreateRouteActivity extends AppCompatActivity {

    private static final String TAG = "CreateRouteActivity";

    // UI Components
    private EditText titleInput, locationInput, slopeInput, difficultyInput, descriptionInput;
    private Button submitButton;
    private ImageButton backButton;

    // Firestore instance
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Find UI components
        titleInput = findViewById(R.id.title_input);
        locationInput = findViewById(R.id.location_input);
        slopeInput = findViewById(R.id.slope_input);
        difficultyInput = findViewById(R.id.difficulty_input);
        descriptionInput = findViewById(R.id.description_input);
        submitButton = findViewById(R.id.btn_submit);
        backButton = findViewById(R.id.back_button);

        // Set up back button
        backButton.setOnClickListener(v -> finish()); // goes back to dashboard view

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to create a route", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String title = titleInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String slope = slopeInput.getText().toString().trim();
        String difficulty = difficultyInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String userId = currentUser.getUid();

        // Check that the user has entered all required input fields
        if (title.isEmpty() || location.isEmpty() || slope.isEmpty() || difficulty.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Route object
        Route route = new Route();
        route.setTitle(title);
        route.setCity(location);
        route.setSlope(slope);
        route.setDifficulty(difficulty);
        route.setDescription(description);
        route.setNumRatings(0); // Default value
        route.setAvgRating(0.0); // Default value
        route.setPhoto(null); // Placeholder for photo

        // Save to "user_routes"
        firestore.collection("user_routes")
                .add(route)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Route added to user_routes with ID: " + documentReference.getId());
                    Toast.makeText(this, "Route added to your routes!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding route to user_routes", e);
                    Toast.makeText(this, "Failed to add route to your routes", Toast.LENGTH_SHORT).show();
                });

        // Save to "community_routes"
        firestore.collection("community_routes")
                .add(route)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Route added to community_routes with ID: " + documentReference.getId());
                    Toast.makeText(this, "Route added to the community!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding route to community_routes", e);
                    Toast.makeText(this, "Failed to add route to the community", Toast.LENGTH_SHORT).show();
                });

        // Finish the activity
        finish();
    }
}
