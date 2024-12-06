package com.example.project2;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project2.model.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;

/**
 * Activity for allowing users to create a new route
 */
public class CreateRouteActivity extends AppCompatActivity {

    private static final String TAG = "CreateRouteActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    /**
     * Variables for elements in the activity_create_route.xml layout.
     */
    private Uri routeImageUri;
    private Bitmap routeImageBitmap;
    private ActivityResultLauncher<Uri> takePhotoLauncher;

    private EditText titleInput, locationInput, slopeInput, difficultyInput, descriptionInput;
    private Button takePhotoButton, submitButton;
    private ImageButton backButton;
    private RadioButton publicRadioButton, privateRadioButton;

    /**
     * Firebase Firestore instance
     */
    private FirebaseFirestore firestore;

    /**
     * Initializes the activity and sets up the button click listeners.
     * @param savedInstanceState The saved state of the activity.
     */
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

        // Initialize the launcher
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        Toast.makeText(this, "Photo taken successfully!", Toast.LENGTH_SHORT).show();
                        // You can load the image into an ImageView using routeImage Uri if needed
                    } else {
                        Toast.makeText(this, "Photo capture failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Set up back button
        backButton.setOnClickListener(v -> finish()); // goes back to dashboard view

        //Set up take photo button
        takePhotoButton.setOnClickListener(v -> takePhoto());

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void takePhoto() {
        // Create a content URI for the image and assign it to the routeImage variable
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Route Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken for the route");
        routeImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Pass the URI to the camera via the intent
        if (routeImageUri != null) {
            takePhotoLauncher.launch(routeImageUri);
            routeImageBitmap = uriToBitmap(routeImageUri);
        } else {
            Toast.makeText(this, "Failed to create image file.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Converts a Uri to a Bitmap.
     *
     * @param uri The URI of the image.
     * @return The image as a Bitmap, or null if conversion fails.
     * @throws IOException If an error occurs while reading the image.
     * @throws IllegalArgumentException If the URI is {@code null}.
     */
    private Bitmap uriToBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            // Open an InputStream from the Uri
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                // Decode the InputStream to a Bitmap
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close(); // Always close the stream to avoid memory leaks
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Capitalize the first letter of each word in user's input to keep text consistent among all users.
     * @param text The text that the user has entered into the create route form.
     * @return Modified text where the first letter in each word is capitalized.
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String[] words = text.split(" ");
        StringBuilder capitalizedText = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedText.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return capitalizedText.toString().trim();
    }

    /**
     * Handles the submission of the route creation form.
     */
    private void handleSubmit() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to create a route", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String title = capitalizeFirstLetter(titleInput.getText().toString().trim());
        String location = capitalizeFirstLetter(locationInput.getText().toString().trim());
        String slope = capitalizeFirstLetter(slopeInput.getText().toString().trim());
        String difficulty = capitalizeFirstLetter(difficultyInput.getText().toString().trim());
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
        route.setNumRatings(0);
        route.setAvgRating(0.0);
        route.setPhoto(null);

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

        // Finish the activity to go back to the dashboard view
        finish();
    }

    /**
     * Saves a route to the correct collection in the Firestore database.
     * @param collection The collection to save the route to.
     * @param route The route to save.
     */
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
