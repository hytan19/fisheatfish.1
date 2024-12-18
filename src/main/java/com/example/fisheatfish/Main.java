package com.example.fisheatfish;

import com.example.fisheatfish.menus.MainMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the main menu instance
        MainMenu mainMenu = new MainMenu();

        // Show the main menu with the given stage
        mainMenu.show(primaryStage);

        // Optionally, configure additional properties for the stage
        // Example: Set the title and size of the window
        primaryStage.setTitle("Fish Eat Fish");
        primaryStage.setWidth(800);  // Set the width of the window
        primaryStage.setHeight(600); // Set the height of the window
        primaryStage.show(); // Display the stage
    }
}

