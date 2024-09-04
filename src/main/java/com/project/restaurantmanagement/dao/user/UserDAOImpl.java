package com.project.restaurantmanagement.dao.user;

import com.project.restaurantmanagement.models.user.UserModel;
import com.project.restaurantmanagement.models.user.VIPUserModel;
import com.project.restaurantmanagement.utils.DatabaseConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {

    @Override
    public void addUser(UserModel user) throws Exception {
        String sql = """
                    INSERT INTO user (first_name, last_name, username, password)
                    VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = DatabaseConn.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("User Insert Error: ", e);
        }
    }

    @Override
    public void updateUser(UserModel user) throws Exception {
        String sql = """
                    UPDATE user
                    SET first_name = ?, last_name = ?, username = ?, password = ?, is_vip = false
                    WHERE id = ?
                """;

        try (
                Connection conn = DatabaseConn.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setInt(5, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("User Update Error: ", e);
        }
    }

    @Override
    public void updateVIPUser(VIPUserModel user) throws Exception {
        String sql = """
                    UPDATE user
                    SET first_name = ?, last_name = ?, username = ?, password = ?, is_vip = true, email = ?, credit = ?
                    WHERE id = ?
                """;

        try (
                Connection conn = DatabaseConn.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getEmail());
            statement.setInt(6, user.getCredit());
            statement.setInt(7, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("VIP User Update Error: ", e);
        }
    }

    @Override
    public UserModel getUserByUsername(String username) throws Exception {
        String sql = "SELECT * FROM user WHERE username = ?";

        try (
                Connection conn = DatabaseConn.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return getUserModel(rs);
            } else {
                throw new SQLException("Username " + username + " not found");
            }
        } catch (SQLException e) {
            throw new Exception("Username Retrieval Error: ", e);
        }
    }

    @Override
    public UserModel getUserByUsernameAndPassword(String username, String password) throws Exception {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (
                Connection conn = DatabaseConn.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return getUserModel(rs);
            } else {
                throw new SQLException("Invalid Username or Password");
            }
        } catch (SQLException e) {
            throw new Exception("Username and Password Retrieval Error: ", e);
        }
    }

    private UserModel getUserModel(ResultSet rs) throws SQLException {
        if (rs.getBoolean("is_vip")) {
            return new VIPUserModel(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    true,
                    rs.getString("email"),
                    rs.getInt("credit")
            );
        }

        return new UserModel(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                false
        );
    }

    @Override
    public void deleteUser(UserModel user) throws Exception {
        String sql = "DELETE FROM user WHERE id = ?";

        try (
                Connection conn = DatabaseConn.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("User Deletion Error: ", e);
        }
    }
}
