package com.learning.systemdesign.module5.post.repository;

import com.learning.systemdesign.module5.post.entity.PostEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends ListCrudRepository<PostEntity, Long> {
    
    // Derived query method: "SELECT * FROM posts WHERE user_id = ?"
    List<PostEntity> findByUserId(Long userId);
}
