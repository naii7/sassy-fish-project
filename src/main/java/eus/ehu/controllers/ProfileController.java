package eus.ehu.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import eus.ehu.businesslogic.BusinessLogic;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ProfileController {

    private boolean editMode = false;
    private static final int FAVORITE_SLOTS = 3;

    private static final String DEFAULT_PROFILE_RESOURCE = "/default_pfp.jpg";

    private final Post[] favoriteSlotPosts = new Post[FAVORITE_SLOTS];
    private String draftProfilePicturePath;

    @FXML private TextField usernameField;
    @FXML private TextField bioField;
    @FXML private ImageView profileImageView; 
    @FXML private ImageView favourite1;
    @FXML private ImageView favourite2;
    @FXML private ImageView favourite3;
    @FXML private ScrollPane feedScroll;
    @FXML private VBox feedContainer;
    @FXML private Button saveChangesButton; 
    @FXML private Button editProfileButton;
    @FXML private Button backButton;

    private BusinessLogic businessLogic;
    private User currentUser;

    public void initData(BusinessLogic bl) {
        this.businessLogic = bl;
        this.currentUser = bl == null ? null : bl.getCurrentUser();

        // Cargamos la información del perfil una vez que tenemos el usuario, para evitar problemas de datos no cargados al abrir la vista directamente en modo edición
        loadUserProfile();
        loadFeedAndFavorites();
    }

    // Lo mismo que lo de arriba pero con user, para el edit mode pero con usuario
    public void initData(BusinessLogic bl, User user) {
        this.businessLogic = bl;
        this.currentUser = user != null ? user : (bl == null ? null : bl.getCurrentUser());

        loadUserProfile();
        loadFeedAndFavorites();
    }

    @FXML
    public void initialize() {
        if (profileImageView != null) {
            makeCircular(profileImageView);
            profileImageView.setCursor(Cursor.HAND);
        }
        if (feedScroll != null) {
            feedScroll.setFitToWidth(true);
        }

        setClickableFavouriteCursors();

        // DEFAULT 
        setMode(false);
    }

    private void loadUserProfile() {
        try {
            if (currentUser == null) {
                if (usernameField != null) {
                    usernameField.setText("@guest");
                }
                if (bioField != null) {
                    bioField.setText("No user loaded.");
                }
                if (profileImageView != null) {
                    profileImageView.setImage(loadImage(null, DEFAULT_PROFILE_RESOURCE));
                }
                return;
            }
            
            if (usernameField != null) {
                usernameField.setText(currentUser.getUsername());
            }
            if (bioField != null) {
                bioField.setText(currentUser.getBio());
            }
            if (profileImageView != null) {
                String profilePath = getDisplayedProfilePicturePath();
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
            favoriteSlotPosts[index] = favouritePost;
            String imagePath = favouritePost != null ? favouritePost.getImagePath() : null;
            setImage(favouriteViews[index], imagePath, "/default.png");
        }
    }

    private void showDefaultFavorites() {
        clearFavouriteSlots();
        renderFavouriteSlots();
    }

    private void clearFavouriteSlots() {
        for (int index = 0; index < favoriteSlotPosts.length; index++) {
            favoriteSlotPosts[index] = null;
        }
    }

    private void renderFavouriteSlots() {
        ImageView[] favouriteViews = {favourite1, favourite2, favourite3};
        for (int index = 0; index < favouriteViews.length; index++) {
            Post favouritePost = favoriteSlotPosts[index];
            String imagePath = favouritePost != null ? favouritePost.getImagePath() : null;
            setImage(favouriteViews[index], imagePath, "/default.png");
        }
    }

    private String favoriteKey(Post post) {
        String author = post == null ? "" : post.getAuthor();
        if ((author == null || author.isBlank()) && post != null && post.getUser() != null) {
            author = post.getUser().getUsername();
        }

        String title = post == null ? "" : post.getTitle();
        return (author == null ? "" : author.trim()) + "::" + (title == null ? "" : title.trim());
    }

    @FXML
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
                try {
                    return new Image(imagePath, true);
                } catch (Exception ignored) {
                    String resourcePath = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
                    var resourceUrl = getClass().getResource(resourcePath);
                    if (resourceUrl != null) {
                        return new Image(resourceUrl.toExternalForm(), true);
                    }
                }
            }
        } catch (Exception ignored) {
            // Si llegamos hasta aqui, es que el path ha fallado por completo, asi que cargamos el fallback
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

    private void makeCircular(ImageView imageView) {
        double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2;
        Circle clip = new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, radius);
        imageView.setClip(clip);
    }

    private void setClickableFavouriteCursors() {
        ImageView[] favouriteViews = {favourite1, favourite2, favourite3};
        for (ImageView favouriteView : favouriteViews) {
            if (favouriteView != null) {
                favouriteView.setCursor(Cursor.HAND);
            }
        }
    }

    private String getDisplayedProfilePicturePath() {
        if (editMode && draftProfilePicturePath != null && !draftProfilePicturePath.isBlank()) {
            return draftProfilePicturePath;
        }

        return currentUser == null ? null : currentUser.getProfilePicturePath();
    }

    // GO BACK TO VIEW MODE SAVING CHANGES
    @FXML
    private void saveChangesButtonClicked() {
        if (currentUser == null) {
            return;
        }

        String previousUsername = currentUser.getUsername();
        String newUsername = normalizeUsername(usernameField.getText());
        if (newUsername == null || newUsername.isBlank()) {
            setErrorMessage("Username cannot be empty.");
            return;
        }

        currentUser.setUsername(newUsername);
        currentUser.setBio(bioField.getText());

        if (draftProfilePicturePath != null && !draftProfilePicturePath.isBlank()) {
            currentUser.setProfilePicturePath(draftProfilePicturePath);
        }

        if (businessLogic != null) {
            businessLogic.updateUserProfile(currentUser, previousUsername);
            businessLogic.updateFavouritePostsForUser(currentUser.getUsername(), getSelectedFavouritePosts());
        }

        setMode(false);
        loadUserProfile();
        loadFeedAndFavorites();
        syncDraftStateFromCurrentUser();
        saveChangesButton.setVisible(false);
    }

    // GO BACK TO VIEW MODE WITHOUT SAVING CHANGES
    @FXML
    private void backButtonClicked() {
        loadUserProfile();
        loadFeedAndFavorites();
        setMode(false);
    }

    
    @FXML
    private void handleProfileImageClicked(MouseEvent event) {
        if (editMode) {
            openProfileImagePicker();
        }
    }

    @FXML
    private void handleFavouriteClicked(MouseEvent event) {
        int slotIndex = event.getSource() == favourite1 ? 0 : event.getSource() == favourite2 ? 1 : event.getSource() == favourite3 ? 2 : -1;
        if (slotIndex >= 0) {
            openFavouriteGallery(slotIndex);
        }
    }

    private void openProfileImagePicker() {
        if (profileImageView == null || profileImageView.getScene() == null) {
            return;
        }

        List<String> resourceImages = findBundledImageResources();
        if (resourceImages.isEmpty()) {
            setErrorMessage("No profile images were found in resources.");
            return;
        }

        Stage modalStage = createModalStage("Choose Profile Photo");
        VBox root = new VBox(16);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fffdf7, #f8fafc);");

        Label title = new Label("Choose a profile photo");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Images available in the resources folder");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        FlowPane gallery = new FlowPane();
        gallery.setHgap(14);
        gallery.setVgap(14);
        gallery.setPrefWrapLength(500);
        gallery.setPadding(new Insets(4));

        for (String resourcePath : resourceImages) {
            gallery.getChildren().add(createResourceImageCard(resourcePath, () -> {
                draftProfilePicturePath = resourcePath;
                profileImageView.setImage(loadImage(resourcePath, DEFAULT_PROFILE_RESOURCE));
                modalStage.close();
            }));
        }

        scrollPane.setContent(gallery);
        root.getChildren().addAll(title, subtitle, scrollPane);
        modalStage.setScene(new Scene(root, 620, 520));
        modalStage.showAndWait();
    }

    private void openFavouriteGallery(int slotIndex) {
        List<Post> favouritePosts = getVisibleFavouritePosts();
        Stage modalStage = createModalStage(slotIndex >= 0 ? "Favourite Posts for Slot " + (slotIndex + 1) : "Favourite Posts");

        VBox root = new VBox(16);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fffdf7, #f8fafc);");

        Label title = new Label(slotIndex >= 0 ? "Choose a favourite for slot " + (slotIndex + 1) : "Favourite posts");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label(slotIndex >= 0
                ? "Click one of your hearted posts to use it in the selected slot"
                : "Posts the user currently has marked as favourite");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        FlowPane gallery = new FlowPane();
        gallery.setHgap(14);
        gallery.setVgap(14);
        gallery.setPrefWrapLength(700);
        gallery.setPadding(new Insets(4));

        if (favouritePosts.isEmpty()) {
            Label empty = new Label("No favourite posts yet.");
            empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 15px;");
            gallery.getChildren().add(empty);
        } else {
            for (Post favouritePost : favouritePosts) {
                gallery.getChildren().add(createFavouritePostCard(favouritePost, slotIndex, modalStage));
            }
        }

        scrollPane.setContent(gallery);
        root.getChildren().addAll(title, subtitle, scrollPane);
        modalStage.setScene(new Scene(root, 820, 560));
        modalStage.showAndWait();
    }

    private VBox createResourceImageCard(String resourcePath, Runnable onSelect) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e2e8f0; -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);");

        ImageView preview = new ImageView(loadImage(resourcePath, DEFAULT_PROFILE_RESOURCE));
        preview.setFitWidth(110);
        preview.setFitHeight(110);
        preview.setPreserveRatio(true);
        preview.setSmooth(true);

        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        Label name = new Label(fileName);
        name.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");

        Button selectButton = new Button("Select");
        selectButton.setOnAction(event -> onSelect.run());
        selectButton.setStyle("-fx-background-color: #ff6b9d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        card.getChildren().addAll(preview, name, selectButton);
        card.setOnMouseClicked(event -> onSelect.run());
        return card;
    }

    private VBox createFavouritePostCard(Post post, int slotIndex, Stage modalStage) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e2e8f0; -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);");

        ImageView preview = new ImageView(loadImage(post.getImagePath(), "/default.png"));
        preview.setFitWidth(180);
        preview.setFitHeight(130);
        preview.setPreserveRatio(true);
        preview.setSmooth(true);

        Label title = new Label(post.getTitle() == null ? "Untitled post" : post.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label description = new Label(post.getDescription() == null ? "" : post.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(185);
        description.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        card.getChildren().addAll(preview, title, description);

        if (editMode && slotIndex >= 0) {
            Button useButton = new Button("Use in slot " + (slotIndex + 1));
            useButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            useButton.setOnAction(event -> {
                favoriteSlotPosts[slotIndex] = post;
                renderFavouriteSlots();
                modalStage.close();
            });
            card.getChildren().add(useButton);
        }

        card.setOnMouseClicked(event -> {
            if (editMode && slotIndex >= 0) {
                favoriteSlotPosts[slotIndex] = post;
                renderFavouriteSlots();
                modalStage.close();
            }
        });

        return card;
    }

    private List<String> findBundledImageResources() {
        Set<String> resources = new LinkedHashSet<>();
        collectImageResources(Paths.get(System.getProperty("user.dir"), "src/main/resources/profilePic"), resources);
        collectImageResources(Paths.get(System.getProperty("user.dir"), "target/classes"), resources);
        return new ArrayList<>(resources);
    }

    private void collectImageResources(Path baseDirectory, Set<String> resources) {
        if (baseDirectory == null || !Files.exists(baseDirectory)) {
            return;
        }

        try (var stream = Files.walk(baseDirectory)) {
            stream.filter(Files::isRegularFile)
                    .filter(this::isImageFile)
                    .map(baseDirectory::relativize)
                    .map(path -> "/" + path.toString().replace(File.separatorChar, '/'))
                    .forEach(resources::add);
        } catch (IOException e) {
            setErrorMessage("Error reading resources: " + e.getMessage());
        }
    }

    private boolean isImageFile(Path path) {
        if (path == null) {
            return false;
        }

        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || fileName.endsWith(".webp");
    }

    private Stage createModalStage(String title) {
        Stage stage = new Stage();
        stage.initOwner(profileImageView.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        return stage;
    }

    private List<Post> getVisibleFavouritePosts() {
        List<Post> favourites = new ArrayList<>();
        for (Post favouritePost : favoriteSlotPosts) {
            if (favouritePost != null && !favourites.contains(favouritePost)) {
                favourites.add(favouritePost);
            }
        }
        return favourites;
    }

    private List<Post> getSelectedFavouritePosts() {
        List<Post> selectedPosts = new ArrayList<>();
        for (Post favoriteSlotPost : favoriteSlotPosts) {
            if (favoriteSlotPost != null && !selectedPosts.contains(favoriteSlotPost)) {
                selectedPosts.add(favoriteSlotPost);
            }
        }
        return selectedPosts;
    }

    private void syncDraftStateFromCurrentUser() {
        draftProfilePicturePath = currentUser == null ? null : currentUser.getProfilePicturePath();
        clearFavouriteSlots();

        if (currentUser != null && businessLogic != null) {
            try {
                List<Post> favouritePosts = businessLogic.getFavouritePostsByUser(currentUser.getUsername());
                for (int index = 0; index < favouritePosts.size() && index < favoriteSlotPosts.length; index++) {
                    favoriteSlotPosts[index] = favouritePosts.get(index);
                }
            } catch (Exception e) {
                setErrorMessage("Error loading favourites: " + e.getMessage());
            }
        }

        renderFavouriteSlots();
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            return null;
        }

        String normalized = username.trim();
        while (normalized.startsWith("@")) {
            normalized = normalized.substring(1).trim();
        }
        return normalized;
    }

    @FXML
    private void homeButtonClicked() {
        try {

            loadUserProfile();
            loadFeedAndFavorites();
            setMode(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/FeedPage.fxml"));
            Parent root = loader.load();
            FeedController feedController = loader.getController();
            feedController.initData(this.businessLogic);

            Stage stage = (Stage) feedScroll.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Feed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editProfile() {
        loadUserProfile();
        loadFeedAndFavorites();
        setMode(true);
        syncDraftStateFromCurrentUser();
    }

    @FXML
    private void openProfile() {
        if (!editMode) {
            loadUserProfile();
            loadFeedAndFavorites();
        }
    }

    @FXML
    private void newPostButtonClicked() {
        openCreatePostView();
    }

    private void openCreatePostView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eus/ehu/createPost.fxml"));
            Parent createPostView = loader.load();

            CreatePostController controller = loader.getController();
            controller.initData(this.businessLogic);

            Stage newStage = new Stage();
            newStage.setScene(new Scene(createPostView));
            newStage.setTitle("create new post");
            newStage.showAndWait();

            loadFeedAndFavorites();
        } catch (Exception e) {
            System.err.println("error opening create post view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMode(boolean editable) {
        this.editMode = editable;

        usernameField.setEditable(editable);
        bioField.setEditable(editable);

        if (editable) {
            usernameField.setStyle("-fx-background-color: white; -fx-border-color: #f59e0b; -fx-border-radius: 10; -fx-background-radius: 10;");
            bioField.setStyle("-fx-background-color: white; -fx-border-color: #f59e0b; -fx-border-radius: 10; -fx-background-radius: 10;");
        } else {
            usernameField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            bioField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        }

        if (profileImageView != null) {
            profileImageView.setDisable(false);
            profileImageView.setOpacity(1.0);
        }

        setClickableFavouriteCursors();

        // activate/deactivate buttons
        saveChangesButton.setVisible(editable);
        saveChangesButton.setManaged(editable);
        editProfileButton.setVisible(!editable);
        backButton.setVisible(editable);
    }
}
