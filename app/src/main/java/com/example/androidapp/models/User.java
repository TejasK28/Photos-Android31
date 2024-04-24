package com.example.androidapp.models;

import com.example.androidapp.models.Album;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Album> albums;

    public User() {
        this.albums = new ArrayList<Album>();
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds the specified album to the user's album list.
     * The album is only added if it is not null.
     *
     * @param album the album to add
     */
    public void addAlbum(Album album) {
        if (album != null) {
            albums.add(album);
        }
    }

    /**
     * Removes the specified album from the user's album list.
     *
     * @param album the album to remove
     */
    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    /**
     * Sets the user's album list to the specified list.
     *
     * @param albumsList the new album list
     */
    public void setAlbumsList(ArrayList<Album> albums) {
        this.albums = albums;
    }




}
