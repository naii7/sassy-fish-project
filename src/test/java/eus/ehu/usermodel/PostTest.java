package eus.ehu.usermodel;

// inspired form here: https://gist.github.com/juananpe/5c80302aa0b6127a7fd7dde0318bf1a5


import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void testPostCreationAndBasicData() {
        // 1. Test that the post correctly stores basic text fields
        Post post = new Post();
        post.setTitle("Sassy Fish is awesome");
        post.setAuthor("sassy_queen");
        post.setDescription("Just testing the domain model");

        assertEquals("Sassy Fish is awesome", post.getTitle(), "The title should match");
        assertEquals("sassy_queen", post.getAuthor(), "The author should match");
        assertEquals("Just testing the domain model", post.getDescription(), "The description should match");
    }

    @Test
    void testAddCommentsToPost() {
        // 2. Test that the comment lists work correctly
        Post post = new Post();
        Comment comment1 = new Comment("user1", "First comment!", LocalDate.now(), post);
        Comment comment2 = new Comment("user2", "Second comment!", LocalDate.now(), post);

        post.addComment(comment1);
        post.addComment(comment2);

        assertEquals(2, post.getComments().size(), "The post should have exactly 2 comments");
        assertEquals("First comment!", post.getComments().get(0).getText(), "The text of the first comment should match");
    }

    @Test
    void testAddTagsToPost() {
        // 3. Test that we can add and clear Tags
        Post post = new Post();
        post.addTag(Tag.FOOD);
        post.addTag(Tag.MUSIC);

        assertEquals(2, post.getTags().size(), "There should be exactly 2 tags");
        assertTrue(post.getTags().contains(Tag.FOOD), "The tag list should contain FOOD");

        post.clearTags();
        assertEquals(0, post.getTags().size(), "After clearing, there should be no tags");
    }

    @Test
    void testPostStarRating() {
        // 4. Test the star rating and favorites logic
        Post post = new Post();
        post.setStarRating(4.5);
        post.setIsFavourite(true);

        assertEquals(4.5, post.getStarRating(), "The star rating should be 4.5");
        assertTrue(post.getIsFavourite(), "The post should be marked as favourite");
    }
}