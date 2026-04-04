package eus.ehu.controllers;

import eus.ehu.data_access.DbAccessManager;
import eus.ehu.businesslogic.BlInterface;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private ImageView profileImageView; 
    @FXML private ImageView favourite1;
    @FXML private ImageView favourite2;
    @FXML private ImageView favourite3;
    @FXML private ScrollPane feedScroll;
    @FXML private VBox feedContainer;

    //private ManageProfileUseCase manageProfileUseCase;
    private BlInterface businessLogic;
    private User currentUser;

    // This method is called from FeedController right before switching scenes
    public void initData(BlInterface bl, User user) {
        this.businessLogic = bl;
        this.currentUser = user;

        // Now that we have the data, we can load the profile info
        loadUserProfile();
        loadFeedAndFavorites();
    }

    @FXML
    /*public void initialize() {
        //manageProfileUseCase = new ManageProfileUseCase(); (TO SOLVE: Use case integration)
        loadUserProfile();
        loadFeedAndFavorites();
        if (profileImageView != null) {
            makeCircular(profileImageView);
        }
        if (feedScroll != null) {
            feedScroll.setFitToWidth(true);
        }
    }*/
    public void initialize() {
        // We only do visual setup here, data loading moved to initData()
        if (profileImageView != null) {
            makeCircular(profileImageView);
        }
        if (feedScroll != null) {
            feedScroll.setFitToWidth(true);
        }
    }

    private void loadUserProfile() {
        try {
            // Simulación de carga de usuario actual (TO SOLVE: Integrar con base de datos)
            currentUser = new User("sassy_user", "Sassy User");
            currentUser.setBio("This is my bio!");
            //currentUser.setProfilePicturePath("path/to/profile/picture.jpg"); (cosa que no funciona de momento, las bases de datos pueden guardar .jpg??)
            if (usernameLabel != null) {
                usernameLabel.setText("@" + currentUser.getUsername());
            }
            if (bioLabel != null) {
                bioLabel.setText(currentUser.getBio());
            }
            if (profileImageView != null) {
                profileImageView.setImage(loadImage(currentUser.getProfilePicturePath(), "/default pfp.jpg"));
            }
        } catch (Exception e) {
            setErrorMessage("Error loading profile: " + e.getMessage());
        }
    }

    private void loadFeedAndFavorites() {
        try {
            /*DbAccessManager dbManager = new DbAccessManager();
            List<Post> posts = dbManager.getAllPosts();*/

            // CRITICAL CHANGE: Use the injected businessLogic instead of creating a new DbAccessManager!
            // This ensures we reuse the same database connection
            List<Post> posts = businessLogic.getAllPosts();

            showFeedPosts(posts);
            showFavouritePosts(posts);

        } catch (Exception e) {
            setErrorMessage("Error loading posts: " + e.getMessage());
            showEmptyFeed();
            showDefaultFavorites();
        }
    }

    /*private void showFeedPosts(List<Post> posts) {
        if (feedContainer == null) {
            return;
        }

        feedContainer.getChildren().clear();
        if (posts == null || posts.isEmpty()) {
            showEmptyFeed();
            return;
        }

        for (Post post : posts) {
            feedContainer.getChildren().add(createPostCard(post));
        } // Ineficiente, pero suficiente para esta demo. Para mejorar, se podría implementar paginación o carga bajo demanda.
    }*/

    private void showFeedPosts(List<Post> posts) {
        if (feedContainer == null) {
            return;
        }

        feedContainer.getChildren().clear();
        
        // Filter posts to only show ones authored by the current user
        List<Post> userPosts = posts.stream()
                .filter(p -> p.getAuthor() != null && p.getAuthor().equals(currentUser.getUsername()))
                .collect(Collectors.toList());

        if (userPosts.isEmpty()) {
            showEmptyFeed();
            return;
        }

        for (Post post : userPosts) {
            feedContainer.getChildren().add(createPostCard(post));
        } 
    }

    public void showPosts(List<Post> posts) {
        showFeedPosts(posts);
    }

    private void showFavouritePosts(List<Post> posts) {
        List<Post> favouritePosts = posts == null ? List.of() : posts.stream()
            .filter(Post::getIsFavourite)
            .limit(3)
            .collect(Collectors.toList());

        ImageView[] favouriteViews = {favourite1, favourite2, favourite3};
        for (int index = 0; index < favouriteViews.length; index++) {
            Post favouritePost = index < favouritePosts.size() ? favouritePosts.get(index) : null;
            String imagePath = favouritePost != null ? favouritePost.getImagePath() : null;
            setImage(favouriteViews[index], imagePath, "/default.png");
        }
    }

    private void showDefaultFavorites() {
        setImage(favourite1, null, "/default.png");
        setImage(favourite2, null, "/default.png");
        setImage(favourite3, null, "/default.png");
    }

    private void showEmptyFeed() {
        if (feedContainer == null) {
            return;
        }

        Label emptyLabel = new Label("No posts yet.");
        emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 15px; -fx-padding: 12 8 12 8;");
        feedContainer.getChildren().setAll(emptyLabel);
    }

    private HBox createPostCard(Post post) {
        HBox postCard = new HBox(16);
        postCard.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e2e8f0; -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 12, 0, 0, 3);");
        postCard.setPrefWidth(680);

        ImageView postImageView = new ImageView(loadImage(post.getImagePath(), "/default.png"));
        postImageView.setFitWidth(180);
        postImageView.setFitHeight(135);
        postImageView.setPreserveRatio(true);
        postImageView.setSmooth(true);

        VBox postContent = new VBox(6);
        postContent.setStyle("-fx-padding: 18;");

        Label titleLabel = new Label(post.getTitle() == null ? "Untitled post" : post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label authorLabel = new Label(post.getAuthor() == null ? "Unknown author" : "by " + post.getAuthor());
        authorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        Label descriptionLabel = new Label(post.getDescription() == null ? "" : post.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155;");

        postContent.getChildren().addAll(titleLabel, authorLabel, descriptionLabel);
        postCard.getChildren().addAll(postImageView, postContent);
        return postCard;
    }

    private Image loadImage(String imagePath, String fallbackResource) {
        try {
            if (imagePath != null && !imagePath.isBlank()) {
                return new Image(imagePath, true);
            }
        } catch (Exception ignored) {
            // Fall back to bundled resource below.
        }

        var fallbackUrl = getClass().getResource(fallbackResource);
        if (fallbackUrl != null) {
            return new Image(fallbackUrl.toExternalForm(), true);
        }
        return null;
    }

    private void setImage(ImageView imageView, String imagePath, String fallbackResource) {
        if (imageView == null) {
            return;
        }

        Image image = loadImage(imagePath, fallbackResource);
        if (image != null) {
            imageView.setImage(image);
        }
    }

    private void setErrorMessage(String message) {
        System.err.println(message);
    }

    private void makeCircular(ImageView imageView){
        double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2;

        Circle clip = new Circle (imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, radius);
        imageView.setClip(clip);
    }

    @FXML
    private void backButtonClicked() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Navigate back to the feed in the current window.
            javafx.stage.Stage stage = (javafx.stage.Stage) feedScroll.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Feed");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
