package eus.ehu.controllers;


import eus.ehu.businesslogic.BusinessLogic;
//import eus.ehu.controllers.CommentOnPostController;
//import eus.ehu.controllers.CreatePostController;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FeedController {

    // links these java variables to the specific id tags in your feed.fxml file
    @FXML
    private ScrollPane feedScroll;
    @FXML
    private Button newPostButton;
    @FXML
    private Button profileButton;
    @FXML
    private VBox feedContainer; // this is the empty vertical box where we will inject our posts
    @FXML
    private HBox PostMockup;

    private BusinessLogic businessLogic;

    // initialize() is a magic javafx method that runs automatically right after the fxml is loaded
    @FXML
    void initialize() {
        try {
            // 1. instantiate the business logic (which talks to the database under the hood)
            // BusinessLogic bl = new BusinessLogic(); 
            
            // 2. get the list of all posts and pass them to the method that draws the ui
            //showPosts(bl.getAllPosts());

            this.businessLogic = new BusinessLogic();
            showPosts(this.businessLogic.getAllPosts());

        } catch (Exception e) {
            System.err.println("could not load feed: " + e.getMessage());
            // fallback: if the database fails, we pass an empty list so the app doesn't crash completely
            showPosts(new ArrayList<>()); 
        }
    }

    // this method loops through the list of posts and creates a visual component for each one
    public void showPosts(List<Post> posts) {
        // clear any old posts from the screen to avoid duplicates if we refresh
        feedContainer.getChildren().clear();
        
        for (Post post : posts) {
            // create a postcard ui element for the post and add it to our main container
            feedContainer.getChildren().add(createPostCard(post));
        }
    }

    // triggered by the fxml when the user clicks the "new post" button
    @FXML
    void newPostButtonClicked() {
        System.out.println("debug: new post button clicked!");
        openCreatePostView();
    }

    /*  triggered by the fxml when the user clicks the profile button
    @FXML
    void profileButtonClicked() {
        try {
            // 1. locate the fxml file for the profile screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/profile.fxml"));
            
            // 2. load the fxml into a parent object (the root of the new scene graph)
            Parent profileView = loader.load(); 
            
            // 3. get the current window (stage) using the button we just clicked
            Stage stage = (Stage) profileButton.getScene().getWindow();
            
            // 4. change the scene to the new profile view
            stage.setScene(new Scene(profileView));

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }*/
   // triggered by the fxml when the user clicks the profile button
    /*@FXML
    void profileButtonClicked() {
        try {
            // 1. locate the fxml file for the profile screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/profile.fxml"));
            
            // 2. load the fxml into a parent object (the root of the new scene graph)
            Parent profileView = loader.load(); 
            
            // we get the profile controller
            ProfileController controller = loader.getController();

            // we create our fake user for the demo
            User currentUser = new User();
            currentUser.setUsername("currentUser"); 
            currentUser.setBio("aupa eibar yay");

            // we inject the connection and the user to the Profile
            controller.initData(this.businessLogic, currentUser);

            // 3. get the current window (stage) using the button we just clicked
            Stage stage = (Stage) profileButton.getScene().getWindow();
            
            // 4. change the scene to the new profile view
            stage.setScene(new Scene(profileView));
            stage.setTitle("User Profile"); // Un detalle para que la ventana quede bonita

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    @FXML
    void openProfile() {
        System.out.println("profile button clicked!");
    } */

    // triggered by the fxml when the user clicks the profile button
    @FXML
    void openProfile() {
        try {
            // 1. locate the fxml file for the profile screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/profile.fxml"));
            
            // 2. load the fxml into a parent object (the root of the new scene graph)
            Parent profileView = loader.load(); 
            
            // we get the profile controller
            ProfileController controller = loader.getController();

            // we create our fake user for the demo
            User currentUser = new User();
            currentUser.setUsername("currentUser"); 
            currentUser.setBio("aupa eibar yay");

            // we inject the connection and the user to the Profile
            controller.initData(this.businessLogic, currentUser);

            // 3. get the current window (stage) using the button we just clicked
            Stage stage = (Stage) profileButton.getScene().getWindow();
            
            // 4. change the scene to the new profile view
            stage.setScene(new Scene(profileView));
            stage.setTitle("User Profile");

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    // this is the factory method. it builds a user interface dynamically using java code instead of fxml
    public VBox createPostCard(Post post) {
        
        // 1. create the main container for this single post. vbox means elements stack vertically
        VBox postCard = new VBox(10); 
        // add css styling directly via code to make it look like a card
        postCard.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: white;");

        // 2. load the image
        Image postImage = null;
        String imagePath = post.getImagePath();
        
        // check if the post has a valid image path saved in the database
        if (imagePath != null && !imagePath.isBlank()) {
            try {
                postImage = new Image(imagePath);
            } catch (Exception ignored) {
                postImage = null;
            }
        }

        // if the database didn't have an image, load a default placeholder image from our resources
        if (postImage == null) {
            var defaultImageStream = getClass().getResourceAsStream("/default.png");
            if (defaultImageStream != null) {
                postImage = new Image(defaultImageStream);
            }
        }

        // 3. if we successfully got an image, create an imageview to display it and add it to the card
        if (postImage != null) {
            ImageView imageView = new ImageView(postImage);
            imageView.setFitWidth(300);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            postCard.getChildren().add(imageView);
        }

        // 4. create a smaller inner vertical box just for the text elements
        VBox postContent = new VBox(5);
        
        // create the title label and style it to be bold
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(270);

        // create the author label
        Label authorLabel = new Label("by " + post.getAuthor());
        authorLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        authorLabel.setMaxWidth(270);

        // create the description label and allow it to wrap to the next line if the text is long
        Label descriptionLabel = new Label(post.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: #1f2937; -fx-font-size: 14px;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(270);
        descriptionLabel.setMinHeight(84);
        descriptionLabel.setPrefHeight(84);

        // add the three text labels into the inner text box
        postContent.getChildren().addAll(titleLabel, authorLabel, descriptionLabel);

        // 5. create the comment button, showing the current amount of comments dynamically
        Button commentButton = new Button("💬 comment (" + post.getComments().size() + ")");
        commentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px;");
        
        // this is a lambda expression. it says: "when clicked, run opencommentview() and pass THIS specific post"
        commentButton.setOnAction(e -> openCommentView(post));

        // 6. assemble the final card by adding the text block and the comment button
        postCard.getChildren().addAll(postContent, commentButton);

        // return the fully assembled visual component so showposts() can put it on the screen
        return postCard;
    }

    // helper method to navigate to the comments screen for a specific post
    private void openCommentView(Post post) {
        try {
            // 1. load the fxml for the comments screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/addComment.fxml"));
            Parent commentView = loader.load();

            // 2. crucial step: get the controller instance that was just created by the fxml loader
            CommentOnPostController controller = loader.getController();

            // 3. create a temporary dummy user (in a real app, this comes from the login manager)
            User currentUser = new User();
            currentUser.setUsername("currentUser"); 
            // otherwise db rejecst the comennt due to null author
            currentUser.setBio("aupa eibar yay"); // TO SOLVE: get the real logged-in user instead of a fake one


            // 4. inject our context into the new controller before showing the window
            //controller.initData(post, currentUser, new BusinessLogic());
            
            //!!!!!!!!!!!
            // Pasamos this.businessLogic en vez de usar "new BusinessLogic()"
            controller.initData(post, currentUser, this.businessLogic);

            // 5. switch the visible scene to the comments screen
            Stage stage = (Stage) feedScroll.getScene().getWindow();
            stage.setScene(new Scene(commentView));
            stage.setTitle("add comment - " + post.getTitle());

        } catch (Exception e) {
            System.err.println("error opening comment view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // helper method to navigate to the create post screen
    private void openCreatePostView() {
        try {
            // 1. load the fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/createPost.fxml"));
            Parent createPostView = loader.load();

            // 2. get the controller so we can pass data to it
            CreatePostController controller = loader.getController();

            // 3. fake user for testing
            User currentUser = new User();
            currentUser.setUsername("currentUser"); 
            // !!! otherwise db will reject the post due to null author
            currentUser.setBio("aupa eibar yay"); // TO SOLVE: get the real logged-in user instead of a fake one

            // 4. inject the business logic and the logged-in user to the create post controller
            //controller.initData(new BusinessLogic(), currentUser);

            // !!!
            // Pasamos this.businessLogic en vez de usar "new BusinessLogic()"
            controller.initData(this.businessLogic, currentUser);

            // 5. switch the scene
            Stage stage = (Stage) newPostButton.getScene().getWindow();
            stage.setScene(new Scene(createPostView));
            stage.setTitle("create new post");

        } catch (Exception e) {
            System.err.println("error opening create post view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
}