package com.example.project2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * @author Noah
 * This class allows the user to edit a photo and upload it to the database.
 */
public class ImageEditActivity extends AppCompatActivity {
    //TODO: update navigation to this page
    private ImageView image;
    private BottomNavigationView nav;
    private Button uploadBtn;

    private Bitmap bmp;
    private Canvas canvas;
    private int heightOffset = 0;
    private int widthOffset = 0;
    private int circleColor = R.color.solid_blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        image = findViewById(R.id.imageView);
        nav = findViewById(R.id.bottom_navigation_image_edit);
        uploadBtn = findViewById(R.id.upload_btn);

        nav.setItemActiveIndicatorColor(getColorStateList(R.color.light_blue));
        //decodes a file into a bitmap. TODO: change this to use photo from phone and not resource file.
        bmp = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.test_image).copy(Bitmap.Config.ARGB_8888, true);
        //creates a canvas using the bitmap of the image as a base to be drawn on.
        canvas = new Canvas(bmp);

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                createCircle((int) motionEvent.getX(), (int) motionEvent.getY());
                return false;
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add upload code
            }
        });

        nav.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    /**
     * This method is draws a circle at the given touch coordinates on the cnavas.
     * @param x x touch coordinate
     * @param y y touch coordinate
     */
    private void createCircle(int x, int y){
        //These offsets are used to correct the x and y values if the image does not have the same aspect ratio of the imageView.
        heightOffset = (int)(image.getHeight() / 2 - ((double)image.getWidth() / getDrawable(R.drawable.test_image).getIntrinsicWidth()) * getDrawable(R.drawable.test_image).getIntrinsicHeight() / 2);
        widthOffset = (int)(image.getWidth() / 2 - ((double)image.getHeight() / getDrawable(R.drawable.test_image).getIntrinsicHeight()) * getDrawable(R.drawable.test_image).getIntrinsicWidth() / 2);

        final ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        //100 pixel diameter circle TODO: Determine appropriate radius for phone image resolution.
        circle.setIntrinsicHeight(100);
        circle.setIntrinsicWidth(100);
        circle.getPaint().setColor(ContextCompat.getColor(this.getApplicationContext(), circleColor));
        if (heightOffset < 0){widthOffset = 0;} //heightOffset is < 0 when the image aspect ratio(H:W) is greater than the imageView's and therefore not necessary.
        if (widthOffset < 0){widthOffset = 0;} //widthOffset is < 0 when the image aspect ratio(H:W) is less than the imageView's and therefore not necessary.
        //sets the area in which the circle will be drawn by translating the touch coordinates into image coordinates.
        circle.setBounds((x - widthOffset) * getDrawable(R.drawable.test_image).getIntrinsicWidth() / image.getWidth() - circle.getIntrinsicWidth() / 2,
                (y - heightOffset) * getDrawable(R.drawable.test_image).getIntrinsicWidth() / image.getWidth() - circle.getIntrinsicHeight() / 2,
                (x - widthOffset) * getDrawable(R.drawable.test_image).getIntrinsicWidth() / image.getWidth() + circle.getIntrinsicWidth() / 2,
                (y - heightOffset) * getDrawable(R.drawable.test_image).getIntrinsicWidth() / image.getWidth() + circle.getIntrinsicHeight() / 2);
        //makes the circle a line instead of filled and sets line width.
        circle.getPaint().setStyle(Paint.Style.STROKE);
        circle.getPaint().setStrokeWidth(10); //TODO: Determine appropriate line width for phone image resolution.
        //draw the circle onto the image.
        circle.draw(canvas);
        //update the UI.
        image.setImageBitmap(bmp);
    }

    /**
     * This method changes the navigation menu active indicator and the next circle's color depending on which item is selected.
     * @param item The selected item.
     * @return
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.leftHand){
            nav.setItemActiveIndicatorColor(getColorStateList(R.color.light_blue));
            circleColor = R.color.solid_blue;
        }
        else if(item.getItemId() == R.id.rightHand){
            nav.setItemActiveIndicatorColor(getColorStateList(R.color.light_green));
            circleColor = R.color.solid_green;
        }
        else if(item.getItemId() == R.id.leftFoot){
            nav.setItemActiveIndicatorColor(getColorStateList(R.color.light_orange));
            circleColor = R.color.solid_orange;
        }
        else if(item.getItemId() == R.id.rightFoot){
            nav.setItemActiveIndicatorColor(getColorStateList(R.color.light_red));
            circleColor = R.color.solid_red;
        }
        item.setChecked(true);
        return false;
    }
}
