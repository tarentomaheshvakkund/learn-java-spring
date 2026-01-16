package com.learning.systemdesign.ops_security.caching.post.repository;

import com.learning.systemdesign.ops_security.caching.post.entity.PostEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("com.learning.systemdesign.ops_security.caching.post.repository.PostRepository")
public interface PostRepository extends ListCrudRepository<PostEntity, Long> {
    
    // Derived query method: "SELECT * FROM posts WHERE user_id = ?"
    List<PostEntity> findByUserId(Long userId);
}
