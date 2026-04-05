package eus.ehu.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTest extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start with the Feed page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/Login.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Sassy Fish - Social Media");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}