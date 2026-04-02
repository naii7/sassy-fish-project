package eus.ehu.businesslogic;

import java.util.List;

import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;

public class BusinessLogic implements BlInterface {

    private List<Post> mockDatabase = new java.util.ArrayList<>(); 

    // this is a mock database, in a real application this would be replaced by actual database calls
    @Override
    public void addCommentToPost(Post post, Comment comment) {
        
        // 1. link the comment to the post in memory
        post.addComment(comment);

        // 2. TODO: save the comment to the database
        System.out.println("mock db: saved comment '" + comment.getText() + "' from user '" + comment.getAuthor() + "'");
    }

    @Override
    public void savePost(Post post) {
        // add the post to our fake database
        mockDatabase.add(post);
        System.out.println("mock db: successfully saved post titled '" + post.getTitle() + "'");
    }

    @Override
    public List<Post> getAllPosts() {
        // return the list of posts so the feed controller can display them
        return mockDatabase;
    }
}