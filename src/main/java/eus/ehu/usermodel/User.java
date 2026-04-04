package eus.ehu.usermodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @OneToMany(mappedBy = "user")
    private java.util.List<Post> posts = new java.util.ArrayList<>();

    public User() {
        this.posts = new java.util.ArrayList<>();
    }   //Default constructor for JPA  

    public User(String username, String email){
        this.username = username;
        this.email = email;
        this.posts = new java.util.ArrayList<>();
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
    
    public java.util.List<Post> getPosts() {
        return posts;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { 
        this.profilePicturePath = profilePicturePath;
    }

    
}
