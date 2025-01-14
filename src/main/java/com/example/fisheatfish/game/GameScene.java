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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

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
    private Label highScoreLabel;;
    int userId = MainMenu.getLoggedInUserId();
    private int highScore = DatabaseConnection.getHighScore(userId);;

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

    // Sound attributes
    private MediaPlayer bgmPlayer;
    private MediaPlayer eatSoundPlayer;
    private MediaPlayer levelUpSoundPlayer;
    private MediaPlayer gameOverSoundPlayer;


    public GameScene(Stage stage) {
        this.stage = stage;
        this.pauseMenu = new PauseMenu(); // Initialize PauseMenu
        this.gameOverPanel = new GameOverPanel(); // Initialize GameOverPanel

        // Load sounds
        bgmPlayer = createMediaPlayer("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\sound\\bgm.wav");
        eatSoundPlayer = createMediaPlayer("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\sound\\eating (mp3cut.net).mp3");
        levelUpSoundPlayer = createMediaPlayer("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\sound\\levelup.mp3");
        gameOverSoundPlayer = createMediaPlayer("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\sound\\collision.mp3");
    }

    private void playLevelUpSound() {
        levelUpSoundPlayer.stop();
        levelUpSoundPlayer.play();
    }


    private MediaPlayer createMediaPlayer(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        return player;
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

        // Start background music
        bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
        bgmPlayer.play();

        // Add the background to the game scene
        addBackground(root);  // This adds the background image

        // Initialize player fish
        playerFish = new PlayerFish();
        playerFish.setTranslateX(400); // Set the initial position for the player fish
        playerFish.setTranslateY(300);

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

        // Initialize high score label
        highScoreLabel = new Label("High Score: " + highScore);
        highScoreLabel.setFont(new Font("Arial", 20));
        highScoreLabel.setTextFill(Color.BLACK);
        highScoreLabel.setTranslateX(10);
        highScoreLabel.setTranslateY(70);
        root.getChildren().add(highScoreLabel);

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

            // Flag to track if the sound has already been played for this collision
            boolean soundPlayed = false;

            // Check collision with the player fish
            if (checkCollision(playerFish, enemyFish)) {
                if (playerFish.getRadius() >= enemyFish.getRadius()) {
                    // Player eats the enemy fish
                    if (!soundPlayed) {  // Ensure sound plays only once per collision
                        // Stop and reset the eating sound before playing it
                        eatSoundPlayer.stop();
                        eatSoundPlayer.seek(Duration.ZERO);  // Reset to start
                        eatSoundPlayer.play();  // Play the sound again
                        soundPlayed = true;  // Mark sound as played
                    }

                    score += enemyFish.getPointValue();
                    playerFish.incrementFishEaten();  // Track the number of fish eaten
                    System.out.println("Debug: Fish Eaten - Current Count = " + fishEaten);
                    updateScoreLabel();

                    toRemove.add(enemyFish);  // Mark the enemy fish for removal
                } else {
                    // End the game if the player collides with a larger enemy fish
                    gameOverSoundPlayer.stop();  // Stop the sound if it's still playing
                    gameOverSoundPlayer.play();  // Play the sound again
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
            playLevelUpSound();
            System.out.println("Triggering level-up sound for Level: " + currentLevel);
            progressToNextLevel(root, 2);
        } else if (score >= 100 && currentLevel == 2) {
            playLevelUpSound();
            System.out.println("Triggering level-up sound for Level: " + currentLevel);
            progressToNextLevel(root, 3);
        } else if (score >= 150 && currentLevel == 3) {
            playLevelUpSound();
            System.out.println("Triggering level-up sound for Level: " + currentLevel);
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
        double screenHeight = 565; // Or use your actual screen height if different

        // Define a speed multiplier based on the current level or game state
        double speedMultiplier = 3.0;  // Default multiplier (could increase based on level)

        // For example, increase the multiplier as the level progresses
        if (currentLevel > 1) {
            speedMultiplier = 4.0;  // Increase speed by 1.5x on higher levels
        }
        if (currentLevel > 2) {
            speedMultiplier = 5.0;  // Increase speed by 2x on even higher levels
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

        // First, we determine the type based on the score threshold (level progress)
        if (score >= levelMidpointScore) {
            if (currentLevel == 1) {
                // Level 1: Lower chance for Small fish (50%) and higher chance for Medium fish (50%)
                return random.nextInt(10) < 5 ? EnemyFish.FishType.SMALL : EnemyFish.FishType.MEDIUM;
            } else if (currentLevel == 2) {
                // Level 2: Lower chance for Small fish (40%), Medium (40%), and higher chance for Large fish (20%)
                return random.nextInt(10) < 4 ? EnemyFish.FishType.SMALL :
                        (random.nextInt(10) < 8 ? EnemyFish.FishType.MEDIUM : EnemyFish.FishType.LARGE);
            } else {
                // Level 3 and higher: Low chance for Small fish (20%), Medium (30%), Large (30%), and very high chance for Giant (20%)
                int chance = random.nextInt(10);
                if (chance < 2) {
                    return EnemyFish.FishType.SMALL;  // 20% chance for Small
                } else if (chance < 5) {
                    return EnemyFish.FishType.MEDIUM; // 30% chance for Medium
                } else if (chance < 8) {
                    return EnemyFish.FishType.LARGE;  // 30% chance for Large
                } else {
                    return EnemyFish.FishType.GIANT;  // 20% chance for Giant
                }
            }
        } else {
            // Below level threshold: Default to Small fish
            return EnemyFish.FishType.SMALL;
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private void updateLevelLabel() {
        levelLabel.setText("Level: " + currentLevel);
    }

    private void updateHighScoreLabel() {
        highScoreLabel.setText("High Score: " + highScore);
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

    private void updatePlayerMovement(double deltaTime) {
        double speed = 100; // Pixels per second
        boolean positionChanged = false;

        // Move up
        if (moveUp) {
            double newY = Math.max(PADDING, playerFish.getTranslateY() - speed * deltaTime);
            if (newY != playerFish.getTranslateY()) {
                playerFish.setTranslateY(newY);
                positionChanged = true;
            }
        }
        // Move down
        if (moveDown) {
            double newY = Math.min(565 - PADDING, playerFish.getTranslateY() + speed * deltaTime);
            if (newY != playerFish.getTranslateY()) {
                playerFish.setTranslateY(newY);
                positionChanged = true;
            }
        }
        // Move left
        if (moveLeft) {
            double newX = Math.max(PADDING, playerFish.getTranslateX() - speed * deltaTime);
            if (newX != playerFish.getTranslateX()) {
                playerFish.setTranslateX(newX);
                playerFish.setLeftImage();  // Switch to the left-facing image
                positionChanged = true;
            }
        }
        // Move right
        if (moveRight) {
            double newX = Math.min(800 - PADDING, playerFish.getTranslateX() + speed * deltaTime);
            if (newX != playerFish.getTranslateX()) {
                playerFish.setTranslateX(newX);
                playerFish.setRightImage();  // Switch to the right-facing image
                positionChanged = true;
            }
        }

        // Update the ImageView position only if the position has changed
        if (positionChanged) {
            playerFish.getFishImageView().setX(playerFish.getTranslateX() - playerFish.getRadius());
            playerFish.getFishImageView().setY(playerFish.getTranslateY() - playerFish.getRadius());
        }
    }

    private void togglePause() {
        paused = !paused;

        if (paused) {
            // Pause the game logic and show the pause menu
            pauseMenu.showPauseMenu((Group) stage.getScene().getRoot());

            // Pause the background music
            bgmPlayer.pause();
        } else {
            // Resume the game logic and hide the pause menu
            pauseMenu.hidePauseMenu((Group) stage.getScene().getRoot());

            // Resume the background music
            bgmPlayer.play();
        }
    }

    private void endGame() {
        gameOver = true;

        // Play game-over sound
        bgmPlayer.stop(); // Stop background music
        gameOverSoundPlayer.stop();
        gameOverSoundPlayer.play();

        // Get the logged-in user's ID
        int userId = MainMenu.getLoggedInUserId();
        int fishEaten = playerFish.getFishEaten(); // Replace with actual logic to get fish eaten by the player.

        System.out.println("EndGame Debug: userId=" + userId + ", fishEaten=" + fishEaten + ", currentLevel=" + currentLevel + ", score=" + score);


        // Initialize the high score variables
        int oldHighScore = 0;
        int newHighScore = 0;

        if (userId != -1) {
            // Fetch the current high score for the user from the database
            oldHighScore = DatabaseConnection.getHighScore(userId);

            // If the current score exceeds the high score, save the new score
            if (score > oldHighScore) {
                // Update the high score in the database
                DatabaseConnection.updateHighScore(userId, score, currentLevel, fishEaten);  // Pass currentLevel and fishEaten
                newHighScore = score;  // Set new high score
            } else {
                // If no new high score, save the score anyway
                DatabaseConnection.saveScore(userId, score, currentLevel, fishEaten);  // Save score, level, fishEaten
                newHighScore = oldHighScore;  // The old high score remains
            }
        }

        // Show the Game Over panel with the current score, level, and high score comparison
        gameOverPanel.showGameOverPanel(
                (Group) stage.getScene().getRoot(),  // Root node of the scene
                score,                              // Current score
                currentLevel,                       // Current level
                oldHighScore,                       // Old high score for the user
                newHighScore,                       // New high score after comparison
                MainMenu.getLoggedInUserId(),       // User ID
                this::restartGame,                  // Restart method (Runnable)
                this::exitToMainMenu                // Exit to Main Menu method (Runnable)
        );
    }

    private void restartGame() {
        // Stop the game over sound if it's still playing
        gameOverSoundPlayer.stop();
        eatSoundPlayer.stop();

        // Reset game variables
        score = 0;
        currentLevel = 1;
        fishEaten = 0;
        playerFish.resetFishEaten(); // Reset the fish eaten count
        gameOver = false;
        paused = false;
        enemySpawnTimer = 0;

        // Debug: Confirm reset
        System.out.println("Game reset: Score = " + score + ", Level = " + currentLevel + ", Fish Eaten = " + fishEaten);

        // Update UI labels with the reset values
        updateScoreLabel();  // Update the score label
        updateLevelLabel();  // Update the level label
        updateHighScoreLabel();  // Update the high score label (assuming you have this method)

        // Clear existing enemy fish
        Group root = (Group) stage.getScene().getRoot();
        for (EnemyFish enemyFish : enemyFishList) {
            root.getChildren().remove(enemyFish.getFishImageView());  // Remove enemy fish from scene
        }
        enemyFishList.clear();

        // Reset player fish position and radius
        playerFish.setTranslateX(400);
        playerFish.setTranslateY(300);
        playerFish.setRadius(10);  // Ensure this matches your initial game settings

        // Reset the associated ImageView position and size
        playerFish.getFishImageView().setX(playerFish.getTranslateX() - playerFish.getRadius());
        playerFish.getFishImageView().setY(playerFish.getTranslateY() - playerFish.getRadius());
        playerFish.getFishImageView().setFitWidth(playerFish.getRadius() * 2);
        playerFish.getFishImageView().setFitHeight(playerFish.getRadius() * 2);

        // Clear the root node and re-add the background and player fish
        root.getChildren().clear();  // Clear any existing elements from the root node
        addBackground(root);  // Reapply the background
        root.getChildren().add(playerFish.getFishImageView());  // Add player fish to the scene

        // Add the labels back to the root
        root.getChildren().add(scoreLabel);  // Add score label to root
        root.getChildren().add(levelLabel);  // Add level label to root
        root.getChildren().add(highScoreLabel);  // Add high score label to root

        // Restart the game loop
        gameLoop.play();

        // Hide the Game Over panel (if it's visible)
        gameOverPanel.hideGameOverPanel(root);

        // Spawn new enemy fish
        spawnEnemyFish(root, determineFishTypeBasedOnLevel());

        // Restart background music
        bgmPlayer.stop();
        bgmPlayer.play();

        // Reset sound effects
        eatSoundPlayer.seek(Duration.ZERO);
        levelUpSoundPlayer.seek(Duration.ZERO);
        gameOverSoundPlayer.seek(Duration.ZERO);

        System.out.println("Game restarted.");
    }

    private void exitToMainMenu() {
        gameOverPanel.hideGameOverPanel((Group) stage.getScene().getRoot());
        MainMenu.show(stage);

    }
}

































