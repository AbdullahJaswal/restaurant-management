package com.project.restaurantmanagement.models.cart;

import com.project.restaurantmanagement.models.food.*;
import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.models.order.OrderModel;
import com.project.restaurantmanagement.models.user.UserModel;

import java.time.LocalDateTime;
import java.util.HashMap;

public class CartModel {
    private int id;
    private UserModel user;
    private int burritoCount = 0;
    private int friesCount = 0;
    private int sodaCount = 0;
    private int mealCount = 0;
    private CartStatus status = CartStatus.ACTIVE;
    private OrderModel order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartModel() {
    }

    public CartModel(UserModel user, int burritoCount, int friesCount, int sodaCount, int mealCount, CartStatus status) {
        this.user = user;
        this.burritoCount = burritoCount;
        this.friesCount = friesCount;
        this.sodaCount = sodaCount;
        this.mealCount = mealCount;
        this.status = status;
    }

    public CartModel(int id, UserModel user, int burritoCount, int friesCount, int sodaCount, int mealCount, CartStatus status, OrderModel order, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.burritoCount = burritoCount;
        this.friesCount = friesCount;
        this.sodaCount = sodaCount;
        this.mealCount = mealCount;
        this.status = status;
        this.order = order;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public int getBurritoCount() {
        return burritoCount;
    }

    public void setBurritoCount(int burritoCount) {
        this.burritoCount = burritoCount;
    }

    public int getFriesCount() {
        return friesCount;
    }

    public void setFriesCount(int friesCount) {
        this.friesCount = friesCount;
    }

    public int getSodaCount() {
        return sodaCount;
    }

    public void setSodaCount(int sodaCount) {
        this.sodaCount = sodaCount;
    }

    public int getMealCount() {
        return mealCount;
    }

    public void setMealCount(int mealCount) {
        this.mealCount = mealCount;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCartSize() {
        return burritoCount + friesCount + sodaCount + mealCount;
    }

    public double getCartTotal(HashMap<OrderItem, FoodModel> foods) {
        double totalAmount = 0.0;

        if (this.getBurritoCount() > 0) {
            BurritoModel burrito = (BurritoModel) foods.get(OrderItem.Burrito);
            totalAmount += burrito.getPrice() * this.getBurritoCount();
        }

        if (this.getFriesCount() > 0) {
            FriesModel fries = (FriesModel) foods.get(OrderItem.Fries);
            totalAmount += fries.getPrice() * this.getFriesCount();
        }

        if (this.getSodaCount() > 0) {
            SodaModel soda = (SodaModel) foods.get(OrderItem.Soda);
            totalAmount += soda.getPrice() * this.getSodaCount();

        }

        if (this.getMealCount() > 0) {
            MealModel meal = (MealModel) foods.get(OrderItem.Meal);
            totalAmount += meal.getPrice() * this.getMealCount();
        }

        return totalAmount;
    }

    public int getPrepTime(HashMap<OrderItem, FoodModel> foods) {
        int burritoQuantity = this.getBurritoCount();
        int friesQuantity = this.getFriesCount();
        int mealQuantity = this.getMealCount();

        if (mealQuantity > 0) {
            burritoQuantity += mealQuantity;
            friesQuantity += mealQuantity;
        }

        BurritoModel burrito = (BurritoModel) foods.get(OrderItem.Burrito);
        FriesModel fries = (FriesModel) foods.get(OrderItem.Fries);

        int burritoTotalTime = burrito.getTotalTime(burritoQuantity);
        HashMap<String, Integer> friesTotalTimeWithRemainingFries = fries.getTotalTimeWithRemainingFries(friesQuantity);

        return Math.max(burritoTotalTime, friesTotalTimeWithRemainingFries.get("totalTime"));
    }
}
