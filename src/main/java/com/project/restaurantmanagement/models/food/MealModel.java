package com.project.restaurantmanagement.models.food;


import com.project.restaurantmanagement.models.order.OrderItem;

public class MealModel extends FoodModel {
    private BurritoModel burrito;
    private FriesModel fries;
    private SodaModel soda;

    public MealModel(BurritoModel burrito, FriesModel fries, SodaModel soda, String imageFile) {
        super(
                OrderItem.Meal,
                burrito.getPrice() + fries.getPrice() + soda.getPrice() - 3,
                0,
                0,
                imageFile
        );

        this.burrito = burrito;
        this.fries = fries;
        this.soda = soda;
    }

    public BurritoModel getBurrito() {
        return burrito;
    }

    public void setBurrito(BurritoModel burrito) {
        this.burrito = burrito;
    }

    public FriesModel getFries() {
        return fries;
    }

    public void setFries(FriesModel fries) {
        this.fries = fries;
    }

    public SodaModel getSoda() {
        return soda;
    }

    public void setSoda(SodaModel soda) {
        this.soda = soda;
    }
}
