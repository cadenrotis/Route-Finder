package com.example.project2.adapter;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * RecyclerView adapter that displays the route previews in the dashboard view
 */
public class RouteAdapter extends FirestoreAdapter<RouteAdapter.ViewHolder> {

    /**
     * Interface for handling route selection events
     */
    public interface OnRouteSelectedListener {
        void onRouteSelected(DocumentSnapshot route);
    }

    /**
     * Listener for route selection events
     */
    private OnRouteSelectedListener mListener;

    /**
     * Variables for Firestore
     */
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RouteAdapter mAdapter;

    /**
     * Constructor for RouteAdapter that takes a Firestore Query and a listener
     * @param query    Firestore query
     * @param listener Listener for route selection events
     */
    public RouteAdapter(Query query, OnRouteSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    /**
     * Creates a new ViewHolder for a Route Object
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_route, parent, false));
    }

    /**
     * Binds a Route object to a ViewHolder
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    /**
     * Class called to update a ViewHolder for a Route object
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * UI elements defined in item_route.xml
         */
        ImageView iconView;
        TextView titleView;
        TextView subtitleView;

        /**
         * Constructor for ViewHolder that takes a View
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.item_icon);
            titleView = itemView.findViewById(R.id.item_title);
            subtitleView = itemView.findViewById(R.id.item_subtitle);
        }

        /**
         * Binds a Route object to a ViewHolder
         * @param snapshot The snapshot of the route
         * @param listener The listener for route selection events
         */
        public void bind(final DocumentSnapshot snapshot, final OnRouteSelectedListener listener) {
            Route route = snapshot.toObject(Route.class);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReferenceFromUrl("gs://project-2-1d31a.firebasestorage.app");
            StorageReference riversRef = storageRef.child("RoutePhotos/" + route.getTitle() + ".jpeg");
            riversRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Convert the Base64 photo string to a Bitmap
                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Log.d("Cloud return", Arrays.toString(bytes));
                    Log.d("Cloud return", bytes.length + "");
                    // Set the converted Bitmap to the ImageView
                    iconView.setImageBitmap(photoBitmap);}
            });

            // Set the text for several TextViews
            titleView.setText(route.getTitle());
            subtitleView.setText(String.format("Location: %s\nDifficulty: %s\nSlope: %s\nRating: %.2f",
                    route.getCity(), route.getDifficulty(), route.getSlope(), route.getAvgRating()));

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRouteSelected(snapshot);
                }
            });
        }
    }
}
