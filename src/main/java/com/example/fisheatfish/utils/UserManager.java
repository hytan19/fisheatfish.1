package com.example.fisheatfish.utils;

public class UserManager {
    private static UserManager instance;
    private boolean isLoggedIn = false; // This could be tied to actual user session management

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean login() {
        // This method should check credentials against the database and return true if successful.
        // Here, it's mocked to return true for the sake of the example.
        this.isLoggedIn = true; // Update this based on actual authentication logic
        return isLoggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void logout() {
        isLoggedIn = false;
    }
}


