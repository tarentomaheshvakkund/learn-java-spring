package com.learning.systemdesign.module2.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

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
@Entity
@Table(name = "users_m2")
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

    // We can add a custom constructor for easier creation
    // But we leave the no-args constructor for JPA (Lombok handles it)
    public UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
}
