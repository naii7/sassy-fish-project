package com.example.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

public class CreatePostController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane imageDropArea;

    @FXML
    private ImageView imageView;

    @FXML
    private FlowPane tagContainer;

    @FXML
    void handleDragDropped(DragEvent event) {

    }

    @FXML
    void handleDragOver(DragEvent event) {

    }

    @FXML
    void handleImageClick(MouseEvent event) {
        // get the file chooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        
        // restrict to image files only
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // show the file chooser dialog (pop-up) & get the selected file
        File file = fileChooser.showOpenDialog(imageDropArea.getScene().getWindow());
        
        if (file != null) {
            // load the selected image into the ImageView
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }   

    @FXML
    void handleStarClicked(MouseEvent event) {
        
    }

    @FXML
    void initialize() {
        assert imageDropArea != null : "fx:id=\"imageDropArea\" was not injected: check your FXML file 'createPost.fxml'.";
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'createPost.fxml'.";
        assert tagContainer != null : "fx:id=\"tagContainer\" was not injected: check your FXML file 'createPost.fxml'.";

    }

}
