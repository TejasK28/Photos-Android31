package com.example.androidapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listAlbums;
    private TextView tvNoAlbums;
    private ArrayList<String> albumList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAlbums = findViewById(R.id.list_albums);
        tvNoAlbums = findViewById(R.id.tv_no_albums);
        Button btnAddAlbum = findViewById(R.id.btn_add_album);
        Button btnDeleteAlbum = findViewById(R.id.btn_delete_album);
        albumList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumList);
        listAlbums.setAdapter(adapter);
        updateView();

        btnAddAlbum.setOnClickListener(v -> showAddAlbumDialog());
        btnDeleteAlbum.setOnClickListener(v -> showDeleteAlbumDialog());
    }

    private void showAddAlbumDialog() {
        EditText albumInput = new EditText(this);
        albumInput.setHint("Album Name");

        new AlertDialog.Builder(this)
                .setTitle("Add New Album")
                .setView(albumInput)
                .setPositiveButton("Add", (dialog, which) -> {
                    String albumName = albumInput.getText().toString().trim();
                    if (!albumName.isEmpty()) {
                        albumList.add(albumName);
                        adapter.notifyDataSetChanged();
                        updateView();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAlbumDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle("Delete Album")
                .setItems(albumList.toArray(new String[0]), (dialog, which) -> {
                    albumList.remove(which);
                    adapter.notifyDataSetChanged();
                    updateView();
                })
                .show();
    }

    private void updateView()
    {
        if (albumList.isEmpty()) {
            tvNoAlbums.setVisibility(View.VISIBLE);
            listAlbums.setVisibility(View.GONE);
        } else {
            tvNoAlbums.setVisibility(View.GONE);
            listAlbums.setVisibility(View.VISIBLE);
        }
    }
}
