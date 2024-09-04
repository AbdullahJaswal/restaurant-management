package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.cart.CartDAOImpl;
import com.project.restaurantmanagement.dao.food.FoodDAOImpl;
import com.project.restaurantmanagement.dao.order.OrderDAOImpl;
import com.project.restaurantmanagement.dao.user.UserDAOImpl;
import com.project.restaurantmanagement.models.cart.CartModel;
import com.project.restaurantmanagement.models.cart.CartStatus;
import com.project.restaurantmanagement.models.food.FoodModel;
import com.project.restaurantmanagement.models.food.FriesModel;
import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.models.order.OrderModel;
import com.project.restaurantmanagement.models.order.OrderStatus;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.models.user.VIPUserModel;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;

public class PaymentController {
    @FXML
    public Label prepTime;

    @FXML
    public Label total;

    @FXML
    public TextField cardNumber;

    @FXML
    public TextField expiryDate;

    @FXML
    public TextField cvv;

    @FXML
    public TextField placedAt;

    @FXML
    public Label redeemCreditsLabel;

    @FXML
    public TextField redeemCreditsValue;

    @FXML
    public Label errorLabel;

    private VIPUserModel vipUser;

    @FXML
    public void initialize() {
        UserModel user = UserSession.getUserSession().getUser();

        // If user is VIP, show redeem credits field
        if (user.isVIP()) {
            UserDAOImpl userDAO = new UserDAOImpl();

            try {
                this.vipUser = (VIPUserModel) userDAO.getUserByUsername(user.getUsername());

                redeemCreditsLabel.setVisible(true);
                redeemCreditsValue.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FoodDAOImpl foodDAO = new FoodDAOImpl();
        CartDAOImpl cartDAO = new CartDAOImpl();

        // Get latest active cart by user
        try {
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);
            HashMap<OrderItem, FoodModel> foods = foodDAO.getAllFoods();

            prepTime.setText(cart.getPrepTime(foods) + " minute(s)");
            total.setText("$" + String.format("%.2f", cart.getCartTotal(foods)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditCartButtonClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("menu-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    public void onOrderButtonClick(MouseEvent event) throws IOException {
        errorLabel.setText("");

        UserModel user = UserSession.getUserSession().getUser();

        FoodDAOImpl foodDAO = new FoodDAOImpl();
        CartDAOImpl cartDAO = new CartDAOImpl();

        try {
            CartModel cart = cartDAO.getLatestActiveCartByUser(user);
            HashMap<OrderItem, FoodModel> foods = foodDAO.getAllFoods();

            // Validate credit card details
            String validationCheck = OrderModel.validateCreditCardDetails(
                    cardNumber.getText(),
                    expiryDate.getText(),
                    cvv.getText()
            );

            if (validationCheck != null) {
                errorLabel.setText(validationCheck);
                return;
            }

            HashMap<OrderItem, Integer> orderItems = new HashMap<>();
            orderItems.put(OrderItem.Burrito, cart.getBurritoCount());
            orderItems.put(OrderItem.Fries, cart.getFriesCount());
            orderItems.put(OrderItem.Soda, cart.getSodaCount());
            orderItems.put(OrderItem.Meal, cart.getMealCount());

            // placedAt is in format HH:MM
            LocalTime orderTime = LocalTime.parse(placedAt.getText());
            LocalTime prepTime = orderTime.plusMinutes(cart.getPrepTime(foods));

            OrderModel order;
            OrderDAOImpl orderDAO = new OrderDAOImpl();

            // If user is VIP, redeem credits based on the value entered
            if (user.isVIP()) {
                int redeemCredits = redeemCreditsValue.getText().isEmpty() ? 0 : Integer.parseInt(redeemCreditsValue.getText());

                if (redeemCredits > vipUser.getCredit()) {
                    errorLabel.setText("You do not have enough credits to redeem.");
                    return;
                } else if (redeemCredits < 0) {
                    errorLabel.setText("Invalid redeem credits value.");
                    return;
                }

                double amountPayed = cart.getCartTotal(foods) - ((double) redeemCredits / 100);

                order = new OrderModel(
                        vipUser,
                        OrderStatus.AWAIT_FOR_COLLECTION,
                        orderTime,
                        prepTime,
                        null,
                        orderItems,
                        cart.getCartTotal(foods),
                        amountPayed,
                        redeemCreditsValue.getText().isEmpty() ? 0 : Integer.parseInt(redeemCreditsValue.getText())
                );

                orderDAO.createOrder(order);

                UserDAOImpl userDAO = new UserDAOImpl();

                vipUser.setCredit(vipUser.getCredit() - redeemCredits);
                userDAO.updateVIPUser(vipUser);
            } else {
                // If user is not VIP, create order without redeeming credits
                order = new OrderModel(
                        user,
                        OrderStatus.AWAIT_FOR_COLLECTION,
                        orderTime,
                        prepTime,
                        null,
                        orderItems,
                        cart.getCartTotal(foods),
                        cart.getCartTotal(foods),
                        0
                );

                orderDAO.createOrder(order);
            }

            // Update VIP user's remaining credit if user is VIP
            if (user.isVIP()) {
                UserDAOImpl userDAO = new UserDAOImpl();

                VIPUserModel vipUser = (VIPUserModel) userDAO.getUserByUsername(user.getUsername());

                vipUser.setCredit((int) (vipUser.getCredit() + order.getTotalCost()));

                userDAO.updateVIPUser(vipUser);
            }

            int orderId = orderDAO.getLatestActiveOrderIdByUser(user);

            // Update cart status
            cart.setOrder(new OrderModel(orderId));
            cart.setStatus(CartStatus.COMPLETED);
            cartDAO.updateCart(cart);

            FriesModel fries = (FriesModel) foods.get(OrderItem.Fries);

            HashMap<String, Integer> friesTotalTimeWithRemainingFries = fries.getTotalTimeWithRemainingFries(cart.getFriesCount() + cart.getMealCount());
            int remainingFries = friesTotalTimeWithRemainingFries.get("currCapacity");

            // Update fries' remaining capacity
            fries.setCurrentCapacity(remainingFries);
            foodDAO.updateFood(fries);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("order-success-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }
}
