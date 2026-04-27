package eus.ehu.usermodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    private String username; // Unique username for each user
    private String password;
    private String profilePicturePath;

    @OneToMany(mappedBy = "user") // One user can have many posts, mapped by the "user" field in Post
    private java.util.List<Post> posts = new java.util.ArrayList<>();

    @ManyToMany
    @JoinTable( // Join table for the many-to-many relationship between User and Post (favorites)
        name = "user_favourite_posts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private java.util.List<Post> favoritePosts = new java.util.ArrayList<>();

    // Default constructor for JPA
    // Initializes the lists to avoid NullPointerExceptions
    public User() {
        this.posts = new java.util.ArrayList<>(); 
        this.favoritePosts = new java.util.ArrayList<>();
    }   
    
    
    public User(String username, String password){
        this.username = username;
        this.password = password;
        // Default values for new users
        this.bio="Hello! I'm new here.";
        this.location="Unknown";
        this.profilePicturePath="default_pfp.jpg";
        this.posts = new java.util.ArrayList<>();
        this.favoritePosts = new java.util.ArrayList<>();
    }
    
    public void addFavoritePost(Post post) {
        if (post == null) {
            return;
        }
        if (!hasFavoritePost(post)) {
            favoritePosts.add(post);
        }
    }

    public void removeFavoritePost(Post post) {
        if (post == null) {
            return;
        }
        if (post.getId() == null) {
            favoritePosts.remove(post);
            return;
        }
        favoritePosts.removeIf(p -> p.getId() != null && p.getId().equals(post.getId()));
    }

    public boolean hasFavoritePost(Post post) {
        if (post == null) {
            return false;
        }
        if (post.getId() == null) {
            return favoritePosts.contains(post);
        }
        return favoritePosts.stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(post.getId()));
    }

    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public java.util.List<Post> getPosts() {
        return posts;
    }

    public java.util.List<Post> getFavoritePosts() {
        return favoritePosts;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { 
        this.profilePicturePath = profilePicturePath;
    }

    
}
