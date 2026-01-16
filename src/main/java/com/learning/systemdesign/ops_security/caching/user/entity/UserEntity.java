package com.learning.systemdesign.ops_security.caching.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.learning.systemdesign.ops_security.caching.post.entity.PostEntity;

/**
 * DOMAIN LAYER
 * <p>
 * This class represents the "Truth" of our data.
 * It is mapped directly to the database table 'users'.
 * <p>
 * DESIGN NOTE:
 * We use @Entity to mark this as a JPA Managed Object.
 * We use GenerationType.SEQUENCE because it is more performant than IDENTITY
 * for batch inserts (allows Hibernate to pre-fetch IDs).
 */
@Entity(name = "OpsSecurityCachingUserEntity")
@Table(name = "users_m6")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // The INVERSE side of the relationship.
    // 'mappedBy = "user"' tells Hibernate: "Go look at the 'user' field in
    // PostEntity to find the config".
    // CascadeType.ALL: If we save a User, save their posts too.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    // Helper method to add post (Good practice for bidirectional relationships)
    public void addPost(PostEntity post) {
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(PostEntity post) {
        posts.remove(post);
        post.setUser(null);
    }

    // We can add a custom constructor for easier creation
    // But we leave the no-args constructor for JPA (Lombok handles it)
    public UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
}
