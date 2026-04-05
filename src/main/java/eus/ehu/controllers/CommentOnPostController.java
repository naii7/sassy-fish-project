package eus.ehu.controllers;

import java.time.LocalDate;

import eus.ehu.businesslogic.BlInterface;
import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

    // variables to store the context of the comment
    private Post currentPost;
    private User currentUser;
    private BlInterface businessLogic; 


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
    public void initData(Post post, User user, BlInterface bl) {
        this.currentPost = post;
        this.currentUser = user;
        this.businessLogic = bl;
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

        if(currentPost == null || currentUser == null) {
            System.out.println("post or user context is missing!");
            return;
        }

        // print the comment details to the console
        System.out.println("comment saved: " + commentText);
        
        // create the comment object
        Comment newComment = new Comment(currentUser.getUsername(), commentText, LocalDate.now(), currentPost);
        
        // add the comment to the post internally (in mem)
        currentPost.addComment(newComment); 

        // send it to the database via business logic
        businessLogic.addCommentToPost(currentPost, newComment);
        
        // clear the comment area after saving
        commentArea.clear();
        openFeedAndCloseComment();
    }

    //handle the cancel button click event
    @FXML
    private void cancelComment() {
        openFeedAndCloseComment();
    }

    private void openFeedAndCloseComment() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage feedStage = new Stage();
            feedStage.setScene(new javafx.scene.Scene(root));
            feedStage.show();

            Stage commentStage = (Stage) saveButton.getScene().getWindow();
            commentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}