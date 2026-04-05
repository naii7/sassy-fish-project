package eus.ehu.controllers;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import eus.ehu.businesslogic.BlInterface;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.Tag;
import eus.ehu.usermodel.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class CreatePostController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    // DATE
    @FXML private Label dateLabel; // NOT NEEDED

    // IMAGE UPLOAD
    @FXML private AnchorPane imageDropArea;
    @FXML private Label imageUploadLabel;
    @FXML private ImageView imageView;

    // TITLE
    @FXML private Label titleLabel;
    @FXML private TextField titleField;

    // DESCRIPTION
    @FXML private Label descriptionLabel;
    @FXML private TextArea descriptionField;
    
    // TAGS
    @FXML private Label tagsLabel;
    @FXML private AnchorPane tagContainer;


    // STARTS
    @FXML private Label starsLabel;
    @FXML private HBox starContainer;
    @FXML private Label star1, star2, star3, star4, star5;
    private Label[] stars; // to easily loop through them in the code

    @FXML private Label favouriteLabel;
    @FXML private ToggleButton favouriteButton;

    // CREATE / CANCEL POST
    @FXML private Button createPostButton;
    @FXML private Button cancelPostButton;
    @FXML private Label errorLabel;


    private Post currentPost;
    private BlInterface businessLogic;
    private User currentUser; // who is creating the post

    // updated to receive the logged-in user
    public void initData(BlInterface bl, User user) {
        this.businessLogic = bl;
        this.currentUser = user;

        // assign the user to the post immediately so the database knows the author
        if (this.currentPost != null && this.currentUser != null) {
            this.currentPost.setUser(this.currentUser);
            this.currentPost.setAuthor(this.currentUser.getUsername());
        }
    }


    @FXML
    void handleImageClick(MouseEvent event) {
       
        // open file chooser dialog (pop-up) so the user can pick an image file from their computer
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image"); // set the dialog (pop-up) window
        
        // restrict to image files only
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // show the dialog (pop-up) & get the selected file
        File file = fileChooser.showOpenDialog(imageDropArea.getScene().getWindow());
            // `showOpenDialog` returns a File object representing the selected file, or null if the user cancels

        // only continue if the user actually selected a file
        if (file != null) {
            
            // 1. create a JavaFX Image from the selected file path
            Image img = new Image(file.toURI().toString());
                // Image can be displayed in ImageView directly
            
            // 2. display image in ImageViewm (visually)
            imageView.setImage(img);
        
            // 3. update Post object to store the path to the image (for later saving to db)
            currentPost.setImage(img);
            currentPost.setImagePath(file.toURI().toString() ); //convert the file's path (file.toURI()) to string (.toString()) to store it in the db
                // also automatically updates the Post.image
        }
    }   

    @FXML
    void handleDragOver(DragEvent event) {
        // TODO:
        // photo dragged over the image area -> change style to indicate that the user can drop the photo there 
    }

    @FXML
    void handleStarClicked(MouseEvent event) {
        
        // get the star that was clicked
        Label clickedStar = (Label) event.getSource();

        // go through each star until we get to the clicked star
        int i = 0;
        while (stars[i] != clickedStar)
          i++;

        // SET STYLE (colour) of each star
        for (int j = 0; j < stars.length; j++) {
            
            if (j <= i) { // star <= clicked star -> gold
                stars[j].setStyle("-fx-text-fill: gold");
                stars[j].setText("★"); // set to full star (in case it was a half-star before)
            
            } else { // star > clicked star -> grey
                stars[j].setStyle("-fx-text-fill: #b9b9b9");
                stars[j].setText("★"); // set to full star (in case it was a half-star before)
            }
        }

        // SET NEW RATING

        // get the star rating from before (to check if user clicks the same star again)
        double currentRating = currentPost.getStarRating();
        
        // check if user clicks the same star twice
        // A. SAME STAR CLICKED WHEN IT WAS FULL -> CHANGE TO HALF-STAR
        if ( currentRating == i + 1 ) { // rating = clicked star index + 1 (cause arrays start at 0)
            
            clickedStar.setText("⯪"); // change star to half-star

            //save star rating w/ 0.5 (for half-star) inside Post object (for later saving to db)
            currentPost.setStarRating(i + 0.5); // set rating to clicked star index + 0.5 (cause arrays start at 0)
        
        // B. SAME STAR CLICKED WHEN IT WAS HALF -> CHANGE TO FULL STAR
        } else if (currentRating == i + 0.5)  { // rating = clicked star index + 0.5 (cause arrays start at 0)
            
            clickedStar.setText("★"); // change star to full star

            //save star rating w/ 1.0 (for full star) inside Post object (for later saving to db)
            currentPost.setStarRating(i + 1.0); // set rating to clicked star index + 1.0 (cause arrays start at 0)

        // C .DIFFERENT STAR CLICKED -> NORMAL BEHAVIOUR (CHANGE TO FULL STAR)
        } else {

            // save star rating inside Post object (for later saving to db)
            currentPost.setStarRating(i + 1.0); // different star -> set rating to clicked star index + 1 (cause arrays start at 0)
        }
    }


    @FXML
    void handleFavoriteToggle() {

        // check if button is selected
        boolean isSelected = favouriteButton.isSelected();

        // if selected -> style red
        if (isSelected) {
            favouriteButton.setStyle("-fx-text-fill: red");
        
        } else { // if not selected -> style grey
            favouriteButton.setStyle("-fx-text-fill: #b9b9b9");
        }

        // save value inside Post object (for later saving to db)
        currentPost.setIsFavourite(isSelected);
    }

    @FXML
    void cancelPost() {

        // CLOSE WINDOW get back to previous screen (main feed)
        goBackToFeed();
            // no need to clear and reset everything cause we create a new CreatePostController (with empty Post and unselected fields) every time we open the CreatePost screen
    }

    @FXML
    void savePost() {

        // 1. update Post object w/ data from the fields
        currentPost.setTitle(titleField.getText()); // title
        currentPost.setDescription(descriptionField.getText()); // description

        // 2. collect the selected tags

        // remove old tags (prevents duplicates if save button clicked multiple times)
        currentPost.clearTags();

        // loop through the tagContainer children (node = tag)
        for (Node node : tagContainer.getChildren()) {
           
            // cast to CheckBox to access isSelected()
            CheckBox tag = (CheckBox) node; 

            // if tag is selected -> add Tag ENUM
            if ( tag.isSelected() ) {

                // add corresponding Tag enum to the Post object
                currentPost.addTag(Tag.valueOf(tag.getText().toUpperCase())); 
                    // `tag.getText()` gets the text of the CheckBox | convert to uppercase to match ENUM name
            }
        }    

        // 3. check if all required fields are filled & handle error messages
        boolean error = errorCheck();

        // only proceed if there's nothing missing
        if (!error) {

            // 4. save Post to db
                // TO DO. create a FeedController w/ ObservableList<Post> feedPosts
                // feedPosts.add(currentPost);
                businessLogic.savePost(currentPost);
        
            // 5. CLOSE WINDOW get back to previous screen (main feed)
            //imageDropArea.getScene().getWindow().hide();

            // 5. go back to feed and refresh the posts to show the new post (instead of just going back without refreshing and having to click the profile button to see the new post in the feed)
            goBackToFeed();
        }
    }

    // auxiliar to check if all required fields are filled
    private boolean errorCheck () {

        boolean missing = false;
        
        // missing image upload
        if ( currentPost.getImage() == null ) {
            imageUploadLabel.setStyle("-fx-text-fill: red");
            missing = true;
        }

        // missing title
        if ( titleField.getText().isEmpty() ) {
            titleLabel.setStyle("-fx-text-fill: red");
            missing = true;
        }

        // missing tags (0 tags)
        if (currentPost.getTags().size() == 0) {
            tagsLabel.setStyle("-fx-text-fill: red");
            missing = true;
        }

        // missing star rating
        if ( currentPost.getStarRating() == 0 ) {
            starsLabel.setStyle("-fx-text-fill: red");
            missing = true;
        }

        // if any of them missing -> missing = true = errorLabel set visible
        // else (all correct) -> missing = false = errorLabel set !visible
        errorLabel.setVisible(missing);

        if (missing) {
            
            // after 3 secs. 
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(3));
                    
            pause.setOnFinished(e -> { 
                
                // remove error label
                errorLabel.setVisible(false);
                
                // restore to dafault (black) all labels (change all, don´t care if some weren't missing)
                imageUploadLabel.setStyle("-fx-text-fill: black");
                titleLabel.setStyle("-fx-text-fill: black");
                tagsLabel.setStyle("-fx-text-fill: black");
                starsLabel.setStyle("-fx-text-fill: black");
            });

            pause.play();
        }

        // return if there was any missing or not
        return missing;

    }


    public Post getCurrentPost() {
        return currentPost;
    }

    @FXML
    void initialize() {
        
        // 1. create empty Post
        currentPost = new Post(); // create empty Post object to be filled with data as user creates the post    
    
        // 2. initialize stars array for easy access
        stars = new Label[] {star1, star2, star3, star4, star5};

        // stars start unselected
        for (Label star : stars) {
            star.setStyle("-fx-text-fill: #b9b9b9;");
        }
        currentPost.setStarRating(0);
        
        // 3. favourite starts unselected
        favouriteButton.setSelected(false);
        favouriteButton.setStyle("-fx-text-fill: #b9b9b9;");

        // 4. add today's date (date format: yyyy-mm-dd)
        currentPost.setDate(LocalDate.now() ); // add date to the Post object db
        dateLabel.setText(currentPost.getDate().toString() ); // display on window (make it String)
    }

    // met o go back to the feed after creating a post (called at the end of savePost() and cancelPost())
    private void goBackToFeed() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // take the user back to the feed page (switch scene)
            javafx.stage.Stage stage = (javafx.stage.Stage) titleField.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
