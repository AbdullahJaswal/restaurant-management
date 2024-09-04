package com.project.restaurantmanagement.dao.food;

import com.project.restaurantmanagement.models.food.FoodModel;
import com.project.restaurantmanagement.models.order.OrderItem;

import java.util.HashMap;

public interface FoodDAO {
    HashMap<OrderItem, FoodModel> getAllFoods() throws Exception;

    FoodModel getFoodByName(OrderItem name) throws Exception;

    void updateFood(FoodModel food) throws Exception;
}
