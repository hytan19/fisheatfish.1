package com.example.fisheatfish.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EnemyFish extends Fish {

    private ImageView fishImageView;  // ImageView for displaying the enemy fish

    // Constructor for EnemyFish that accepts fish type
    public EnemyFish(FishType type) {
        super(type);  // Call the parent class constructor to set up basic properties (radius, color, speed)

        // Set up the image based on the fish type
        Image fishImage = getFishImageByType(type);
        fishImageView = new ImageView(fishImage);

        // Set the initial position and size of the image based on the fish's radius
        fishImageView.setX(getTranslateX() - getRadius());
        fishImageView.setY(getTranslateY() - getRadius());
        fishImageView.setFitWidth(getRadius() * 2);
        fishImageView.setFitHeight(getRadius() * 2);
    }

    // Method to get the appropriate image for each type of enemy fish
    private Image getFishImageByType(FishType type) {
        return switch (type) {
            case SMALL -> new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\enemyfish\\smallfish.png");  // Path to the small fish image
            case MEDIUM -> new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\enemyfish\\mediumfish.png");  // Path to the medium fish image
            case LARGE -> new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\enemyfish\\bigfish.png");  // Path to the large fish image
            case GIANT -> new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\enemyfish\\giantfish.png");  // Path to the giant fish image
        };
    }

    // Method to move the enemy fish (to be implemented as needed)
    @Override
    public void move(double padding) {
        // Implement movement logic here, for example:
        setTranslateX(getTranslateX() - getSpeed());
        checkBounds(padding);  // Ensure the fish stays within bounds
        fishImageView.setX(getTranslateX() - getRadius());
        fishImageView.setY(getTranslateY() - getRadius());
    }

    // Getter for the ImageView to add it to the scene
    public ImageView getFishImageView() {
        return fishImageView;
    }
}





















