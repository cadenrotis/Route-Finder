package com.example.project2.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.model.Route;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of Routes.
 */
public class RouteAdapter extends FirestoreAdapter<RouteAdapter.ViewHolder> {

    public interface OnRouteSelectedListener {
        void onRouteSelected(DocumentSnapshot route);
    }

    private OnRouteSelectedListener mListener;

    public RouteAdapter(Query query, OnRouteSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_route, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconView;
        TextView titleView;
        TextView subtitleView;

        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.item_icon);
            titleView = itemView.findViewById(R.id.item_title);
            subtitleView = itemView.findViewById(R.id.item_subtitle);
        }

        public void bind(final DocumentSnapshot snapshot, final OnRouteSelectedListener listener) {
            Route route = snapshot.toObject(Route.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(iconView.getContext())
                    .load(route.getPhoto())
                    .into(iconView);

            titleView.setText(route.getCommunityName());
            subtitleView.setText(String.format("Difficulty: %s | Slope: %s",
                    route.getDifficulty(), route.getSlope()));

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRouteSelected(snapshot);
                }
            });
        }
    }
}
