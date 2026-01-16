package com.learning.systemdesign.resilience.bulkhead.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v15/bulkhead")
public class BulkheadController {

    @GetMapping
    @Bulkhead(name = "bulkheadService", fallbackMethod = "bulkheadFallback")
    public ResponseEntity<String> heavyOperation() throws InterruptedException {
        // Simulate a slow operation (taking up a thread for 5 seconds)
        Thread.sleep(5000);
        return ResponseEntity.ok("Processed Heavy Operation");
    }

    public ResponseEntity<String> bulkheadFallback(Throwable t) {
        return ResponseEntity.status(503).body("Service Busy - Bulkhead Full");
    }
}
