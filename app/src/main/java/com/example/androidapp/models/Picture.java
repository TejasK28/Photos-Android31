package com.example.androidapp.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a picture in the system.
 * Each picture has an image path, a caption, a list of tags, and a capture date.
 * @author Tejas Kandri
 */
public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * The path to the image file.
     */
    private String imagePath;

    /**
     * The caption of the picture.
     */
    private String caption; // Additional field for caption

    /**
     * The list of tags associated with this picture.
     */
    private List<Tag> tags; // Additional field for tags

    /**
     * The date when the picture was captured.
     */
    private String captureDate; // Optional: field for capture date

    public String personValue;
    public String locationValue;


    /**
     * Constructs a new Picture with the specified image path and caption.
     * Initializes the tags list.
     *
     * @param imagePath the path to the image
     * @param caption the caption of the picture
     */
    public Picture(String imagePath, String caption) {
        this.imagePath = imagePath;
        this.tags = new ArrayList<Tag>(); // Initialize tags as an empty list
        this.caption = caption;
        personValue = null;
        locationValue =null;
    }

    public Picture(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Constructs a new Picture with the specified image path, caption, and capture date.
     * Initializes the tags list.
     *
     * @param imagePath the path to the image
     * @param caption the caption of the picture
     * @param captureDate the capture date of the picture
     */
    public Picture(String imagePath, String caption, String captureDate) {
        this.imagePath = imagePath;
        this.caption = caption;
        this.captureDate = captureDate;
        this.tags = new ArrayList<>();

        // Log the image path to verify it
        System.out.println("Image path set to: " + imagePath);

        personValue = null;
        locationValue =null;
    }


    /**
     * Returns the path to the image.
     * @return the path to the image
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Returns the caption of the picture.
     * @return the caption of the picture
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the picture.
     * @param caption the caption of the picture
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns the list of tags associated with the picture.
     * @return the list of tags associated with the picture
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the list of tags associated with the picture.
     * @param tags the list of tags associated with the picture
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds the specified tag to the picture's tags list.
     * The tag is only added if it is not null.
     *
     * @param tag the tag to add
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Returns the capture date of the picture.
     * @return the capture date of the picture
     */
    public String getCaptureDate() {
        return captureDate;
    }

    /**
     * Sets the capture date of the picture.
     * @param captureDate the capture date of the picture
     */
    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }

    /**
     * Returns the capture date as a LocalDate.
     * If the capture date is null, returns null.
     * If the capture date cannot be parsed, throws a DateTimeParseException.
     *
     * @return the capture date as a LocalDate
     * @throws DateTimeParseException if the capture date cannot be parsed
     */
    public LocalDate getCaptureDateAsLocalDate() {
        if (captureDate == null) {
            return null; // Or consider returning a default date if appropriate
        }
        String[] datePatterns = {"yyyy-MM-dd", "yyyy-M-d"};
        for (String pattern : datePatterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(captureDate, formatter);
            } catch (DateTimeParseException e) {
                // Try the next pattern
            }
        }
        throw new DateTimeParseException("Could not parse date: " + captureDate, captureDate, 0);
    }

    /**
     * Checks if this picture has a specific tag.
     *
     * @param tagName The name of the tag to check.
     * @return true if the picture has the tag, false otherwise.
     */
    public boolean hasTag(String tagName) {
        return tags.stream()
                .anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName));
    }

    public void setTagPersonValue(String val)
    {
        this.personValue = val;
    }

    public void setTagLocationValue(String val)
    {
        this.locationValue = val;
    }

    public String getTagPersonValue() {
        return personValue;
    }

    public String getTagLocationValue() {
        return locationValue;
    }
}

