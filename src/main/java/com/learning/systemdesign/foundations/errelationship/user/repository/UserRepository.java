package com.learning.systemdesign.foundations.errelationship.user.repository;

import com.learning.systemdesign.foundations.errelationship.user.entity.UserEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORY LAYER (Data Access Object)
 * <p>
 * This interface handles all conversation with the Database.
 * <p>
 * DESIGN NOTE:
 * We extend ListCrudRepository (Spring Data 3), which returns Lists instead of Iterables,
 * making it friendlier for modern Java streams.
 * <p>
 * We DO NOT add business logic here. Only database queries.
 */
@Repository("com.learning.systemdesign.foundations.errelationship.user.repository.UserRepository")
public interface UserRepository extends ListCrudRepository<UserEntity, Long> {

    // Spring Data JPA automatically generates the SQL for this method signature!
    // SELECT * FROM users WHERE username = ?
    boolean existsByUsername(String username);

    // SELECT * FROM users WHERE email = ?
    boolean existsByEmail(String email);
    
    Optional<UserEntity> findByUsername(String username);
}
