package com.example.androidapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import java.util.ArrayList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.io.IOException;
import android.net.Uri;
import java.io.FileNotFoundException;
import android.widget.Toast;


import com.example.androidapp.models.Album;

public class SlideshowActivity extends AppCompatActivity {
    private ImageView imageView;
    private ArrayList<String> imagePaths; // Assume this is passed or set somewhere
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        imageView = findViewById(R.id.imageViewSlideshow);
        Button buttonPrev = findViewById(R.id.buttonPrev);
        Button buttonNext = findViewById(R.id.buttonNext);
        imagePaths = (ArrayList<String>) getIntent().getSerializableExtra("imagePaths");

        // Setup button listeners
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    loadImage(currentIndex);
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < imagePaths.size() - 1) {
                    currentIndex++;
                    loadImage(currentIndex);
                }
            }
        });

        // Load the initial image
        loadImage(currentIndex);
    }

    private void loadImage(int index) {
        String imagePath = imagePaths.get(index);
        try {
            Uri imageUri = Uri.parse(imagePath);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

}
