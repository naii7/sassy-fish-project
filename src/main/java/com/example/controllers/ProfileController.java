package com.example.controllers;

import com.example.usermodel.User;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ProfileController {
    @FXML private TextField usernameField;
    @FXML private TextField bioField;
    @FXML private ComboBox<String> locationComboBox;
    @FXML private Label errorLabel;
    @FXML private ImageView profileImageView; 
    @FXML private ImageView favourite1;
    @FXML private ImageView favourite2;
    @FXML private ImageView favourite3;

    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ImageView editIconView;

    //private ManageProfileUseCase manageProfileUseCase;
    private User currentUser;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        //manageProfileUseCase = new ManageProfileUseCase(); (TO SOLVE: Use case integration)
        loadUserProfile();
        setEditMode(false);
        makeCircular(profileImageView);
        setupHoverEffect();
    }

    private void loadUserProfile() {
        try {
            // Simulación de carga de usuario actual (TO SOLVE: Integrar con base de datos)
            currentUser = new User("sassy_user", "Sassy User");
            currentUser.setBio("This is my bio!");
            currentUser.setLocation("New York");
            //currentUser.setProfilePicturePath("path/to/profile/picture.jpg");
            usernameField.setText(currentUser.getUsername());
            bioField.setText(currentUser.getBio());
            locationComboBox.getItems().addAll("New York", "Los Angeles", "Chicago", "Houston", "Miami");
            locationComboBox.setValue(currentUser.getLocation());
            if (currentUser.getProfilePicturePath() != null) {
                profileImageView.setImage(new Image(currentUser.getProfilePicturePath()));
            } 
        } catch (Exception e) {
            errorLabel.setText("Error loading profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditProfile() {
        setEditMode(true);
    } //TO SOLVE: Put it in the EditButton action in the FXML file

    @FXML
    private void handleSaveProfile() { //Again TO SOLVE with database integration
        errorLabel.setText("");
        if (usernameField.getText().isEmpty()) {
            errorLabel.setText("Username cannot be empty.");
            return;
        }
        if (bioField.getText().length() > 150) {
            errorLabel.setText("Bio cannot exceed 150 characters.");
            return;
        }
        if (locationComboBox.getValue() == null) {
            errorLabel.setText("Please select a location.");
            return;
        }

        try {
            currentUser.setUsername(usernameField.getText());
            currentUser.setBio(bioField.getText());
            currentUser.setLocation(locationComboBox.getValue());
            //manageProfileUseCase.updateProfile(currentUser); (TO SOLVE: Integrar con el caso de uso)
            setEditMode(false);
        } catch (Exception e) {
            errorLabel.setText("Error saving profile: " + e.getMessage());
        }
    }

    @FXML
    private void setEditMode(boolean editing) {
        isEditing = editing;

        //Textos a editar
        usernameField.setEditable(editing);
        bioField.setEditable(editing);
        locationComboBox.setDisable(!editing);

        //Vista de botones
        editButton.setDisable(editing);
        saveButton.setDisable(!editing);
        cancelButton.setDisable(!editing);
    }

    private void makeCircular(ImageView imageView){
        double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2;

        Circle clip = new Circle (imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, radius);
        imageView.setClip(clip);
    }

    @FXML
    private void handleCancelEdit() {
        loadUserProfile();
        setEditMode(false);
    }

    private void setupHoverEffect(){
        profileImageView.setOnMouseEntered(e -> {
            editIconView.setVisible(true);
        });

        profileImageView.setOnMouseExited(e -> {
            editIconView.setVisible(false);
        });
    }
}
