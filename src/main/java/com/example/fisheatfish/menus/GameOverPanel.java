package com.example.fisheatfish.menus;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import com.example.fisheatfish.utils.DatabaseConnection;

public class GameOverPanel {
    private StackPane gameOverPanel;

    // Method to show the Game Over panel with score, level, and high score
    public void showGameOverPanel(Group root, int score, int level, int highScore, int userId, Runnable onRestart, Runnable onExitToMenu) {
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

        // Score label
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setTextAlignment(TextAlignment.CENTER);

        // Level label
        Label levelLabel = new Label("Level: " + level);
        levelLabel.setFont(new Font("Arial", 20));
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setTextAlignment(TextAlignment.CENTER);

        // High Score label
        Label highScoreLabel = new Label("High Score: " + highScore);
        highScoreLabel.setFont(new Font("Arial", 20));
        highScoreLabel.setTextFill(Color.GOLD);
        highScoreLabel.setTextAlignment(TextAlignment.CENTER);

        // New High Score message
        Label newHighScoreLabel = new Label("New High Score!");
        newHighScoreLabel.setFont(new Font("Arial", 18));
        newHighScoreLabel.setTextFill(Color.YELLOW);
        newHighScoreLabel.setTextAlignment(TextAlignment.CENTER);
        newHighScoreLabel.setVisible(true); // Default to hidden

        int highscore = DatabaseConnection.getHighScore(userId);

        // Show the "New High Score!" message only if the player beat the previous high score
        if (score > highscore) {
            System.out.println("New High Score Achieved!");
            highScoreLabel.setText("High Score: " + score);
            newHighScoreLabel.setVisible(true);

            // Update the high score in the database after displaying the new high score
            Platform.runLater(() -> {
                DatabaseConnection.updateHighScore(userId, score);
            });
        } else {
            // Hide the "New High Score!" label if the score did not beat the previous high score
            newHighScoreLabel.setVisible(false);
        }


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
        layout.getChildren().addAll(gameOverLabel, scoreLabel, levelLabel, highScoreLabel, newHighScoreLabel, restartButton, exitButton);
        System.out.println(layout.getChildren()); // Debugging: Print all children

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







