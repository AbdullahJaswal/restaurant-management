package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.user.UserDAOImpl;
import com.project.restaurantmanagement.models.user.UserModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SignUpController {
    @FXML
    public TextField firstNameField;

    @FXML
    public TextField lastNameField;

    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public PasswordField password2Field;

    @FXML
    public Label errorLabel;

    @FXML
    protected void onSignUpButtonClick(ActionEvent event) throws Exception {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || password2Field.getText().isEmpty()) {
            showError("Please fill out all fields.");
            return;
        } else if (!passwordField.getText().equals(password2Field.getText())) {
            showError("Passwords do not match.");
            return;
        }

        UserModel user = new UserModel(firstNameField.getText(), lastNameField.getText(), usernameField.getText(), passwordField.getText(), false);
        UserDAOImpl userDAO = new UserDAOImpl();

        try {
            // Add user to database
            userDAO.addUser(user);

            FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("signup-success-view.fxml"));
            Pane pane = fxmlLoader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();

            currentScene.setRoot(pane);
        } catch (Exception e) {
            showError("Username already exists. Please choose a different username and try again.");
        }
    }

    @FXML
    protected void onGoBackButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("welcome-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
