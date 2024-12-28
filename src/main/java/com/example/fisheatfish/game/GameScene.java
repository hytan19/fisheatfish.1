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
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import com.example.fisheatfish.menus.PauseMenu;
import com.example.fisheatfish.menus.GameOverPanel;
import com.example.fisheatfish.menus.MainMenu;
import com.example.fisheatfish.utils.DatabaseConnection;
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
    private int levelMidpointScore = 25;
    private int fishEaten = 0;
    private Label scoreLabel;
    private Label levelLabel;

    private static final double MIN_DISTANCE_BETWEEN_FISH = 50;
    private static final double PADDING = 20;

    private int enemySpawnTimer = 0;

    private static final int MAX_ENEMY_FISH = 10;

    private boolean paused = false;
    private PauseMenu pauseMenu;
    private GameOverPanel gameOverPanel; // Instance of GameOverPanel class

    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    public GameScene(Stage stage) {
        this.stage = stage;
        this.pauseMenu = new PauseMenu(); // Initialize PauseMenu
        this.gameOverPanel = new GameOverPanel(); // Initialize GameOverPanel
    }

    public void show() {
        Group root = new Group();
        root.setStyle("-fx-padding: " + PADDING + ";");

        // Initialize player fish
        playerFish = new PlayerFish();
        playerFish.setTranslateX(300);
        playerFish.setTranslateY(200);
        root.getChildren().add(playerFish);

        // Initialize enemy fish list
        enemyFishList = new ArrayList<>();

        // Initialize score label
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        root.getChildren().add(scoreLabel);

        // Initialize level label
        levelLabel = new Label("Level: 1");
        levelLabel.setFont(new Font("Arial", 20));
        levelLabel.setTextFill(Color.BLACK);
        levelLabel.setTranslateX(10);
        levelLabel.setTranslateY(40);
        root.getChildren().add(levelLabel);

        // Set up the scene
        Scene gameScene = new Scene(root, 800, 565, Color.CYAN);

        // Set up key press event
        gameScene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));

        // Set up key release event
        gameScene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));

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
            return;
        }

        if (paused) {
            return;
        }

        updatePlayerMovement();

        // List to store enemy fish to be removed
        List<EnemyFish> toRemove = new ArrayList<>();

        // Move enemy fish and check collisions
        for (EnemyFish enemyFish : enemyFishList) {
            enemyFish.setTranslateX(enemyFish.getTranslateX() - enemyFish.getSpeed());
            if (enemyFish.getTranslateX() < PADDING) {
                enemyFish.setTranslateX(800 - PADDING);
                enemyFish.setTranslateY(new Random().nextInt((int) (565 - 2 * PADDING)) + PADDING);
            }

            if (checkCollision(playerFish, enemyFish)) {
                if (playerFish.getRadius() >= enemyFish.getRadius()) {
                    score += enemyFish.getPointValue();
                    playerFish.incrementFishEaten();  // Track the number of fish eaten
                    updateScoreLabel();
                    toRemove.add(enemyFish);
                } else {
                    endGame();
                    return;
                }
            }
        }

        for (EnemyFish enemyFish : toRemove) {
            root.getChildren().remove(enemyFish);
            enemyFishList.remove(enemyFish);
        }

        if (score >= 50 && currentLevel == 1) {
            progressToNextLevel(root, 2);
        } else if (score >= 100 && currentLevel == 2) {
            progressToNextLevel(root, 3);
        } else if (score >= 150 && currentLevel == 3) {
            progressToNextLevel(root, 4);
        }

        playerFish.setScore(score);
        updateLevelLabel();

        enemySpawnTimer++;
        if (enemySpawnTimer > 80 && enemyFishList.size() < MAX_ENEMY_FISH) {
            spawnEnemyFish(root, determineFishTypeBasedOnLevel());
            enemySpawnTimer = 0;
        }
    }

    private boolean checkCollision(PlayerFish playerFish, EnemyFish enemyFish) {
        double dx = playerFish.getTranslateX() - enemyFish.getTranslateX();
        double dy = playerFish.getTranslateY() - enemyFish.getTranslateY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (playerFish.getRadius() + enemyFish.getRadius());
    }

    private void progressToNextLevel(Group root, int level) {
        currentLevel = level;
        playerFish.setRadius(getPlayerFishSize(level));

        if (level == 2) {
            levelMidpointScore = 75;
        } else if (level == 3) {
            levelMidpointScore = 125;
        }
    }

    private double getPlayerFishSize(int level) {
        switch (level) {
            case 1: return 10;
            case 2: return 20;
            case 3: return 30;
            default: return 40;
        }
    }

    private void spawnEnemyFish(Group root, EnemyFish.FishType type) {
        Random random = new Random();
        int numFishToSpawn = 1;

        for (int i = 0; i < numFishToSpawn; i++) {
            EnemyFish enemyFish = new EnemyFish(type);
            boolean validPosition = false;
            double x = 0;
            double y = 0;

            while (!validPosition) {
                x = random.nextInt((int) (800 - 2 * PADDING)) + 800;
                y = random.nextInt((int) (565 - 2 * PADDING)) + PADDING;

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

    private EnemyFish.FishType determineFishTypeBasedOnLevel() {
        Random random = new Random();

        if (score >= levelMidpointScore) {
            if (currentLevel == 1) {
                return random.nextInt(10) < 8 ? EnemyFish.FishType.SMALL : EnemyFish.FishType.MEDIUM;
            } else if (currentLevel == 2) {
                return random.nextInt(10) < 5 ? EnemyFish.FishType.SMALL :
                        (random.nextInt(10) < 8 ? EnemyFish.FishType.MEDIUM : EnemyFish.FishType.LARGE);
            } else {
                return random.nextInt(10) < 3 ? EnemyFish.FishType.SMALL :
                        (random.nextInt(10) < 7 ? EnemyFish.FishType.MEDIUM :
                                (random.nextInt(10) < 9 ? EnemyFish.FishType.LARGE : EnemyFish.FishType.GIANT));
            }
        } else {
            return EnemyFish.FishType.SMALL;
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private void updateLevelLabel() {
        levelLabel.setText("Level: " + currentLevel);
    }

    // Handles key press events
    private void handleKeyPress(KeyCode keyCode) {
        if (gameOver) return;

        switch (keyCode) {
            case W -> moveUp = true;   // Set moveUp to true when 'W' is pressed
            case S -> moveDown = true; // Set moveDown to true when 'S' is pressed
            case A -> moveLeft = true; // Set moveLeft to true when 'A' is pressed
            case D -> moveRight = true; // Set moveRight to true when 'D' is pressed
            case P -> togglePause();    // Pause the game when 'P' is pressed
        }
    }

    // Handles key release events
    private void handleKeyRelease(KeyCode keyCode) {
        switch (keyCode) {
            case W -> moveUp = false;   // Set moveUp to false when 'W' is released
            case S -> moveDown = false; // Set moveDown to false when 'S' is released
            case A -> moveLeft = false; // Set moveLeft to false when 'A' is released
            case D -> moveRight = false; // Set moveRight to false when 'D' is released
        }
    }

    // Update the player's position based on key states
    private void updatePlayerMovement() {
        // Move up
        if (moveUp) {
            playerFish.setTranslateY(Math.max(PADDING, playerFish.getTranslateY() - 5));
        }
        // Move down
        if (moveDown) {
            playerFish.setTranslateY(Math.min(565 - PADDING, playerFish.getTranslateY() + 5));
        }
        // Move left
        if (moveLeft) {
            playerFish.setTranslateX(Math.max(PADDING, playerFish.getTranslateX() - 5));
        }
        // Move right
        if (moveRight) {
            playerFish.setTranslateX(Math.min(800 - PADDING, playerFish.getTranslateX() + 5));
        }
    }

    private void togglePause() {
        paused = !paused;

        if (paused) {
            pauseMenu.showPauseMenu((Group) stage.getScene().getRoot());
        } else {
            pauseMenu.hidePauseMenu((Group) stage.getScene().getRoot());
        }
    }

    private void endGame() {
        gameOver = true;

        // Get the logged-in user's ID
        int userId = MainMenu.getLoggedInUserId();
        int fishEaten = playerFish.getFishEaten(); // Replace with actual logic to get fish eaten by the player.

        if (userId != -1) {
            // Save the score to the database
            DatabaseConnection.saveScore(userId, score, currentLevel, fishEaten);
        }

        // Show the Game Over panel with score and level
        gameOverPanel.showGameOverPanel(
                (Group) stage.getScene().getRoot(),  // Root node of the scene
                score,                              // Current score
                currentLevel,                       // Current level
                this::restartGame,                  // Restart method (Runnable)
                this::exitToMainMenu                // Exit to Main Menu method (Runnable)
        );
    }

    private void restartGame() {
        // Reset game variables
        score = 0;
        currentLevel = 1;
        fishEaten = 0;
        gameOver = false;
        paused = false;
        enemySpawnTimer = 0;

        // Get the logged-in user's ID (same as in endGame)
        int userId = MainMenu.getLoggedInUserId();

        // Fetch the last score and level from the database
        int lastScore = DatabaseConnection.getLastScore(userId);  // Get last saved score from DB
        int lastLevel = DatabaseConnection.getLastLevel(userId);  // Get last saved level from DB

        // Reset UI labels and display them
        updateScoreLabel();
        updateLevelLabel();

        // Display the last score and level on the restart panel
        displayRestartInfo(lastScore, lastLevel);  // Show the score and level on the restart panel

        // Clear existing enemy fish
        Group root = (Group) stage.getScene().getRoot();
        for (EnemyFish enemyFish : enemyFishList) {
            root.getChildren().remove(enemyFish);
        }
        enemyFishList.clear();

        // Reset player fish position and size based on the last level
        playerFish.setTranslateX(300);
        playerFish.setTranslateY(200);
        playerFish.setRadius(getPlayerFishSize(lastLevel));  // Set the size based on the last level

        // Restart game loop
        gameLoop.play();

        // Hide the Game Over panel if it's visible
        gameOverPanel.hideGameOverPanel(root);
    }

    private void displayRestartInfo(int score, int level) {
        // Create labels for score and level
        Label scoreLabel = new Label("Score: " + score);
        Label levelLabel = new Label("Level: " + level);

        // Assuming you have a VBox or some layout to hold these labels
        VBox restartPanel = new VBox(10);
        restartPanel.setPadding(new Insets(20));
        restartPanel.getChildren().addAll(scoreLabel, levelLabel);

        // Show the restart panel (this could be a new stage, or you could update the current scene)
        Stage restartStage = new Stage();
        restartStage.setTitle("Game Restart");
        Scene scene = new Scene(restartPanel, 300, 200);
        restartStage.setScene(scene);
        restartStage.show();
    }


    private void exitToMainMenu() {
        gameOverPanel.hideGameOverPanel((Group) stage.getScene().getRoot());
        MainMenu.show(stage);

    }
}

































