package com.example.fisheatfish.game;

import java.sql.Timestamp;

public class PlayerScore {
    private int score;
    private int level;
    private int fishEaten;
    private int gameplayCount;
    private Timestamp datePlayed;

    // Constructor without Timestamp
    public PlayerScore(int score, int level, int fishEaten, int gameplayCount) {
        this.score = score;
        this.level = level;
        this.fishEaten = fishEaten;
        this.gameplayCount = gameplayCount;
    }

    // Constructor with Timestamp
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

    // Method to format player score information as a string
    public String toFormattedString() {
        return "Score: " + score +
                ", Level: " + level +
                ", Fish Eaten: " + fishEaten +
                ", Gameplay Count: " + gameplayCount +
                (datePlayed != null ? ", Date Played: " + datePlayed.toString() : "");
    }

    @Override
    public String toString() {
        return "PlayerScore{" +
                "score=" + score +
                ", level=" + level +
                ", fishEaten=" + fishEaten +
                ", gameplayCount=" + gameplayCount +
                ", datePlayed=" + datePlayed +
                '}';
    }
}




