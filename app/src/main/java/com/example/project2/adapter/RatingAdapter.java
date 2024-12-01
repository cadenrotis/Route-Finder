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

    public RatingAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Rating.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView dateView;
        RatingBar ratingBar;
        TextView usernameView;
        TextView supportingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.review_title);
            dateView = itemView.findViewById(R.id.review_date);
            ratingBar = itemView.findViewById(R.id.review_rating);
            usernameView = itemView.findViewById(R.id.review_username);
            supportingTextView = itemView.findViewById(R.id.review_supporting_text);
        }

        public void bind(Rating rating) {
            titleView.setText(rating.getText());
            dateView.setText(rating.getFormattedTimestamp());
            ratingBar.setRating((float) rating.getRating());
            usernameView.setText(rating.getUserName());
            supportingTextView.setText(rating.getText());
        }
    }
}
