package com.learning.systemdesign.resilience.idempotency.controller;

import com.learning.systemdesign.resilience.idempotency.annotation.Idempotent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v14/payment")
public class PaymentController {

    @PostMapping
    @Idempotent(headerName = "X-Request-Id")
    public ResponseEntity<String> processPayment(@RequestHeader(value = "X-Request-Id") String requestId) {
        // Business Logic Simulation
        System.out.println("Processing Payment for ID: " + requestId);
        
        return ResponseEntity.ok("Payment Processed Successfully for ID: " + requestId);
    }
}
