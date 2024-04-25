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
     * Constructs a new Album with the specified name, cover image path, and picture details.
     *
     * @param name the name of the album
     * @param coverImagePath the path to the cover image
     * @param imagePaths the paths to the pictures in the album
     * @param captions the captions of the pictures
     * @param dates the capture dates of the pictures
     */
    public Album(String name, String coverImagePath, String[] imagePaths, String [] captions, String [] dates) {

        System.out.println("Cover Image: " + coverImagePath);


        this.name = name;
        this.coverImagePath = coverImagePath;
        this.pictures = new ArrayList<>();
        int imageIndex = 0;



        for (String path : imagePaths)
        {
            // Check if the picture already exists in GlobalPicture.allPictures
            Picture existingPicture = GlobalPicture.allPictures.stream()
                    .filter(p -> p.getImagePath().equals(path))
                    .findFirst()
                    .orElse(null);

            if (existingPicture == null)
            {
                // If the picture does not exist, create a new one and add it to GlobalPicture.allPictures
                //we are setting the caption
                Picture newPicture = new Picture(path, captions[imageIndex], dates[imageIndex]);
                ++imageIndex;

                this.pictures.add(newPicture);
                GlobalPicture.allPictures.add(newPicture);

                System.out.println("NEW PICTURE ADDED TO CURRENT ALBUM AND GLOBAL ALBUM");
            }
            else
            {
                // If the picture exists, use the existing reference
                this.pictures.add(existingPicture);
                System.out.println("EXISTING PICTURE RETURNED");
            }
        }
    }



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
     * Searches for pictures within a specific date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of pictures captured within the specified date range
     */
    public List<Picture> searchPicturesByDateRange(LocalDate startDate, LocalDate endDate) {
        return pictures.stream()
                .filter(picture -> {
                    LocalDate captureDate = picture.getCaptureDateAsLocalDate();
                    System.out.println("The pucture date is: " + captureDate);
                    return captureDate != null && !captureDate.isBefore(startDate) && !captureDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }


    /**
     * Searches for pictures by tag type-value pairs.
     *
     * @param searchQuery the search query in the format "tag1=value1 AND/OR tag2=value2"
     * @return a list of pictures that match the search criteria
     */
    public List<Picture> searchPicturesByTag(String searchQuery) {
        // Split the query into parts to determine if it's a single search, AND, or OR search
        String[] parts = searchQuery.split(" ");

        // Handle single tag-value pair
        if (parts.length == 1) {
            // print single tag
            System.out.println("Single tag");
            return searchSingleTag(parts[0]);
        } else if (parts.length == 3) { // Handle AND / OR
            String operation = parts[1]; // AND or OR
            if ("AND".equalsIgnoreCase(operation)) {
                // print AND
                System.out.println("AND");
                return searchAnd(parts[0], parts[2]);
            } else if ("OR".equalsIgnoreCase(operation)) {
                // print OR
                System.out.println("OR");
                return searchOr(parts[0], parts[2]);
            }
        }

        throw new IllegalArgumentException("Invalid search query");
    }

    /**
     * Searches for pictures by a single tag-value pair.
     * @param tagValuePair
     * @return a list of pictures that match the search criteria
     */
    private List<Picture> searchSingleTag(String tagValuePair) {
        String[] parts = tagValuePair.split("=");
        String tagName = parts[0];
        String tagValue = parts[1];


        return pictures.stream()
                .filter(picture -> picture.getTags().stream()
                        .anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName) && tag.getValues().contains(tagValue)))
                .collect(Collectors.toList());
    }

    /**
     * Searches for pictures by two tag-value pairs with an AND operation.
     * @param firstPair
     * @param secondPair
     * @return a list of pictures that match the search criteria
     */
    private List<Picture> searchAnd(String firstPair, String secondPair) {
        List<Picture> firstSearchResults = searchSingleTag(firstPair);
        return firstSearchResults.stream()
                .filter(picture -> picture.getTags().stream()
                        .anyMatch(tag -> {
                            String[] parts = secondPair.split("=");
                            String tagName = parts[0];
                            String tagValue = parts[1];
                            return tag.getName().equalsIgnoreCase(tagName) && tag.getValues().contains(tagValue);
                        }))
                .collect(Collectors.toList());
    }

    /**
     * Searches for pictures by two tag-value pairs with an OR operation.
     * @param firstPair
     * @param secondPair
     * @return a list of pictures that match the search criteria
     */
    private List<Picture> searchOr(String firstPair, String secondPair) {
        List<Picture> firstSearchResults = searchSingleTag(firstPair);
        List<Picture> secondSearchResults = searchSingleTag(secondPair);

        return Stream.concat(firstSearchResults.stream(), secondSearchResults.stream())
                .distinct()
                .collect(Collectors.toList());
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


    /**
     * Returns the date range of the pictures in the album.
     * @return the date range of the pictures in the album
     */
    public String getDateRange() {
        List<LocalDate> validDates = pictures.stream()
                .map(Picture::getCaptureDateAsLocalDate)
                .filter(Objects::nonNull) // Filter out null dates
                .collect(Collectors.toList());

        if (validDates.isEmpty()) {
            return "No Valid Dates Available";
        }

        LocalDate earliest = validDates.stream().min(LocalDate::compareTo).get();
        LocalDate latest = validDates.stream().max(LocalDate::compareTo).get();

        return String.format("From %s to %s", earliest.toString(), latest.toString());
    }

    public void removeImage(Picture picture) {

        pictures.removeIf(p -> p.equals(picture));  // Using stream-like operation directly
    }


}
