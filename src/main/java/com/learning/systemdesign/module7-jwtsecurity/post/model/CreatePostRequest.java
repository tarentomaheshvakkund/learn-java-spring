package com.learning.systemdesign.module7.post.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 chars")
    String title,

    @NotBlank(message = "Body is required")
    String body
) {}
