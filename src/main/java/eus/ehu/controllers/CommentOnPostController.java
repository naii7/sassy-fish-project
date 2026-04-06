package eus.ehu.controllers;

import java.time.LocalDate;

import eus.ehu.businesslogic.BusinessLogic;
import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CommentOnPostController {

    @FXML
    private TextField wordCount;

    @FXML
    private TextArea commentArea;

    @FXML
    private Button saveButton;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox commentsContainer;

    // variables to store the context of the comment
    private Post currentPost;
    private User currentUser;
    private BusinessLogic businessLogic; 


    // initialize method to set up the character limit and word count display
    @FXML
    void initialize() {
        int maxCharacters = 150;
        
        // create a textformatter to prevent exceeding the character limit
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= maxCharacters) {
                return change;
            }
            // reject the change if it exceeds the limit
            return null; 
        });
        
        commentArea.setTextFormatter(textFormatter);

        // show the character count dynamically binding the text property
        wordCount.textProperty().bind(
            Bindings.createStringBinding(
                () -> commentArea.getText().length() + "/" + maxCharacters,
                commentArea.textProperty()
            )
        );
    }

    // method to receive data from the window that opens this controller
    public void initData(Post post, BusinessLogic bl) {
        this.currentPost = post;
        this.businessLogic = bl;
        reloadComments();
    }

    private void reloadComments() {
        if (commentsContainer == null) {
            return;
        }
        commentsContainer.getChildren().clear();

        if (currentPost == null || currentPost.getComments() == null || currentPost.getComments().isEmpty()) {
            Label emptyLabel = new Label("No comments yet. Be the first one.");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-padding: 8;");
            commentsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int index = currentPost.getComments().size() - 1; index >= 0; index--) {
            commentsContainer.getChildren().add(createCommentCard(currentPost.getComments().get(index)));
        }
    }

    private VBox createCommentCard(Comment comment) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #e2e8f0; -fx-border-radius: 14; -fx-padding: 10;");

        HBox header = new HBox(8);
        Label authorLabel = new Label(comment.getAuthor() == null ? "unknown" : comment.getAuthor());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String commentDate = comment.getDate() == null ? "" : comment.getDate().toString();
        Label dateLabel = new Label(commentDate);
        dateLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        header.getChildren().addAll(authorLabel, spacer, dateLabel);

        Label textLabel = new Label(comment.getText() == null ? "" : comment.getText());
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(350);
        textLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 13px;");

        card.getChildren().addAll(header, textLabel);
        return card;
    }

    private void refreshCurrentPostFromDatabase() {
        if (businessLogic == null || currentPost == null || currentPost.getId() == null) {
            return;
        }

        Post refreshedPost = businessLogic.getAllPosts().stream()
            .filter(p -> p.getId() != null && p.getId().equals(currentPost.getId()))
            .findFirst()
            .orElse(null);

        if (refreshedPost != null) {
            currentPost = refreshedPost;
        }
    }

    // handle the save button click event
    @FXML
    void handleSave() {
        String commentText = commentArea.getText(); // get the comment text from the text area
        
        // basic validaiton

        if (commentText.trim().isEmpty()) {
            System.out.println("comment is empty!");
            errorLabel.setVisible(true);
            
            // after 3 secs. -> hide the error label again
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(3));
                    
            pause.setOnFinished(e -> { 
                
                // remove error label
                errorLabel.setVisible(false);
            });
            pause.play();
            return;
        }

        if (businessLogic == null) {
            System.out.println("business logic context is missing!");
            return;
        }

        // get user from bl (it should be there cause you can't get to the comment screen without being logged in)
        currentUser = businessLogic.getCurrentUser(); // get the current logged-in user from the business logic

        // shouldn't happen but just in case
        if(currentPost == null || currentUser == null) {
            System.out.println("post or user context is missing!");
            return;
        }

        // print the comment details to the console
        System.out.println("comment saved: " + commentText);
        
        // create the comment object
        Comment newComment = new Comment(currentUser.getUsername(), commentText, LocalDate.now(), currentPost);
        
        // send it to the database via business logic
        businessLogic.addCommentToPost(currentPost, newComment);

        // refresh from DB so we always show persisted state in the comments panel
        refreshCurrentPostFromDatabase();
        reloadComments();
        
        // clear the comment area after saving
        commentArea.clear();
    }

    //handle the cancel button click event
    @FXML
    private void cancelComment() {
        openFeedAndCloseComment();
    }

    private void openFeedAndCloseComment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            Parent root = loader.load();

            // get controller for the feed page
            FeedController feedController = loader.getController();

            // inject the business logic to the feed controller so it can load the posts from the db
            feedController.initData(this.businessLogic); // pass the bl so the feed can load the posts from the db
            
            Stage feedStage = new Stage();
            feedStage.setScene(new Scene(root));
            feedStage.show();

            Stage commentStage = (Stage) saveButton.getScene().getWindow();
            commentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}