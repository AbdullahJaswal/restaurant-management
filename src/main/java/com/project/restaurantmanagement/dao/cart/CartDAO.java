package com.project.restaurantmanagement.dao.cart;

import com.project.restaurantmanagement.models.cart.CartModel;
import com.project.restaurantmanagement.models.user.UserModel;

public interface CartDAO {
    CartModel getLatestActiveCartByUser(UserModel user) throws Exception;

    void createCart(CartModel cart) throws Exception;

    void updateCart(CartModel cart) throws Exception;
}
