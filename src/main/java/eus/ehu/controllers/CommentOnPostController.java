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
import javafx.scene.layout.VBox; // BETTER VERSION
import javafx.stage.Stage;
/* // SIMPLE VERSION
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
 */

public class CommentOnPostController {

    @FXML
    private TextField wordCount; // Actually a Character count

    @FXML
    private TextArea commentArea;

    @FXML
    private Button saveButton;

    @FXML
    private Label errorLabel;

    // TABLE OF COMMENTS
    
    /* // SIMPLE VERSION
    @FXML
    private TableView<Comment> commentsTable;

    @FXML
    private TableColumn<Comment, String> usernameColumn;

    @FXML
    private TableColumn<Comment, String> commentTextColumn;
    
    @FXML
    private ObservableList<Comment> comments;
    */

    // BETTER VERSION
    @FXML
    private VBox commentsContainer; // container to hold the previous comment cards
    // END BETTER VERSION

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

        /* SIMPLE VERSION
        // TABLE OF COMMENTS
        // set up the column cell value factories for the tables
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("author")); //calls getAuthor() from Comment class
        commentTextColumn.setCellValueFactory(new PropertyValueFactory<>("text")); //calls getText() from Comment class
        */
    }

    // method to receive data from the window that opens this controller
    public void initData(Post post, BusinessLogic bl) {
        this.currentPost = post;
        this.businessLogic = bl;
        
        // load the comments of the post into the table
        reloadComments(); // BETTER VERSION
        // loadComments(); // SIMPLE VERSION
    }

    /* SIMPLE VERSION (1 METHOD)
    private void loadComments() {

        // get comments from current post
        List<Comment> commentsPost = currentPost.getComments();
    
        // add them to the observable list
        comments = FXCollections.observableArrayList(commentsPost);

        // show them in the table
        commentsTable.setItems(comments);
    }
    */
   
    // BETTER VERSION (3 METHODS)
    private void reloadComments() {
        if (commentsContainer == null) {
            return;
        }
        commentsContainer.getChildren().clear();
        // If there are no comments, show a message instead of an empty container
        if (currentPost == null || currentPost.getComments() == null || currentPost.getComments().isEmpty()) {
            Label emptyLabel = new Label("No comments yet. Be the first one.");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-padding: 8;");
            commentsContainer.getChildren().add(emptyLabel);
            return;
        }
        // Add comments in reverse order to show the most recent ones at the top
        for (int index = currentPost.getComments().size() - 1; index >= 0; index--) {
            commentsContainer.getChildren().add(createCommentCard(currentPost.getComments().get(index)));
        }
    }
    // helper method to create a card for each comment, similar to the post cards in the feed but simpler
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
    // helper method to refresh the current post from the database after adding a comment,
    //  to ensure we show the persisted state in the comments panel
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
    // END BETTER VERSION

    // handle the save button click event
    @FXML
    void handleSave() {
        String commentText = commentArea.getText(); // get the comment text from the text area
        
        // basic validation
        if (commentText.trim().isEmpty()) {
            System.out.println("comment is empty!");
            errorLabel.setVisible(true); 
            // "comment cannot be empty" error message is shown in the UI
            
            // after 3 secs. -> hide the error label again
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(3));
                    
            pause.setOnFinished(e -> { 
                
                // remove error label
                errorLabel.setVisible(false);
            });
            pause.play();
            return;
        }

        // get user from bl (it should be there cause you can't get to the comment screen without being logged in)
        currentUser = businessLogic.getCurrentUser(); 

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

        // loadComments(); // SIMPLE VERSION

        // BETTER VERSION
        // refresh from DB so we always show persisted state in the comments panel (to show new comment)
        refreshCurrentPostFromDatabase();
        reloadComments();
        // END BETTER VERSION
        
        // clear the comment area after saving
        commentArea.clear();
    }

    //handle the cancel button click event
    @FXML
    private void cancelComment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            Parent root = loader.load();

            // get controller for the feed page
            FeedController feedController = loader.getController();

            // inject the business logic to the feed controller so it can load the posts from the db
            feedController.initData(this.businessLogic); 
            
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