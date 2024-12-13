package com.example.project2.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Model POJO for a rating/review for a route.
 */
public class Rating {

    private String userId;
    private String userName;
    private double rating;
    private String text;
    private @ServerTimestamp Date timestamp;

    /**
     * Default constructor for Rating
     */
    public Rating() {}

    /**
     * Constructor for Rating that takes a FirebaseUser, a rating, and a text
     * @param user Firebase user, needed for getting the user's username
     * @param rating Rating to be used for a Ratingbar
     * @param text Contains the description that the user wrote for their review of a route
     */
    public Rating(FirebaseUser user, double rating, String text) {
        this.userId = user.getUid();
        this.userName = user.getDisplayName();

        // Use part of the email before '@' if displayName is empty
        if (TextUtils.isEmpty(this.userName) && user.getEmail() != null) {
            this.userName = user.getEmail().split("@")[0];
        }

        this.rating = rating;
        this.text = text;
    }

    /**
     * Get the username for the user that set the rating/review.
     * @return A string that consists of the username.
     */
    public String getUserName() {
        return userName;
    }

    public double getRating() {
        return rating;
    }

    /**
     * Set the rating that the user set in their review of a route
     * @param rating A double that consists of the rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Get the description that the user wrote for their review of a route
     * @return A string that consists of the description
     */
    public String getText() {
        return text;
    }

    /**
     * Set the description that the user wrote for their review of a route
     * @param text A string that consists of the description
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Format the timestamp to a user-friendly date string.
     * @return A formatted date string or "N/A" if the timestamp is null.
     */
    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "N/A";
        }

        // Format the date (adjust formatting as needed)
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(timestamp);
    }
}
