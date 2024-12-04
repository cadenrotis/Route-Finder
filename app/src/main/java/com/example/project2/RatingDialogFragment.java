package com.example.project2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project2.model.Rating;
import com.example.project2.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Dialog Fragment containing the rating form.
 */
public class RatingDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "RatingDialog";

    /**
     * Variables to hold the UI components of the dialog_rating.xml layout.
     */
    private MaterialRatingBar ratingBar;
    private EditText reviewInput;
    private Button submitButton;
    private Button cancelButton;

    /**
     * Interface for handling rating events.
     */
    interface RatingListener {
        void onRating(Rating rating);
    }

    /**
     * Listener for rating events.
     */
    private RatingListener ratingListener;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View to be displayed by the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rating, container, false);

        // Initialize UI components
        ratingBar = view.findViewById(R.id.route_form_rating);
        reviewInput = view.findViewById(R.id.route_user_input);
        submitButton = view.findViewById(R.id.route_form_button);
        cancelButton = view.findViewById(R.id.route_form_cancel);

        // Set click listeners
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }

    /**
     * Called when the fragment is first attached to its context.
     * @param context The application context.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            ratingListener = (RatingListener) context;
        }
    }

    /**
     * Called when the fragment is no longer attached to its context.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Called when a view has been clicked.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.route_form_button) {
            onSubmitClicked(v);
        } else if (v.getId() == R.id.route_form_cancel) {
            onCancelClicked(v);
        }
    }

    /**
     * Called when the submit button is clicked.
     * @param view The view that was clicked.
     */
    private void onSubmitClicked(View view) {
        FirebaseAuth auth = FirebaseUtil.getAuth();
        if (auth.getCurrentUser() != null) {
            Rating rating = new Rating(
                    auth.getCurrentUser(),
                    ratingBar.getRating(),
                    reviewInput.getText().toString()
            );

            if (ratingListener != null) {
                ratingListener.onRating(rating);
            }

            dismiss();
        }
    }

    /**
     * Called when the cancel button is clicked.
     * @param view The view that was clicked.
     */
    private void onCancelClicked(View view) {
        dismiss();
    }
}
