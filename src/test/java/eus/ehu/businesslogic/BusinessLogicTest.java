package eus.ehu.businesslogic;

import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusinessLogicTest {

    private BlInterface businessLogic;

    @BeforeEach
    void setUp() {
        // Initialize the connection to the database before each test
        businessLogic = new BusinessLogic(); 
    }

    @Test
    void testSavePostAndRetrieveIt() {
        // 1. Create a fake user and a fake post for testing
        User testUser = new User();
        testUser.setUsername("junit_tester");
        testUser.setBio("I am a testing bot");

        Post testPost = new Post();
        testPost.setTitle("JUnit Automated Test Post");
        testPost.setDescription("If you see this, the test is working!");
        testPost.setDate(LocalDate.now());
        testPost.setStarRating(5.0);
        
        // Link them together just like we do in the controllers
        testPost.setUser(testUser);
        testPost.setAuthor(testUser.getUsername());

        // 2. Save the post using Business Logic (this connects to DbAccessManager)
        businessLogic.savePost(testPost);

        // 3. Retrieve all posts from the database
        List<Post> allPosts = businessLogic.getAllPosts();

        // 4. Verify our test post is in the database
        assertNotNull(allPosts, "The list of posts should not be null");
        
        boolean postFound = allPosts.stream()
                .anyMatch(p -> p.getTitle() != null && p.getTitle().equals("JUnit Automated Test Post"));
        
        assertTrue(postFound, "The post we just saved should be found in the database");
    }
}

// run with mvn clean test -> wait 4 the BUILD SUCCESS message in the console 