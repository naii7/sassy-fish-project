package eus.ehu.ui;

import eus.ehu.businesslogic.BusinessLogic;
import eus.ehu.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTest extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        BusinessLogic businessLogic = BusinessLogic.getInstance(); // SINGLETON

        // Start with the Feed page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/login.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setBusinessLogic(businessLogic);
        
        primaryStage.setTitle("Sassy Fish - Social Media");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}