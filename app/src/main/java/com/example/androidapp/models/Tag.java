package com.example.androidapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tag that can be associated with a photo.
 * Each tag has a name, a list of values, and a flag indicating whether multiple values are allowed.
 * @author Habeen Jun
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The name of the tag.
     */
    private String name;

    /**
     * The list of values that this tag can have.
     */
    private List<String> values;

    /**
     * A flag indicating whether this tag allows multiple values.
     */
    private boolean allowMultipleValues;

    /**
     * Constructs a new Tag with the specified name.
     * Initializes the values list and sets the allowMultipleValues flag to false.
     *
     * @param tagName the name of the tag
     */
    public Tag(String tagName) {
        this.name = tagName;
        this.values = new ArrayList<>();
        this.allowMultipleValues = false;
    }

    /**
     * Constructs a new Tag with the specified name and allowMultipleValues flag.
     * Initializes the values list.
     *
     * @param tagName the name of the tag
     * @param allowMultipleValues the flag indicating whether multiple values are allowed
     */
    public Tag(String tagName, boolean allowMultipleValues) {
        this.name = tagName;
        this.values = new ArrayList<>();
        this.allowMultipleValues = allowMultipleValues;
    }

    /**
     * Sets the name of the tag to the specified name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the tag.
     *
     * @return the name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the flag indicating whether multiple values are allowed.
     *
     * @return the flag indicating whether multiple values are allowed
     */
    public boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    /**
     * Sets the flag indicating whether multiple values are allowed.
     *
     * @param allowMultipleValues the flag to set
     */
    public void setAllowMultipleValues(boolean allowMultipleValues) {
        this.allowMultipleValues = allowMultipleValues;
    }

    /**
     * Returns the list of values associated with the tag.
     *
     * @return the list of values
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Adds the specified value to the tag's values list.
     *
     * @param tagValue the value to add
     */
    public void addValue(String tagValue) {
        this.values.add(tagValue);
    }

    /**
     * Removes the specified value from the tag's values list.
     *
     * @param tagValue the value to remove
     */
    public void removeValue(String tagValue) {
        this.values.remove(tagValue);
    }

    /**
     * Sets the tag's values list to the specified list.
     *
     * @param tagValues the new values list
     */
    public void setValues(List<String> tagValues) {
        this.values = tagValues;
    }



}