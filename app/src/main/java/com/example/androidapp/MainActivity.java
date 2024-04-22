package com.example.androidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private RecyclerView albumsRecyclerView;
    private AlbumsAdapter albumsAdapter;
    private ArrayAdapter<String> spinnerAdapter;  // Adapter for the spinner
    private Set<String> albumNames;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddAlbum = findViewById(R.id.btnAddAlbum);
        Button btnRemoveAlbum = findViewById(R.id.btnRemoveAlbum);
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("AlbumPrefs", MODE_PRIVATE);
        albumNames = new HashSet<>(prefs.getStringSet("albumNames", new HashSet<>()));

        albumsAdapter = new AlbumsAdapter(this, new ArrayList<>(albumNames), this::albumClicked);
        albumsRecyclerView.setAdapter(albumsAdapter);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(albumNames));

        btnAddAlbum.setOnClickListener(v -> promptForAlbumName(true));
        btnRemoveAlbum.setOnClickListener(v -> promptForAlbumName(false));
    }

    private void albumClicked(View view) {
        TextView textView = view.findViewById(R.id.textView);
        String albumName = textView.getText().toString();
        Toast.makeText(this, albumName + " was clicked", Toast.LENGTH_SHORT).show();
    }


    private void promptForAlbumName(boolean isAdding) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isAdding ? "Add Album" : "Remove Album");

        if (isAdding) {
            final EditText input = new EditText(this);
            input.setSingleLine();
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String albumName = input.getText().toString().trim();
                if (!albumName.isEmpty() && !albumNames.contains(albumName)) {
                    addAlbum(albumName);
                }
            });
        } else {
            final Spinner spinner = new Spinner(this);
            spinner.setAdapter(spinnerAdapter);  // Set the spinner adapter
            builder.setView(spinner);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String albumName = (String) spinner.getSelectedItem();
                if (albumName != null) {
                    removeAlbum(albumName);
                }
            });
        }

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addAlbum(String albumName) {
        albumNames.add(albumName);
        saveAlbums();
    }

    private void removeAlbum(String albumName) {
        albumNames.remove(albumName);
        saveAlbums();
    }

    private void saveAlbums() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("albumNames", new HashSet<>(albumNames));
        editor.apply();
        albumsAdapter.updateAlbums(new ArrayList<>(albumNames));
        spinnerAdapter.clear();
        spinnerAdapter.addAll(albumNames);
        spinnerAdapter.notifyDataSetChanged();
    }
}
