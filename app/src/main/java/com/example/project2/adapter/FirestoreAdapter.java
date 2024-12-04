/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.example.project2.adapter;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 * 
 * See the adapter classes in FirebaseUI (https://github.com/firebase/FirebaseUI-Android/tree/master/firestore) for a
 * more efficient implementation of a Firestore RecyclerView Adapter.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements EventListener<QuerySnapshot> {

    /**
     * Variables needed to handle updates in the Firestore database
     */
    private static final String TAG = "Firestore Adapter";
    private Query mQuery;
    private ListenerRegistration mRegistration;

    /**
     * ArrayList of DocumentSnapshots
     */
    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();

    /**
     * FireStoreAdapter constructor that takes a Firestore query.
     * @param query Firestore query
     */
    public FirestoreAdapter(Query query) {
        mQuery = query;
    }

    /**
     * Starts listening for changes to the query.
     */
    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    /**
     * Stops listening for changes to the query.
     */
    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    /**
     * Changes with query is being listened too
     */
    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    /**
     * Called when a change is detected with the query.
     * @param documentSnapshots The value of the event. {@code null} if there was an error.
     * @param e The error if there was error. {@code null} otherwise.
     */
    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        // Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }
        // Dispatch the event
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
        // Snapshot of the changed document
            DocumentSnapshot snapshot = change.getDocument();
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }
        onDataChanged();
    }

    /**
     * Called to add a change to a DocumentSnapshot
     * @param change A change to process in a document
     */
    protected void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    /**
     * Called to modify a DocumentSnapshot with a change
     * @param change A change to process in a document
     */
    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    /**
     * Called to remove a change from a DocumentSnapshot
     * @param change A change to process in a document
     */
    protected void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    /**
     * Gets the number of DocumentSnapshots in the database
     * @return The number of DocumentSnapshots
     */
    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    /**
     * Gets a DocumentSnapshot based on the index parameter
     * @param index
     * @return A DocumentSnapshot
     */
    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    /**
     * Called when there is an error
     * @param e The exception thrown upon the error
     */
    protected void onError(FirebaseFirestoreException e) {};

    protected void onDataChanged() {}

}


