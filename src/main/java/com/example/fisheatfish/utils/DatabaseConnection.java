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
        // Step 1: Get the current gameplay_count for the user
        int currentGameplayCount = getCurrentGameplayCount(userId);

        // Step 2: Insert the new score with the incremented gameplay_count
        String query = "INSERT INTO player_scores (user_id, score, level, fish_eaten, gameplay_count, date_played) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);    // Store the player's user_id
            statement.setInt(2, score);     // Store the score
            statement.setInt(3, level);     // Store the level
            statement.setInt(4, fishEaten); // Store the number of fish eaten
            statement.setInt(5, currentGameplayCount + 1); // Incremented gameplay_count

            statement.executeUpdate(); // Execute the insert query

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
        String query = "SELECT SUM(gameplay_count) AS total_gameplay_count FROM player_scores WHERE user_id = ?";
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

    // Method to retrieve the last score, level, fish_eaten, and gameplay_count for a specific player by userId
    public static PlayerScore getLastGameStats(int userId) {
        String query = "SELECT score, level, fish_eaten, gameplay_count FROM player_scores WHERE user_id = ? ORDER BY date_played DESC LIMIT 1";
        PlayerScore lastGameStats = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Filter by user_id

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int score = resultSet.getInt("score");
                    int level = resultSet.getInt("level");
                    int fishEaten = resultSet.getInt("fish_eaten");
                    int gameplayCount = resultSet.getInt("gameplay_count");

                    lastGameStats = new PlayerScore(score, level, fishEaten, gameplayCount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastGameStats;
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

    // Method to retrieve all scores for a specific player by userId
    public static List<PlayerScore> loadScores(int userId) {
        List<PlayerScore> scores = new ArrayList<>();
        String query = "SELECT score, level, fish_eaten, gameplay_count, date_played FROM player_scores WHERE user_id = ? ORDER BY date_played DESC";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);  // Filter scores by user_id

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int score = resultSet.getInt("score");
                    int level = resultSet.getInt("level");
                    int fishEaten = resultSet.getInt("fish_eaten");
                    int gameplayCount = resultSet.getInt("gameplay_count");
                    Timestamp datePlayed = resultSet.getTimestamp("date_played");

                    // Creating PlayerScore object and adding to list
                    PlayerScore playerScore = new PlayerScore(score, level, fishEaten, gameplayCount, datePlayed);
                    scores.add(playerScore);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
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








