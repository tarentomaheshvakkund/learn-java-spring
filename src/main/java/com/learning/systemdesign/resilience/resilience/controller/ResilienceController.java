package com.learning.systemdesign.resilience.resilience.controller;

import com.learning.systemdesign.resilience.resilience.service.ExternalApiService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v10/payment")
public class ResilienceController {

    private final ExternalApiService externalApiService;

    public ResilienceController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayment")
    public String processPayment() {
        return externalApiService.callExternalSystem();
    }

    // FALLBACK METHOD
    // Must have same signature as the original method + Exception parameter
    public String fallbackPayment(Exception e) {
        return "Fallback: Payment Queued (Server is busy). Error: " + e.getMessage();
    }
}
