package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.user.UserDAOImpl;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.utils.UserSession;
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

public class LoginController {
    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Label userNotFoundField;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        UserDAOImpl userDAO = new UserDAOImpl();

        try {
            // Get the user by username and password
            UserModel user = userDAO.getUserByUsernameAndPassword(usernameField.getText(), passwordField.getText());

            // Set the user session for global access
            // This determines if the user is logged in or not
            UserSession.getUserSession().setUser(user);

            FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("dashboard-view.fxml"));
            Pane pane = fxmlLoader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();

            currentScene.setRoot(pane);
        } catch (Exception e) {
            userNotFoundField.setVisible(true);
        }
    }

    @FXML
    protected void onGoBackButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("welcome-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }
}
