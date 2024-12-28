package com.example.fisheatfish.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class Fish extends Circle {

    protected double speed;
    protected final double GAME_WIDTH = 800;  // Changed to protected
    protected final double GAME_HEIGHT = 565;  // Changed to protected

    // Enumeration for Fish Types
    public enum FishType {
        SMALL, MEDIUM, LARGE, GIANT
    }

    // Constructor that sets properties based on fish type
    public Fish(FishType type) {
        super(getRadiusByType(type), getColorByType(type));
        this.speed = getSpeedByType(type);
    }

    public double getSpeed() {
        return speed;
    }

    // Get radius based on fish type
    protected static double getRadiusByType(FishType type) {
        return switch (type) {
            case SMALL -> 10;
            case MEDIUM -> 20;
            case LARGE -> 30;
            case GIANT -> 40;
        };
    }

    // Get color based on fish type
    protected static Color getColorByType(FishType type) {
        return switch (type) {
            case SMALL -> Color.GREEN;
            case MEDIUM -> Color.YELLOW;
            case LARGE -> Color.ORANGE;
            case GIANT -> Color.RED;
        };
    }

    // Get speed based on fish type
    protected static double getSpeedByType(FishType type) {
        return switch (type) {
            case SMALL -> 1.5;
            case MEDIUM -> 2.5;
            case LARGE -> 3.5;
            case GIANT -> 4.5;
        };
    }

    // Abstract method for movement behavior that will be implemented by subclasses
    public abstract void move(double padding);

    // Get point value based on fish type
    public int getPointValue() {
        return switch (this.getType()) {
            case SMALL -> 3;   // Reduced points for small fish
            case MEDIUM -> 6;  // Reduced points for medium fish
            case LARGE -> 9;   // Reduced points for large fish
            case GIANT -> 12;  // Reduced points for giant fish
        };
    }

    public FishType getType() {
        // Return the type of fish based on its radius
        if (getRadius() == 10) {
            return FishType.SMALL;
        } else if (getRadius() == 20) {
            return FishType.MEDIUM;
        } else if (getRadius() == 30) {
            return FishType.LARGE;
        } else if (getRadius() == 40) {
            return FishType.GIANT;
        }
        return FishType.SMALL;  // Default
    }

    // Method to ensure fish stays within screen boundaries, used by subclasses for movement
    protected void checkBounds(double padding) {
        if (getTranslateX() < padding) {
            setTranslateX(GAME_WIDTH - padding);  // Respawn at the right side
            setTranslateY(Math.random() * (GAME_HEIGHT - 2 * padding) + padding);  // Random Y position
        } else if (getTranslateX() > GAME_WIDTH - padding) {
            setTranslateX(padding);  // Respawn at the left side
            setTranslateY(Math.random() * (GAME_HEIGHT - 2 * padding) + padding);  // Random Y position
        }
    }
}

