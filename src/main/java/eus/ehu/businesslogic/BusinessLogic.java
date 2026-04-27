package eus.ehu.businesslogic;

import java.util.List;

import eus.ehu.data_access.DbAccessManager;
import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.Tag;
import eus.ehu.usermodel.User;

public class BusinessLogic implements BlInterface {
    
    private DbAccessManager dbManager = new DbAccessManager(); // real db manager
    
    // we store the logged-in user so all controllers can access it through the bl
    private User currentUser;

    
    // SINGLETON (only one bl instance allowed)
    private static final BusinessLogic INSTANCE = new BusinessLogic();

    private BusinessLogic() {
        // PRIVATE | prevent other new instances
    }
    
    public static BusinessLogic getInstance() {
        return INSTANCE;
    }
    // END SINGLETON


    public User getCurrentUser() {
        return this.currentUser;
    }

    public boolean login(String username, String password) {

        User dbUser = dbManager.getUserByUsername(username); // ask the database for a user with the given username
        

        // if the user doesn't exist, we create it with the provided credentials and store it in the database
        if (dbUser == null) {
            User newUser = new User(username, password);
            dbManager.storeUser(newUser); // save the new user in the bl for the rest of the controllers to access
            this.currentUser = newUser; 
            System.out.println("new user '" + username + "' created and logged in successfully");
            return true; // we consider that creating a new user is also a successful login

        }  else if (java.util.Objects.equals(dbUser.getPassword(), password)) {
            // we found the user + valid credentials
            
            this.currentUser = dbUser; // save the user in the bl for the rest of the controllers to access
            System.out.println("user '" + username + "' logged in successfully");
            return true;
        }

        // user exists but password is wrong
        return false; 

    }

    @Override
    public void addCommentToPost(Post post, Comment comment) {
        
        dbManager.storeComment(post, comment); // db stores the comment and updates the post's comment list
        System.out.println("saved comment '" + comment.getText() + "' from user '" + comment.getAuthor() + "'");

    }

    @Override
    public void savePost(Post post) {
        
        dbManager.storePost(post); // db stores the post and also updates the user's post list
        System.out.println("successfully saved post titled '" + post.getTitle() + "'");
    }

    @Override
    public void updateLikePost(Post post) {
        dbManager.updateLikePost(post); // db updates the like count of the post
    }

    @Override
    public void updateFavouriteForUser(String username, Post post, boolean isFavourite) {
        dbManager.updateFavouritePostForUser(username, post, isFavourite);
    }

    @Override
    public List<Post> getAllPosts() {
        
        return dbManager.getAllPosts(); // ask the real database to fetch all posts
    }
    @Override
    public List<Post> getPostsByTag(Tag tag) {
        return dbManager.getPostsByTag(tag); // ask the real database to fetch posts by a specific tag
    }

    @Override 
    public List<Post> getPostsByUser(String username) {
        return dbManager.getPostsByUser(username); // ask the real database to fetch posts by a specific user
    }

    @Override
    public List<Post> getFavouritePostsByUser(String username) {
        return dbManager.getFavouritePostsByUser(username);
    }

    @Override
    public List<Comment> getAllComments() {
        return dbManager.getAllComments(); // ask the real database to fetch all comments
    }
}