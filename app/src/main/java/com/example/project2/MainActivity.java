package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.util.RouteUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.example.project2.adapter.RouteAdapter;
import com.example.project2.model.Route;

public class MainActivity extends AppCompatActivity implements RouteAdapter.OnRouteSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;

    private RecyclerView mRoutesRecycler;
    private ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RouteAdapter mAdapter;

    // Buttons and search bar
    private Button filterRatingButton;
    private Button filterLocationButton;
    private Button filterDifficultyButton;
    private Button filterSlopeButton;
    private EditText searchLabel;

    private Button selectedFilterButton; // To track the selected filter button

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

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Set up Firestore query to fetch routes
        mQuery = mFirestore.collection("routes")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);

        // Initialize RecyclerView
        mRoutesRecycler = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.view_empty);

        // Initialize filter buttons and search bar
        filterRatingButton = findViewById(R.id.filter_rating);
        filterLocationButton = findViewById(R.id.filter_location);
        filterDifficultyButton = findViewById(R.id.filter_difficulty);
        filterSlopeButton = findViewById(R.id.filter_slope);
        searchLabel = findViewById(R.id.search_label);

        // Set button click listeners
        setUpFilters();

        // Set search bar listener
        setUpSearch();

        // Set up Test Routes button
        Button testRoutesButton = findViewById(R.id.button_test_routes);
        testRoutesButton.setOnClickListener(v -> generateRoutes());

        initRecyclerView();
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        // Create a new adapter
        mAdapter = new RouteAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show or hide RecyclerView based on query results
                if (getItemCount() == 0) {
                    mRoutesRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRoutesRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show error message
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: Check logs for details.", Snackbar.LENGTH_LONG).show();
            }
        };

        // Set up RecyclerView with a grid layout (3 columns for the grid)
        mRoutesRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        mRoutesRecycler.setAdapter(mAdapter);
    }

    // Set onClickListeners for each filter button, and determines the current filter
    private void setUpFilters() {
        filterRatingButton.setOnClickListener(v -> {
            applyFilter(Route.FIELD_AVG_RATING);
            setSelectedFilter(filterRatingButton);
        });
        filterLocationButton.setOnClickListener(v -> {
            applyFilter(Route.FIELD_CITY);
            setSelectedFilter(filterLocationButton);
        });
        filterDifficultyButton.setOnClickListener(v -> {
            applyFilter(Route.FIELD_DIFFICULTY);
            setSelectedFilter(filterDifficultyButton);
        });
        filterSlopeButton.setOnClickListener(v -> {
            applyFilter(Route.FIELD_SLOPE);
            setSelectedFilter(filterSlopeButton);
        });
    }

    // Sets the currently selected filter button by changing backgroundTint
    private void setSelectedFilter(Button button) {
        if (selectedFilterButton != null) {
            selectedFilterButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.greyLight));
        }
        selectedFilterButton = button;
        selectedFilterButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_200));
    }

    // Format the search bar and sort routes when the user types in the search bar
    private void setUpSearch() {
        searchLabel.addTextChangedListener(new TextWatcher() {
            // don't think this is needed actually
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applySearch(s.toString());
            }

            // don't think this is needed actually
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Applying a filter to all routes in the database
    private void applyFilter(String field) {
        String searchText = searchLabel.getText().toString().trim();

        // Difficulty filter that sorts routes from easy to moderate to hard to expert difficulties
        if (field.equals(Route.FIELD_DIFFICULTY)) {
            mQuery = mFirestore.collection("routes")
                    .orderBy(Route.FIELD_DIFFICULTY_ORDER)
                    .orderBy(Route.FIELD_AVG_RATING, Query.Direction.DESCENDING);
        }
        // Slope filter that sorts routes from gentle to steep to very steep slopes
        else if (field.equals(Route.FIELD_SLOPE)) {
            mQuery = mFirestore.collection("routes")
                    .orderBy(Route.FIELD_SLOPE_ORDER)
                    .orderBy(Route.FIELD_AVG_RATING, Query.Direction.DESCENDING);
        }
        // Rating filter sorts routes by descending order of average rating
        else if (field.equals(Route.FIELD_AVG_RATING)) {
            mQuery = mFirestore.collection("routes").orderBy(field, Query.Direction.DESCENDING);
        }

        // Location filter sorts routes by ascending order of city name strings
        else {
            mQuery = mFirestore.collection("routes").orderBy(field, Query.Direction.ASCENDING);
        }

        // Update the adapter with the new query
        mAdapter.setQuery(mQuery);
    }

    // Filter routes based on user's input into the search bar
    private void applySearch(String searchText) {
        if (selectedFilterButton == null) {
            // Default behavior if no filter button is selected
            mQuery = mFirestore.collection("routes")
                    .orderBy(Route.FIELD_AVG_RATING, Query.Direction.DESCENDING); // sort by descending avg rating by default
        }
        else {
            // Determine the field based on the currently selected filter button
            String field = null;
            if (selectedFilterButton == filterLocationButton) {
                field = Route.FIELD_CITY;
            } else if (selectedFilterButton == filterDifficultyButton) {
                field = Route.FIELD_DIFFICULTY;
            } else if (selectedFilterButton == filterSlopeButton) {
                field = Route.FIELD_SLOPE;
            } else if (selectedFilterButton == filterRatingButton) {
                field = Route.FIELD_AVG_RATING;
            }

            // if the currently selected filter button is anything other than Ratings, then filter by the search text and fix input if needed
            if (field != null  && !field.equals(Route.FIELD_AVG_RATING) && !searchText.isEmpty()) {
                // Use case-insensitive searching with Firestore's array-contains or equality logic
                mQuery = mFirestore.collection("routes")
                        .whereEqualTo(field, capitalizeFirstLetter(searchText)) // Adjust string since Firebase is case-sensitive
                        .orderBy(Route.FIELD_AVG_RATING, Query.Direction.DESCENDING); // sort by descending avg rating by default
            }
            // if the currently selected filter button is Ratings, then convert user input to an integer to show all routes with similar ratings
            else if (field != null && field.equals(Route.FIELD_AVG_RATING) && !searchText.isEmpty()) {
                // Convert search text to an integer
                int ratingValue = Integer.parseInt(searchText);

                mQuery = mFirestore.collection("routes")
                        .whereGreaterThanOrEqualTo(field, ratingValue) // ratingValue is a lower bound
                        .whereLessThan(field, ratingValue+1)// ratingValue+1 is an upper bound
                        .orderBy(Route.FIELD_AVG_RATING, Query.Direction.DESCENDING); // sort by descending avg rating by default
            }
            else {
                applyFilter(field);
            }
        }

        // Update the adapter with the new query
        mAdapter.setQuery(mQuery);
    }


    // Utility method to capitalize the first letter of a string since Firebase is case-sensitive.
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start listening to Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Stop listening to Firestore updates
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void generateRoutes() {
        CollectionReference routes = mFirestore.collection("routes");

        // Generate and add 2 random Route objects to Firestore
        for (int i = 0; i < 2; i++) {
            Route randomRoute = RouteUtil.getRandom(this);
            routes.add(randomRoute);
        }
    }

    @Override
    public void onRouteSelected(DocumentSnapshot route) {
        // Handle route selection (navigate to a details screen, for example)
        Log.d(TAG, "Route selected: " + route.getId());
    }
}
