package com.example.fisheatfish.game;

import javafx.scene.paint.Color;

public class PlayerFish extends Fish {

    private int score = 0;      // Track player's score
    private int fishEaten = 0;  // Track the number of fish eaten

    // Constructor for PlayerFish with a distinct color (blue)
    public PlayerFish() {
        super(FishType.SMALL);  // Default to small fish
        setFill(Color.BLUE);    // Set player fish color to blue
        setTranslateX(300);     // Initial X position of the player fish
        setTranslateY(200);     // Initial Y position of the player fish
    }

    // Method to grow the player fish based on the score
    public void grow() {
        if (score >= 50 && score < 100) {
            setRadius(20);  // Grow the player fish to radius 20 when score is between 50 and 99
        } else if (score >= 100 && score < 150) {
            setRadius(30);  // Grow to radius 30 when score is between 100 and 149
        } else if (score >= 150) {
            setRadius(40);  // Grow to radius 40 when score is 150 and above
        }
    }

    // Movement methods for player fish
    public void moveUp() {
        if (getTranslateY() - 5 >= 0) {  // Prevent moving above the top edge
            setTranslateY(getTranslateY() - 5);
        }
    }

    public void moveDown() {
        if (getTranslateY() + 5 <= GAME_HEIGHT) {  // Prevent moving below the bottom edge
            setTranslateY(getTranslateY() + 5);
        }
    }

    public void moveLeft() {
        if (getTranslateX() - 5 >= 0) {  // Prevent moving past the left edge
            setTranslateX(getTranslateX() - 5);
        }
    }

    public void moveRight() {
        if (getTranslateX() + 5 <= GAME_WIDTH) {  // Prevent moving past the right edge
            setTranslateX(getTranslateX() + 5);
        }
    }

    // Set the score for the player fish and trigger growth
    public void setScore(int score) {
        this.score = score;
        grow();  // Call grow to change the fish size based on score
    }

    // Get the score of the player fish
    public int getScore() {
        return score;
    }

    // Increment the number of fish eaten
    public void incrementFishEaten() {
        fishEaten++;
    }

    // Get the number of fish eaten
    public int getFishEaten() {
        return fishEaten;
    }

    // Override the abstract move method (this is already handled by player controls)
    @Override
    public void move(double padding) {
        // The movement of the player fish is handled by keyboard input.
        // Therefore, no need for any additional logic here.
    }
}















