package com.learning.systemdesign.foundations.errelationship.post.service;

import com.learning.systemdesign.common.exception.ResourceNotFoundException;
import com.learning.systemdesign.foundations.errelationship.post.entity.PostEntity;
import com.learning.systemdesign.foundations.errelationship.post.model.CreatePostRequest;
import com.learning.systemdesign.foundations.errelationship.post.model.PostResponse;
import com.learning.systemdesign.foundations.errelationship.post.repository.PostRepository;
import com.learning.systemdesign.foundations.errelationship.user.entity.UserEntity;
import com.learning.systemdesign.foundations.errelationship.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        // 1. Fetch User (or throw 404)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // 2. Create Post
        PostEntity post = new PostEntity(request.title(), request.body(), user);
        
        // 3. Save (Cascade optional, but we save explicit for clarity)
        PostEntity savedPost = postRepository.save(post);
        
        // 4. Return DTO
        return PostResponse.fromEntity(savedPost);
    }
}
