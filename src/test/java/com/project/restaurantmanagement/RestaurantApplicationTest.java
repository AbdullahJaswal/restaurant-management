package com.project.restaurantmanagement;

import com.project.restaurantmanagement.dao.user.UserDAOImpl;
import com.project.restaurantmanagement.models.user.UserModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestaurantApplicationTest {
    private UserDAOImpl userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAOImpl();
    }

    // Test for user login by checking if the user exists in the database
    @Test
    public void testUserLogin() {
        try {
            UserModel user = userDAO.getUserByUsernameAndPassword("admin", "admin");

            assertNotNull(user);
        } catch (Exception e) {
            assert false;
        }
    }

    // Test for user sign up by adding a new user to the database
    // and then logging in with the same credentials to check if the user was added
    @Test
    public void testUserSignUp() {
        try {
            UserModel user = new UserModel("test", "test", "test", "test", false);

            userDAO.addUser(user);

            UserModel user2 = userDAO.getUserByUsernameAndPassword("test", "test");

            assertNotNull(user2);
        } catch (Exception e) {
            assert false;
        }
    }

    // Remove the test user from the database after all tests have been run
    @AfterAll
    public static void removeTestUsers() {
        try {
            UserDAOImpl userDAO = new UserDAOImpl();

            UserModel user = userDAO.getUserByUsernameAndPassword("test", "test");

            userDAO.deleteUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
