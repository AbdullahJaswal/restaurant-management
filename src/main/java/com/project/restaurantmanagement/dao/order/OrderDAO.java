package com.project.restaurantmanagement.dao.order;

import com.project.restaurantmanagement.models.order.OrderModel;
import com.project.restaurantmanagement.models.user.UserModel;

import java.util.List;

public interface OrderDAO {
    void createOrder(OrderModel order) throws Exception;

    OrderModel getOrderById(int id) throws Exception;

    List<OrderModel> getOrdersByUser(UserModel user) throws Exception;

    int getLatestActiveOrderIdByUser(UserModel user) throws Exception;

    void updateOrder(OrderModel order) throws Exception;
}
