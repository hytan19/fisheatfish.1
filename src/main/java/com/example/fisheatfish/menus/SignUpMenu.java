package com.example.fisheatfish.menus;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.application.Platform;

import com.example.fisheatfish.utils.DatabaseConnection;
import com.example.fisheatfish.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpMenu {

    public void show(Stage stage) {
        // Create the VBox layout for the sign-up screen
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));


        // Background styling (same as login)
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title label for the sign-up screen
        Label titleLabel = new Label("Fish Eat Fish - Sign Up");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        titleLabel.setStyle("-fx-padding: 10px 0px 10px 0px;");

        // Create the username, name, and password fields with placeholder text and set a consistent width
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        usernameField.setMaxWidth(250);  // Set preferred width for consistency

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        nameField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        nameField.setMaxWidth(250);  // Set preferred width for consistency

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        passwordField.setMaxWidth(250);  // Set preferred width for consistency

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        confirmPasswordField.setMaxWidth(250);  // Set preferred width for consistency

        // Create the buttons with the same styling as login
        Button signUpButton = new Button("Sign Up");
        signUpButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        signUpButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        signUpButton.setOnAction(e -> signUp(stage, usernameField.getText(), nameField.getText(), passwordField.getText(), confirmPasswordField.getText()));

        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        backButton.setOnAction(e -> backToLoginMenu(stage));  // Go back to login screen

        // Add all components to the VBox
        vbox.getChildren().addAll(titleLabel, usernameField, nameField, passwordField, confirmPasswordField, signUpButton, backButton);

        // Force layout computation to ensure bounds are available
        vbox.layout();

        // Debugging: Print the bounds of VBox and its children
        System.out.println("Forced Layout - VBox Bounds: " + vbox.getBoundsInParent());
        for (javafx.scene.Node child : vbox.getChildren()) {
            System.out.println("Forced Layout - Child Bounds: " + child.getClass().getSimpleName() + " -> " + child.getBoundsInParent());
        }

        // Create and display the scene (same size as login)
        Scene scene = new Scene(vbox, 350, 350);  // Adjusted size for better layout
        stage.setScene(scene);
        stage.setTitle("Sign Up");
        stage.show();

        // Debugging: Delayed bounds check to confirm rendering is complete
        Platform.runLater(() -> {
            System.out.println("Post-Rendering - VBox Bounds: " + vbox.getBoundsInParent());
            for (javafx.scene.Node child : vbox.getChildren()) {
                System.out.println("Post-Rendering - Child Bounds: " + child.getClass().getSimpleName() + " -> " + child.getBoundsInParent());
            }
        });

        // Optionally, request layout pass to update layout
        vbox.requestLayout();
    }

    private void signUp(Stage stage, String username, String name, String password, String confirmPassword) {
        if (username.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(stage, "Error", "All fields are required", AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(stage, "Error", "Passwords do not match", AlertType.ERROR);
            return;
        }

        // Check if username is already taken
        if (isUsernameTaken(username)) {
            showAlert(stage, "Error", "Username is already taken. Please choose a different username.", AlertType.ERROR);
            return;
        }

        // Hash the password before storing it
        String hashedPassword = PasswordHasher.hashPassword(password);

        if (hashedPassword == null) {
            showAlert(stage, "Error", "Error hashing password", AlertType.ERROR);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String query = "INSERT INTO users (username, name, password) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, hashedPassword);  // Store the hashed password
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert(stage, "Success", "Sign Up Successful", AlertType.INFORMATION);
                        backToLoginMenu(stage);  // Redirect to login after successful sign-up
                    } else {
                        showAlert(stage, "Error", "Sign Up Failed", AlertType.ERROR);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(stage, "Error", "Sign Up Failed", AlertType.ERROR);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(stage, "Error", "Database connection error", AlertType.ERROR);
        }
    }

    // Check if the username already exists in the database
    private boolean isUsernameTaken(String username) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM users WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    return resultSet.next();  // If a result is returned, the username exists
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;  // Return false if no username is found
    }

    private void backToLoginMenu(Stage stage) {
        new LoginMenu().show(stage);  // Go back to login after sign-up
    }

    private void showAlert(Stage stage, String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
















