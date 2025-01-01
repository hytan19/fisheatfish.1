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
import com.example.fisheatfish.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> backToLoginMenu(stage));  // Go back to login screen

        vbox.getChildren().addAll(usernameField, nameField, passwordField, confirmPasswordField, signUpButton, backButton);

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












