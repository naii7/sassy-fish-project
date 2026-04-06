package eus.ehu.controllers;

import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;

import java.util.List;
import java.util.stream.Collectors;

import eus.ehu.businesslogic.BusinessLogic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.LinkedHashMap;

public class ProfileController {

    private static final String DEFAULT_PROFILE_RESOURCE = "/default_pfp.jpg";

    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private ImageView profileImageView; 
    @FXML private ImageView favourite1;
    @FXML private ImageView favourite2;
    @FXML private ImageView favourite3;
    @FXML private ScrollPane feedScroll;
    @FXML private VBox feedContainer;

    //private ManageProfileUseCase manageProfileUseCase;
    private BusinessLogic businessLogic;
    private User currentUser;

    // This method is called from FeedController right before switching scenes
    public void initData(BusinessLogic bl) {
        this.businessLogic = bl;
        this.currentUser = bl == null ? null : bl.getCurrentUser();

        // Now that we have the data, we can load the profile info
        loadUserProfile();
        loadFeedAndFavorites();
    }

    // Overload used by visual tests that pass an explicit profile user
    public void initData(BusinessLogic bl, User user) {
        this.businessLogic = bl;
        this.currentUser = user != null ? user : (bl == null ? null : bl.getCurrentUser());

        // Now that we have the data, we can load the profile info
        loadUserProfile();
        loadFeedAndFavorites();
    }

    @FXML
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
            if (currentUser == null) {
                if (usernameLabel != null) {
                    usernameLabel.setText("@guest");
                }
                if (bioLabel != null) {
                    bioLabel.setText("No user loaded.");
                }
                if (profileImageView != null) {
                    profileImageView.setImage(loadImage(null, DEFAULT_PROFILE_RESOURCE));
                }
                return;
            }
            
            //currentUser.setProfilePicturePath("path/to/profile/picture.jpg"); (cosa que no funciona de momento, las bases de datos pueden guardar .jpg??)
            if (usernameLabel != null) {
                usernameLabel.setText("@" + currentUser.getUsername());
            }
            if (bioLabel != null) {
                bioLabel.setText(currentUser.getBio());
            }
            if (profileImageView != null) {
                String profilePath = currentUser.getProfilePicturePath();
                if (isDefaultProfilePath(profilePath)) {
                    profilePath = null;
                }
                profileImageView.setImage(loadImage(profilePath, DEFAULT_PROFILE_RESOURCE));
            }
        } catch (Exception e) {
            setErrorMessage("Error loading profile: " + e.getMessage());
        }
    }

    private boolean isDefaultProfilePath(String path) {
        if (path == null || path.isBlank()) return true;
        String normalized = path.trim();
        return normalized.equals("default_pfp.jpg") || normalized.equals("/default_pfp.jpg");
    }

    private void loadFeedAndFavorites() {
        try {
            if (businessLogic == null) {
                showEmptyFeed();
                showDefaultFavorites();
                return;
            }
            
            List<Post> posts = currentUser == null
                    ? List.of()
                    : businessLogic.getPostsByUser(currentUser.getUsername());
            showFeedPosts(posts);

            try {
                List<Post> favouritePosts = currentUser == null
                        ? List.of()
                        : businessLogic.getFavouritePostsByUser(currentUser.getUsername());
                showFavouritePosts(favouritePosts);
            } catch (Exception e) {
                setErrorMessage("Error loading favourites: " + e.getMessage());
                showDefaultFavorites();
            }

        } catch (Exception e) {
            setErrorMessage("Error loading posts: " + e.getMessage());
            showEmptyFeed();
            showDefaultFavorites();
        }
    }


    private void showFeedPosts(List<Post> posts) {
        if (feedContainer == null) {
            return;
        }

        feedContainer.getChildren().clear();

        if (posts == null) {
            showEmptyFeed();
            return;
        }

        if (posts.isEmpty()) {
            showEmptyFeed();
            return;
        }

        for (Post post : posts) {
            feedContainer.getChildren().add(createPostCard(post));
        } 
    }

    public void showPosts(List<Post> posts) {
        showFeedPosts(posts);
    }

    private void showFavouritePosts(List<Post> favouritePosts) {
        List<Post> favoritesToShow = favouritePosts == null
            ? List.of()
            : favouritePosts.stream()
                .collect(Collectors.toMap(
                    this::favoriteKey,
                    post -> post,
                    (first, second) -> first,
                    LinkedHashMap::new))
                .values().stream()
                .limit(3)
                .collect(Collectors.toList());

        ImageView[] favouriteViews = {favourite1, favourite2, favourite3};
        for (int index = 0; index < favouriteViews.length; index++) {
            Post favouritePost = index < favoritesToShow.size() ? favoritesToShow.get(index) : null;
            String imagePath = favouritePost != null ? favouritePost.getImagePath() : null;
            setImage(favouriteViews[index], imagePath, "/default.png");
        }
    }

    private void showDefaultFavorites() {
        setImage(favourite1, null, "/default.png");
        setImage(favourite2, null, "/default.png");
        setImage(favourite3, null, "/default.png");
    }

    private String favoriteKey(Post post) {
        String author = post == null ? "" : post.getAuthor();
        if ((author == null || author.isBlank()) && post != null && post.getUser() != null) {
            author = post.getUser().getUsername();
        }

        String title = post == null ? "" : post.getTitle();
        return (author == null ? "" : author.trim()) + "::" + (title == null ? "" : title.trim());
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
        postImageView.setFitWidth(160);
        postImageView.setFitHeight(120);
        postImageView.setPreserveRatio(true);
        postImageView.setSmooth(true);

        StackPane imageContainer = new StackPane(postImageView);
        imageContainer.setPrefSize(180, 135);
        imageContainer.setMinSize(180, 135);
        imageContainer.setMaxSize(180, 135);
        StackPane.setAlignment(postImageView, javafx.geometry.Pos.CENTER);

        VBox postContent = new VBox(6);
        postContent.setStyle("-fx-padding: 18;");

        Label titleLabel = new Label(post.getTitle() == null ? "Untitled post" : post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(420);

        String author = post.getAuthor();
        if ((author == null || author.isBlank()) && post.getUser() != null) {
            author = post.getUser().getUsername();
        }
        Label authorLabel = new Label((author == null || author.isBlank()) ? "Unknown author" : "by " + author);
        authorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        authorLabel.setMaxWidth(420);

        Label descriptionLabel = new Label(post.getDescription() == null ? "" : post.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155;");
        descriptionLabel.setMaxWidth(420);
        descriptionLabel.setMinHeight(66);
        descriptionLabel.setPrefHeight(66);

        Label ratingLabel = new Label(formatRating(post.getStarRating()));
        ratingLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #d97706;");
        ratingLabel.setMaxWidth(420);

        postContent.getChildren().addAll(titleLabel, authorLabel, descriptionLabel, ratingLabel);
        postCard.getChildren().addAll(imageContainer, postContent);
        return postCard;
    }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            Parent root = loader.load();
            
            // get controller for the feed page
            FeedController feedController = loader.getController();

            // inject the business logic to the feed controller so it can load the posts from the db
            feedController.initData(this.businessLogic); // pass the bl so the feed can load the posts from the db
            

            // Navigate back to the feed in the current window.
            Stage stage = (javafx.stage.Stage) feedScroll.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Feed");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
