package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.order.OrderDAOImpl;
import com.project.restaurantmanagement.models.order.OrderModel;
import com.project.restaurantmanagement.models.order.OrderStatus;
import com.project.restaurantmanagement.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.time.LocalTime;

public class ViewOrderDetailController {
    @FXML
    public Label orderId;

    @FXML
    public Label readyTime;

    @FXML
    public TextField collectionTime;

    @FXML
    public Label errorLabel;

    public OrderModel order;

    @FXML
    public void initialize() {
        int OrderId = UserSession.getUserSession().getOrderDetailId();

        OrderDAOImpl orderDAO = new OrderDAOImpl();

        try {
            this.order = orderDAO.getOrderById(OrderId);

            orderId.setText(String.valueOf(order.getId()));
            readyTime.setText(order.getPrepTime().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDashboardOptionClick(MouseEvent event) throws Exception {
        UserSession.getUserSession().setOrderDetailId(-1);

        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("dashboard-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    public void collectOrderButtonClick(MouseEvent event) {
        errorLabel.setText("");

        if (collectionTime.getText().isEmpty()) {
            errorLabel.setText("Please enter a collection time");
            return;
        }

        if (!collectionTime.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            errorLabel.setText("Please enter a valid time in the format HH:MM");
            return;
        }

        LocalTime colTime = LocalTime.parse(collectionTime.getText());

        if (colTime.isBefore(order.getPrepTime())) {
            errorLabel.setText("Collection time must be after the order + preparation time");
            return;
        }

        OrderDAOImpl orderDAO = new OrderDAOImpl();

        // Update the order status to collected
        try {
            order.setCollectionTime(colTime);
            order.setStatus(OrderStatus.COLLECTED);

            updateOrderAndRedirectToOrders(event, orderDAO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancelButtonClick(MouseEvent event) {
        errorLabel.setText("");

        OrderDAOImpl orderDAO = new OrderDAOImpl();

        // Update the order status to cancelled
        try {
            order.setStatus(OrderStatus.CANCELLED);

            updateOrderAndRedirectToOrders(event, orderDAO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOrderAndRedirectToOrders(MouseEvent event, OrderDAOImpl orderDAO) throws Exception {
        // Update the order in the database
        orderDAO.updateOrder(order);

        // Unassign the order from the user session
        UserSession.getUserSession().setOrderDetailId(-1);

        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("view-orders-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }
}
