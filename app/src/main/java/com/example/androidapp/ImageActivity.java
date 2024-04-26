package com.example.androidapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.models.Album;
import com.example.androidapp.models.Picture;
import com.example.androidapp.models.User;
import com.example.androidapp.models.CurrentUser;


import java.util.Locale;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;
import android.widget.Spinner;
import android.widget.Toast;


// If you're using LocalDate which is available from API 26
// If your min API level is below 26

public class ImageActivity extends AppCompatActivity {

    private Button createAlbumButton; // Button to create album from search results

    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for picking an image
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Album selectedAlbum;
    private RecyclerView recyclerViewImages;
    private ImagesAdapter imagesAdapter;

    public Button searchButton;

    public Button moveImageButton;

    private Spinner filterSpinner;

    AutoCompleteTextView personAutoCompleteTextView;
    AutoCompleteTextView locationAutoCompleteTextView;

    public boolean doesPictureHaveTag(Picture p, String whichTag, String desiredValue)
    {
        if(whichTag.equals("person"))
        {
            return p.person.getValues().contains(desiredValue);
        }

        return p.location.getValues().contains(desiredValue);
    }

    private void searchImages() {
        String selectedLogic = filterSpinner.getSelectedItem().toString();
        String personTags = personAutoCompleteTextView.getText().toString().trim();
        String locationTags = locationAutoCompleteTextView.getText().toString().trim();

        if(selectedLogic.equals("AND"))
        {
            if (personTags.isEmpty() || locationTags.isEmpty()) {
                Toast.makeText(this, "Both person and location fields must be filled for 'AND' logic.", Toast.LENGTH_LONG).show();
                return;
            }
            // Proceed to filter pictures
            List<Picture> filteredPictures = new ArrayList<>();
            User user = CurrentUser.getInstance().getUser();
            for (Album album : user.getAlbums()) {
                for (Picture picture : album.getImages()) {
                    if (doesPictureHaveTag(picture, "person", personTags) && doesPictureHaveTag(picture, "location", locationTags)) {
                        filteredPictures.add(picture);
                    }
                }
            }

            // Update RecyclerView with the filtered pictures
            imagesAdapter = new ImagesAdapter(this, filteredPictures);
            recyclerViewImages.setAdapter(imagesAdapter);
            recyclerViewImages.scrollToPosition(0);

            return;
        }

        if(selectedLogic.equals("OR"))
        {
            if (personTags.isEmpty() || locationTags.isEmpty()) {
                Toast.makeText(this, "At least one of person or location must be filled for 'OR' logic.", Toast.LENGTH_LONG).show();
                return;
            }
            // Proceed to filter pictures
            List<Picture> filteredPictures = new ArrayList<>();
            User user = CurrentUser.getInstance().getUser();
            for (Album album : user.getAlbums()) {
                for (Picture picture : album.getImages()) {
                    if (doesPictureHaveTag(picture, "person", personTags) || doesPictureHaveTag(picture, "location", locationTags)) {
                        filteredPictures.add(picture);
                    }
                }
            }

            // Update RecyclerView with the filtered pictures
            imagesAdapter = new ImagesAdapter(this, filteredPictures);
            recyclerViewImages.setAdapter(imagesAdapter);
            recyclerViewImages.scrollToPosition(0);

            return;
        }






        // Check if both input fields are empty
        if (personTags.isEmpty() && locationTags.isEmpty()) {
            // Directly reset the RecyclerView to show all images from the selected album
            imagesAdapter = new ImagesAdapter(this, selectedAlbum.getImages());
            recyclerViewImages.setAdapter(imagesAdapter);
            recyclerViewImages.scrollToPosition(0); // Optionally, scroll to the top of the list
            return;
        }

        // Split the input strings into lists based on commas, considering spaces around commas
        List<String> personList = Arrays.asList(personTags.split("\\s*,\\s*"));
        List<String> locationList = Arrays.asList(locationTags.split("\\s*,\\s*"));

        // Proceed to filter pictures
        List<Picture> filteredPictures = new ArrayList<>();
        User user = CurrentUser.getInstance().getUser();
        for (Album album : user.getAlbums()) {
            for (Picture picture : album.getImages()) {
                if (matchesTags(picture.getPersonTags(), personList) || matchesTags(picture.getLocationTags(), locationList)) {
                    filteredPictures.add(picture);
                }
            }
        }

        // Update RecyclerView with the filtered pictures
        imagesAdapter = new ImagesAdapter(this, filteredPictures);
        recyclerViewImages.setAdapter(imagesAdapter);
        recyclerViewImages.scrollToPosition(0); // Optionally, scroll to the top of the list
    }

