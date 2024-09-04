package com.project.restaurantmanagement.models.user;

public class VIPUserModel extends UserModel {
    private String email;
    private int credit = 0;

    public VIPUserModel(
            int id,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean isVIP,
            String email,
            int credit
    ) {
        super(id, firstName, lastName, username, password, isVIP);
        this.email = email;
        this.credit = credit;
    }

    public VIPUserModel(
            int id,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean isVIP,
            String email
    ) {
        super(id, firstName, lastName, username, password, isVIP);
        this.email = email;
    }

    public VIPUserModel(
            String firstName,
            String lastName,
            String username,
            String password,
            boolean isVIP,
            String email,
            int credit
    ) {
        super(firstName, lastName, username, password, isVIP);
        this.email = email;
        this.credit = credit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
