package com.example.androidapp.models;

import com.example.androidapp.models.Album;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Album> albums;

    public User() {
        this.albums = new ArrayList<Album>();
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }


    public Album getAlbum(int position) {
        return albums.get(position);
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

    public Set<String> getAllPersonTagValues() {
        Set<String> allPersonTags = new HashSet<>();
        for (Album album : albums) {
            for (Picture picture : album.getImages()) {
                allPersonTags.addAll(picture.getAllPersonTagValues());
            }
        }
        return allPersonTags;
    }

    public Set<String> getAllLocationTagValues() {
        Set<String> allLocationTags = new HashSet<>();
        for (Album album : albums) {
            for (Picture picture : album.getImages()) {
                allLocationTags.addAll(picture.getAllLocationTagValues());
            }
        }
        return allLocationTags;
    }

}
