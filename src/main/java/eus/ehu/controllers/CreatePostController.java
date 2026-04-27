package eus.ehu.controllers;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import eus.ehu.businesslogic.BusinessLogic;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private BusinessLogic businessLogic;
    private User currentUser; // who is creating the post
    private boolean postShouldBeFavourite;

    // updated to receive the logged-in user
    public void initData(BusinessLogic bl) {
        this.businessLogic = bl;

        // get user from bl (it should be there cause you can't get to the create post screen without being logged in)
        this.currentUser = bl.getCurrentUser(); 

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
            
            // 2. display image in ImageView (visually)
            imageView.setImage(img);
        
            // 3. update Post object to store the path to the image (for later saving to db)
            currentPost.setImage(img);
            currentPost.setImagePath(file.toURI().toString() ); //convert the file's path (file.toURI()) to string (.toString()) to store it in the db
                // also automatically updates the Post.image
        }
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
            favouriteButton.setStyle("-fx-text-fill: red; -fx-font-size: 40px; -fx-background-color: transparent;");
        
        } else { // if not selected -> style grey
            favouriteButton.setStyle("-fx-text-fill: #b9b9b9; -fx-font-size: 40px; -fx-background-color: transparent;");
        }

        // keep local state and persist it after the post has been saved and has an id
        postShouldBeFavourite = isSelected;
    }

    @FXML
    void cancelPost() {

        // CLOSE WINDOW get back to previous screen (main feed)
        goBackToFeed();
        // no need to clear and reset everything cause we create a new CreatePostController 
        // (with empty Post and unselected fields) every time we open the CreatePost screen
    }

    @FXML
    void savePost() {

        // 1. update Post object w/ data from the fields
        currentPost.setTitle(titleField.getText()); // title
        currentPost.setDescription(descriptionField.getText()); // description
        currentPost.setDate(LocalDate.now()); // date (set to today's date automatically)
        currentPost.setLikeCount(0);

        // 2. set author of the post to the current logged-in user (get it from the bl)
        User postUser = businessLogic.getCurrentUser();
        currentPost.setUser(postUser);
        if (postUser != null) {
            currentPost.setAuthor(postUser.getUsername());
        }

        // 3. collect the selected tags

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

        // 4. check if all required fields are filled & handle error messages
        boolean error = errorCheck();

        // only proceed if there's nothing missing
        if (!error) {

            // 5. save Post to db
                // TO DO. create a FeedController w/ ObservableList<Post> feedPosts
                // feedPosts.add(currentPost);
                businessLogic.savePost(currentPost);

            // 6. save favourite relationship for the logged-in user
            User favouriteUser = businessLogic.getCurrentUser();
            if (favouriteUser != null) {
                businessLogic.updateFavouriteForUser(
                    favouriteUser.getUsername(),
                    currentPost,
                    postShouldBeFavourite
                );
            }

            // 8. go back to feed and refresh the posts to show the new post 
            // (instead of just going back without refreshing and having to click the profile button to see the new post in the feed)
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
        // "Please fill in all required fields" error message only visible if there's any missing field

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
        favouriteButton.setStyle("-fx-text-fill: #b9b9b9; -fx-font-size: 40px; -fx-background-color: transparent;");
        postShouldBeFavourite = false;

        // 4. add today's date (date format: yyyy-mm-dd)
        currentPost.setDate(LocalDate.now() ); // add date to the Post object db
        dateLabel.setText(currentPost.getDate().toString() ); // display on window (make it String)
    }

    // method to go back to the feed after creating a post (called at the end of savePost() and cancelPost())
    private void goBackToFeed() {
        
        // get the current window (the create post window)
        Stage stage = (Stage) titleField.getScene().getWindow();

        // close window
        stage.close();

    }

}
