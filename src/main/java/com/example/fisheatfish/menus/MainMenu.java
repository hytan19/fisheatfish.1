package com.example.fisheatfish.menus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.fisheatfish.game.GameScene;

public class MainMenu {
    private static boolean isLoggedIn = false;  // Track login status

    // Show the main menu only after successful login
    public static void show(Stage stage) {
        if (!isLoggedIn) {
            // Redirect to login if the user is not logged in
            new LoginMenu().show(stage);
            return;
        }

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Start game button
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startGame(stage));

        Button gameHistoryButton = new Button("Game History");
        gameHistoryButton.setOnAction(e -> showGameHistory(stage));

        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setOnAction(e -> showLeaderboard(stage));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> logout(stage));

        vbox.getChildren().addAll(startGameButton, gameHistoryButton, leaderboardButton, logoutButton);

        Scene scene = new Scene(vbox, 300, 250);
        stage.setScene(scene);
        stage.setTitle("Main Menu");
        stage.show();
    }

    private static void startGame(Stage stage) {
        if (isLoggedIn) {
            // Start the game by showing the GameScene
            new GameScene(stage).show();
        } else {
            System.out.println("You must log in first!");
        }
    }

    private static void showGameHistory(Stage stage) {
        // Implement the game history functionality
        System.out.println("Showing Game History");
    }

    private static void showLeaderboard(Stage stage) {
        // Implement the leaderboard functionality
        System.out.println("Showing Leaderboard");
    }

    private static void logout(Stage stage) {
        isLoggedIn = false;
        new LoginMenu().show(stage);  // Redirect to login screen after logging out
    }

    // Call this method after a successful login to update the login status
    public static void loginSuccessful() {
        isLoggedIn = true;
    }
}
































