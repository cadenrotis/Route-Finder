package com.example.project2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class ImageFullscreenActivity extends AppCompatActivity {
    private ImageView photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        photo = findViewById(R.id.photo);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://project-2-1d31a.firebasestorage.app");
        StorageReference riversRef = storageRef.child("RoutePhotos/" + getIntent().getExtras().getString("Route Title") + ".jpeg");
        // Load images
        riversRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convert the Base64 photo string to a Bitmap
                Bitmap photoBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.d("Cloud return", Arrays.toString(bytes));
                Log.d("Cloud return", bytes.length + "");
                // Set the converted Bitmap to the ImageView
                photo.setImageBitmap(photoBitmap);
            }
        });

    }
}
