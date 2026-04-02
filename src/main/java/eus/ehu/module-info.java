module eus.ehu {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires javafx.graphics;

    opens eus.ehu.controllers to javafx.fxml;
    exports eus.ehu.controllers;

    opens eus.ehu.usermodel to javafx.fxml;

    opens eus.ehu.ui to javafx.graphics, javafx.fxml;
    exports eus.ehu.ui;
}