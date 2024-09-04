package com.project.restaurantmanagement.dao.food;

import com.project.restaurantmanagement.models.food.*;
import com.project.restaurantmanagement.models.order.OrderItem;
import com.project.restaurantmanagement.utils.DatabaseConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class FoodDAOImpl implements FoodDAO {
    @Override
    public HashMap<OrderItem, FoodModel> getAllFoods() throws Exception {
        HashMap<OrderItem, FoodModel> foods = new HashMap<>();
        String sql = "SELECT * FROM food order by id";

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                FoodModel food = getFoodModelFromResultSet(rs);

                foods.put(food.getName(), food);
            }
        } catch (SQLException e) {
            throw new Exception("Foods Retrieval Error: ", e);
        }

        return foods;
    }

    @Override
    public FoodModel getFoodByName(OrderItem name) throws Exception {
        String sql = "SELECT * FROM food WHERE name = ?";

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, name.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return getFoodModelFromResultSet(rs);
            } else {
                throw new SQLException(name + " food not found!");
            }
        } catch (SQLException e) {
            throw new Exception("Food Retrieval Error: ", e);
        }
    }

    @Override
    public void updateFood(FoodModel food) throws Exception {
        String sql = "UPDATE food SET price = ?, prep_minutes = ?, prep_capacity = ?, curr_capacity = ? WHERE name = ?";

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setDouble(1, food.getPrice());
            statement.setInt(2, food.getPrepMinutes());
            statement.setInt(3, food.getPrepCapacity());

            if (food.getName().equals(OrderItem.Fries)) {
                statement.setInt(4, ((FriesModel) food).getCurrentCapacity());
            } else {
                statement.setInt(4, 0);
            }

            statement.setString(5, food.getName().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Food Update Error: ", e);
        }
    }

    // Return the FoodModel object based on the name of the food
    private FoodModel getFoodModelFromResultSet(ResultSet rs) throws Exception {
        String name = rs.getString("name");
        double price = rs.getDouble("price");
        int prepMinutes = rs.getInt("prep_minutes");
        int prepCapacity = rs.getInt("prep_capacity");
        String imageFile = rs.getString("image_file");

        return switch (name) {
            case "Burrito" -> new BurritoModel(price, prepMinutes, prepCapacity, imageFile);
            case "Fries" -> new FriesModel(price, prepMinutes, prepCapacity, rs.getInt("curr_capacity"), imageFile);
            case "Soda" -> new SodaModel(price, prepMinutes, prepCapacity, imageFile);
            case "Meal" ->
                    new MealModel((BurritoModel) getFoodByName(OrderItem.Burrito), (FriesModel) getFoodByName(OrderItem.Fries), (SodaModel) getFoodByName(OrderItem.Soda), imageFile);
            default -> throw new IllegalArgumentException("Unknown food type: " + name);
        };
    }
}
