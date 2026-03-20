module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires javafx.graphics;

    opens com.example.controllers to javafx.fxml;
    exports com.example.controllers;

    opens com.example.usermodel to javafx.fxml;
    exports com.example.usermodel;

}
