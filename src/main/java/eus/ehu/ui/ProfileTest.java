package eus.ehu.ui;

import eus.ehu.businesslogic.BusinessLogic;
import eus.ehu.controllers.ProfileController;
import eus.ehu.data_access.DbAccessManager;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DbAccessManager dbManager = new DbAccessManager();
        User profileUser = new User("sassy_user", "sassy_user@example.com");
        profileUser.setBio("Profile smoke test user");
        profileUser = dbManager.storeUserIfNotExists(profileUser);

        User notProfileUser = new User("qa-user", "qa-user@example.com");
        notProfileUser.setBio("Another user for testing.");
        notProfileUser = dbManager.storeUserIfNotExists(notProfileUser);

        Post post1 = new Post();
        post1.setTitle("My first post");
        post1.setAuthor("sassy_user");
        post1.setUser(profileUser);
        post1.setDescription("Testing that the feed card is created from in-memory data.");
        dbManager.storePost(post1); // Guardamos el post para que tenga un ID y se pueda mostrar en el perfil
        Post post2 = new Post();
        post2.setTitle("Second mock post");
        post2.setAuthor("sassy_user");
        post2.setUser(profileUser);
        post2.setDescription("This one helps verify author and description labels.");
        dbManager.storePost(post2); // Guardamos el post para que tenga un ID y se pueda mostrar en el perfil
    
        Post post3 = new Post();
        post3.setTitle("UI smoke check");
        post3.setAuthor("qa-user");
        post3.setUser(notProfileUser);
        post3.setDescription("No real DB, just checking if cards render in the scroll feed.");
        dbManager.storePost(post3); // Guardamos el post para que tenga un ID y NO se muestre en el perfil

        dbManager.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/profile.fxml"));
        Parent root = loader.load();
        ProfileController controller = loader.getController();
        controller.initData(new BusinessLogic(), profileUser);
        // Loading posts after having creted posts in DB to ensure they have IDs and are properly linked to the user
        primaryStage.setTitle("Test - Profile");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}