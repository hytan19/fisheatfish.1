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
import java.sql.SQLException;

public class SignUpMenu {
    public void show(Stage stage) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> signUp(stage, usernameField.getText(), nameField.getText(), passwordField.getText(), confirmPasswordField.getText()));

        Button backToMenuButton = new Button("Back to Menu");
        backToMenuButton.setOnAction(e -> backToMenu(stage));

        vbox.getChildren().addAll(usernameField, nameField, passwordField, confirmPasswordField, signUpButton, backToMenuButton);

        Scene scene = new Scene(vbox, 300, 250);
        stage.setScene(scene);
        stage.setTitle("Sign Up");
        stage.show();
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

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String query = "INSERT INTO users (username, name, password) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, password); // Password should be hashed in real scenarios
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert(stage, "Success", "Sign Up Successful", AlertType.INFORMATION);
                    } else {
                        showAlert(stage, "Error", "Sign Up Failed", AlertType.ERROR);
                    }
                    backToMenu(stage);
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

    private void backToMenu(Stage stage) {
        new MainMenu().show(stage);
    }

    private void showAlert(Stage stage, String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}







