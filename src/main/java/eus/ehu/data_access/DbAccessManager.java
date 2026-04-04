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
    public void close() {
        db.close();
        System.out.println("DataBase is closed");
    }

    public void storePost(Post post) {
        db.getTransaction().begin();
        User author = post.getUser();
        if(author != null) {
            User managedAuthor = db.find(User.class, author.getId());
            if(managedAuthor == null) {
                System.out.println("Author not found in database!");
                db.persist(author); // if the author is not in the database, we need to save it first to get an ID
                managedAuthor = db.find(User.class, author.getId());
            }
            post.setUser(managedAuthor); // link the post to the managed user entity
            if (post.getAuthor() == null) {
                post.setAuthor(managedAuthor.getUsername());
            }
            managedAuthor.getPosts().add(post); // link the post to the user in memory
            db.persist(post);
        }         else {
            System.out.println("Post has no author!");
        }
        db.getTransaction().commit();
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