    private boolean matchesTags(List<String> pictureTags, List<String> searchTags) {
        if (searchTags.isEmpty() || (searchTags.size() == 1 && searchTags.get(0).isEmpty())) {
            // If search tags are empty, return false as we don't want to show all pictures
            return false;
        }

        // Convert list of picture tags to lower case for case insensitive comparison
        List<String> lowerCasePictureTags = new ArrayList<>();
        for (String tag : pictureTags) {
            lowerCasePictureTags.add(tag.toLowerCase());
        }

        for (String searchTag : searchTags) {
            if (lowerCasePictureTags.contains(searchTag.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);
        moveImageButton = findViewById(R.id.moveImageButton);
        searchButton = findViewById(R.id.searchButton);

        filterSpinner = findViewById(R.id.filterSpinner);

        personAutoCompleteTextView = findViewById(R.id.personEditText);
        locationAutoCompleteTextView = findViewById(R.id.locationEditText);

        setupAutoCompleteTextViews();
        setupFilterSpinner();



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchImages();
            }

        });

        createAlbumButton = findViewById(R.id.createAlbumButton);
        createAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagesAdapter.getItemCount() > 0) {
                    showCreateAlbumDialog();
                } else {
                    Toast.makeText(ImageActivity.this, "No images to include in a new album.", Toast.LENGTH_SHORT).show();
                }
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


    private void setupAutoCompleteTextViews() {
        Set<String> personTags = CurrentUser.getInstance().getUser().getAllPersonTagValues();
        Set<String> locationTags = CurrentUser.getInstance().getUser().getAllLocationTagValues();

        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(personTags));
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(locationTags));

        personAutoCompleteTextView.setAdapter(personAdapter);
        locationAutoCompleteTextView.setAdapter(locationAdapter);

        personAutoCompleteTextView.setThreshold(1); //start searching from one character
        locationAutoCompleteTextView.setThreshold(1); //start searching from one character

        personAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                updatePersonTags(personAdapter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            private void updatePersonTags(ArrayAdapter<String> adapter) {
                Set<String> newTags = CurrentUser.getInstance().getUser().getAllPersonTagValues();
                adapter.clear();
                adapter.addAll(newTags);
                adapter.notifyDataSetChanged();
            }

        });

        locationAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateLocationTags(locationAdapter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            private void updateLocationTags(ArrayAdapter<String> adapter) {
                Set<String> newTags = CurrentUser.getInstance().getUser().getAllLocationTagValues();
                adapter.clear();
                adapter.addAll(newTags);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showCreateAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Album");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString().trim();
                if (!albumName.isEmpty() && !albumExists(albumName)) {
                    createNewAlbum(albumName, imagesAdapter.getCurrentList());
                } else {
                    Toast.makeText(ImageActivity.this, "Album already exists or invalid name.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private boolean albumExists(String albumName) {
        User user = CurrentUser.getInstance().getUser();
        return user.getAlbums().stream().anyMatch(album -> album.getName().equalsIgnoreCase(albumName));
    }





    private void createNewAlbum(String albumName, List<Picture> pictures) {
        Album newAlbum = new Album(albumName, pictures);
        CurrentUser.getInstance().getUser().getAlbums().add(newAlbum);
        UserUtility.saveUser(this, CurrentUser.getInstance().getUser(), "me.ser");
        // FIXME find a way to update the album view after creating an album
        Toast.makeText(this, "New album created: " + albumName, Toast.LENGTH_SHORT).show();
    }



    private void setupFilterSpinner() {
        // Define the values for the spinner
        String[] filterOptions = new String[]{"NONE", "AND", "OR"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        filterSpinner.setAdapter(adapter);
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
