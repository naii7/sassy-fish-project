module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;

    opens com.example to javafx.fxml;
    opens com.SassyMeProject.controllers to javafx.fxml;
    opens com.SassyMeProject.usermodel;
    exports com.example;
    exports com.SassyMeProject.controllers;
    exports com.SassyMeProject.usermodel;
}
