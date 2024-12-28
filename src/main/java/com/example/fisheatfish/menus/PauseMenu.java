package com.example.fisheatfish.menus;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PauseMenu {
    private Group pausePanel; // Group to hold the pause menu UI elements

    public void showPauseMenu(Group root) {
        pausePanel = new Group();

        // Create the "Game Paused" label
        Label pauseLabel = new Label("Game Paused");
        pauseLabel.setFont(new Font("Arial", 30));
        pauseLabel.setTextFill(Color.WHITE);
        pauseLabel.setTranslateX(300);
        pauseLabel.setTranslateY(200);
        pausePanel.getChildren().add(pauseLabel);

        // Create the "Press P to Resume" label
        Label resumeLabel = new Label("Press P to Resume");
        resumeLabel.setFont(new Font("Arial", 20));
        resumeLabel.setTextFill(Color.WHITE);
        resumeLabel.setTranslateX(300);
        resumeLabel.setTranslateY(250);
        pausePanel.getChildren().add(resumeLabel);

        // Add the pause menu to the root
        root.getChildren().add(pausePanel);
    }

    public void hidePauseMenu(Group root) {
        if (pausePanel != null) {
            root.getChildren().remove(pausePanel);
            pausePanel = null;
        }
    }
}
