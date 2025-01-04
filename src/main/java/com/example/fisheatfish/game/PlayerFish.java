package com.example.fisheatfish.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PlayerFish extends Fish {

    private int score = 0;      // Track player's score
    private int fishEaten = 0;  // Track the number of fish eaten
    private ImageView fishImageView;  // ImageView for displaying the player fish
    private Image fishImageRight; // Image when facing right
    private Image fishImageLeft;  // Image when facing left

    // Constructor for PlayerFish with a distinct color (blue) and the image
    public PlayerFish() {
        super(FishType.SMALL);  // Default to small fish
        setFill(Color.BLUE);    // Set player fish color to blue

        // Set initial position of the fish
        setTranslateX(400); // Set X to the center of the screen
        setTranslateY(300); // Set Y to the center of the screen

        // Initialize the images for both directions
        fishImageRight = new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\playerfish\\moveright.png");  // Image for moving right
        fishImageLeft = new Image("C:\\Users\\User\\IdeaProjects\\fisheatfish\\fisheatfish\\src\\main\\resources\\images\\fish\\playerfish\\moveleft.png");   // Image for moving left

        // Initialize the ImageView with the right-facing fish image
        fishImageView = new ImageView(fishImageRight);

        // Set the initial position of the fish image, centered based on the PlayerFish
        fishImageView.setX(getTranslateX() - getRadius());
        fishImageView.setY(getTranslateY() - getRadius());

        // Set the size of the image based on the radius
        fishImageView.setFitWidth(getRadius() * 2);
        fishImageView.setFitHeight(getRadius() * 2);
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

        // Update the image size after growing the fish
        fishImageView.setFitWidth(getRadius() * 2);
        fishImageView.setFitHeight(getRadius() * 2);
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

    // Reset the number of fish eaten
    public void resetFishEaten() {
        fishEaten = 0;
    }

    // Override the abstract move method (this is already handled by player controls)
    @Override
    public void move(double padding) {
        // The movement of the player fish is handled by keyboard input.
        // Therefore, no need for any additional logic here.
    }

    // Getter for the ImageView, so you can add it to the scene
    public ImageView getFishImageView() {
        return fishImageView;
    }

    // Switch to the left-facing image
    public void setLeftImage() {
        fishImageView.setImage(fishImageLeft);
    }

    // Switch to the right-facing image
    public void setRightImage() {
        fishImageView.setImage(fishImageRight);
    }
}
























