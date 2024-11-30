package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.util.RouteUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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
