package com.project.restaurantmanagement.models.food;

import com.project.restaurantmanagement.models.order.OrderItem;

public class SodaModel extends FoodModel {
    public SodaModel(
            double price,
            int prepMinutes,
            int prepCapacity,
            String imageFile
    ) {
        super(OrderItem.Soda, price, prepMinutes, prepCapacity, imageFile);
    }

    public int getTotalTime(int quantity) {
        return 0;
    }
}
