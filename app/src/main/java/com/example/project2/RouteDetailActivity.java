package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project2.model.Route;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for displaying information about a route when clicked on in the dashboard view
 */
public class RouteDetailActivity extends AppCompatActivity {

    private static final String TAG = "RouteDetailActivity";
    public static final String KEY_ROUTE_ID = "key_route_id";
    public static final String KEY_ROUTE_COLLECTION = "key_route_collection";

    // UI Components
    private TextView routeTitle, location, difficulty, slope, routeDescription;
    private RatingBar communityRatingBar;
    private ImageView imageOne, imageTwo;

    // Firestore
    private FirebaseFirestore firestore;
    private DocumentReference routeRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get Route ID and Collection from Intent Extras
        String routeId = getIntent().getStringExtra(KEY_ROUTE_ID);
        String routeCollection = getIntent().getStringExtra(KEY_ROUTE_COLLECTION);
        if (routeId == null || routeCollection == null) {
            throw new IllegalArgumentException("Must pass extras " + KEY_ROUTE_ID + " and " + KEY_ROUTE_COLLECTION);
        }

        // Get reference to the route collection
        routeRef = firestore.collection(routeCollection).document(routeId);

        // Initialize UI Components
        routeTitle = findViewById(R.id.route_title);
        location = findViewById(R.id.location);
        difficulty = findViewById(R.id.difficulty);
        slope = findViewById(R.id.slope);
        routeDescription = findViewById(R.id.route_description);
        communityRatingBar = findViewById(R.id.community_rating_bar);
        imageOne = findViewById(R.id.image_one);
        imageTwo = findViewById(R.id.image_two);

        // Load route details
        loadRouteDetails();

        // When back button is pressed, go back to the dashboard view
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(RouteDetailActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Fetch route information from the database
    private void loadRouteDetails() {
        routeRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Toast.makeText(this, "Route not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Route route = snapshot.toObject(Route.class);
            if (route != null) {
                displayRouteDetails(route);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to load route", e);
            Toast.makeText(this, "Failed to load route", Toast.LENGTH_SHORT).show();
        });
    }

    // Display route information in the UI
    private void displayRouteDetails(Route route) {
        // Set UI elements with route data
        routeTitle.setText(route.getTitle());
        location.setText(String.format("Location: %s", route.getCity()));
        difficulty.setText(String.format("Difficulty: %s", route.getDifficulty()));
        slope.setText(String.format("Slope: %s", route.getSlope()));
        routeDescription.setText(route.getDescription());
        communityRatingBar.setRating((float) route.getAvgRating());

        // Load images
        Glide.with(this)
                .load(route.getPhoto())
                .into(imageOne);

        Glide.with(this)
                .load(route.getPhoto())
                .into(imageTwo);
    }
}
