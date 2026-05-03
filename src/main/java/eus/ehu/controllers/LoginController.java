package eus.ehu.controllers;

import eus.ehu.businesslogic.BusinessLogic;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML 
    private Label errorLabel;
    
    private BusinessLogic businessLogic;

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }
    public void initialize() {
        errorLabel.setVisible(false);
        loginButton.setDefaultButton(true);
    }
    @FXML
    void handleCancel() {

    }

    @FXML
    void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // avoid empty credentials
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Please enter username and password!");

            // after 3 secs. 
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(3));
                  
            // remove error label
            pause.setOnFinished(e -> errorLabel.setVisible(false));
            pause.play();

            return;
        }

        // try to log in
        if (businessLogic.login(username, password)) { 
            // successful
            loadFeedPage();

        } else {

            // failed
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid username or password!");
        
            // after 3 secs. 
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(3));
                  
            // remove error label
            pause.setOnFinished(e -> errorLabel.setVisible(false));
            pause.play();
        }
    }


    private void loadFeedPage() {
        try {
            FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            Parent root = loader.load();

            // get controller for the feed page
            FeedController feedController = loader.getController();

            // pass existing bl (cause now it has the logged-in user)
            feedController.initData(this.businessLogic);

            // change scene to the feed page
            Stage feedStage = (Stage) loginButton.getScene().getWindow(); // pick any element from the login window to id which stage
            feedStage.setScene(new Scene(root, 1250, 820));
            feedStage.setTitle("Feed");
            feedStage.centerOnScreen();
            feedStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
