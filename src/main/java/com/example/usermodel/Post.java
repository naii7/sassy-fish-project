package com.example.usermodel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import javafx.scene.image.Image;

public class Post {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENT ID
	private Long id;
    private String title;
    private String description;
    private boolean isFavourite;
    private double starRating = 0.0; // 1-5
    private List<Tag> tags = new ArrayList<>(); // ENUM of tags
    private LocalDate date;

    
    // IMAGE
    private Image image;
    private String imagePath;

    public Post() {
    }

    // GETTERS & SETTERS

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public double getStarRating() {
        return starRating;
    }

    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) { // tag w/ ENUM sintax (value)
        tags.add(tag);
    }

    public void clearTags() {
        tags.clear();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
}
