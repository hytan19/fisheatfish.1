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

        // Method to show the Game Over panel with score, level, and high score comparison
        public void showGameOverPanel(Group root, int score, int level, int oldHighScore, int newHighScore, int userId, Runnable onRestart, Runnable onExitToMenu) {
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

            // Old High Score label
            Label oldHighScoreLabel = new Label("High Score: " + oldHighScore);
            oldHighScoreLabel.setFont(new Font("Arial", 20));
            oldHighScoreLabel.setTextFill(Color.GOLD);
            oldHighScoreLabel.setTextAlignment(TextAlignment.CENTER);

            // New High Score label
            Label newHighScoreLabel = new Label("New High Score: " + newHighScore);
            newHighScoreLabel.setFont(new Font("Arial", 20));
            newHighScoreLabel.setTextFill(Color.YELLOW);
            newHighScoreLabel.setTextAlignment(TextAlignment.CENTER);
            newHighScoreLabel.setVisible(newHighScore > oldHighScore); // Only show if new high score

            // Display congratulations message if new high score is achieved
            Label congratsLabel = new Label("Congratulations! You've achieved a new high score!");
            congratsLabel.setFont(new Font("Arial", 18));
            congratsLabel.setTextFill(Color.YELLOW);
            congratsLabel.setTextAlignment(TextAlignment.CENTER);
            congratsLabel.setVisible(newHighScore > oldHighScore); // Only show if new high score

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
            layout.getChildren().addAll(gameOverLabel, scoreLabel, levelLabel, oldHighScoreLabel, newHighScoreLabel, congratsLabel, restartButton, exitButton);

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









