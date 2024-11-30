package com.example.project2.util;

import android.content.Context;

import com.example.project2.R;
import com.example.project2.model.Route;

import java.util.Locale;
import java.util.Random;

/**
 * Utilities for Routes.
 */
public class RouteUtil {

    private static final String[] COMMUNITY_NAMES = {
            "Sunny Hills",
            "Maple Grove",
            "Oak Ridge",
            "Pine Valley",
            "Cedar Point",
            "Willow Creek",
            "Highland Peaks",
            "Silver Meadows"
    };

    private static final String[] DIFFICULTY_LEVELS = {
            "Easy",
            "Moderate",
            "Hard",
            "Expert"
    };

    private static final String[] SLOPES = {
            "Gentle",
            "Inclined",
            "Steep",
            "Very Steep"
    };

    /**
     * Array of drawable resource IDs for rock climbing route pictures.
     */
    private static final int[] ROUTE_IMAGES = {
            R.drawable.route_1,
            R.drawable.route_2,
            R.drawable.route_3,
    };

    /**
     * Create a random Route POJO.
     */
    public static Route getRandom(Context context) {
        Route route = new Route();
        Random random = new Random();

        // Cities (assuming you have R.array.cities in strings.xml)
        String[] cities = context.getResources().getStringArray(R.array.cities);

        route.setCommunityName(getRandomString(COMMUNITY_NAMES, random));
        route.setCity(getRandomString(cities, random));
        route.setDifficulty(getRandomString(DIFFICULTY_LEVELS, random));
        route.setSlope(getRandomString(SLOPES, random));
        route.setPhoto(getRandomImage(random, context));
        route.setAvgRating(getRandomRating(random));
        route.setNumRatings(random.nextInt(50)); // Up to 50 ratings

        return route;
    }

    /**
     * Get a random image from local drawables.
     */
    private static String getRandomImage(Random random, Context context) {
        int index = random.nextInt(ROUTE_IMAGES.length);
        int drawableId = ROUTE_IMAGES[index];

        // Return the resource name for display or use
        return context.getResources().getResourceEntryName(drawableId);
    }

    /**
     * Get a random string from an array.
     */
    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    /**
     * Get a random rating between 1.0 and 5.0.
     */
    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }
}
