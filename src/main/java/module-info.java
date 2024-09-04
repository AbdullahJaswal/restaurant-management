module com.project.restaurantmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;

    opens com.project.restaurantmanagement.controllers to javafx.fxml;
    exports com.project.restaurantmanagement;
    exports com.project.restaurantmanagement.controllers;
}