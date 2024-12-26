package com.example.fisheatfish;

import com.example.fisheatfish.menus.MainMenu;
import com.example.fisheatfish.menus.LoginMenu;
import com.example.fisheatfish.menus.SignUpMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Show the Login Menu first
        LoginMenu loginMenu = new LoginMenu();
        loginMenu.show(primaryStage);

        // Set stage properties
        primaryStage.setTitle("Fish Eat Fish");
        primaryStage.setWidth(800);  // Set the width of the window
        primaryStage.setHeight(600); // Set the height of the window
        primaryStage.show();
    }
}



