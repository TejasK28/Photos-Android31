package com.example.androidapp;
// AlbumsAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.content.Intent;

import com.example.androidapp.models.Album;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    private ArrayList<Album> albumsList;

    public AlbumsAdapter(ArrayList<Album> albums) {
        this.albumsList = albums;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView albumNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            albumNameTextView = itemView.findViewById(R.id.album_name);
            itemView.setOnClickListener(this); // Set the click listener for the itemView
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // Get the position of the clicked item
            if (position != RecyclerView.NO_POSITION) {
                Album clickedAlbum = albumsList.get(position);
                Toast.makeText(view.getContext(), "Clicked on: " + clickedAlbum.getName(), Toast.LENGTH_SHORT).show();

                // Create an intent to start ImageActivity
                Intent intent = new Intent(view.getContext(), ImageActivity.class);
                // Add extra data to the intent (e.g., the album's name)
                intent.putExtra("album_name", clickedAlbum.getName());
                // Add the entire Album object to the intent
                intent.putExtra("selected_album", clickedAlbum);

                view.getContext().startActivity(intent); // Start the AlbumActivity
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Album album = albumsList.get(position);
        holder.albumNameTextView.setText(album.getName());
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    // Method to retrieve the list of albums
    public ArrayList<Album> getAlbums() {
        return albumsList;
    }

    public boolean addAlbumIfNotExists(String albumName) {
        for (Album album : albumsList) {
            if (album.getName().equalsIgnoreCase(albumName)) {
                return false; // Album already exists
            }
        }
        albumsList.add(new Album(albumName));
        notifyItemInserted(albumsList.size() - 1);
        return true;
    }

    public void removeAlbum(int position) {
        if (position >= 0 && position < albumsList.size()) {
            albumsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String[] getAlbumNames()
    {
        String [] names = new String[this.albumsList.size()];
        int x = 0;

        for(Album a : albumsList)
        {
            names[x] = a.getName();
            ++x;
        }

        return names;
    }
}