package com.project.restaurantmanagement.models.food;

import com.project.restaurantmanagement.models.order.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class FriesModel extends FoodModel {
    private int currentCapacity;

    public FriesModel(double price, int prepMinutes, int prepCapacity, int currentCapacity, String imageFile) {
        super(OrderItem.Fries, price, prepMinutes, prepCapacity, imageFile);
        this.currentCapacity = currentCapacity;
    }

    public int getCurrentCapacity() {
        return this.currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public HashMap<String, Integer> getTotalTimeWithRemainingFries(int friesQuantity) {
        int totalTime = 0;
        int currCapacity = this.getCurrentCapacity();

        for (int i = 0; i < friesQuantity; i++) {
            if (currCapacity == 0) {
                totalTime += this.getPrepMinutes();
                currCapacity = 5;
            }

            currCapacity -= 1;
        }

        return new HashMap<>(Map.of("totalTime", totalTime, "currCapacity", currCapacity));
    }
}
