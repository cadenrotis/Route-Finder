package com.example.project2.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Route POJO.
 */
@IgnoreExtraProperties
public class Route {

    public static final String FIELD_COMMUNITY_NAME = "communityName";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_DIFFICULTY = "difficulty";
    public static final String FIELD_SLOPE = "slope";
    public static final String FIELD_NUM_RATINGS = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    private String communityName;
    private String city;
    private String difficulty;
    private String photo;
    private String slope;
    private int numRatings;
    private double avgRating;

    public Route() {}

    public Route(String communityName, String city, String difficulty, String photo,
                 String slope, int numRatings, double avgRating) {
        this.communityName = communityName;
        this.city = city;
        this.difficulty = difficulty;
        this.photo = photo;
        this.slope = slope;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}
