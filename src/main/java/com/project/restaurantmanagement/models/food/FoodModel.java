package com.project.restaurantmanagement.models.food;

import com.project.restaurantmanagement.RestaurantApplication;
import com.project.restaurantmanagement.models.order.OrderItem;

public abstract class FoodModel {
    private OrderItem name;
    private double price;
    private int prepMinutes;
    private int prepCapacity;
    private String imageFile;

    public FoodModel(OrderItem name, double price, int prepMinutes, int prepCapacity, String imageFile) {
        this.name = name;
        this.price = price;
        this.prepMinutes = prepMinutes;
        this.prepCapacity = prepCapacity;
        this.imageFile = imageFile;
    }

    public OrderItem getName() {
        return name;
    }

    public void setName(OrderItem name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPrepMinutes() {
        return prepMinutes;
    }

    public void setPrepMinutes(int prepMinutes) {
        this.prepMinutes = prepMinutes;
    }

    public int getPrepCapacity() {
        return prepCapacity;
    }

    public void setPrepCapacity(int prepCapacity) {
        this.prepCapacity = prepCapacity;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImagePath() {
        String imageFile = this.getImageFile();

        return String.valueOf(RestaurantApplication.class.getResource("/com/project/restaurantmanagement/assets/" + imageFile));
    }
}
