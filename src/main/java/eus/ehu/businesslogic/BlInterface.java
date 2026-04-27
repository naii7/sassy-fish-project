package eus.ehu.businesslogic;

import java.util.List;

import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.Tag;

public interface BlInterface {
    
    // adds a comment to a specific post and saves it to the database
    void addCommentToPost(Post post, Comment comment);
    
    // persists a newly created post into the database
    void savePost(Post post);

    // increments/decrements the like count of a post and updates it in the database
    void updateLikePost(Post post);

    // marks/unmarks a post as favourite for a concrete user and saves the relation in DB
    void updateFavouriteForUser(String username, Post post, boolean isFavourite);

    // retrieves a list of all posts currently stored in the databas
    List<Post> getAllPosts();

    // retrieves a list of posts created by a specific user
    List<Post> getPostsByUser(String username); 

    // retrieves the favourite posts for a specific user
    List<Post> getFavouritePostsByUser(String username);
    
    // retrieves a list of all comments currently stored in the database
    List<Comment> getAllComments();

    // filters posts by a specific tag
    List<Post> getPostsByTag(Tag tag);
}