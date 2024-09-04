package com.project.restaurantmanagement.models.user;

public class UserModel {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isVIP;

    public UserModel(int id) {
        this.id = id;
    }

    public UserModel(int id, String firstName, String lastName, String username, String password, boolean isVIP) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isVIP = isVIP;
    }

    public UserModel(String firstName, String lastName, String username, String password, boolean isVIP) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isVIP = isVIP;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean isVIP) {
        this.isVIP = isVIP;
    }
}
