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
    public static final String FIELD_DIFFICULTY_ORDER = "difficultyOrder";
    public static final String FIELD_SLOPE_ORDER = "slopeOrder";

    private String communityName;
    private String city;
    private String difficulty;
    private String photo;
    private String slope;
    private int numRatings;
    private double avgRating;
    private int difficultyOrder;
    private int slopeOrder;  // used for sorting routes by their difficulty

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
        this.difficultyOrder = calculateDifficultyOrder(difficulty);
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
        this.slopeOrder = calculateSlopeOrder(slope);
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

    public int getDifficultyOrder() {
        return difficultyOrder;
    }

    public void setDifficultyOrder(int difficultyOrder) {
        this.difficultyOrder = difficultyOrder;
    }

    public int getSlopeOrder() {
        return slopeOrder;
    }

    public void setSlopeOrder(int slopeOrder) {
        this.slopeOrder = slopeOrder;
    }


    // Helper method to calculate the difficulty order for sorting by difficulty
    public static int calculateDifficultyOrder(String difficulty) {
        if (difficulty == null) return Integer.MAX_VALUE; // Unknown difficulty
        switch (difficulty.toLowerCase()) {
            case "easy":
                return 1;
            case "moderate":
                return 2;
            case "hard":
                return 3;
            case "expert":
                return 4;
            default:
                return Integer.MAX_VALUE; // Unknown difficulty
        }
    }

    // Helper method to calculate the slope order for sorting by slope
    public static int calculateSlopeOrder(String slope) {
        if (slope == null) return Integer.MAX_VALUE; // Unknown slope
        switch (slope.toLowerCase()) {
            case "gentle":
                return 1;
            case "inclined":
                return 2;
            case "steep":
                return 3;
            case "very steep":
                return 4;
            default:
                return Integer.MAX_VALUE; // Unknown slope
        }
    }
}
