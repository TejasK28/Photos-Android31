package com.example.androidapp.datamanager;

import com.example.androidapp.models.Picture;
import java.util.ArrayList;
import java.util.List;
/**
 * Manages the pictures in the system.
 * This class is used only for the admin sub-system.
 *
 * @author Tejas Kandri
 */
public class GlobalPicture
{
    /**
     * The list of all pictures in the system.
     */
    public static List<Picture> allPictures = new ArrayList<>();
}