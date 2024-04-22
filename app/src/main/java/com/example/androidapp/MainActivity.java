package com.example.androidapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView albumsRecyclerView;
    private AlbumsAdapter albumsAdapter;
    private Button addAlbumButton;
    private Button removeAlbumButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        addAlbumButton = findViewById(R.id.addAlbumButton);
        removeAlbumButton = findViewById(R.id.removeAlbumButton);

        sharedPreferences = getSharedPreferences("AlbumsPrefs", MODE_PRIVATE);
        ArrayList<Album> albums = loadAlbums(); // Load albums from SharedPreferences
        albumsAdapter = new AlbumsAdapter(albums);
        albumsRecyclerView.setAdapter(albumsAdapter);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addAlbumButton.setOnClickListener(v -> showAddAlbumDialog());
        removeAlbumButton.setOnClickListener(v -> handleRemoveAlbum());
    }

    private ArrayList<Album> loadAlbums() {
        String savedAlbums = sharedPreferences.getString("albums", "");
        ArrayList<Album> albums = new ArrayList<>();
        if (!savedAlbums.isEmpty()) {
            String[] albumNames = savedAlbums.split(";");
            for (String name : albumNames) {
                albums.add(new Album(name));
            }
        }
        return albums;
    }

    private void saveAlbums(ArrayList<Album> albums) {
        StringBuilder sb = new StringBuilder();
        for (Album album : albums) {
            sb.append(album.getName()).append(";");
        }
        sharedPreferences.edit().putString("albums", sb.toString()).apply();
    }

    private void showAddAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Album Name");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String albumName = input.getText().toString().trim();
            if (!albumName.isEmpty() && albumsAdapter.addAlbumIfNotExists(albumName)) {
                saveAlbums(albumsAdapter.getAlbums()); // Save albums after adding
                Toast.makeText(MainActivity.this, albumName + " album added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Album already exists or invalid name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void handleRemoveAlbum() {
        String[] albumNames = albumsAdapter.getAlbumNames();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick an album to remove");
        builder.setItems(albumNames, (dialog, which) -> {
            albumsAdapter.removeAlbum(which);
            saveAlbums(albumsAdapter.getAlbums()); // Save albums after removal
            Toast.makeText(MainActivity.this, "Album removed", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }


}
