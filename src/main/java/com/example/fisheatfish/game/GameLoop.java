package com.example.fisheatfish.game;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameLoop {
    private Pane root;

    public GameLoop(Pane root) {
        this.root = root;
    }

    public void start() {
        Scene scene = new Scene(root, 800, 600);
        Stage gameStage = new Stage();
        gameStage.setTitle("Fish Eat Fish Game");
        gameStage.setScene(scene);
        gameStage.show();

        // Initialize and start game mechanics
        initializeGame();
    }

    private void initializeGame() {
        // Initialize your game mechanics, setup player, enemies, etc.
        // This is where you'll set up your game logic, movement, and interactions
    }
}

