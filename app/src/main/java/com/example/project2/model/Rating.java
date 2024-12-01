package com.example.project2.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Model POJO for a rating.
 */
public class Rating {

    private String userId;
    private String userName;
    private double rating;
    private String text;
    private @ServerTimestamp Date timestamp;

    public Rating() {}

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Format the timestamp to a user-friendly date string.
     * @return formatted date string or "N/A" if the timestamp is null.
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
