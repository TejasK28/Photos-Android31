package com.example.androidapp.models;

//import datamanager.GlobalPicture;

import com.example.androidapp.datamanager.GlobalPicture;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an album in the system.
 * Each album has a name, a cover image path, and a list of pictures.
 * @author Tejas Kandri
 */
public class Album implements Serializable {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the album.
     */
    private String name;

    /**
     * The path to the cover image of the album. This is stored for serialization.
     */
    private String coverImagePath;

    /**
     * The list of pictures in the album.
     */
    private List<Picture> pictures;


    /**
     * Constructs a new Album with the specified name and list of pictures.
     *
     * @param name the name of the album
     * @param pictures the list of pictures in the album
     */
    public Album(String name, List<Picture> pictures) {
        this.name = name;
        this.coverImagePath = pictures.isEmpty() ? "" : pictures.get(0).getImagePath(); // Use the first picture as cover, or empty if no pictures.
        this.pictures = new ArrayList<>(pictures); // Create a deep copy of the list to prevent modifications outside this class.

        // Add all new pictures to the GlobalPicture list if they don't already exist
        for (Picture picture : pictures) {
            if (GlobalPicture.allPictures.stream().noneMatch(p -> p.getImagePath().equals(picture.getImagePath()))) {
                GlobalPicture.allPictures.add(picture);
            }
        }
    }

    public Album(String name) {
        this.name = name;
    }



    /**
     * Returns the name of the album.
     *
     * @return the name of the album
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the album.
     *
     * @param name the name of the album
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the path to the cover image of the album.
     *
     * @return the path to the cover image of the album
     */
    public String getCoverImagePath() {
        return coverImagePath;
    }

    /**
     * Sets the path to the cover image of the album.
     *
     * @param coverImagePath the path to the cover image of the album
     */
    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    /**
     * Returns the list of pictures in the album.
     *
     * @return the list of pictures in the album
     */
    public List<Picture> getImages() {
        if (this.pictures == null) {
            this.pictures = new ArrayList<>();
        }
        return this.pictures;
    }

    public ArrayList<String> getImagesPath() {
        ArrayList<String> paths = new ArrayList<>();
        if (this.pictures == null) {
            this.pictures = new ArrayList<>();
        }

        for (Picture p: pictures) {
            paths.add(p.getImagePath());
        }

        return paths;
    }

    /**
     * Adds a new picture to the album.
     * If the picture already exists in the system, the existing reference is used.
     *
     * @param newPicture the picture to add
     */
    public void addImage(Picture newPicture)
    {
        // Picture does not exist, so add the new one
        this.pictures.add(newPicture);
        System.out.println("New picture added to the album.");
    }




    public void removeImage(Picture picture) {

        pictures.removeIf(p -> p.equals(picture));  // Using stream-like operation directly
    }


}
