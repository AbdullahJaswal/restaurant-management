package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.order.OrderDAOImpl;
import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.models.order.OrderModel;
import com.project.restaurantmanagement.models.order.OrderStatus;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DashboardController {
    @FXML
    public GridPane orderItems;

    public List<OrderModel> orders;

    @FXML
    protected void onOrderOptionClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("menu-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    protected void onViewAllOrdersOptionClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("view-orders-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    protected void onExitOptionClick() throws IOException {
        System.exit(0);
    }

    @FXML
    public void initialize() {
        UserModel user = UserSession.getUserSession().getUser();

        OrderDAOImpl orderDAO = new OrderDAOImpl();

        // Get all orders for the user
        try {
            this.orders = orderDAO.getOrdersByUser(user);

            int rowIndex = 1;

            for (OrderModel order : orders) {
                if (order.getStatus().equals(OrderStatus.AWAIT_FOR_COLLECTION)) {
                    addOrderRow(rowIndex++, order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add a row to the orderItems grid
    private void addOrderRow(int rowIndex, OrderModel order) {
        Label idLabel = new Label(String.valueOf(order.getId()));
        idLabel.getStyleClass().add("b");

        StringBuilder itemsStr = new StringBuilder();
        HashMap<OrderItem, Integer> items = order.getOrderItems();
        for (OrderItem item : items.keySet()) {
            itemsStr.append(item.toString()).append(" (Qty: ").append(items.get(item)).append(")\n");
        }
        Label itemsLabel = new Label(itemsStr.toString());
        itemsLabel.setAlignment(Pos.CENTER_LEFT);

        Label payedAmountLabel = new Label("$" + String.format("%.2f", order.getUserPaymentAmount()));
        payedAmountLabel.setAlignment(Pos.CENTER);

        Label placedAtLabel = new Label(order.getOrderTime().toString());
        placedAtLabel.setAlignment(Pos.CENTER);

        Label readyAtLabel = new Label(order.getPrepTime().toString());
        readyAtLabel.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("Await for Collection");

        Button actionLabel = new Button("Collect");
        actionLabel.getStyleClass().add("btn-info");
        actionLabel.setAlignment(Pos.CENTER_RIGHT);

        // redirect to order detail view on click
        if (order.getStatus() == OrderStatus.AWAIT_FOR_COLLECTION) {
            actionLabel.setOnMouseClicked(event -> {
                try {
                    UserSession.getUserSession().setOrderDetailId(order.getId());

                    FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("view-order-detail-view.fxml"));
                    Pane pane = fxmlLoader.load();

                    Scene currentScene = ((Node) event.getSource()).getScene();

                    currentScene.setRoot(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            actionLabel.setText("N/A");
            actionLabel.getStyleClass().add("disabled");
        }

        // Add the labels to the grid
        orderItems.add(idLabel, 0, rowIndex);
        orderItems.add(itemsLabel, 1, rowIndex);
        orderItems.add(payedAmountLabel, 2, rowIndex);
        orderItems.add(placedAtLabel, 3, rowIndex);
        orderItems.add(readyAtLabel, 4, rowIndex);
        orderItems.add(statusLabel, 5, rowIndex);
        orderItems.add(actionLabel, 6, rowIndex);
    }
}
