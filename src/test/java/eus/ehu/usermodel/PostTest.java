package eus.ehu.usermodel;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void testPostCreationAndData() {
        // 1. Create a new post
        Post post = new Post();
        post.setTitle("My Sassy Test Post");
        post.setDescription("Testing my amazing social media app");
        post.setAuthor("test_user");
        post.setStarRating(4.5);

        // 2. Verify that the data was saved correctly in the object
        assertEquals("My Sassy Test Post", post.getTitle(), "The title should match");
        assertEquals("test_user", post.getAuthor(), "The author should match");
        assertEquals(4.5, post.getStarRating(), "The star rating should be 4.5");
    }

    @Test
    void testAddCommentToPost() {
        // 1. Setup a post
        Post post = new Post();
        post.setTitle("Post for comments");

        // 2. Setup a comment
        Comment comment = new Comment("commenter99", "I love this post!", LocalDate.now(), post);

        // 3. Add comment to post
        post.addComment(comment);

        // 4. Verify the comment is inside the post's list
        assertEquals(1, post.getComments().size(), "Post should have exactly 1 comment");
        assertEquals("commenter99", post.getComments().get(0).getAuthor(), "Comment author should match");
        assertEquals("I love this post!", post.getComments().get(0).getText(), "Comment text should match");
    }
}