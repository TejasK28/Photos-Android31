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


import java.util.HashMap;
import java.util.Locale;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidapp.UserUtility;

import org.w3c.dom.Text;


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

    public Button searchButton;

    public Button moveImageButton;

    private EditText tagTextField;




    private HashMap<String, List<String>> parseSearchQuery(String query) {
        HashMap<String, List<String>> tags = new HashMap<>();
        query = query.toLowerCase(Locale.ROOT).trim();

        String[] conditions = query.split("\\s+or\\s+");

        for (String condition : conditions) {
            String[] parts = condition.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                tags.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return tags;
    }



    private void searchImages() {
        String query = tagTextField.getText().toString();
        if (query.trim().isEmpty()) {
            imagesAdapter = new ImagesAdapter(this, selectedAlbum.getImages());
            recyclerViewImages.setAdapter(imagesAdapter);
            return;
        }

        HashMap<String, List<String>> tags = parseSearchQuery(query);
        List<Picture> filteredPictures = new ArrayList<>();
        for (Album album : CurrentUser.getInstance().getUser().getAlbums()) {
            for (Picture picture : album.getImages()) {
                boolean personMatches = checkMatches(picture.getTagPersonValue(), tags.get("person"));
                boolean locationMatches = checkMatches(picture.getTagLocationValue(), tags.get("location"));

                if (personMatches || locationMatches) {
                    filteredPictures.add(picture);
                }
            }
        }

        imagesAdapter = new ImagesAdapter(this, filteredPictures);
        recyclerViewImages.setAdapter(imagesAdapter);
    }

    private boolean checkMatches(String tagValue, List<String> values) {
        if (values == null || tagValue == null) return false;
        return values.stream().anyMatch(value -> tagValue.equalsIgnoreCase(value));
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        moveImageButton = findViewById(R.id.moveImageButton);
        searchButton = findViewById(R.id.searchButton);
        tagTextField = findViewById(R.id.tagTextField);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchImages();
            }
        });





        int albumPosition = getIntent().getIntExtra("album_position", 0);
        selectedAlbum = CurrentUser.getInstance().getUser().getAlbum(albumPosition);



        UserUtility.saveUser(ImageActivity.this, CurrentUser.getInstance().getUser(), "me.ser");


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


        moveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPictureDialog();
            }

            private void showSelectPictureDialog() {
                final String[] imageNames = selectedAlbum.getImages().stream()
                        .map(p -> p.getCaption()) // Assuming getImageName() returns a user-friendly name.
                        .toArray(String[]::new);

                AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                builder.setTitle("Select a Picture to Move");

                builder.setItems(imageNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Picture selectedPicture = selectedAlbum.getImages().get(which);
                        showSelectAlbumDialog(selectedPicture);
                    }

                    private void showSelectAlbumDialog(final Picture pictureToMove) {
                        User user = CurrentUser.getInstance().getUser();
                        List<Album> allAlbums = user.getAlbums();
                        ArrayList<String> albumNames = new ArrayList<>();

                        for (Album album : allAlbums) {
                            if (!album.equals(selectedAlbum)) {
                                albumNames.add(album.getName());
                            }
                        }

                        if (albumNames.isEmpty()) {
                            Toast.makeText(ImageActivity.this, "There's only one album.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        final CharSequence[] albumsArray = albumNames.toArray(new CharSequence[0]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                        builder.setTitle("Select an Album to Move to");
                        builder.setItems(albumsArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedAlbumName = albumsArray[which].toString(); // This will get the name of the album
                                System.out.println("ALBUM SELECTED: " + selectedAlbumName);

                                // Fetch the Album object based on the name
                                Album targetAlbum = allAlbums.stream()
                                        .filter(album -> album.getName().equals(selectedAlbumName))
                                        .findFirst()
                                        .orElse(null);

                                if (targetAlbum != null) {
                                    movePictureToAlbum(pictureToMove, targetAlbum);
                                } else {
                                    Toast.makeText(ImageActivity.this, "Selected album not found.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    private void movePictureToAlbum(Picture picture, Album targetAlbum) {
                        selectedAlbum.removeImage(picture); // Assumes method to remove image
                        targetAlbum.addImage(picture); // Assumes method to add image
                        UserUtility.saveUser(ImageActivity.this, CurrentUser.getInstance().getUser(), "me.ser");
                        imagesAdapter.notifyDataSetChanged();
                        Toast.makeText(ImageActivity.this, "Image moved successfully!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                if (imageNames.length > 0) {
                    dialog.show();
                } else {
                    Toast.makeText(ImageActivity.this, "No images available to move.", Toast.LENGTH_LONG).show();
                }
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
                UserUtility.saveUser(ImageActivity.this, CurrentUser.getInstance().getUser(), "me.ser");
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
