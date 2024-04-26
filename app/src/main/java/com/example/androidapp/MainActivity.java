package com.example.androidapp;

import android.content.SharedPreferences;
import android.database.CursorWindowAllocationException;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidapp.models.Album;
import com.example.androidapp.models.User;
import com.example.androidapp.models.CurrentUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private RecyclerView albumsRecyclerView;
    private AlbumsAdapter albumsAdapter;
    private Button addAlbumButton;
    private Button removeAlbumButton;
    private SharedPreferences sharedPreferences;

    Button refreshButton;


    private User user;

    private ArrayList<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        addAlbumButton = findViewById(R.id.addAlbumButton);
        removeAlbumButton = findViewById(R.id.removeAlbumButton);

        sharedPreferences = getSharedPreferences("AlbumsPrefs", MODE_PRIVATE);

        // loadUser
        user = UserUtility.loadUser(getApplicationContext(), "me.ser");
        CurrentUser.getInstance().setUser(user);

        albums = CurrentUser.getInstance().getUser().getAlbums();
        albumsAdapter = new AlbumsAdapter(albums);
        albumsRecyclerView.setAdapter(albumsAdapter);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addAlbumButton.setOnClickListener(v -> showAddAlbumDialog());
        removeAlbumButton.setOnClickListener(v -> handleRemoveAlbum());

        refreshButton = findViewById(R.id.btnRefreshView);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAlbums();
            }
        });
    }

    private void refreshAlbums() {
        // Assuming you have a method to fetch new data if needed
        albumsAdapter.notifyDataSetChanged(); // Notify the adapter to refresh views
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
                // Saving albums
                UserUtility.saveUser(getApplicationContext(), CurrentUser.getInstance().getUser(), "me.ser");
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
            UserUtility.saveUser(getApplicationContext(), CurrentUser.getInstance().getUser(), "me.ser");
            Toast.makeText(MainActivity.this, "Album removed", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    void showRenameDialog(Album album, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                album.setName(newName);
                albumsAdapter.notifyItemChanged(position);
                UserUtility.saveUser(getApplicationContext(), CurrentUser.getInstance().getUser(), "me.ser");
                Toast.makeText(MainActivity.this, "Album renamed to " + newName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Invalid album name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }





}
