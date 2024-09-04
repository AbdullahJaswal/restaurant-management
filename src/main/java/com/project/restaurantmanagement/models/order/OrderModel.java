package com.project.restaurantmanagement.models.order;

import com.project.restaurantmanagement.models.user.UserModel;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class OrderModel {
    private int id;
    private UserModel user;
    private OrderStatus status = OrderStatus.AWAIT_FOR_COLLECTION;
    private LocalTime orderTime;
    private LocalTime prepTime;
    private LocalTime collectionTime;
    private HashMap<OrderItem, Integer> orderItems;
    private double totalCost;
    private double userPaymentAmount;
    private int creditsUsed;

    public OrderModel(int id) {
        this.id = id;
    }

    public OrderModel(int id, UserModel user, OrderStatus status, LocalTime orderTime, LocalTime prepTime, LocalTime collectionTime, HashMap<OrderItem, Integer> orderItems) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.orderTime = orderTime;
        this.prepTime = prepTime;
        this.collectionTime = collectionTime;
        this.orderItems = orderItems;
    }

    public OrderModel(
            UserModel user,
            OrderStatus status,
            LocalTime orderTime,
            LocalTime prepTime,
            LocalTime collectionTime,
            HashMap<OrderItem, Integer> orderItems,
            double totalCost,
            double userPaymentAmount,
            int creditsUsed
    ) {
        this.user = user;
        this.status = status;
        this.orderTime = orderTime;
        this.prepTime = prepTime;
        this.collectionTime = collectionTime;
        this.orderItems = orderItems;
        this.totalCost = totalCost;
        this.userPaymentAmount = userPaymentAmount;
        this.creditsUsed = creditsUsed;
    }

    public OrderModel(UserModel user, OrderStatus status, LocalTime orderTime, LocalTime prepTime, LocalTime collectionTime) {
        this.user = user;
        this.status = status;
        this.orderTime = orderTime;
        this.prepTime = prepTime;
        this.collectionTime = collectionTime;

        this.orderItems = new HashMap<>();
        this.orderItems.put(OrderItem.Burrito, 0);
        this.orderItems.put(OrderItem.Fries, 0);
        this.orderItems.put(OrderItem.Soda, 0);
        this.orderItems.put(OrderItem.Meal, 0);
    }

    public int getId() {
        return id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalTime getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(LocalTime prepTime) {
        this.prepTime = prepTime;
    }

    public LocalTime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(LocalTime collectionTime) {
        this.collectionTime = collectionTime;
    }

    public HashMap<OrderItem, Integer> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(HashMap<OrderItem, Integer> orderItems) {
        this.orderItems = orderItems;
    }

    public double getUserPaymentAmount() {
        return userPaymentAmount;
    }

    public void setUserPaymentAmount(double userPaymentAmount) {
        this.userPaymentAmount = userPaymentAmount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(int creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public static String validateCreditCardDetails(String cardNumber, String expiryDate, String cvv) {
        if (cardNumber.length() != 16) {
            return "Card number must be 16 digits long";
        }

        try {
            new BigInteger(cardNumber);
        } catch (NumberFormatException e) {
            return "Card number must be a number";
        }

        if (expiryDate.length() != 5) {
            return "Expiry date must be in the format MM/YY";
        }

        String[] expiryDateParts = expiryDate.split("/");

        try {
            int month = Integer.parseInt(expiryDateParts[0]);

            if (month < 1 || month > 12) {
                return "Expiry date month must be between 1 and 12";
            }
        } catch (NumberFormatException e) {
            return "Expiry date month must be a number";
        }

        try {
            int year = Integer.parseInt(expiryDateParts[1]);

            if (year < 0 || year > 99) {
                return "Expiry date year must be between 0 and 99";
            }
        } catch (NumberFormatException e) {
            return "Expiry date year must be a number";
        }

        LocalDate currentDate = LocalDate.now();
        currentDate = currentDate.withDayOfMonth(1);

        LocalDate expiryDateLocal = LocalDate.of(2000 + Integer.parseInt(expiryDateParts[1]), Integer.parseInt(expiryDateParts[0]), 1);

        if (expiryDateLocal.isBefore(currentDate)) {
            return "Expiry date must be in the future";
        }

        if (cvv.length() != 3) {
            return "CVV must be 3 digits long";
        }

        try {
            Integer.parseInt(cvv);
        } catch (NumberFormatException e) {
            return "CVV must be a number";
        }

        return null;
    }
}
