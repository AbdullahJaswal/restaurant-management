package com.project.restaurantmanagement.controllers;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.dao.food.FoodDAOImpl;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOrdersController {
    @FXML
    public GridPane orderItems;

    public List<OrderModel> orders;

    @FXML
    protected void onDashboardOptionClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("dashboard-view.fxml"));
        Pane pane = fxmlLoader.load();

        Scene currentScene = ((Node) event.getSource()).getScene();

        currentScene.setRoot(pane);
    }

    @FXML
    public void initialize() {
        UserModel user = UserSession.getUserSession().getUser();

        FoodDAOImpl foodDAO = new FoodDAOImpl();
        OrderDAOImpl orderDAO = new OrderDAOImpl();

        try {
            this.orders = orderDAO.getOrdersByUser(user);

            int rowIndex = 1;

            for (OrderModel order : orders) {
                addOrderRow(rowIndex++, order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        Label totalLabel = new Label("$" + String.format("%.2f", order.getTotalCost()));
        totalLabel.setAlignment(Pos.CENTER);

        Label payedAmountLabel = new Label("$" + String.format("%.2f", order.getUserPaymentAmount()));
        payedAmountLabel.setAlignment(Pos.CENTER);

        Label placedAtLabel = new Label(order.getOrderTime().toString());
        placedAtLabel.setAlignment(Pos.CENTER);

        Label readyAtLabel = new Label(order.getPrepTime().toString());
        readyAtLabel.setAlignment(Pos.CENTER);

        Label statusLabel = getLabel(order);

        Button actionLabel = new Button("Collect");
        actionLabel.getStyleClass().add("btn-info");
        actionLabel.setAlignment(Pos.CENTER_RIGHT);

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

        orderItems.add(idLabel, 0, rowIndex);
        orderItems.add(itemsLabel, 1, rowIndex);
        orderItems.add(totalLabel, 2, rowIndex);
        orderItems.add(payedAmountLabel, 3, rowIndex);
        orderItems.add(placedAtLabel, 4, rowIndex);
        orderItems.add(readyAtLabel, 5, rowIndex);
        orderItems.add(statusLabel, 6, rowIndex);
        orderItems.add(actionLabel, 7, rowIndex);
    }

    private static Label getLabel(OrderModel order) {
        OrderStatus orderStatus = order.getStatus();

        Label statusLabel = new Label();
        switch (orderStatus) {
            case AWAIT_FOR_COLLECTION:
                statusLabel.setText("Await for Collection");
                break;
            case COLLECTED:
                statusLabel.setText("Collected");
                break;
            case CANCELLED:
                statusLabel.setText("Cancelled");
                break;
            default:
                statusLabel.setText("N/A");
                break;
        }
        statusLabel.setAlignment(Pos.CENTER);
        return statusLabel;
    }

    @FXML
    public void onDownloadOptionClick(MouseEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setTitle("Save Orders");
        fileChooser.setInitialFileName("orders.csv");

        // Show save file dialog
        Scene currentScene = ((Node) event.getSource()).getScene();
        File file = fileChooser.showSaveDialog(currentScene.getWindow());

        if (file != null) {
            // Save orders to CSV
            saveOrdersToCsv(file);
        }
    }

    private void saveOrdersToCsv(File file) throws IOException {
        // Write orders to CSV
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("ID,User,Status,Order Time,Prep Time,Collection Time,Total Cost,User Payment Amount,Credits Used,Order Items\n");

            for (OrderModel order : orders) {
                writer.append(String.valueOf(order.getId())).append(',')
                        .append(order.getUser().getUsername()).append(',')
                        .append(order.getStatus().toString()).append(',')
                        .append(order.getOrderTime().toString()).append(',')
                        .append(order.getPrepTime().toString()).append(',')
                        .append(order.getCollectionTime() != null ? order.getCollectionTime().toString() : "").append(',')
                        .append(String.valueOf(order.getTotalCost())).append(',')
                        .append(String.valueOf(order.getUserPaymentAmount())).append(',')
                        .append(String.valueOf(order.getCreditsUsed())).append(',')
                        .append(orderItemsToString(order.getOrderItems())).append('\n');
            }
        }
    }

    // Convert order items to string
    private String orderItemsToString(Map<OrderItem, Integer> orderItems) {
        StringBuilder itemsString = new StringBuilder();

        for (Map.Entry<OrderItem, Integer> entry : orderItems.entrySet()) {
            itemsString.append(entry.getKey().name()).append("(").append(entry.getValue()).append(")|");
        }

        if (!itemsString.isEmpty()) {
            itemsString.setLength(itemsString.length() - 1);
        }

        return itemsString.toString();
    }
}
