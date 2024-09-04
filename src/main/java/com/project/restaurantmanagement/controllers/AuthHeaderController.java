package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.cart.CartDAOImpl;
import com.project.restaurantmanagement.dao.user.UserDAOImpl;
import com.project.restaurantmanagement.models.cart.CartModel;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.models.user.VIPUserModel;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class AuthHeaderController {
    @FXML
    public Label creditsLabel;

    @FXML
    public Label userLabel;

    @FXML
    public Label cartSizeLabel;

    @FXML
    public void initialize() {
        UserModel user = UserSession.getUserSession().getUser();

        // VIP User details
        String usrLabel = user.getUsername();
        if (user.isVIP()) {
            usrLabel += " (VIP)";

            UserDAOImpl userDAO = new UserDAOImpl();

            try {
                VIPUserModel vipUser = (VIPUserModel) userDAO.getUserByUsername(user.getUsername());

                creditsLabel.setVisible(true);
                creditsLabel.setText("Credits: " + vipUser.getCredit());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userLabel.setText(usrLabel);

        // Cart details
        try {
            CartDAOImpl cartDAO = new CartDAOImpl();
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);

            cartSizeLabel.setText("(" + String.valueOf(cart.getCartSize()) + ")");
        } catch (Exception e) {
            cartSizeLabel.setText("0");
        }
    }

    @FXML
    protected void onCartClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("cart-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    protected void onProfileClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("profile-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    protected void onLogoutClick(MouseEvent event) throws IOException {
        UserSession.getUserSession().clearSession();

        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("welcome-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }
}
