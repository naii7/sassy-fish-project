package com.SassyMeProject.usermodel;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bio;
    private String location;


    @Column(nullable = false, unique = true)
    private String username;
    private String email;
    private String profilePicturePath;

    public User() {
    }   //Default constructor for JPA  

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

        // Getters y Setters básicos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { 
        this.profilePicturePath = profilePicturePath;
    }
}
