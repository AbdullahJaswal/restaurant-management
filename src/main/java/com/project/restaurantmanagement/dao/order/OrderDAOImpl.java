package com.project.restaurantmanagement.dao.order;

import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.models.order.OrderModel;
import com.project.restaurantmanagement.models.order.OrderStatus;
import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.utils.DatabaseConn;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public void createOrder(OrderModel order) throws Exception {
        String sqlOrder = """
                INSERT INTO "order" (user_id, status, order_time, prep_time, collection_time, total_cost, user_payment_amount, credits_used)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlOrderItem = """
                INSERT INTO order_item (order_id, food_id, quantity)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseConn.getConnection()) {
            conn.setAutoCommit(false);

            // Create order row
            try (PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrder.setInt(1, order.getUser().getId());
                stmtOrder.setString(2, order.getStatus().name());
                stmtOrder.setString(3, order.getOrderTime().toString());
                stmtOrder.setString(4, order.getPrepTime().toString());
                stmtOrder.setString(5, order.getCollectionTime() != null ? order.getCollectionTime().toString() : null);
                stmtOrder.setDouble(6, order.getTotalCost());
                stmtOrder.setDouble(7, order.getUserPaymentAmount());
                stmtOrder.setInt(8, order.getCreditsUsed());

                stmtOrder.executeUpdate();

                // Create order_item row
                try (ResultSet generatedKeys = stmtOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        try (PreparedStatement stmtOrderItem = conn.prepareStatement(sqlOrderItem)) {
                            for (OrderItem item : order.getOrderItems().keySet()) {
                                stmtOrderItem.setInt(1, orderId);
                                stmtOrderItem.setInt(2, getItemFoodId(item));
                                stmtOrderItem.setInt(3, order.getOrderItems().get(item));

                                stmtOrderItem.addBatch();
                            }

                            stmtOrderItem.executeBatch();
                        }
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new Exception("Order creation failed: ", e);
            }
        }
    }

    @Override
    public OrderModel getOrderById(int id) throws Exception {
        String sql = """
                SELECT *
                FROM "order"
                WHERE id = ?;
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    UserModel user = new UserModel(resultSet.getInt("user_id"));
                    return getOrderModel(resultSet, user);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Order retrieval failed: ", e);
        }

        return null;
    }

    @Override
    public List<OrderModel> getOrdersByUser(UserModel user) throws Exception {
        String sql = """
                SELECT *
                FROM "order"
                WHERE user_id = ?
                ORDER BY id DESC;
                """;

        List<OrderModel> orders = new ArrayList<>();

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(getOrderModel(resultSet, user));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Order retrieval by user failed: ", e);
        }

        return orders;
    }

    @Override
    public void updateOrder(OrderModel order) throws Exception {
        String sql = """
                UPDATE "order"
                SET status = ?, collection_time = ?, total_cost = ?, user_payment_amount = ?, credits_used = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, order.getStatus().name());
            statement.setString(2, order.getCollectionTime() != null ? order.getCollectionTime().toString() : null);
            statement.setDouble(3, order.getTotalCost());
            statement.setDouble(4, order.getUserPaymentAmount());
            statement.setInt(5, 0);
            statement.setInt(6, order.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Order update failed: ", e);
        }
    }

    @Override
    public int getLatestActiveOrderIdByUser(UserModel user) throws Exception {
        String sql = """
                SELECT id
                FROM "order"
                WHERE user_id = ? AND status = ?
                ORDER BY order_time DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, user.getId());
            statement.setString(2, OrderStatus.AWAIT_FOR_COLLECTION.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Order fetch failed: ", e);
        }

        return -1;
    }

    private HashMap<OrderItem, Integer> getOrderItems(int orderId) throws Exception {
        String sql = """
                SELECT oi.*, f.id AS food_id, f.name, f.price, f.prep_minutes, f.prep_capacity, f.curr_capacity, f.image_file
                FROM order_item oi
                JOIN food f ON oi.food_id = f.id
                WHERE oi.order_id = ?
                """;

        HashMap<OrderItem, Integer> orderItems = new HashMap<>();

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderItem item = OrderItem.valueOf(resultSet.getString("name"));
                    int quantity = resultSet.getInt("quantity");

                    orderItems.put(item, quantity);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Order items retrieval failed: ", e);
        }

        return orderItems;
    }

    private int getItemFoodId(OrderItem item) throws Exception {
        String sql = """
                SELECT id
                FROM food
                WHERE name = ?
                """;

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, item.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Food item retrieval failed: ", e);
        }

        throw new Exception("Food item not found: " + item.name());
    }

    private OrderModel getOrderModel(ResultSet resultSet, UserModel user) throws Exception {
        OrderModel order = new OrderModel(resultSet.getInt("id"), user, OrderStatus.valueOf(resultSet.getString("status")), LocalTime.parse(resultSet.getString("order_time")), LocalTime.parse(resultSet.getString("prep_time")), resultSet.getString("collection_time") != null ? LocalTime.parse(resultSet.getString("collection_time")) : null, getOrderItems(resultSet.getInt("id")));

        order.setTotalCost(resultSet.getDouble("total_cost"));
        order.setUserPaymentAmount(resultSet.getDouble("user_payment_amount"));

        return applyOrderItemsToOrder(order);
    }

    private OrderModel applyOrderItemsToOrder(OrderModel order) throws Exception {
        String sql = """
                SELECT *
                FROM order_item
                JOIN food f on f.id = order_item.food_id
                WHERE order_id = ?;
                """;

        HashMap<OrderItem, Integer> orderItems = new HashMap<>();

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, order.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderItem item = OrderItem.valueOf(resultSet.getString("name"));
                    int quantity = resultSet.getInt("quantity");

                    orderItems.put(item, quantity);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Order Items fetch failed: ", e);
        }

        order.setOrderItems(orderItems);

        return order;
    }
}
