package com.example.fisheatfish.menus;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GameOverPanel {
    private StackPane gameOverPanel;

    // Method to show the Game Over panel with score and level
    public void showGameOverPanel(Group root, int score, int level, Runnable onRestart, Runnable onExitToMenu) {
        if (gameOverPanel != null) {
            return; // Prevent duplicate panels
        }

        gameOverPanel = new StackPane();
        gameOverPanel.setPrefSize(800, 565);
        gameOverPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // "Game Over" label
        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(new Font("Arial", 40));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setTextAlignment(TextAlignment.CENTER);

        // Score label (displaying the score here)
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setTextAlignment(TextAlignment.CENTER);

        // Level label (displaying the level here)
        Label levelLabel = new Label("Level: " + level);
        levelLabel.setFont(new Font("Arial", 20));
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setTextAlignment(TextAlignment.CENTER);

        // Restart button
        Button restartButton = new Button("Restart");
        restartButton.setFont(new Font("Arial", 18));
        restartButton.setOnAction(e -> {
            hideGameOverPanel(root);
            onRestart.run();  // Trigger the restart method passed in
        });

        // Exit to main menu button
        Button exitButton = new Button("Exit to Main Menu");
        exitButton.setFont(new Font("Arial", 18));
        exitButton.setOnAction(e -> {
            hideGameOverPanel(root);
            onExitToMenu.run();  // Trigger the exit to main menu method passed in
        });

        // Layout arrangement (VBox)
        VBox layout = new VBox(20); // VBox to arrange elements vertically
        layout.setStyle("-fx-alignment: center;");
        layout.getChildren().addAll(gameOverLabel, scoreLabel, levelLabel, restartButton, exitButton);

        // Add the panel to the root of the scene
        gameOverPanel.getChildren().add(layout);
        root.getChildren().add(gameOverPanel);  // Add the panel to the root
    }

    // Method to hide the Game Over panel
    public void hideGameOverPanel(Group root) {
        if (gameOverPanel != null) {
            root.getChildren().remove(gameOverPanel);  // Remove the Game Over panel from the root
            gameOverPanel = null;
        }
    }
}





