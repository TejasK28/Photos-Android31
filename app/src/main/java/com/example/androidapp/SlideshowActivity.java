package com.example.androidapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.io.IOException;
import android.net.Uri;
import java.io.FileNotFoundException;
import android.widget.Toast;

import com.example.androidapp.models.Album;
import com.example.androidapp.models.Picture;

public class SlideshowActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textViewCaption;
    private TextView textViewTags;
    private TextView textViewImageName;
    private Album selectedAlbum;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        imageView = findViewById(R.id.imageViewSlideshow);
        textViewCaption = findViewById(R.id.textViewCaption);
        textViewTags = findViewById(R.id.textViewTags);
        Button buttonPrev = findViewById(R.id.buttonPrev);
        Button buttonNext = findViewById(R.id.buttonNext);
        selectedAlbum = (Album) getIntent().getSerializableExtra("selectedAlbum");

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
                if (currentIndex < selectedAlbum.getImages().size() - 1) {
                    currentIndex++;
                    loadImage(currentIndex);
                }
            }
        });

        // Load the initial image
        loadImage(currentIndex);
    }

    private void loadImage(int index) {
        Picture picture = selectedAlbum.getImages().get(index);
        try {
            Uri imageUri = Uri.parse(picture.getImagePath());
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            textViewCaption.setText(picture.getCaption());
            textViewTags.setText("Person: " + picture.getPersonTagsString() + "\n" + "Location: " + picture.getLocationTagsString());
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