package com.example.fisheatfish.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.fisheatfish.game.PlayerScore;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/authentication"; // Your database URL
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "240103Hykk#"; // Replace with your MySQL password

    // Method to establish and return a connection to the database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to insert a player's score into the player_scores table
    public static void saveScore(int userId, int score, int level, int fishEaten) {
        // Insert the new score (no need to check is_high_score)
        String query = "INSERT INTO player_scores (user_id, score, level, fish_eaten, gameplay_count, date_played) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // User ID
            statement.setInt(2, score);   // Score
            statement.setInt(3, level);   // Level
            statement.setInt(4, fishEaten); // Fish eaten
            statement.setInt(5, getCurrentGameplayCount(userId) + 1); // Incremented gameplay count

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve the current high score for a specific user
    public static int getHighScore(int userId) {
        // SQL query to retrieve the maximum score for the user
        String query = "SELECT MAX(score) AS high_score FROM player_scores WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Set the userId parameter
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("high_score");  // Get the maximum score for the user
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Return 0 if no score is found for the user
    }

    // Method to update the high score for a specific player by userId
    public static void updateHighScore(int userId, int newHighScore) {
        // SQL query to update the high score for the player
        String query = "UPDATE player_scores SET score = ? WHERE user_id = ? AND score < ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set parameters: new high score, userId, and check if current score is lower than the new high score
            statement.setInt(1, newHighScore);  // The new high score
            statement.setInt(2, userId);        // User ID
            statement.setInt(3, newHighScore);  // Only update if current score is less than the new high score

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("High score updated successfully for userId: " + userId);
            } else {
                System.out.println("The new high score is not greater than the current score.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to get the current gameplay count for the user
    private static int getCurrentGameplayCount(int userId) {
        String query = "SELECT COALESCE(MAX(gameplay_count), 0) FROM player_scores WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId); // Filter by user_id

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1); // Return the current max gameplay_count
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no previous record is found
    }

    public static int getTotalGameplayCount(int userId) {
        String query = "SELECT COUNT(*) AS total_gameplay_count FROM player_scores WHERE user_id = ?";

        int totalGameplayCount = 0;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Filter by user_id

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalGameplayCount = resultSet.getInt("total_gameplay_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalGameplayCount;
    }


    // Method to retrieve the last score for a specific player by userId
    public static int getLastScore(int userId) {
        String query = "SELECT score FROM player_scores WHERE user_id = ? ORDER BY date_played DESC LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Filter by user_id

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("score");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no score is found
    }

    // Method to retrieve the last level for a specific player by userId
    public static int getLastLevel(int userId) {
        String query = "SELECT level FROM player_scores WHERE user_id = ? ORDER BY date_played DESC LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Filter by user_id

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Return level 1 if no level is found
    }


    public static List<PlayerScore> loadScores(int userId, int page, int entriesPerPage) {
        List<PlayerScore> scores = new ArrayList<>();
        String query = "SELECT score, level, fish_eaten, gameplay_count, date_played " +
                "FROM player_scores WHERE user_id = ? ORDER BY date_played DESC " +
                "LIMIT ?, ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, (page - 1) * entriesPerPage);
            statement.setInt(3, entriesPerPage);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int score = resultSet.getInt("score");
                    int level = resultSet.getInt("level");
                    int fishEaten = resultSet.getInt("fish_eaten");
                    int gameplayCount = resultSet.getInt("gameplay_count");

                    // Convert java.sql.Date to java.sql.Timestamp
                    java.sql.Date sqlDate = resultSet.getDate("date_played");
                    Timestamp datePlayed = (sqlDate != null) ? new Timestamp(sqlDate.getTime()) : null;

                    PlayerScore playerScore = new PlayerScore(score, level, fishEaten, gameplayCount, datePlayed);
                    scores.add(playerScore);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
}








