package com.learning.systemdesign.module13_ratelimiting.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v13/limited")
public class RateLimitedController {

    @GetMapping
    @RateLimiter(name = "basic", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<String> limitedEndpoint() {
        return ResponseEntity.ok("Request Successful");
    }

    public ResponseEntity<String> rateLimitFallback(Throwable t) {
        return ResponseEntity.status(429).body("Too Many Requests - Rate Limit Exceeded");
    }
}
