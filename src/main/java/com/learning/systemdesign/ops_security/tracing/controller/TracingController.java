package com.learning.systemdesign.ops_security.tracing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v16/trace")
public class TracingController {

    private static final Logger logger = LoggerFactory.getLogger(TracingController.class);

    @GetMapping
    public ResponseEntity<String> traceRequest() {
        logger.info("Request received at TracingController");
        
        internalServiceMethod();
        
        return ResponseEntity.ok("Check logs for Trace ID!");
    }

    private void internalServiceMethod() {
        logger.info("Processing inside internalServiceMethod. This should have the SAME Trace ID.");
    }
}
