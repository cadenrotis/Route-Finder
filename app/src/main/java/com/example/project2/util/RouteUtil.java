package com.example.project2.util;

import android.content.Context;

import com.example.project2.R;
import com.example.project2.model.Route;

import java.util.Random;

/**
 * Class used for generating random Route objects for testing.
 */
public class RouteUtil {

    /**
     * String array of random route titles.
     */
    private static final String[] TITLES = {
            "Sunny Hills Adventure",
            "Maple Grove Trail",
            "Oak Ridge Challenge",
            "Pine Valley Path",
            "Cedar Point Climb",
            "Willow Creek Walk",
            "Highland Peaks Summit",
            "Silver Meadows Exploration"
    };

    /**
     * String array of difficulty levels.
     */
    private static final String[] DIFFICULTY_LEVELS = {
            "Easy",
            "Moderate",
            "Hard",
            "Expert"
    };

    /**
     * String array of slopes.
     */
    private static final String[] SLOPES = {
            "Gentle",
            "Inclined",
            "Steep",
            "Very Steep"
    };

    /**
     * String array of route descriptions.
     */
    private static final String[] DESCRIPTIONS = {
            "A beautiful scenic route with gentle inclines.",
            "Perfect for intermediate climbers looking for a challenge.",
            "A steep path with breathtaking views at the summit.",
            "Ideal for beginners to learn the basics of climbing.",
            "An expert-level trail with sharp slopes and rocky terrain.",
            "Enjoy a moderate hike through lush greenery and meadows.",
            "An exhilarating climb with steep paths and tricky turns.",
            "A gentle walk along serene landscapes and creeks."
    };

    /**
     * String array of drawable resource IDs for rock climbing route pictures.
     */
    private static final int[] ROUTE_IMAGES = {
            R.drawable.route_1,
            R.drawable.route_2,
            R.drawable.route_3,
    };

    /**
     * Get a random Route object.
     * @param context The application context.
     * @return A random Route object.
     */
    public static Route getRandom(Context context) {
        Route route = new Route();
        Random random = new Random();

        String[] cities = context.getResources().getStringArray(R.array.cities);

        route.setTitle(getRandomString(TITLES, random));
        route.setCity(getRandomString(cities, random));
        route.setDifficulty(getRandomString(DIFFICULTY_LEVELS, random));
        route.setSlope(getRandomString(SLOPES, random));
        route.setDescription(getRandomString(DESCRIPTIONS, random));
        route.setPhoto(getRandomImage(random, context));
        route.setAvgRating(getRandomRating(random));
        route.setNumRatings(random.nextInt(50)); // Up to 50 ratings

        return route;
    }

    /**
     * Get a random image from the ROUTE_IMAGES array.
     * @param random A Random object passed down from getRandom().
     * @param context The application context passed down from getRandom().
     * @return A random image for a random Route object to use.
     */
    private static String getRandomImage(Random random, Context context) {
        int index = random.nextInt(ROUTE_IMAGES.length);
        int drawableId = ROUTE_IMAGES[index];

        // Return the resource name for display or use
        return context.getResources().getResourceEntryName(drawableId);
    }

    /**
     * Get a random string from the array parameter.
     * @param array An array of strings for either titles, difficulties, slopes, or descriptions.
     * @param random A Random object passed down from getRandom().
     * @return A random string from the array parameter to be used by the random Route created.
     */
    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    /**
     * Get a random rating for a Route between 1.0-5.0
     * @param random A Random object passed down from getRandom().
     * @return A random rating for the random Route created.
     */
    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }
}
