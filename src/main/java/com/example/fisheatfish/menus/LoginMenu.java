package com.example.fisheatfish.menus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.fisheatfish.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginMenu {

    public void show(Stage stage) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> login(stage, usernameField.getText(), passwordField.getText()));

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> showSignUpMenu(stage));  // Show the sign-up form

        Button exitGameButton = new Button("Exit Game");
        exitGameButton.setOnAction(e -> System.exit(0));  // Exit the application

        vbox.getChildren().addAll(usernameField, passwordField, loginButton, signUpButton, exitGameButton);

        Scene scene = new Scene(vbox, 300, 250);  // Increase size to fit all buttons
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void login(Stage stage, String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(stage, "Error", "All fields are required", AlertType.ERROR);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

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





















