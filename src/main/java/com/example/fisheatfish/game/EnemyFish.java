package com.example.fisheatfish.game;

public class EnemyFish extends Fish {

    private boolean movingLeft;  // New property to track movement direction

    // Constructor that sets properties based on fish type
    public EnemyFish(FishType type) {
        super(type);  // Call the Fish constructor to set speed, radius, and color
        this.movingLeft = true;  // Set movingLeft to true by default for leftward movement
    }

    // Getter and setter for movingLeft
    public boolean isMovingLeft() {
        return movingLeft;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    // Override the move method to implement movement behavior
    @Override
    public void move(double padding) {
        double newX = getTranslateX();

        // Moving left or right based on the state of movingLeft
        if (movingLeft) {
            newX -= speed;  // Move to the left (negative direction)
        } else {
            newX += speed;  // Move to the right (positive direction)
        }

        // Check if the fish has moved off the screen (wrap around logic)
        if (newX < padding) {
            setTranslateX(GAME_WIDTH - padding);  // Respawn at the right side of the screen
            setTranslateY(Math.random() * (GAME_HEIGHT - 2 * padding) + padding);  // Random Y position
        } else if (newX > GAME_WIDTH - padding) {
            setTranslateX(padding);  // Respawn at the left side of the screen
            setTranslateY(Math.random() * (GAME_HEIGHT - 2 * padding) + padding);  // Random Y position
        } else {
            setTranslateX(newX);  // Update the X position
        }
    }
}




















