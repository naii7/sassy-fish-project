package eus.ehu.ui;

import eus.ehu.controllers.FeedController;
import eus.ehu.usermodel.Post;

import java.util.List;

import eus.ehu.businesslogic.BusinessLogic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FeedTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
        Parent root = loader.load();
        FeedController controller = loader.getController();

        Post post1 = new Post();
        post1.setTitle("My first post");
        post1.setAuthor("testUser");
        post1.setDescription("Testing that the feed card is created from in-memory data.");

        Post post2 = new Post();
        post2.setTitle("Second mock post");
        post2.setAuthor("bruno");
        post2.setDescription("This one helps verify author and description labels.");

        Post post3 = new Post();
        post3.setTitle("UI smoke check");
        post3.setAuthor("qa-user");
        post3.setDescription("No real DB, just checking if cards render in the scroll feed.");
        
        controller.showPosts(List.of(post1, post2, post3));

        primaryStage.setTitle("Test - Feed");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}