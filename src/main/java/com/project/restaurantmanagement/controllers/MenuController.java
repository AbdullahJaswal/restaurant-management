package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.cart.CartDAOImpl;
import com.project.restaurantmanagement.dao.food.FoodDAOImpl;
import com.project.restaurantmanagement.models.cart.CartModel;
import com.project.restaurantmanagement.models.cart.CartStatus;
import com.project.restaurantmanagement.models.food.FoodModel;
import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;

public class MenuController {
    @FXML
    private Label burritoQuantity;
    @FXML
    private Label friesQuantity;
    @FXML
    private Label sodaQuantity;
    @FXML
    private Label menuQuantity;

    @FXML
    private ImageView burritoImage;
    @FXML
    private ImageView friesImage;
    @FXML
    private ImageView sodaImage;
    @FXML
    private ImageView mealImage;

    @FXML
    public HBox mealBox;

    @FXML
    public void initialize() {
        UserModel user = UserSession.getUserSession().getUser();

        if (user.isVIP()) {
            mealBox.setVisible(true);
        }

        CartDAOImpl cartDAO = new CartDAOImpl();
        FoodDAOImpl foodDAO = new FoodDAOImpl();

        try {
            // Get the latest active cart for the user
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);

            // If the cart is not null, set the quantity of each item in the cart
            if (cart != null) {
                burritoQuantity.setText(String.valueOf(cart.getBurritoCount()));
                friesQuantity.setText(String.valueOf(cart.getFriesCount()));
                sodaQuantity.setText(String.valueOf(cart.getSodaCount()));
                menuQuantity.setText(String.valueOf(cart.getMealCount()));
            }

            HashMap<OrderItem, FoodModel> foods = foodDAO.getAllFoods();

            // Set the images for each food item
            for (OrderItem orderItem : foods.keySet()) {
                FoodModel food = foods.get(orderItem);

                String imageFile = food.getImageFile();
                String imagePath = String.valueOf(RestaurantApplication.class.getResource(
                        "/com/project/restaurantmanagement/assets/" + imageFile
                ));

                switch (orderItem) {
                    case Burrito -> burritoImage.setImage(new Image(imagePath));
                    case Fries -> friesImage.setImage(new Image(imagePath));
                    case Soda -> sodaImage.setImage(new Image(imagePath));
                    case Meal -> mealImage.setImage(new Image(imagePath));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Label getLabelForButton(String buttonId) {
        return switch (buttonId) {
            case "burritoPlusButton", "burritoMinusButton" -> burritoQuantity;
            case "friesPlusButton", "friesMinusButton" -> friesQuantity;
            case "sodaPlusButton", "sodaMinusButton" -> sodaQuantity;
            case "menuPlusButton", "menuMinusButton" -> menuQuantity;
            default -> null;
        };
    }

    @FXML
    protected void onPlusButtonClick(MouseEvent event) {
        Node node = (Node) event.getSource();
        String buttonId = node.getId();
        Label label = getLabelForButton(buttonId);

        if (label != null) {
            int quantity = Integer.parseInt(label.getText());

            label.setText(String.valueOf(quantity + 1));
        }
    }

    @FXML
    protected void onMinusButtonClick(MouseEvent event) {
        Node node = (Node) event.getSource();
        String buttonId = node.getId();
        Label label = getLabelForButton(buttonId);

        if (label != null) {
            int quantity = Integer.parseInt(label.getText());

            if (quantity > 0) {
                label.setText(String.valueOf(quantity - 1));
            }
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
    protected void onAddToCartButtonClick(MouseEvent event) throws Exception {
        UserModel user = UserSession.getUserSession().getUser();

        CartDAOImpl cartDAO = new CartDAOImpl();

        try {
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);

            // If the cart is not null, update the cart with the new quantities
            if (cart.getUser() != null) {
                cart.setBurritoCount(Integer.parseInt(burritoQuantity.getText()));
                cart.setFriesCount(Integer.parseInt(friesQuantity.getText()));
                cart.setSodaCount(Integer.parseInt(sodaQuantity.getText()));
                cart.setMealCount(Integer.parseInt(menuQuantity.getText()));

                cartDAO.updateCart(cart);
            } else {
                // If the cart is null, create a new cart with the quantities
                cartDAO.createCart(new CartModel(
                        user,
                        Integer.parseInt(burritoQuantity.getText()),
                        Integer.parseInt(friesQuantity.getText()),
                        Integer.parseInt(sodaQuantity.getText()),
                        Integer.parseInt(menuQuantity.getText()),
                        CartStatus.ACTIVE
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("cart-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }
}
