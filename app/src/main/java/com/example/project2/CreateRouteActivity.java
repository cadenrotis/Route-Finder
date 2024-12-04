package com.example.project2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project2.model.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for allowing users to create a new route
 */
public class CreateRouteActivity extends AppCompatActivity {

    private static final String TAG = "CreateRouteActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    // UI Components
    private Uri routeImage;
    private EditText titleInput, locationInput, slopeInput, difficultyInput, descriptionInput;
    private Button takePhotoButton, submitButton;
    private ImageButton backButton;
    private RadioButton publicRadioButton, privateRadioButton;

    // Firestore instance
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        // Check if the app has camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Find UI components
        takePhotoButton = findViewById(R.id.take_photo_btn);
        titleInput = findViewById(R.id.title_input);
        locationInput = findViewById(R.id.location_input);
        slopeInput = findViewById(R.id.slope_input);
        difficultyInput = findViewById(R.id.difficulty_input);
        descriptionInput = findViewById(R.id.description_input);
        submitButton = findViewById(R.id.btn_submit);
        backButton = findViewById(R.id.back_button);
        publicRadioButton = findViewById(R.id.radio_public);
        privateRadioButton = findViewById(R.id.radio_private);

        // Set up back button
        backButton.setOnClickListener(v -> finish()); // goes back to dashboard view

        //Set up take photo button
        takePhotoButton.setOnClickListener(v -> takePhoto());

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void takePhoto() {
        // Create an Intent to launch the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a content URI for the image and assign it to the routeImage variable
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Route Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken for the route");
        routeImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Pass the URI to the camera via the intent
        if (routeImage != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, routeImage);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Failed to create image file.", Toast.LENGTH_SHORT).show();
        }
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

        // Check the selected access type via the radio button checked by the user
        if (publicRadioButton.isChecked()) {
            // Save to both "user_routes" and "community_routes"
            saveRouteToFirestore("user_routes", route);
            saveRouteToFirestore("community_routes", route);
        } else if (privateRadioButton.isChecked()) {
            // Save only to "user_routes"
            saveRouteToFirestore("user_routes", route);
        } else {
            Toast.makeText(this, "Please select Public or Private access", Toast.LENGTH_SHORT).show();
            return;
        }

        // Finish the activity
        finish();
    }

    private void saveRouteToFirestore(String collection, Route route) {
        firestore.collection(collection)
                .add(route)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Route added to " + collection + " with ID: " + documentReference.getId());
                    if (collection.equals("user_routes")) {
                        Toast.makeText(this, "Route added to your routes!", Toast.LENGTH_SHORT).show();
                    } else if (collection.equals("community_routes")) {
                        Toast.makeText(this, "Route added to the community!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding route to " + collection, e);
                    Toast.makeText(this, "Failed to add route to " + collection, Toast.LENGTH_SHORT).show();
                });
    }
}
