package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.adapter.RatingAdapter;
import com.example.project2.model.Rating;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class RouteReviewsActivity extends AppCompatActivity {

    private static final String TAG = "RouteReviewsActivity";
    public static final String KEY_ROUTE_ID = "key_route_id";
    public static final String KEY_ROUTE_COLLECTION = "key_route_collection";

    // Firestore
    private FirebaseFirestore firestore;
    private Query reviewsQuery;
    private DocumentReference routeRef;
    private String routeCollection;

    // UI Components
    private RecyclerView reviewsRecyclerView;
    private RatingAdapter ratingAdapter;
    private TextView routeNameView;
    private View emptyView;
    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_reviews);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get Route ID from Intent
        String routeId = getIntent().getStringExtra(KEY_ROUTE_ID);
        routeCollection = getIntent().getStringExtra(KEY_ROUTE_COLLECTION);
        if (routeId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_ROUTE_ID);
        }

        // Get reference to the route collection
        routeRef = firestore.collection(routeCollection).document(routeId);

        // Initialize UI Components
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        emptyView = findViewById(R.id.view_empty);
        routeNameView = findViewById(R.id.route_name);
        backButton = findViewById(R.id.back_button);

        // Set up RecyclerView
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Query Firestore for reviews
        reviewsQuery = firestore.collection("routes/" + routeId + "/ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Set up Adapter
        ratingAdapter = new RatingAdapter(reviewsQuery) {
            @Override
            public void onDataChanged() {
                // Show/hide the RecyclerView based on data
                if (getItemCount() == 0) {
                    reviewsRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    reviewsRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                Log.e(TAG, "Error loading reviews", e);
            }
        };

        reviewsRecyclerView.setAdapter(ratingAdapter);

        // Back button to return to MainActivity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RouteReviewsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Load Route Name
        loadRouteName(routeId);

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_route){
            switchToRouteView();
            return true;
        }
        else {
            return false;
        }
    }

    // Go to the route details page when the "Route" button is pressed
    private void switchToRouteView() {
        // Create an Intent to open the RouteDetailActivity
        Intent intent = new Intent(RouteReviewsActivity.this, RouteDetailActivity.class);

        // Pass the route ID to the RouteDetailActivity
        intent.putExtra(RouteDetailActivity.KEY_ROUTE_ID, routeRef.getId());

        // Pass the collection the route is in (community_routes or user_routes)
        intent.putExtra(RouteDetailActivity.KEY_ROUTE_COLLECTION, routeCollection);

        // Start the RouteDetailActivity
        startActivity(intent);
    }

    private void loadRouteName(String routeId) {
        firestore.collection(routeCollection).document(routeId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String routeName = snapshot.getString("title");
                        routeNameView.setText(routeName != null ? routeName : "Route Name");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching route name", e));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ratingAdapter != null) {
            ratingAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ratingAdapter != null) {
            ratingAdapter.stopListening();
        }
    }
}
