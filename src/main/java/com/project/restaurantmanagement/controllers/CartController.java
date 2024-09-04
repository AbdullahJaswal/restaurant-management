package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.cart.CartDAOImpl;
import com.project.restaurantmanagement.dao.food.FoodDAOImpl;
import com.project.restaurantmanagement.models.cart.CartModel;
import com.project.restaurantmanagement.models.cart.CartStatus;
import com.project.restaurantmanagement.models.food.*;
import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class CartController {
    @FXML
    public GridPane cartItems;

    @FXML
    public Label total;

    @FXML
    public HBox editCartButton;

    @FXML
    public HBox cancelCartButton;

    @FXML
    public HBox payCartButton;

    @FXML
    public Label prepTime;

    @FXML
    public void initialize() {
        // Load Cart
        refreshCart();
    }

    @FXML
    protected void onDashboardOptionClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("dashboard-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    public void onEditCartButtonClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("menu-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    public void onCancelButtonClick(MouseEvent event) throws IOException {
        UserModel user = UserSession.getUserSession().getUser();

        CartDAOImpl cartDAO = new CartDAOImpl();

        // Sets the user cart status to CANCELED
        try {
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);

            cart.setStatus(CartStatus.CANCELED);
            cartDAO.updateCart(cart);

            cancelCartButton.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("cart-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    public void onPaymentButtonClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("payment-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    public void refreshCart() {
        UserModel user = UserSession.getUserSession().getUser();

        CartDAOImpl cartDAO = new CartDAOImpl();
        FoodDAOImpl foodDAO = new FoodDAOImpl();

        try {
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);

            // Clear existing rows
            cartItems.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

            int rowIndex = 1;

            HashMap<OrderItem, FoodModel> foods = foodDAO.getAllFoods();

            // Add food rows
            if (cart.getBurritoCount() > 0) {
                BurritoModel burrito = (BurritoModel) foods.get(OrderItem.Burrito);
                addFoodRow(rowIndex++, burrito, cart.getBurritoCount());
            }

            if (cart.getFriesCount() > 0) {
                FriesModel fries = (FriesModel) foods.get(OrderItem.Fries);
                addFoodRow(rowIndex++, fries, cart.getFriesCount());
            }

            if (cart.getSodaCount() > 0) {
                SodaModel soda = (SodaModel) foods.get(OrderItem.Soda);
                addFoodRow(rowIndex++, soda, cart.getSodaCount());
            }

            if (cart.getMealCount() > 0) {
                MealModel meal = (MealModel) foods.get(OrderItem.Meal);
                addFoodRow(rowIndex++, meal, cart.getMealCount());
            }

            // Total and Prep Time row
            prepTime.setText(cart.getPrepTime(foods) + " minute(s)");
            total.setText("$" + String.format("%.2f", cart.getCartTotal(foods)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFoodRow(int rowIndex, FoodModel food, int count) {
        // Show buttons
        editCartButton.setVisible(true);
        cancelCartButton.setVisible(true);
        payCartButton.setVisible(true);

        // Add food row items
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/project/restaurantmanagement/assets/" + food.getImageFile())).toExternalForm()));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);

        Label itemLabel = new Label(food.getName().toString());
        itemLabel.getStyleClass().add("b");

        HBox itemBox = new HBox(10, imageView, itemLabel);
        itemBox.setSpacing(10);
        itemBox.setAlignment(Pos.CENTER_LEFT);

        Label quantityLabel = new Label(String.valueOf(count));
        quantityLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("$" + String.format("%.2f", food.getPrice()));
        priceLabel.setAlignment(Pos.CENTER);

        Label totalLabel = new Label("$" + String.format("%.2f", food.getPrice() * count));
        totalLabel.setAlignment(Pos.CENTER_RIGHT);

        cartItems.add(itemBox, 0, rowIndex);
        cartItems.add(quantityLabel, 1, rowIndex);
        cartItems.add(priceLabel, 2, rowIndex);
        cartItems.add(totalLabel, 3, rowIndex);
    }
}
