package com.project.restaurantmanagement.models.food;

import com.project.restaurantmanagement.models.order.OrderItem;

public class BurritoModel extends FoodModel {
    public BurritoModel(
            double price,
            int prepMinutes,
            int prepCapacity,
            String imageFile
    ) {
        super(OrderItem.Burrito, price, prepMinutes, prepCapacity, imageFile);
    }

    public int getTotalTime(int burritoQuantity) {
        if (this.getPrepCapacity() == 0) return 0;

        // how many batches based on quantity
        int batches = (int) Math.ceil((double) burritoQuantity / this.getPrepCapacity());

        // how long to prepare all batches
        return this.getPrepMinutes() * batches;
    }
}
