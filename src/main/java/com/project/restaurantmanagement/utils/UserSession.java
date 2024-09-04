package com.project.restaurantmanagement.utils;

import com.project.restaurantmanagement.models.user.UserModel;

public class UserSession {
    // Signed in user
    private static UserSession session;

    // To keep track of the order detail id when the user clicks on an order
    public static int orderDetailId = -1;

    private UserModel user;

    private UserSession() {
    }

    public static UserSession getUserSession() {
        if (session == null) {
            session = new UserSession();
        }

        return session;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getUser() {
        return user;
    }

    public void clearSession() {
        user = null;
    }

    public void setOrderDetailId(int id) {
        orderDetailId = id;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }
}
