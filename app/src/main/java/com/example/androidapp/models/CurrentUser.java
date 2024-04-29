package com.example.androidapp.models;


/**
 * Represents the current user in the system.
 * This class follows the Singleton design pattern to ensure that there is only one instance of CurrentUser in the system.
 * @author Habeen Jun
 */
public class CurrentUser {
    private static CurrentUser instance;
    private User user;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private CurrentUser() {}


    /**
     * Returns the single instance of CurrentUser.
     * If the instance does not exist, it is created.
     *
     * @return the single instance of CurrentUser
     */
    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    /**
     * Sets the current user.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user.
     *
     * @return the current user
     */
    public User getUser() {
        return this.user;
    }
}
