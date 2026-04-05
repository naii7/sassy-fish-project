module eus.ehu {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires transitive javafx.graphics;
    requires org.hibernate.orm.core;
    opens eus.ehu.controllers to javafx.fxml;
    exports eus.ehu.controllers;

    exports eus.ehu.businesslogic;

    opens eus.ehu.usermodel to javafx.fxml, org.hibernate.orm.core;
    exports eus.ehu.usermodel;

    opens eus.ehu.ui to javafx.graphics, javafx.fxml;
    exports eus.ehu.ui;
}