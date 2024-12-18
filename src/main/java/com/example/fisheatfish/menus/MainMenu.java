package com.example.fisheatfish.menus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.fisheatfish.game.GameLoop;
import javafx.scene.layout.Pane;

public class MainMenu {
    public void show(Stage stage) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> showSignUpMenu(stage));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> showLoginMenu(stage));

        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startGame(stage));

        Button exitGameButton = new Button("Exit Game");
        exitGameButton.setOnAction(e -> System.exit(0)); // Exit the application

        vbox.getChildren().addAll(signUpButton, loginButton, startGameButton, exitGameButton);

        Scene scene = new Scene(vbox, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Fish Eat Fish");
        stage.show();
    }

    private void showSignUpMenu(Stage stage) {
        new SignUpMenu().show(stage);
    }

    private void showLoginMenu(Stage stage) {
        new LoginMenu().show(stage);
    }

    private void startGame(Stage stage) {
        Pane gamePane = new Pane(); // Create the root pane for the game
        GameLoop gameLoop = new GameLoop(gamePane);
        gameLoop.start();
    }
}

















