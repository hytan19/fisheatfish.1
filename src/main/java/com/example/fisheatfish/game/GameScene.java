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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private long lastTime = System.nanoTime(); // Tracks the last frame's time
    private double spawnTimeAccumulator = 0;

    public GameScene(Stage stage) {
        this.stage = stage;
        this.pauseMenu = new PauseMenu(); // Initialize PauseMenu
        this.gameOverPanel = new GameOverPanel(); // Initialize GameOverPanel
    }

    private void addBackground(Group root) {
        // Load the background image from the resources folder
        Image backgroundImage = new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\Background\\sea.jpeg");

        // Create an ImageView for the background
        ImageView backgroundImageView = new ImageView(backgroundImage);

        // Set the width and height of the background to match the game window size
        backgroundImageView.setFitWidth(800);  // Adjust to your screen width
        backgroundImageView.setFitHeight(565); // Adjust to your screen height

        // Set the position of the background (0, 0) to start from the top-left corner
        backgroundImageView.setX(0);
        backgroundImageView.setY(0);

        // Add the background image to the root Group (at index 0 so it stays behind other elements)
        root.getChildren().add(0, backgroundImageView);
    }

    public void show() {
        Group root = new Group();
        root.setStyle("-fx-padding: " + PADDING + ";");

        // Add the background to the game scene
        addBackground(root);  // This adds the background image

        // Initialize player fish
        playerFish = new PlayerFish();
        playerFish.setTranslateX(300); // Set the initial position for the player fish
        playerFish.setTranslateY(200);

        // Add the player's fish image to the root of the scene
        root.getChildren().add(playerFish.getFishImageView());

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
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(0.016), e -> {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1e9; // Convert nanoseconds to seconds
            lastTime = now;
            gameLoopAction(root, deltaTime);
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void gameLoopAction(Group root, double deltaTime) {
        if (gameOver || paused) {
            return;
        }

        // Use the passed deltaTime directly instead of recalculating it
        double elapsedTime = deltaTime;

        // Update player movement based on elapsed time
        updatePlayerMovement(elapsedTime);

        // List to store enemy fish to be removed
        List<EnemyFish> toRemove = new ArrayList<>();

// Move enemy fish and check for collisions
        for (EnemyFish enemyFish : enemyFishList) {
            // Ensure fish movement is scaled by deltaTime for smooth movement
            double speed = enemyFish.getSpeed();  // Get the speed of each enemy fish
            enemyFish.setTranslateX(enemyFish.getTranslateX() - speed * elapsedTime); // Move the fish based on speed and elapsed time

            // Update the image position as well
            enemyFish.getFishImageView().setX(enemyFish.getTranslateX() - enemyFish.getRadius());
            enemyFish.getFishImageView().setY(enemyFish.getTranslateY() - enemyFish.getRadius());

            // Reset fish position if it moves off-screen
            if (enemyFish.getTranslateX() < PADDING) {
                enemyFish.setTranslateX(800 - PADDING); // Set the fish back to the right side of the screen
                enemyFish.setTranslateY(new Random().nextInt((int) (565 - 2 * PADDING)) + PADDING); // Randomize y position
                enemyFish.getFishImageView().setX(enemyFish.getTranslateX() - enemyFish.getRadius());
                enemyFish.getFishImageView().setY(enemyFish.getTranslateY() - enemyFish.getRadius());
            }

            // Check collision with the player fish
            if (checkCollision(playerFish, enemyFish)) {
                if (playerFish.getRadius() >= enemyFish.getRadius()) {
                    // Player eats the enemy fish
                    score += enemyFish.getPointValue();
                    playerFish.incrementFishEaten();  // Track the number of fish eaten
                    updateScoreLabel();
                    toRemove.add(enemyFish);  // Mark the enemy fish for removal
                } else {
                    // End the game if the player collides with a larger enemy fish
                    endGame();
                    return;
                }
            }
        }

// Remove enemy fish that have been eaten or are out of bounds
        for (EnemyFish enemyFish : toRemove) {
            root.getChildren().remove(enemyFish.getFishImageView());  // Remove fish from the scene using the ImageView
            enemyFishList.remove(enemyFish);  // Remove fish from the list
        }

// Handle level progression based on score
        if (score >= 50 && currentLevel == 1) {
            progressToNextLevel(root, 2);
        } else if (score >= 100 && currentLevel == 2) {
            progressToNextLevel(root, 3);
        } else if (score >= 150 && currentLevel == 3) {
            progressToNextLevel(root, 4);
        }


        // Update player score and level display
        playerFish.setScore(score);
        updateLevelLabel();

        // Debugging: Print spawn timer and enemy fish count
        System.out.println("Spawn Timer: " + spawnTimeAccumulator);
        System.out.println("Enemy Fish Count: " + enemyFishList.size());

        // Enemy fish spawning
        spawnTimeAccumulator += elapsedTime; // Accumulate elapsed time for spawning

        // Adjust spawn interval and ensure the number of enemy fish doesn't exceed the maximum limit
        if (spawnTimeAccumulator >= 0.5 && enemyFishList.size() < MAX_ENEMY_FISH) {
            System.out.println("Spawning Enemy Fish: " + enemyFishList.size());
            spawnEnemyFish(root, determineFishTypeBasedOnLevel()); // Spawn enemy fish based on current level
            spawnTimeAccumulator = 0; // Reset spawn timer after spawning
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

        // Get screen width and height
        double screenWidth = 800;  // Or use your actual screen width if different
        double screenHeight = 600; // Or use your actual screen height if different

        // Define a speed multiplier based on the current level or game state
        double speedMultiplier = 2.0;  // Default multiplier (could increase based on level)

        // For example, increase the multiplier as the level progresses
        if (currentLevel > 1) {
            speedMultiplier = 3.0;  // Increase speed by 1.5x on higher levels
        }
        if (currentLevel > 2) {
            speedMultiplier = 4.0;  // Increase speed by 2x on even higher levels
        }

        for (int i = 0; i < numFishToSpawn; i++) {
            EnemyFish enemyFish = new EnemyFish(type);

            // Apply the speed multiplier to the enemy fish's speed
            enemyFish.speed *= speedMultiplier;

            boolean validPosition = false;
            double x = 0;
            double y = 0;

            // Ensure spawn happens at the rightmost part of the screen
            while (!validPosition) {
                // x should always be the rightmost edge (with padding from the edge)
                x = screenWidth - PADDING;

                // Randomly generate the y position within the bounds (from PADDING to screenHeight - PADDING)
                y = random.nextInt((int) (screenHeight - 2 * PADDING)) + PADDING;

                // Print spawn positions to check if they are within bounds
                System.out.println("Trying to spawn at x: " + x + ", y: " + y);

                validPosition = true;
                // Ensure the new position doesn't overlap with existing fish
                for (EnemyFish existingFish : enemyFishList) {
                    double distance = Math.sqrt(Math.pow(x - existingFish.getTranslateX(), 2) +
                            Math.pow(y - existingFish.getTranslateY(), 2));
                    if (distance < MIN_DISTANCE_BETWEEN_FISH) {
                        validPosition = false;
                        break;
                    }
                }
            }

            // Set the position and add to the scene
            enemyFish.setTranslateX(x);
            enemyFish.setTranslateY(y);

            // Set the ImageView position as well
            enemyFish.getFishImageView().setX(x - enemyFish.getRadius());
            enemyFish.getFishImageView().setY(y - enemyFish.getRadius());

            // Print final position for debugging
            System.out.println("Enemy Fish Spawned at x: " + x + ", y: " + y);

            // Add the fish to the list and the scene
            enemyFishList.add(enemyFish);
            root.getChildren().add(enemyFish.getFishImageView());  // Add the image view to the scene
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
    private void updatePlayerMovement(double deltaTime) {
        double speed = 50; // Pixels per second

        // Move up
        if (moveUp) {
            playerFish.setTranslateY(Math.max(PADDING, playerFish.getTranslateY() - 3));
            playerFish.getFishImageView().setY(playerFish.getTranslateY() - playerFish.getRadius());  // Update image position
        }
        // Move down
        if (moveDown) {
            playerFish.setTranslateY(Math.min(565 - PADDING, playerFish.getTranslateY() + 3));
            playerFish.getFishImageView().setY(playerFish.getTranslateY() - playerFish.getRadius());  // Update image position
        }
        // Move left
        if (moveLeft) {
            playerFish.setTranslateX(Math.max(PADDING, playerFish.getTranslateX() - 3));
            playerFish.getFishImageView().setX(playerFish.getTranslateX() - playerFish.getRadius());  // Update image position
            playerFish.setLeftImage();  // Switch to the left-facing image
        }
        // Move right
        if (moveRight) {
            playerFish.setTranslateX(Math.min(800 - PADDING, playerFish.getTranslateX() + 3));
            playerFish.getFishImageView().setX(playerFish.getTranslateX() - playerFish.getRadius());  // Update image position
            playerFish.setRightImage();  // Switch to the right-facing image
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

        // Initialize the high score variable
        int highScore = 0;

        if (userId != -1) {
            // Fetch the current high score for the user from the database
            highScore = DatabaseConnection.getHighScore(userId);

            // If the current score exceeds the high score, save the new score
            if (score > highScore) {
                // Save the new score (this implicitly becomes the new high score)
                DatabaseConnection.saveScore(userId, score, currentLevel, fishEaten);
                highScore = score;  // Update highScore to the current score
            } else {
                // Save the score without marking it as a new high score
                DatabaseConnection.saveScore(userId, score, currentLevel, fishEaten);
            }
        }

        // Show the Game Over panel with the current score, level, and high score
        gameOverPanel.showGameOverPanel(
                (Group) stage.getScene().getRoot(),  // Root node of the scene
                score,                              // Current score
                currentLevel,                       // Current level
                highScore,                          // High score for the user
                MainMenu.getLoggedInUserId(),       // User ID
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

        // Clear existing enemy fish
        Group root = (Group) stage.getScene().getRoot();

        // Remove the enemy fish images from the scene
        for (EnemyFish enemyFish : enemyFishList) {
            root.getChildren().remove(enemyFish.getFishImageView());  // Remove the ImageView from the scene
        }
        enemyFishList.clear();  // Clear the list of enemy fish

        // Reset player fish position and size based on the last level
        playerFish.setTranslateX(300);  // You can adjust this if needed
        playerFish.setTranslateY(200);  // You can adjust this if needed
        playerFish.setRadius(10);  // Reset radius to the initial size

        // Add the background again (after clearing the screen)
        addBackground(root);  // Reapply the background

        // Restart the game loop
        gameLoop.play();

        // Hide the Game Over panel if it's visible
        gameOverPanel.hideGameOverPanel(root);

        // Spawn new enemy fish based on current level (if desired)
        spawnEnemyFish(root, determineFishTypeBasedOnLevel());  // This can be adjusted as needed
    }



    private void exitToMainMenu() {
        gameOverPanel.hideGameOverPanel((Group) stage.getScene().getRoot());
        MainMenu.show(stage);

    }
}

































