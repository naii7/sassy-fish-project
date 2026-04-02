package eus.ehu.businesslogic;

import java.util.List;

import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;

public interface BlInterface {
    
    // adds a comment to a specific post
    void addCommentToPost(Post post, Comment comment);
    
    // saves a newly created post
    void savePost(Post post);

    // retrieves all posts for the main feed
    List<Post> getAllPosts();
}