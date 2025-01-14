package com.example.fisheatfish.menus;

import com.example.fisheatfish.game.GameScene;
import com.example.fisheatfish.utils.DatabaseConnection;
import com.example.fisheatfish.game.PlayerScore;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class MainMenu {
    private static boolean isLoggedIn = false;  // Track login status
    private static int loggedInUserId = -1;  // Store the logged-in user's ID
    private static int currentPage = 1;  // Track the current page for pagination
    private static final int ENTRIES_PER_PAGE = 5;  // Number of entries per page

    // Show the main menu only after successful login
    public static void show(Stage stage) {
        if (!isLoggedIn) {
            // Redirect to login if the user is not logged in
            new LoginMenu().show(stage);
            return;
        }

        // Create the VBox layout for the main menu
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));


        // Set background styling similar to LoginMenu
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title for the Main Menu
        Label titleLabel = new Label("Main Menu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        titleLabel.setStyle("-fx-padding: 10px 0px 20px 0px;");

        // Create buttons with consistent styling
        Button startGameButton = new Button("Start Game");
        startGameButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        startGameButton.setOnAction(e -> startGame(stage));

        Button gameHistoryButton = new Button("Game History");
        gameHistoryButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gameHistoryButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        gameHistoryButton.setOnAction(e -> showGameHistory(stage));

        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        leaderboardButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        leaderboardButton.setOnAction(e -> displayLeaderboard(stage));

        Button logoutButton = new Button("Logout");
        logoutButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        logoutButton.setOnAction(e -> logout(stage));

        // Add title label and buttons to the VBox layout
        vbox.getChildren().addAll(titleLabel, startGameButton, gameHistoryButton, leaderboardButton, logoutButton);

        // Create and display the scene with appropriate size
        Scene scene = new Scene(vbox, 350, 400);  // Adjusted size for better spacing
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
        if (!isLoggedIn) {
            System.out.println("Please log in first!");
            return;
        }

        // Fetch the total number of game history entries
        int totalEntries = DatabaseConnection.getTotalGameplayCount(loggedInUserId);

        // Fetch the game history from the database (pagination logic)
        List<PlayerScore> scores = DatabaseConnection.loadScores(loggedInUserId, currentPage, ENTRIES_PER_PAGE);

        // Create a VBox to display the game history
        VBox gameHistoryVBox = new VBox(10);
        gameHistoryVBox.setPadding(new Insets(20));
        gameHistoryVBox.setStyle("-fx-background-color: #f4f4f9; -fx-border-radius: 10;");

        Label titleLabel = new Label("Game History");
        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Display total gameplay count
        int totalGameplayCount = DatabaseConnection.getTotalGameplayCount(loggedInUserId);
        Label totalGameplayLabel = new Label("Total Gameplay Count: " + totalGameplayCount);
        totalGameplayLabel.setFont(new Font(14));
        gameHistoryVBox.getChildren().addAll(titleLabel, totalGameplayLabel);

        // Display the scores in a more structured layout
        if (scores.isEmpty()) {
            Label noScoresLabel = new Label("No game history available.");
            gameHistoryVBox.getChildren().add(noScoresLabel);
        } else {
            for (PlayerScore score : scores) {
                HBox scoreBox = new HBox(10);
                scoreBox.setPadding(new Insets(10));
                scoreBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5; -fx-border-color: #ddd;");

                Label scoreLabel = new Label("Score: " + score.getScore());
                Label levelLabel = new Label("Level: " + score.getLevel());
                Label fishLabel = new Label("Fish Eaten: " + score.getFishEaten());

                scoreLabel.setFont(new Font("Arial", 14));
                levelLabel.setFont(new Font("Arial", 14));
                fishLabel.setFont(new Font("Arial", 14));

                scoreBox.getChildren().addAll(scoreLabel, levelLabel, fishLabel);

                gameHistoryVBox.getChildren().add(scoreBox);
            }
        }

        // Pagination Buttons: Previous and Next
        HBox paginationBox = new HBox(10);
        paginationBox.setPadding(new Insets(20));
        Button prevButton = new Button("Previous");
        prevButton.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                showGameHistory(stage);
            }
        });

        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> {
            if (currentPage * ENTRIES_PER_PAGE < totalEntries) {
                currentPage++;
                showGameHistory(stage);
            }
        });

        paginationBox.getChildren().addAll(prevButton, nextButton);
        gameHistoryVBox.getChildren().add(paginationBox);

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> show(stage));

        gameHistoryVBox.getChildren().add(backButton);

        // Set the scene with the Game History VBox
        Scene historyScene = new Scene(gameHistoryVBox, 600, 400);
        stage.setScene(historyScene);
        stage.setTitle("Game History");
        stage.show();
    }

    private static void logout(Stage stage) {
        isLoggedIn = false;
        loggedInUserId = -1;  // Reset the logged-in user ID
        new LoginMenu().show(stage);  // Redirect to login screen after logging out
    }

    // Call this method after a successful login to update the login status
    public static void loginSuccessful(int userId) {
        isLoggedIn = true;
        loggedInUserId = userId;  // Store the logged-in user's ID
    }

    // Getter for logged-in user's ID
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    // This method is used to show the restart panel after the game ends
    public static void showRestartPanel(Stage stage) {
        VBox restartVBox = new VBox(10);
        restartVBox.setPadding(new Insets(20));

        // Fetch the most recent score and level for the logged-in user from the database
        PlayerScore latestScore = getLatestScoreForUser(loggedInUserId);

        if (latestScore != null) {
            // Display score and level in the restart panel
            String scoreText = "Score: " + latestScore.getScore();
            String levelText = "Level: " + latestScore.getLevel();

            // Display score and level buttons (you can style them as you like)
            Button scoreButton = new Button(scoreText);
            Button levelButton = new Button(levelText);

            // Add buttons to the restart panel
            restartVBox.getChildren().addAll(scoreButton, levelButton);
        }

        // Button to restart the game
        Button restartButton = new Button("Restart Game");
        restartButton.setOnAction(e -> startGame(stage));

        // Button to exit the game
        Button exitButton = new Button("Exit Game");
        exitButton.setOnAction(e -> System.exit(0)); // Exit the game

        // Add the restart and exit buttons to the panel
        restartVBox.getChildren().addAll(restartButton, exitButton);

        Scene restartScene = new Scene(restartVBox, 300, 250);
        stage.setScene(restartScene);
        stage.setTitle("Game Over");
        stage.show();
    }

    // Method to fetch the latest score and level for the logged-in user
    private static PlayerScore getLatestScoreForUser(int userId) {
        String query = "SELECT * FROM player_scores WHERE user_id = ? ORDER BY date_played DESC LIMIT 1";
        try (var connection = DatabaseConnection.getConnection();
             var statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Filter scores by user_id
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int score = resultSet.getInt("score");
                    int level = resultSet.getInt("level");
                    int fishEaten = resultSet.getInt("fish_eaten"); // Fetch the number of fish eaten
                    int gameplayCount = resultSet.getInt("gameplay_count"); // Fetch gameplay count
                    Timestamp datePlayed = resultSet.getTimestamp("date_played");

                    // Create PlayerScore with all required fields
                    return new PlayerScore(score, level, fishEaten, gameplayCount, datePlayed);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void displayLeaderboard(Stage stage) {
        // Fetch leaderboard data from the database
        List<PlayerScore.LeaderboardEntry> leaderboard = DatabaseConnection.getLeaderboard();

        // Clear the existing content of the main menu
        VBox mainMenuLayout = (VBox) stage.getScene().getRoot();  // Assuming the root is a VBox for the main menu
        mainMenuLayout.getChildren().clear();

        // Title label for the leaderboard
        Label title = new Label("Leaderboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.DARKGREEN);
        title.setAlignment(Pos.CENTER_LEFT);  // Left-align the title

        // Use HBox to hold the title and left-align it
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER_LEFT);  // Left-align the title box
        titleBox.setPadding(new Insets(20, 0, 10, 20)); // Padding to make sure it's not too close to the edge

        // Add table-like headers for the leaderboard
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);  // Left-align the header
        header.setPadding(new Insets(5, 0, 10, 0));
        Label rankHeader = new Label("Rank");
        Label usernameHeader = new Label("Username");
        Label scoreHeader = new Label("Score");

        rankHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        usernameHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        rankHeader.setMinWidth(50);
        usernameHeader.setMinWidth(150);
        scoreHeader.setMinWidth(100);

        header.getChildren().addAll(rankHeader, usernameHeader, scoreHeader);

        // Add leaderboard entries in a VBox
        VBox entriesBox = new VBox(5);
        int rank = 1;
        for (PlayerScore.LeaderboardEntry entry : leaderboard) {
            HBox entryRow = new HBox(20);
            entryRow.setAlignment(Pos.CENTER_LEFT);  // Left-align each row

            Label rankLabel = new Label(String.valueOf(rank++));
            Label usernameLabel = new Label(entry.getUsername());
            Label scoreLabel = new Label(String.valueOf(entry.getScore()));

            rankLabel.setFont(Font.font("Arial", 14));
            usernameLabel.setFont(Font.font("Arial", 14));
            scoreLabel.setFont(Font.font("Arial", 14));

            rankLabel.setMinWidth(50);
            usernameLabel.setMinWidth(150);
            scoreLabel.setMinWidth(100);

            entryRow.getChildren().addAll(rankLabel, usernameLabel, scoreLabel);
            entriesBox.getChildren().add(entryRow);
        }

        // Scrollable list of leaderboard entries
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(entriesBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Allow scrolling if the content exceeds the screen height
        scrollPane.setPrefHeight(300);  // Adjust height based on the number of entries
        scrollPane.setMaxWidth(350);  // Narrower width for the leaderboard box
        scrollPane.setStyle("-fx-background-color: #f4f4f9;");

        // Back button to return to the main menu
        Button backButton = new Button("Back to Menu");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setTextFill(Color.WHITE);
        backButton.setStyle("-fx-background-color: #007acc;");  // Blue button
        backButton.setOnAction(e -> show(stage));

        // Create a VBox for the leaderboard layout
        VBox leaderboardLayout = new VBox(10);
        leaderboardLayout.setPadding(new Insets(10));
        leaderboardLayout.setAlignment(Pos.TOP_LEFT);  // Align the whole VBox to the left
        leaderboardLayout.setStyle("-fx-background-color: #f4f4f9; -fx-border-radius: 10;");

        // Set a specific width to the layout to make it narrower
        leaderboardLayout.setPrefWidth(400);  // Set the preferred width of the leaderboard box to make it narrower
        leaderboardLayout.getChildren().addAll(titleBox, header, new Separator(), scrollPane, backButton);

        // Set the layout for the leaderboard view
        mainMenuLayout.getChildren().addAll(leaderboardLayout);
    }
}



































