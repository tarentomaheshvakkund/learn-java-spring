package com.learning.systemdesign.ops_security.jwtsecurity.post.controller;

import com.learning.systemdesign.ops_security.jwtsecurity.post.model.CreatePostRequest;
import com.learning.systemdesign.ops_security.jwtsecurity.post.model.PostResponse;
import com.learning.systemdesign.ops_security.jwtsecurity.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v7/users")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // POST /api/v5/users/{userId}/posts
    @PostMapping("/{userId}/posts")
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        
        PostResponse response = postService.createPost(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
