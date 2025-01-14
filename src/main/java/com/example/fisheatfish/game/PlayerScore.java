package com.example.fisheatfish.game;

import java.sql.Timestamp;

public class PlayerScore {
    private int score;
    private int level;
    private int fishEaten;
    private int gameplayCount;
    private Timestamp datePlayed;
    private String username;

    // Constructor with all details
    public PlayerScore(int score, int level, int fishEaten, int gameplayCount, Timestamp datePlayed) {
        this.score = score;
        this.level = level;
        this.fishEaten = fishEaten;
        this.gameplayCount = gameplayCount;
        this.datePlayed = datePlayed;
    }

    // Getters and setters
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFishEaten() {
        return fishEaten;
    }

    public void setFishEaten(int fishEaten) {
        this.fishEaten = fishEaten;
    }

    public int getGameplayCount() {
        return gameplayCount;
    }

    public void setGameplayCount(int gameplayCount) {
        this.gameplayCount = gameplayCount;
    }

    public Timestamp getDatePlayed() {
        return datePlayed;
    }

    public void setDatePlayed(Timestamp datePlayed) {
        this.datePlayed = datePlayed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static class LeaderboardEntry {
        private final String username;
        private final int score;

        public LeaderboardEntry(String username, int score) {
            this.username = username;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public int getScore() {
            return score;
        }
    }

    // Method to format player score information as a string
    public String toFormattedString() {
        return "Username: " + username +
                ", Score: " + score +
                ", Level: " + level +
                ", Fish Eaten: " + fishEaten +
                ", Gameplay Count: " + gameplayCount +
                (datePlayed != null ? ", Date Played: " + datePlayed.toString() : "");
    }

    @Override
    public String toString() {
        return "PlayerScore{" +
                "username='" + username + '\'' +
                ", score=" + score +
                ", level=" + level +
                ", fishEaten=" + fishEaten +
                ", gameplayCount=" + gameplayCount +
                ", datePlayed=" + datePlayed +
                '}';
    }
}





