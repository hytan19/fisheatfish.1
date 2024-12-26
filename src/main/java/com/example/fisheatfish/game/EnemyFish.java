package com.example.fisheatfish.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class EnemyFish extends Circle {

    private double speed;

    public enum FishType {
        SMALL, MEDIUM, LARGE, GIANT
    }

    public EnemyFish(FishType type) {
        super(getRadiusByType(type), getColorByType(type));
        this.speed = getSpeedByType(type);
    }

    public double getSpeed() {
        return speed;
    }

    // Get radius based on fish type
    private static double getRadiusByType(FishType type) {
        return switch (type) {
            case SMALL -> 10;
            case MEDIUM -> 20;
            case LARGE -> 30;
            case GIANT -> 40;
        };
    }

    // Get color based on fish type
    private static Color getColorByType(FishType type) {
        return switch (type) {
            case SMALL -> Color.GREEN;
            case MEDIUM -> Color.YELLOW;
            case LARGE -> Color.ORANGE;
            case GIANT -> Color.RED;
        };
    }

    // Get speed based on fish type
    private static double getSpeedByType(FishType type) {
        return switch (type) {
            case SMALL -> 1.5;
            case MEDIUM -> 2.5;
            case LARGE -> 3.5;
            case GIANT -> 4.5;
        };
    }

    // Ensure the fish stays within game boundaries when moving
    public void move() {
        double newX = getTranslateX() - speed;
        if (newX < 0) {
            setTranslateX(600); // Respawn at the right side of the screen
            setTranslateY(Math.random() * 400); // Random Y position within the screen
        } else {
            setTranslateX(newX);
        }
    }
}











