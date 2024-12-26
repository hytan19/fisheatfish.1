package com.example.fisheatfish.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerFish extends Circle {

    private final double GAME_WIDTH = 600;
    private final double GAME_HEIGHT = 400;

    public PlayerFish() {
        super(20, Color.BLUE); // Player's fish is a blue circle with radius 20
        setTranslateX(300);
        setTranslateY(200);
    }

    public void moveUp() {
        if (getTranslateY() - 5 >= 0) { // Prevent moving above the top edge
            setTranslateY(getTranslateY() - 5);
        }
    }

    public void moveDown() {
        if (getTranslateY() + 5 <= GAME_HEIGHT) { // Prevent moving below the bottom edge
            setTranslateY(getTranslateY() + 5);
        }
    }

    public void moveLeft() {
        if (getTranslateX() - 5 >= 0) { // Prevent moving past the left edge
            setTranslateX(getTranslateX() - 5);
        }
    }

    public void moveRight() {
        if (getTranslateX() + 5 <= GAME_WIDTH) { // Prevent moving past the right edge
            setTranslateX(getTranslateX() + 5);
        }
    }
}










