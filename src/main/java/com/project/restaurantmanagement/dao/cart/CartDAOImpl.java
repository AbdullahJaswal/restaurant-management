package com.project.restaurantmanagement.dao.cart;

import com.project.restaurantmanagement.models.cart.CartModel;
import com.project.restaurantmanagement.models.cart.CartStatus;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.utils.DatabaseConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class CartDAOImpl implements CartDAO {
    @Override
    public CartModel getLatestActiveCartByUser(UserModel user) throws Exception {
        String sql = """
                     SELECT *
                     FROM cart
                     WHERE user_id = ? AND status = 'ACTIVE'
                     ORDER BY created_at DESC
                     LIMIT 1
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, user.getId());

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    CartStatus status = CartStatus.valueOf(result.getString("status"));
                    LocalDateTime created_at = result.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updated_at = result.getTimestamp("updated_at").toLocalDateTime();

                    // 'Active' user will never have an order associated with their cart
                    return new CartModel(
                            result.getInt("id"),
                            user,
                            result.getInt("burrito_qty"),
                            result.getInt("fries_qty"),
                            result.getInt("soda_qty"),
                            result.getInt("meal_qty"),
                            status,
                            null,
                            created_at,
                            updated_at
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Cart Select Error: ", e);
        }

        return new CartModel();
    }

    @Override
    public void createCart(CartModel cart) throws Exception {
        String sql = """
                     INSERT INTO cart (user_id, burrito_qty, fries_qty, soda_qty, meal_qty, status, order_id, created_at, updated_at)
                     VALUES (?, ?, ?, ?, ?, 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, cart.getUser().getId());
            statement.setInt(2, cart.getBurritoCount());
            statement.setInt(3, cart.getFriesCount());
            statement.setInt(4, cart.getSodaCount());
            statement.setInt(5, cart.getMealCount());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Cart Insert Error: ", e);
        }
    }

    @Override
    public void updateCart(CartModel cart) throws Exception {
        String sql = """
                     UPDATE cart
                     SET burrito_qty = ?, fries_qty = ?, soda_qty = ?, meal_qty = ?, status = ?, order_id = ?, updated_at = CURRENT_TIMESTAMP
                     WHERE id = ?
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, cart.getBurritoCount());
            statement.setInt(2, cart.getFriesCount());
            statement.setInt(3, cart.getSodaCount());
            statement.setInt(4, cart.getMealCount());
            statement.setString(5, cart.getStatus().toString());

            if (cart.getOrder() != null) {
                statement.setInt(6, cart.getOrder().getId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }

            statement.setInt(7, cart.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Cart Update Error: ", e);
        }
    }
}
