package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.adapter.RatingAdapter;
import com.example.project2.model.Rating;
import com.example.project2.model.Route;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class RouteReviewsActivity extends AppCompatActivity implements RatingDialogFragment.RatingListener {

    private static final String TAG = "RouteReviewsActivity";
    public static final String KEY_ROUTE_ID = "key_route_id";
    public static final String KEY_ROUTE_COLLECTION = "key_route_collection";
    public static final String KEY_ROUTE_TITLE = "key_route_title";

    // Firestore
    private FirebaseFirestore firestore;
    private Query reviewsQuery;
    private DocumentReference routeRef;
    private String routeCollection;
    private String routeTitle;

    // UI Components
    private RecyclerView reviewsRecyclerView;
    private RatingAdapter ratingAdapter;
    private TextView routeNameView;
    private View emptyView;
    private ImageButton backButton;
    private Button reviewButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_reviews);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get Route ID from Intent
        String routeId = getIntent().getStringExtra(KEY_ROUTE_ID);
        routeCollection = getIntent().getStringExtra(KEY_ROUTE_COLLECTION);
        routeTitle = getIntent().getStringExtra(KEY_ROUTE_TITLE);
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
        reviewButton = findViewById(R.id.review_button);

        // Set up RecyclerView
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Query Firestore for reviews
        reviewsQuery = routeRef.collection("ratings")
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

        // Review button to open RatingDialogFragment
        reviewButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RatingDialogFragment dialog = new RatingDialogFragment();
            dialog.show(fragmentManager, RatingDialogFragment.TAG);
        });

        // Load Route Name
        loadRouteName(routeId);

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_route) {
            switchToRouteView();
            return true;
        }
        else if(item.getItemId() == R.id.nav_delete) {
            deleteRoute();
            return true;
        }
        else {
            return false;
        }
    }

    private void switchToRouteView() {
        Intent intent = new Intent(RouteReviewsActivity.this, RouteDetailActivity.class);
        intent.putExtra(RouteDetailActivity.KEY_ROUTE_ID, routeRef.getId());
        intent.putExtra(RouteDetailActivity.KEY_ROUTE_COLLECTION, routeCollection);
        startActivity(intent);
    }

    // Delete the route from the database (and all collections if the route is public)
    private void deleteRoute() {
        // Check if the route exists in the "user_routes" collection, aka if the route belongs to the user
        firestore.collection("user_routes")
                .whereEqualTo("title", routeTitle) // Use the title to identify the route
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Route exists in "user_routes", proceed with deletion
                        routeRef.delete().addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Route deleted from " + routeCollection);
                            Toast.makeText(this, "Route deleted successfully", Toast.LENGTH_SHORT).show();

                            // If the route is public, delete it from the other collection
                            String otherCollection = routeCollection.equals("community_routes") ? "user_routes" : "community_routes";
                            deleteFromOtherCollection(otherCollection, (String) routeTitle);

                            // Navigate back to the dashboard after deletion
                            Intent intent = new Intent(RouteReviewsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to delete route from " + routeCollection, e);
                            Toast.makeText(this, "Failed to delete route", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // Route does not exist in "user_routes", aka the route was not created by the user
                        Toast.makeText(this, "Route cannot be deleted as you didn't create it", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Route does not exist in user_routes");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to check route in user_routes", e);
                    Toast.makeText(this, "Failed to validate route existence", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper function to delete route from another collection if route was posted publicly
    private void deleteFromOtherCollection(String otherCollection, String routeTitle) {
        firestore.collection(otherCollection)
                .whereEqualTo("title", routeTitle) // Assuming "title" is unique per route
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Route deleted from " + otherCollection))
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to delete route from " + otherCollection, e));
                        }
                    } else {
                        Log.d(TAG, "No matching route found in " + otherCollection);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to query " + otherCollection, e));
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

    // Add a new rating to the route (either in one collection or both depending on user access of route) in Firebase
    @Override
    public void onRating(Rating rating) {
        // Add the rating to the current collection (either community_routes or user_routes)
        routeRef.collection("ratings").add(rating)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Rating added to " + routeCollection);
                    updateRouteAvgRating(rating.getRating(), routeCollection, routeTitle);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error adding rating to " + routeCollection, e));

        // Determine the other collection to update
        String otherCollection = routeCollection.equals("community_routes") ? "user_routes" : "community_routes";

        // Check if the route exists in the other collection
        firestore.collection(otherCollection)
                .whereEqualTo("title", routeTitle)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // If the route exists in the other collection, add the rating and update averages
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            DocumentReference otherRouteRef = document.getReference();

                            // Add the rating to the existing route
                            otherRouteRef.collection("ratings").add(rating)
                                    .addOnSuccessListener(userDocRef -> {
                                        Log.d(TAG, "Rating added to existing route in " + otherCollection);
                                        updateRouteAvgRating(rating.getRating(), otherCollection, routeTitle);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error adding rating to " + otherCollection, e));
                        }
                    } else {
                        Log.d(TAG, "Route doesn't exist in " + otherCollection);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error querying " + otherCollection + " for title: " + routeTitle, e));
    }



    // Helper method to update the route's average rating and number of ratings
    private void updateRouteAvgRating(double newRating, String collection, String routeTitle) {
        firestore.collection(collection)
                .whereEqualTo("title", routeTitle)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            DocumentReference routeRefToUpdate = document.getReference();

                            firestore.runTransaction(transaction -> {
                                DocumentSnapshot snapshot = transaction.get(routeRefToUpdate);

                                if (snapshot.exists()) {
                                    long numRatings = snapshot.getLong("numRatings") != null ? snapshot.getLong("numRatings") : 0;
                                    double avgRating = snapshot.getDouble("avgRating") != null ? snapshot.getDouble("avgRating") : 0.0;

                                    long updatedNumRatings = numRatings + 1;
                                    double updatedAvgRating = ((avgRating * numRatings) + newRating) / updatedNumRatings;

                                    transaction.update(routeRefToUpdate, "numRatings", updatedNumRatings);
                                    transaction.update(routeRefToUpdate, "avgRating", updatedAvgRating);
                                }
                                return null;
                            }).addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Successfully updated avgRating and numRatings for route in " + collection);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating avgRating and numRatings for route in " + collection, e);
                            });
                        }
                    } else {
                        Log.e(TAG, "No route found with title: " + routeTitle + " in " + collection);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error querying route by title in " + collection, e));
    }

}
