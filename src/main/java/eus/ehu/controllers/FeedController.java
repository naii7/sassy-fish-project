package eus.ehu.controllers;

import eus.ehu.data_access.DbAccessManager;
import eus.ehu.usermodel.Post;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FeedController {

    @FXML
    private ScrollPane feedScroll;
    @FXML
    private Button newPostButton;
    @FXML
    private Button profileButton;
    @FXML
    private VBox feedContainer;
    @FXML
    private HBox PostMockup;

    @FXML
    void initialize() {
        try {
            DbAccessManager dbManager = new DbAccessManager();
            showPosts(dbManager.getAllPosts());
            dbManager.close();
        } catch (Exception e) {
            System.err.println("Could not load feed from DB: " + e.getMessage());
        }
    }

    public void showPosts(List<Post> posts) {
        feedContainer.getChildren().clear();
        for (Post post : posts) {
            feedContainer.getChildren().add(createPostCard(post));
        }
    }
    @FXML
    void newPostButtonClicked() {
        System.out.println("New Post button clicked!");

    }
    @FXML
    void openProfile() {
        System.out.println("Profile button clicked!");
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
    
}