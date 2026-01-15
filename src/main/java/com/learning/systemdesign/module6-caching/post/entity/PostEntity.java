package com.learning.systemdesign.module6.post.entity;

import com.learning.systemdesign.module6.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts_m6") // M6 = Module 6
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user") // Prevent infinite recursion in toString
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_gen")
    @SequenceGenerator(name = "post_seq_gen", sequenceName = "posts_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // The OWNING side of the relationship.
    // This column 'user_id' will actually exist in the 'posts_m5' table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public PostEntity(String title, String body, UserEntity user) {
        this.title = title;
        this.body = body;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
