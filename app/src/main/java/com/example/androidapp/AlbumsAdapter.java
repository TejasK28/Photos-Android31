package com.example.androidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> albumNames;
    private View.OnClickListener onAlbumClickListener;

    public AlbumsAdapter(Context context, ArrayList<String> albumNames, View.OnClickListener onAlbumClickListener) {
        this.context = context;
        this.albumNames = albumNames;
        this.onAlbumClickListener = onAlbumClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        view.setOnClickListener(onAlbumClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String albumName = albumNames.get(position);
        holder.textView.setText(albumName);
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    // Method to update the list of albums and notify the adapter of data change
    public void updateAlbums(ArrayList<String> newAlbumNames) {
        albumNames.clear();
        albumNames.addAll(newAlbumNames);
        notifyDataSetChanged();  // Notify any registered observers that the data set has changed.
    }
}
