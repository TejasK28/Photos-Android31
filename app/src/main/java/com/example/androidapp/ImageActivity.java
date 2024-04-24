package com.example.androidapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.CursorWindowAllocationException;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.models.Album;
import com.example.androidapp.models.Picture;
import com.example.androidapp.models.User;
import com.example.androidapp.models.CurrentUser;


import java.util.Locale;

import java.time.LocalDate;
import java.util.List;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.androidapp.UserUtility;


// If you're using LocalDate which is available from API 26
// If your min API level is below 26

public class ImageActivity extends AppCompatActivity {
    // Existing declarations...
    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for picking an image
    private Button searchByDateButton;

    private LocalDate startDate, endDate;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Album selectedAlbum;
    private RecyclerView recyclerViewImages;
    private ImagesAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);

        int albumPosition = getIntent().getIntExtra("album_position", 0);
        selectedAlbum = CurrentUser.getInstance().getUser().getAlbum(albumPosition);



        UserUtility.saveUser(getApplicationContext(), CurrentUser.getInstance().getUser(), "me.ser");


        System.out.println("CURRENT IMAGES:" + selectedAlbum.getImages());

        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
        imagesAdapter = new ImagesAdapter(this, selectedAlbum.getImages());
        recyclerViewImages.setAdapter(imagesAdapter);


        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the current activity
            }
        });

        Button addButton = findViewById(R.id.addImageButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });

        Button startSlideshowButton = findViewById(R.id.startSlideshowButton);
        startSlideshowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSlideshowActivity();
            }
        });
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePickerDialog();
            }
        });

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePickerDialog();
            }
        });
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void launchSlideshowActivity() {
        ArrayList<String> imagePaths = new ArrayList<>();
        fillImagePathsFromSelectedAlbum(imagePaths);

        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "There are no images in the album to display in a slideshow.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, SlideshowActivity.class);
            intent.putStringArrayListExtra("imagePaths", imagePaths);
            startActivity(intent);
        }
    }

    private void fillImagePathsFromSelectedAlbum(ArrayList<String> imagePaths) {
        // For example, pulling image paths from an Album object
        for (Picture picture : selectedAlbum.getImages()) {
            imagePaths.add(picture.getImagePath());
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Take persistable URI permission to retain access across reboots
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            addImageToList(imageUri);
        }
    }

    private void addImageToList(final Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Caption");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String caption = input.getText().toString();
                // Create a new Picture instance with the caption
                Picture newPicture = new Picture(imageUri.toString(), caption);
                selectedAlbum.addImage(newPicture);
                System.out.println("SelectedAlbum: " + selectedAlbum.getImagesPath());
                UserUtility.saveUser(getApplicationContext(), CurrentUser.getInstance().getUser(), "me.ser");
                imagesAdapter.notifyDataSetChanged();
                System.out.println("CURRENT IMAGES:" + selectedAlbum.getImages());


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void showStartDatePickerDialog() {
        // Get the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Month is zero based, just add 1
                        String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        editTextStartDate.setText(formattedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showEndDatePickerDialog() {
        // Get the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Month is zero based, just add 1
                        String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        editTextEndDate.setText(formattedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

}
