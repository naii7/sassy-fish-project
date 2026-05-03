package eus.ehu.controllers;


import java.util.ArrayList;
import java.util.List;

import eus.ehu.businesslogic.BusinessLogic;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FeedController {

    // links these java variables to the specific id tags in feed.fxml file
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

    
    @FXML
    private Button Bathroom;
    @FXML
    private Button Disaster;
    @FXML
    private Button Food;
    @FXML
    private Button Movie;
    @FXML
    private Button Music;
    @FXML
    private Button Videogame;
    @FXML
    private Button Book;
    @FXML
    private Button Other;

    private BusinessLogic businessLogic;

    public void initData(BusinessLogic bl) {
        this.businessLogic = bl;

        try {
           // now that we have the data, we can load the post feed
            showAllPosts();

        } catch (Exception e) {
            System.err.println("could not load feed: " + e.getMessage());
            // fallback: if the database fails, we pass an empty list so the app doesn't crash completely
            showPosts(new ArrayList<>()); 
        }
    }
    
    // initialize() is a magic javafx method that runs automatically right after the fxml is loaded
    @FXML
    void initialize() {
        // not needed cause previous controller calls initData and that method does the loading of the feed 
        // we could also call it from here if we wanted to, but como el resto tiene initData, así all the same
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

    @FXML
    // one like button for each post, so we pass the specific button and post that was clicked as parameters
    private void handleLikeButton(ToggleButton likeBtn, Post post) {

        if (this.businessLogic == null) {
            return;
        }
        
        int likes = post.getLikeCount();

        // check if button is selected
        boolean isSelected = likeBtn.isSelected();
        
        // if selected -> style red
        if (isSelected) {
            likes++; // increment
            likeBtn.setStyle("-fx-text-fill: #ff0000e6; -fx-background-color: transparent; -fx-font-size: 20px");
        
        } else { // if not selected -> style grey
           
            if (likes > 0) { // avoid negative like count
                likes--; // decrement
            }
            likeBtn.setStyle("-fx-text-fill: #b9b9b9; -fx-background-color: transparent; -fx-font-size: 20px");
        }

        // update the like count in the post object in memory
        post.setLikeCount(likes);

        // save the like count in the database via business logic
        this.businessLogic.updateLikePost(post);
    
        // update the button text to show the new like count
        likeBtn.setText("♥" + likes);

    }
    

    // triggered by the fxml when the user clicks the profile button
    @FXML
    void openProfile() {
        try {
            // 1. locate the fxml file for the profile screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/profile.fxml"));
            
            // 2. load the fxml into a parent object (the root of the new scene graph)
            Parent profileView = loader.load(); 
            
            // we get the profile controller
            ProfileController profileController = loader.getController();

            // get the real logged-in user from the bl
            profileController.initData(this.businessLogic);

            // 3. get the current window (stage) using the button we just clicked
            Stage stage = (Stage) profileButton.getScene().getWindow();
            
            // 4. change the scene to the new profile view
            stage.setScene(new Scene(profileView, 1250, 820));
            stage.setTitle("User Profile");

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    @FXML
    void showAllPosts() {
        if (this.businessLogic == null) {
            showPosts(new ArrayList<>());
            return;
        }

        List<Post> posts = this.businessLogic.getAllPosts();
        showPosts(posts);
    }

    // this is the factory method. it builds a user interface dynamically using java code instead of fxml
    public HBox createPostCard(Post post) {
        
        // 1. create the main container for this single post. hbox means elements stack horizontally
        HBox postCard = new HBox(10); 
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

        // 3. left column with image + comments button underneath
        VBox mediaColumn = new VBox(6);
        mediaColumn.setPrefWidth(240);
        mediaColumn.setMinWidth(240);
        mediaColumn.setMaxWidth(240);
        mediaColumn.setAlignment(Pos.TOP_LEFT);

        if (postImage != null) {
            ImageView imageView = new ImageView(postImage);
            imageView.setFitWidth(240);
            imageView.setFitHeight(160);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            mediaColumn.getChildren().add(imageView);
        }

        // 4. create a smaller inner vertical box just for the text elements
        VBox postContent = new VBox(5);
        
        // create the title label and style it to be bold
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(270);

        // create the author label
        String author = post.getAuthor();
        if ((author == null || author.isBlank()) && post.getUser() != null) {
            author = post.getUser().getUsername();
        }
        if (author == null || author.isBlank()) {
            author = "unknown";
        }
        Label authorLabel = new Label("by " + author);
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

        // 5. create the comment button for each post, showing the current amount of comments dynamically
        Button commentButton = new Button("💬 Comments (" + post.getComments().size() + ")");
        commentButton.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 10 4 10;");
        commentButton.setPrefWidth(120);
        commentButton.setMinHeight(28);
        commentButton.setAlignment(Pos.CENTER_LEFT);
        
        // this is a lambda expression. it says: "when clicked, run opencommentview() and pass THIS specific post"
        commentButton.setOnAction(e -> openCommentView(post, commentButton));

        mediaColumn.getChildren().add(commentButton);

        // 6. create like button for each post, showing the current amount of likes dynamically
        ToggleButton likeButton = new ToggleButton();

        // set initial text and style
        likeButton.setText("♥" + post.getLikeCount());
        likeButton.setStyle("-fx-text-fill: #b9b9b9; -fx-background-color: transparent; -fx-font-size: 20px;");
        likeButton.setSelected(false); // default state -> not liked

        // this is a lambda expression. it says: "when clicked, run handleLikeButton() and pass THIS specific post"
        likeButton.setOnAction(e -> handleLikeButton(likeButton, post));

        // 7. show post rating using stars
            // BETTER VERSION (AUXILIAR METHOD formatRating() NEEDED)
        Label starRating = new Label(formatRating(post.getStarRating()));
        starRating.setStyle("-fx-text-fill: #d97706; -fx-font-size: 14px; -fx-font-weight: bold;");
            // END BETTER VERSION

            /* // SIMPLE VERSION (JUST SHOW VALUE)
        Label starRating = new Label();
        starRating.setText(String.valueOf(post.getStarRating()));
            */
        
        // add the star rating label to the post content
        postContent.getChildren().add(starRating);

        // 8. create tag labels for each post
        HBox tagsContainer = new HBox(5);
        
        // go through the list of tags of each post
        for (Tag tag : post.getTags()) {

            // create label for each tag
            Label tagLabel = new Label(tag.name());  // cause tag is an ENUM, we can use name() to get the string value          
            // style the tag label to look like a badge)
            tagLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333");
            
            tagsContainer.getChildren().add(tagLabel);
        }
        //add tag container to the post content
        postContent.getChildren().add(tagsContainer);

        // 7. assemble the final card
        postCard.getChildren().addAll(mediaColumn, postContent, likeButton);

        // return the fully assembled visual component so showposts() can put it on the screen
        return postCard;
    }

    // BETTER VERSION AUXILIAR
    private String formatRating(double rating) {
        double safeRating = Math.max(0.0, Math.min(5.0, rating));
        if (safeRating == 0.0) {
            return "☆☆☆☆☆ (0.0)";
        }

        int fullStars = (int) safeRating;
        boolean hasHalfStar = (safeRating - fullStars) >= 0.5;
        int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

        String stars = "★".repeat(fullStars)
                + (hasHalfStar ? "⯪" : "")
                + "☆".repeat(Math.max(0, emptyStars));

        return stars + " (" + String.format(java.util.Locale.US, "%.1f", safeRating) + ")";
    }
    // END BETTER VERSION AUXILIAR

    // helper method to navigate to the comments screen for a specific post
    private void openCommentView(Post post, Button commentButton) {
        try {
            if (this.businessLogic == null) {
                return;
            }

            // 1. load the fxml for the comments screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/addComment.fxml"));
            Parent commentView = loader.load();

            // 2. crucial step: get the controller instance that was just created by the fxml loader
            CommentOnPostController controller = loader.getController();

            // 4. inject our context into the new controller before showing the window
            controller.initData(post, this.businessLogic);

            // 5. create the mew comments window (scene)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(commentView));
            newStage.setTitle("add comment - " + post.getTitle());
            newStage.show();

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

            // 4. open the new create post window (scene)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(createPostView));
            newStage.setTitle("create new post");
            newStage.showAndWait(); // wait until the new window closes to update (refresh)

            // 5. refresh feed
            showAllPosts();

        } catch (Exception e) {
            System.err.println("error opening create post view: " + e.getMessage());
            e.printStackTrace();
        }


       
    }
    @FXML
    private void filterByTag(ActionEvent event) {
        try {
            if (this.businessLogic == null) {
                return;
            }

            // get the tag from the button text
            Button clickedButton = (Button) event.getSource();
            String tagText = clickedButton.getText().toUpperCase(); // ENUM sintax

            // convert the button text to a Tag enum
            Tag selectedTag = Tag.valueOf(tagText);

            // get posts with the selected tag from the business logic
            List<Post> filteredPosts = this.businessLogic.getPostsByTag(selectedTag);

            // update the feed to show only the filtered posts
            showPosts(filteredPosts);

        } catch (Exception e) {
            System.err.println("error filtering by tag: " + e.getMessage());
            e.printStackTrace();
        }
    
    }
}