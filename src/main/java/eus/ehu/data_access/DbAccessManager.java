package eus.ehu.data_access;

import java.util.List;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import eus.ehu.usermodel.Comment;
import eus.ehu.usermodel.Post;
import eus.ehu.usermodel.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
        ensureLegacySchemaCompatibility();

    }

    private void ensureLegacySchemaCompatibility() {
        try {
            db.getTransaction().begin();
            // Legacy DBs may still have ISFAVOURITE as NOT NULL in posts.
            db.createNativeQuery("ALTER TABLE posts ALTER COLUMN ISFAVOURITE SET DEFAULT FALSE").executeUpdate();
            db.getTransaction().commit();
        } catch (Exception ignored) {
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }
            // Ignore when the column doesn't exist in fresh schemas.
        }
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
            if (existingUser.getPassword() == null && user.getPassword() != null) {
                db.getTransaction().begin();
                existingUser.setPassword(user.getPassword());
                db.merge(existingUser);
                db.getTransaction().commit();
            }
            return existingUser;
        }

        storeUser(user);
        return user;
    }

    public void close() {
        db.close();
        System.out.println("DataBase is closed");
    }


    public User getUserByUsername(String username) {
        try{
            return db.createQuery("FROM User u WHERE u.username = :username", User.class)
                             .setParameter("username", username)
                             .getSingleResult();
                
        } catch (Exception e) {
            System.out.println("User not found!");
            return null;
        }
    }

    public void storePost(Post post) {
        // Llama a la database y si está cerrada, la vuelve a abrir para evitar errores de conexión
        if (!db.getTransaction().isActive()) {
            db.getTransaction().begin();
        }
        
        try {
            //Conseguimos el autor.java del post para asegurarnos de que está guardado en la base de datos, confirmar que lo tenemos vaya
            User author = post.getUser();
            if(author != null) {
                User managedAuthor = null;
                
                // Y una vez conseguido el autor, intentamos encontrarlo en la base de datos por su ID (si lo tiene) para obtener el username desde la DB
                if (author.getId() != null) {
                    managedAuthor = db.find(User.class, author.getId());
                }
                
                // Si el autor no tiene ID o no se encuentra en la base de datos, lo guardamos primero para asegurarnos de que tiene un ID válido y evitar problemas de integridad referencial al guardar el post
                if(managedAuthor == null) {
                    System.out.println("Author is new or fake, saving to database first to get an ID...");
                    db.persist(author); // This automatically assigns a real ID to the user object
                    managedAuthor = author; 
                }

                Post existingPost = findPostByAuthorAndTitle(managedAuthor.getUsername(), post.getTitle());
                if (existingPost != null) {
                    // Keep legacy rows up to date instead of skipping inserts silently.
                    if (existingPost.getUser() == null) {
                        existingPost.setUser(managedAuthor);
                    }
                    if (existingPost.getAuthor() == null || existingPost.getAuthor().isBlank()) {
                        existingPost.setAuthor(managedAuthor.getUsername());
                    }
                    if (post.getDescription() != null && !post.getDescription().isBlank()) {
                        existingPost.setDescription(post.getDescription());
                    }
                    if (post.getImagePath() != null && !post.getImagePath().isBlank()) {
                        existingPost.setImagePath(post.getImagePath());
                    }
                    if (post.getStarRating() > 0.0) {
                        existingPost.setStarRating(post.getStarRating());
                    }

                    db.merge(existingPost);
                    post.setId(existingPost.getId());
                    post.setUser(existingPost.getUser());
                    post.setAuthor(existingPost.getAuthor());
                    db.getTransaction().commit();
                    System.out.println("Post already existed. Updated legacy fields for id: " + existingPost.getId());
                    return;
                }

                post.setUser(managedAuthor);
                if (post.getAuthor() == null || post.getAuthor().isBlank()) {
                    post.setAuthor(managedAuthor.getUsername());
                }
                managedAuthor.getPosts().add(post);
            } else {
                System.out.println("Post has no author!");
            }
            
            //Aqui ya si que guardas el post despues de tantas comprobaciones
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

    private Post findPostByAuthorAndTitle(String username, String title) {
        if (username == null || username.isBlank() || title == null || title.isBlank()) {
            return null;
        }

        return db.createQuery(
                "FROM Post p WHERE p.author = :username AND p.title = :title",
                Post.class)
                .setParameter("username", username)
                .setParameter("title", title)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Post> getAllPosts() {
        return db.createQuery("FROM Post p ORDER BY p.id DESC", Post.class).getResultList();
    }
    public List<Post> getPostsByUser(String username) {
        return db.createQuery(
            "FROM Post p WHERE (p.user IS NOT NULL AND p.user.username = :username) OR p.author = :username ORDER BY p.id DESC",
            Post.class)
                 .setParameter("username", username)
                 .getResultList();
    }

    public void updateFavouritePostForUser(String username, Post post, boolean isFavourite) {
        if (username == null || username.isBlank() || post == null || post.getId() == null) {
            return;
        }

        db.getTransaction().begin();
        try {
            User managedUser = db.createQuery("FROM User u LEFT JOIN FETCH u.favoritePosts WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            Post managedPost = db.find(Post.class, post.getId());

            if (managedUser == null || managedPost == null) {
                db.getTransaction().rollback();
                return;
            }

            if (isFavourite) {
                if (!managedUser.hasFavoritePost(managedPost)) {
                    managedUser.addFavoritePost(managedPost);
                }
            } else {
                managedUser.removeFavoritePost(managedPost);
            }

            db.merge(managedUser);
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Post> getFavouritePostsByUser(String username) {
        if (username == null || username.isBlank()) {
            return List.of();
        }

        return db.createQuery(
            "SELECT p FROM User u JOIN u.favoritePosts p WHERE u.username = :username ORDER BY p.id DESC",
                Post.class)
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

    public List<Comment> getAllComments() {
        return db.createQuery("FROM Comment", Comment.class).getResultList();
    }

    public void updateLikePost(Post post) {
        db.getTransaction().begin();
        
        // find post in the db
        Post managedPost = db.find(Post.class, post.getId());
            
        if(managedPost != null) {

            // sync db like count with the one from the post object (value updated in the controller
            managedPost.setLikeCount(post.getLikeCount());
            
            // save the updated post back to the database
            db.merge(managedPost);
        }
        db.getTransaction().commit();
    }
}


