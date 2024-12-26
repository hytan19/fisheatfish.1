package com.example.fisheatfish.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScene {

    private Stage stage;
    private PlayerFish playerFish;
    private List<EnemyFish> enemyFishList;
    private Timeline gameLoop;
    private boolean gameOver = false;
    private int score = 0;
    private int currentLevel = 1;
    private Label scoreLabel;

    // Minimum distance between enemy fish to avoid overlapping
    private static final double MIN_DISTANCE_BETWEEN_FISH = 50;

    public GameScene(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Group root = new Group();

        // Initialize player fish
        playerFish = new PlayerFish();
        root.getChildren().add(playerFish);

        // Initialize enemy fish
        enemyFishList = new ArrayList<>();
        spawnEnemyFish(root, EnemyFish.FishType.SMALL);

        // Initialize score label
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        root.getChildren().add(scoreLabel);

        // Set up the scene
        Scene gameScene = new Scene(root, 600, 400, Color.CYAN);
        gameScene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));

        // Start the game loop
        startGameLoop(root);

        // Set up the stage
        stage.setScene(gameScene);
        stage.setTitle("Fish Eat Fish - Game");
        stage.show();
    }

    private void startGameLoop(Group root) {
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(0.016), e -> gameLoopAction(root)));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void gameLoopAction(Group root) {
        if (gameOver) {
            gameLoop.stop();
            return;
        }

        // List to store enemy fish to be removed
        List<EnemyFish> toRemove = new ArrayList<>();

        // Move enemy fish and check collisions
        for (EnemyFish enemyFish : enemyFishList) {
            enemyFish.setTranslateX(enemyFish.getTranslateX() - enemyFish.getSpeed());
            if (enemyFish.getTranslateX() < 0) {
                // Respawn enemy fish if they go off-screen
                enemyFish.setTranslateX(600);
                enemyFish.setTranslateY(new Random().nextInt(400));
            }

            // Check for collision with player
            if (playerFish.getBoundsInParent().intersects(enemyFish.getBoundsInParent())) {
                if (playerFish.getRadius() > enemyFish.getRadius()) {
                    // Player eats the enemy fish
                    score += 5;
                    updateScoreLabel();
                    toRemove.add(enemyFish);  // Add enemy fish to be removed
                } else {
                    // Player is eaten by a larger fish
                    endGame();
                    return;
                }
            }
        }

        // Remove enemies marked for removal
        for (EnemyFish enemyFish : toRemove) {
            root.getChildren().remove(enemyFish);
            enemyFishList.remove(enemyFish);
        }

        // Check for level progression
        if (score >= 10 && currentLevel == 1) {
            progressToNextLevel(root, 2, EnemyFish.FishType.MEDIUM);
        } else if (score >= 20 && currentLevel == 2) {
            progressToNextLevel(root, 3, EnemyFish.FishType.LARGE);
        } else if (score >= 30 && currentLevel == 3) {
            progressToNextLevel(root, 4, EnemyFish.FishType.GIANT);
        }
    }


    private void progressToNextLevel(Group root, int level, EnemyFish.FishType type) {
        currentLevel = level;
        spawnEnemyFish(root, type);
        System.out.println("Progressed to Level " + level);
    }

    private void spawnEnemyFish(Group root, EnemyFish.FishType type) {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            EnemyFish enemyFish = new EnemyFish(type);
            boolean validPosition = false;
            double x = 0;
            double y = 0;

            // Ensure enemy fish doesn't spawn too close to any other fish
            while (!validPosition) {
                x = random.nextInt(600) + 600; // Spawn off-screen initially
                y = random.nextInt(400);

                // Check if the new position is far enough from other enemy fish
                validPosition = true;
                for (EnemyFish existingFish : enemyFishList) {
                    double distance = Math.sqrt(Math.pow(x - existingFish.getTranslateX(), 2) +
                            Math.pow(y - existingFish.getTranslateY(), 2));
                    if (distance < MIN_DISTANCE_BETWEEN_FISH) {
                        validPosition = false;
                        break;
                    }
                }
            }

            enemyFish.setTranslateX(x);
            enemyFish.setTranslateY(y);
            enemyFishList.add(enemyFish);
            root.getChildren().add(enemyFish);
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private void handleKeyPress(KeyCode keyCode) {
        if (gameOver) return;

        switch (keyCode) {
            case W -> playerFish.moveUp();
            case S -> playerFish.moveDown();
            case A -> playerFish.moveLeft();
            case D -> playerFish.moveRight();
        }
    }

    private void endGame() {
        gameOver = true;
        System.out.println("Game Over! Final Score: " + score);

        // Optionally, show a game over message or transition to another scene
        Label gameOverLabel = new Label("Game Over! Final Score: " + score);
        gameOverLabel.setFont(new Font("Arial", 30));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setTranslateX(150);
        gameOverLabel.setTranslateY(180);
        ((Group) stage.getScene().getRoot()).getChildren().add(gameOverLabel);
    }
}







