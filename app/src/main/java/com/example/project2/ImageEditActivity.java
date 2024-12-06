package com.example.project2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.vector.PathNode;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.io.InputStream;

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

    private int previousXLH = -1;
    private int previousYLH = -1;
    private int previousXRH = -1;
    private int previousYRH = -1;
    private int previousXLF = -1;
    private int previousYLF = -1;
    private int previousXRF = -1;
    private int previousYRF = -1;

    private final int CIRCLE_RADIUS = 50;
    private final int CIRCLE_STROKE_WIDTH = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        image = findViewById(R.id.imageView);
        nav = findViewById(R.id.bottom_navigation_image_edit);
        uploadBtn = findViewById(R.id.upload_btn);

        nav.setItemActiveIndicatorColor(getColorStateList(R.color.light_blue));
        //decodes a file into a bitmap. TODO: change this to use photo from phone and not resource file.
        //bmp = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.test_image).copy(Bitmap.Config.ARGB_8888, true);
        bmp = uriToBitmap((Uri)getIntent().getExtras().get("photo")).copy(Bitmap.Config.ARGB_8888, true);
        //creates a canvas using the bitmap of the image as a base to be drawn on.
        canvas = new Canvas(bmp);

        image.setImageBitmap(bmp);

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
        heightOffset = (int)(image.getHeight() / 2 - ((double)image.getWidth() / canvas.getWidth()) * canvas.getHeight() / 2);
        widthOffset = (int)(image.getWidth() / 2 - ((double)image.getHeight() / canvas.getHeight()) * canvas.getWidth() / 2);

        final ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        //100 pixel diameter circle TODO: Determine appropriate radius for phone image resolution.
        circle.setIntrinsicHeight(CIRCLE_RADIUS * 2);
        circle.setIntrinsicWidth(CIRCLE_RADIUS * 2);
        circle.getPaint().setColor(ContextCompat.getColor(this.getApplicationContext(), circleColor));
        if (heightOffset < 0){widthOffset = 0;} //heightOffset is < 0 when the image aspect ratio(H:W) is greater than the imageView's and therefore not necessary.
        if (widthOffset < 0){widthOffset = 0;} //widthOffset is < 0 when the image aspect ratio(H:W) is less than the imageView's and therefore not necessary.
        int circleLeft = (int)((x - widthOffset) * (double)canvas.getWidth() / image.getWidth() - circle.getIntrinsicWidth() / 2);
        int circleRight = (int)((x - widthOffset) * (double)canvas.getWidth() / image.getWidth() + circle.getIntrinsicWidth() / 2);
        int circleTop = (int)((y - heightOffset) * (double)canvas.getWidth() / image.getWidth() - circle.getIntrinsicHeight() / 2);
        int circleBottom = (int)((y - heightOffset) * (double)canvas.getWidth() / image.getWidth() + circle.getIntrinsicHeight() / 2);
        Log.d("Canvas Width", canvas.getWidth() + "");
        Log.d("ImageView Width", image.getWidth() + "");
        Log.d("Circle Left", circleLeft + "");
        Log.d("Circle Top", circleTop + "");
        //sets the area in which the circle will be drawn by translating the touch coordinates into image coordinates.
        circle.setBounds(circleLeft, circleTop, circleRight, circleBottom);
        //makes the circle a line instead of filled and sets line width.
        circle.getPaint().setStyle(Paint.Style.STROKE);
        circle.getPaint().setStrokeWidth(CIRCLE_STROKE_WIDTH); //TODO: Determine appropriate line width for phone image resolution.
        //draw the circle onto the image.
        circle.draw(canvas);

        //draws a line between the previous circle sharing the same color and then update the previous circle coordinates
        if (circleColor == R.color.solid_blue){
            if(previousXLH >= 0) {
                canvas.drawLines(drawLine(previousXLH, previousYLH, circleLeft + circle.getIntrinsicWidth() / 2, circleTop + circle.getIntrinsicHeight() / 2), circle.getPaint());
            }
            previousXLH = circleLeft + circle.getIntrinsicWidth() / 2;
            previousYLH = circleTop + circle.getIntrinsicHeight() / 2;
        }
        else if (circleColor == R.color.solid_green){
            if(previousXRH > 0) {
                canvas.drawLines(drawLine(previousXRH, previousYRH, circleLeft + circle.getIntrinsicWidth() / 2, circleTop + circle.getIntrinsicHeight() / 2), circle.getPaint());
            }
            previousXRH = circleLeft + circle.getIntrinsicWidth() / 2;
            previousYRH = circleTop + circle.getIntrinsicHeight() / 2;
        }
        else if (circleColor == R.color.solid_orange){
            if(previousXLF > 0) {
                canvas.drawLines(drawLine(previousXLF, previousYLF, circleLeft + circle.getIntrinsicWidth() / 2, circleTop + circle.getIntrinsicHeight() / 2), circle.getPaint());
            }
            previousXLF = circleLeft + circle.getIntrinsicWidth() / 2;
            previousYLF = circleTop + circle.getIntrinsicHeight() / 2;
        }
        else {
            if(previousXRF > 0) {
                canvas.drawLines(drawLine(previousXRF, previousYRF, circleLeft + circle.getIntrinsicWidth() / 2, circleTop + circle.getIntrinsicHeight() / 2), circle.getPaint());
            }
            previousXRF = circleLeft + circle.getIntrinsicWidth() / 2;
            previousYRF = circleTop + circle.getIntrinsicHeight() / 2;
        }
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

    /**
     * draws a line from the edge of one circle to another. subtracts the circle radius from both ends.
     * @param prevX x coordinate of the center of the previous circle
     * @param prevY y coordinate of the center of the previous circle
     * @param newX x coordinate of the center of the new circle
     * @param newY y coordinate of the center of the new circle
     * @return float[] containing the coordinates of the start and end of the line
     */
    private float[] drawLine(int prevX, int prevY, int newX, int newY){
        double deltaX = CIRCLE_RADIUS * Math.cos(Math.atan((newY - prevY) / (double)(newX - prevX)));
        double deltaY = CIRCLE_RADIUS * Math.sin(Math.atan((newY - prevY) / (double)(newX - prevX)));
        if ((double)(newX - prevX) < 0){
            deltaX *= -1;
            deltaY *= -1;
        }
        float[] lineCords = {(float)(prevX + deltaX), (float)(prevY + deltaY), (float)(newX - deltaX), (float)(newY - deltaY)};
        return lineCords;
    }

    private Bitmap uriToBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            // Open an InputStream from the Uri
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                // Decode the InputStream to a Bitmap
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close(); // Always close the stream to avoid memory leaks
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
