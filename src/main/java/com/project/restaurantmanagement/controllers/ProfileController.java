package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.user.UserDAOImpl;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.models.user.VIPUserModel;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ProfileController {
    @FXML
    public Label usernameField;

    @FXML
    public TextField firstNameField;

    @FXML
    public TextField lastNameField;

    @FXML
    public PasswordField oldPasswordField;

    @FXML
    public PasswordField newPasswordField;

    @FXML
    public PasswordField newPassword2Field;

    @FXML
    public TextField emailField;

    @FXML
    public Label errorLabel;

    @FXML
    protected void initialize() {
        UserModel user = UserSession.getUserSession().getUser();

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        usernameField.setText(user.getUsername());

        if (user.isVIP()) {
            emailField.setText(((VIPUserModel) user).getEmail());
        }
    }

    @FXML
    protected void onDashboardOptionClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("dashboard-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    protected void onSubmitButtonClick(MouseEvent event) {
        UserModel user = UserSession.getUserSession().getUser();

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showError("First name and last name are required");
            return;
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);

        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String newPassword2 = newPassword2Field.getText();

        if (!oldPassword.isEmpty()) {
            if (!oldPassword.equals(user.getPassword())) {
                showError("Old password is incorrect");
                return;
            } else if (newPassword.isEmpty() || newPassword2.isEmpty()) {
                showError("New password is required");
                return;
            } else if (!newPassword.equals(newPassword2)) {
                showError("New passwords do not match");
                return;
            } else if (newPassword.equals(oldPassword)) {
                showError("New password cannot be the same as the old password");
                return;
            } else {
                user.setPassword(newPassword);
            }
        }

        String email = emailField.getText();

        if (!email.isEmpty()) {
            if (!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
                showError("Email is not in correct format");
                return;
            } else {
                user.setVIP(true);
            }
        }

        updateUser(event, user);
    }

    private void updateUser(MouseEvent event, UserModel user) {
        try {
            UserDAOImpl userDAO = new UserDAOImpl();

            // update user based on VIP status
            if (user.isVIP()) {
                VIPUserModel vipUser = new VIPUserModel(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUsername(),
                        user.getPassword(),
                        user.isVIP(),
                        emailField.getText()
                );

                userDAO.updateVIPUser(vipUser);

                // update user session
                UserSession.getUserSession().setUser(vipUser);
            } else {
                userDAO.updateUser(user);

                // update user session
                UserSession.getUserSession().setUser(user);
            }

            errorLabel.setVisible(false);

            // redirect to dashboard
            FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("dashboard-view.fxml"));
            Pane pane = fxmlLoader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();

            currentScene.setRoot(pane);
        } catch (Exception e) {
            errorLabel.setText("An error occurred while updating the user");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
