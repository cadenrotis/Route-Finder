package com.example.project2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.R;
import com.example.project2.model.Rating;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for displaying a list of Ratings.
 */
public class RatingAdapter extends FirestoreAdapter<RatingAdapter.ViewHolder> {

    /**
     * Constructor for RatingAdapter that takes a Firestore Query
     * @param query
     */
    public RatingAdapter(Query query) {
        super(query);
    }

    /**
     * Creates a new ViewHolder for a Rating
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_rating, parent, false));
    }

    /**
     * Binds a Rating to a ViewHolder
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Rating.class));
    }

    /**
     * Class called to update a ViewHolder for a Rating object
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * UI elements defined in item_route_rating.xml
         */
        TextView dateView;
        RatingBar ratingBar;
        TextView usernameView;
        TextView supportingTextView;

        /**
         * Constructor for ViewHolder that takes a View
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.review_date);
            ratingBar = itemView.findViewById(R.id.review_rating);
            usernameView = itemView.findViewById(R.id.review_username);
            supportingTextView = itemView.findViewById(R.id.review_supporting_text);
        }

        /**
         * Binds a Rating object to a ViewHolder
         * @param rating
         */
        public void bind(Rating rating) {
            dateView.setText(rating.getFormattedTimestamp());
            ratingBar.setRating((float) rating.getRating());
            usernameView.setText(rating.getUserName());
            supportingTextView.setText(rating.getText());
        }
    }
}
