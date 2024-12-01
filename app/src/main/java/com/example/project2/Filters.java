package com.example.project2;

import android.content.Context;
import android.text.TextUtils;

import com.example.project2.model.Route;
import com.example.project2.util.RouteUtil;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters related to Routes. Currently not used
 */
public class Filters {

    private String communityName = null;
    private String city = null;
    private String difficulty = null;
    private String slope = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortDirection(Query.Direction.DESCENDING);
        filters.setSortBy(Route.FIELD_AVG_RATING); // Default sorting by average rating
        return filters;
    }

    public boolean hasCommunityName() {
        return !TextUtils.isEmpty(communityName);
    }

    public boolean hasCity() {
        return !TextUtils.isEmpty(city);
    }

    public boolean hasDifficulty() {
        return !TextUtils.isEmpty(difficulty);
    }

    public boolean hasSlope() {
        return !TextUtils.isEmpty(slope);
    }

    public boolean hasSortBy() {
        return !TextUtils.isEmpty(sortBy);
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

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (TextUtils.isEmpty(communityName) && TextUtils.isEmpty(city)) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_routes));
            desc.append("</b>");
        }

        if (!TextUtils.isEmpty(communityName)) {
            desc.append("<b>");
            desc.append(communityName);
            desc.append("</b>");
        }

        if (!TextUtils.isEmpty(communityName) && !TextUtils.isEmpty(city)) {
            desc.append(" in ");
        }

        if (!TextUtils.isEmpty(city)) {
            desc.append("<b>");
            desc.append(city);
            desc.append("</b>");
        }

        if (!TextUtils.isEmpty(difficulty)) {
            desc.append(", Difficulty: ");
            desc.append("<b>");
            desc.append(difficulty);
            desc.append("</b>");
        }

        if (!TextUtils.isEmpty(slope)) {
            desc.append(", Slope: ");
            desc.append("<b>");
            desc.append(slope);
            desc.append("</b>");
        }

        return desc.toString();
    }
}
