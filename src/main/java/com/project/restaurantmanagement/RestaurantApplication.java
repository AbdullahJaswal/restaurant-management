package com.project.restaurantmanagement;

import com.project.restaurantmanagement.dao.food.FoodDAOImpl;
import com.project.restaurantmanagement.models.food.FriesModel;
import com.project.restaurantmanagement.models.order.OrderItem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class RestaurantApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantApplication.class.getResource("welcome-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        // Reset remaining fries state to 5
        FoodDAOImpl foodDAO = new FoodDAOImpl();
        try {
            FriesModel fries = (FriesModel) foodDAO.getFoodByName(OrderItem.Fries);
            fries.setCurrentCapacity(5);
            foodDAO.updateFood(fries);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stage.setTitle("Burrito King Restaurant");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
