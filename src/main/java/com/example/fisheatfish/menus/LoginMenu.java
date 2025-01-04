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

import com.example.fisheatfish.utils.DatabaseConnection;
import com.example.fisheatfish.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginMenu {

    public void show(Stage stage) {
        // Create the VBox layout for the login screen
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        // Background styling
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title label for the login screen
        Label titleLabel = new Label("Fish Eat Fish");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        titleLabel.setStyle("-fx-padding: 10px 0px 10px 0px;");

        // Create the username and password fields with placeholder text and set a maximum width
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-background-radius: 15; -fx-padding: 10; -fx-max-width: 250px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-radius: 15; -fx-padding: 10; -fx-max-width: 250px;");

        // Create the buttons with some styling
        Button loginButton = new Button("Login");
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        loginButton.setOnAction(e -> login(stage, usernameField.getText(), passwordField.getText()));

        Button signUpButton = new Button("Sign Up");
        signUpButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        signUpButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        signUpButton.setOnAction(e -> showSignUpMenu(stage));

        Button exitGameButton = new Button("Exit Game");
        exitGameButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exitGameButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
        exitGameButton.setOnAction(e -> System.exit(0));

        // Add all components to the VBox
        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, signUpButton, exitGameButton);

        // Create and display the scene
        Scene scene = new Scene(vbox, 350, 350);  // Adjusted size for better layout
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void login(Stage stage, String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(stage, "Error", "All fields are required", AlertType.ERROR);
            return;
        }

        // Hash the entered password
        String hashedPassword = PasswordHasher.hashPassword(password);

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, hashedPassword);  // Compare the hashed password

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("id");  // Get the userId
                        MainMenu.loginSuccessful(userId);  // Pass userId to MainMenu.loginSuccessful()
                        showAlert(stage, "Success", "Login Successful", AlertType.INFORMATION);
                        showMainMenu(stage);  // Show the main menu after successful login
                    } else {
                        showAlert(stage, "Error", "Invalid username or password", AlertType.ERROR);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(stage, "Error", "Login Failed", AlertType.ERROR);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(stage, "Error", "Database connection error", AlertType.ERROR);
        }
    }

    private void showMainMenu(Stage stage) {
        // This method shows the MainMenu after a successful login
        new MainMenu().show(stage);  // Show the main menu screen
    }

    private void showSignUpMenu(Stage stage) {
        new SignUpMenu().show(stage); // Show the Sign Up menu
    }

    private void showAlert(Stage stage, String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}























