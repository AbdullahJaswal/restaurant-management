package com.project.restaurantmanagement.dao.user;

import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.models.user.VIPUserModel;

public interface UserDAO {
    public void addUser(UserModel user) throws Exception;

    public void updateUser(UserModel user) throws Exception;

    public void updateVIPUser(VIPUserModel user) throws Exception;

    public UserModel getUserByUsername(String username) throws Exception;

    public UserModel getUserByUsernameAndPassword(String username, String password) throws Exception;

    public void deleteUser(UserModel user) throws Exception;
}
