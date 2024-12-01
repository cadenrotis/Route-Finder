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
 * Dialog Fragment containing rating form.
 */
public class RatingDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "RatingDialog";

    private MaterialRatingBar ratingBar;
    private EditText reviewInput;
    private Button submitButton;
    private Button cancelButton;

    interface RatingListener {
        void onRating(Rating rating);
    }

    private RatingListener ratingListener;

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            ratingListener = (RatingListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.route_form_button) {
            onSubmitClicked(v);
        } else if (v.getId() == R.id.route_form_cancel) {
            onCancelClicked(v);
        }
    }

    // Called when the submit button is clicked
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

    // Called when the cancel button is clicked
    private void onCancelClicked(View view) {
        dismiss();
    }
}
