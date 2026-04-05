package eus.ehu.data_access;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;

public class DbAccessManager {

    protected EntityManager db;
    protected EntityManagerFactory emf;

    public DbAccessManager() {
       final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        try {
            emf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new IllegalStateException("Failed to initialize Hibernate SessionFactory", e);
        }

        db = emf.createEntityManager();
        System.out.println("DataBase opened");

    }
    public void storeUser(User user) {
        db.getTransaction().begin();
        db.persist(user);
        db.getTransaction().commit();
    }

    public User findUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return db.createQuery("FROM User WHERE username = :username", User.class)
                 .setParameter("username", username)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }

    public User storeUserIfNotExists(User user) {
        User existingUser = findUserByUsername(user.getUsername());
        if (existingUser != null) {
            return existingUser;
        }

        storeUser(user);
        return user;
    }

    private boolean userAlreadyHasPostWithTitle(String username, String title) {
        if (username == null || username.isBlank() || title == null || title.isBlank()) {
            return false;
        }

        Long count = db.createQuery(
                "SELECT COUNT(p) FROM Post p WHERE p.author = :username AND p.title = :title",
                Long.class)
                .setParameter("username", username)
                .setParameter("title", title)
                .getSingleResult();

        return count != null && count > 0;
    }

    public void close() {
        db.close();
        System.out.println("DataBase is closed");
    }

    // vNEW VERSION W/ FIXED USER-POST LINKING LOGIC
    public void storePost(Post post) {
        // 1. Prevent the "Transaction already active" error
        // Check if there is an ongoing transaction before starting a new one
        if (!db.getTransaction().isActive()) {
            db.getTransaction().begin();
        }
        
        try {
            User author = post.getUser();
            if(author != null) {
                User managedAuthor = null;
                
                // 2. ONLY query the database if the user already has an ID assigned
                // This prevents IllegalArgumentException for fake/unsaved users
                if (author.getId() != null) {
                    managedAuthor = db.find(User.class, author.getId());
                }
                
                // 3. If we didn't find them (or if it was our fake user without an ID), save them first
                if(managedAuthor == null) {
                    System.out.println("Author is new or fake, saving to database first to get an ID...");
                    db.persist(author); // This automatically assigns a real ID to the user object
                    managedAuthor = author; 
                }
                
                // Link the post and the author in both directions
                post.setUser(managedAuthor); 
                managedAuthor.getPosts().add(post); 

                if (userAlreadyHasPostWithTitle(managedAuthor.getUsername(), post.getTitle())) {
                    db.getTransaction().rollback();
                    System.out.println("Post already exists for this user/title. Skipping insert.");
                    return;
                }
            } else {
                System.out.println("Post has no author!");
            }
            
            // 4. Actually save the post to the database!
            db.persist(post);
            
            // Commit the transaction to make changes permanent
            db.getTransaction().commit();
            System.out.println("Post successfully saved to Database!");
            
        } catch (Exception e) {
            // If anything fails, rollback the transaction so the database doesn't hang
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Post> getAllPosts() {
        return db.createQuery("FROM Post", Post.class).getResultList();
    }
    public List<Post> getPostsByUser(String username) {
        return db.createQuery("FROM Post WHERE author = :username", Post.class)
                 .setParameter("username", username)
                 .getResultList();
    }

    public void storeComment(Post post, Comment comment) {
        if(post.getId() == null) {
            System.out.println("Post must be saved before adding comments!");
            return;
        }
        db.getTransaction().begin();
        Post managedPost = db.find(Post.class, post.getId());
        if(managedPost == null) {
            System.out.println("Post not found in database!");
            storePost(post); // if the post is not in the database, we need to save it first to get an ID
            managedPost = db.find(Post.class, post.getId());
        }
        managedPost.addComment(comment); // link the comment to the post in memory
        db.persist(comment);
        db.getTransaction().commit();
    }
}


