package eus.ehu.businesslogic;

import java.util.List;

import eus.ehu.data_access.DbAccessManager;
import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;

public class BusinessLogic implements BlInterface {
    
    private DbAccessManager dbManager = new DbAccessManager(); // real db manager
    

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
    public List<Post> getAllPosts() {
        
        return dbManager.getAllPosts(); // ask the real database to fetch all posts
    }
}