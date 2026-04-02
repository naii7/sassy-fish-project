package eus.ehu.ui;

import eus.ehu.businesslogic.BusinessLogic;
import eus.ehu.controllers.CommentOnPostController;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // 1. set up mock data for testing
        // normally, this would come from the database or the login screen
        User dummyUser = new User();
        dummyUser.setUsername("testUser"); 
        
        Post dummyPost = new Post();
        dummyPost.setTitle("My awesome post about AI");
        
        BusinessLogic dummyLogic = new BusinessLogic();

        // 2. load the fxml
        // adjust the path if your fxml is in a different resources folder
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/addComment.fxml"));
        Parent root = loader.load();

        // 3. get the controller and pass the mock data
        CommentOnPostController controller = loader.getController();
        controller.initData(dummyPost, dummyUser, dummyLogic);

        // 4. show the window
        primaryStage.setTitle("Test - Add Comment");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}