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


    public Tag person;
    public Tag location;


    /**
     * Constructs a new Picture with the specified image path and caption.
     * Initializes the tags list.
     *
     * @param imagePath the path to the image
     * @param caption the caption of the picture
     */
    public Picture(String imagePath, String caption) {
        this.imagePath = imagePath;
        this.caption = caption;
        person = new Tag("person");
        location = new Tag("location");
    }

    public void addValueToTag(String name, String value)
    {
        if(name.equalsIgnoreCase("person"))
        {
            person.addValue(value);
        }
        else
        {
            location.addValue(value);
        }
    }

    /*
        Given the name of the tag and a string array of values,
        will add the values to a respective tag
     */
    public void addValuesToTag(String name, String [] values)
    {
        if(name.equalsIgnoreCase("person"))
        {
            for(String value : values)
            {
                if(!person.getValues().contains(value))
                    person.addValue(value);
            }
        }
        else
        {
            for(String value : values)
            {
                if(!location.getValues().contains(value))
                    location.addValue(value);
            }
        }
    }




    public Picture(String imagePath) {
        this.imagePath = imagePath;
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

    public ArrayList<String> getAllPersonTagValues()
    {
        if (person == null) {
            return new ArrayList<String>();
        }
        return (ArrayList<String>) person.getValues();
    }


    public ArrayList<String> getAllLocationTagValues()
    {
        if (location == null) {
            return new ArrayList<String>();
        }
        return (ArrayList<String>) location.getValues();
    }


    public List<String> getPersonTags() {
        return person.getValues();
    }


    public String getPersonTagsString() {
        return String.join(", ", getPersonTags());
    }

    public String getLocationTagsString() {
        return String.join(", ", getLocationTags());
    }
    public List<String> getLocationTags() {
        return location.getValues();
    }
}

