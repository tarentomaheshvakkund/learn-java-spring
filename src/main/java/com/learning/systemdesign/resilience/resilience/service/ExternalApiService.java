package com.learning.systemdesign.resilience.resilience.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class ExternalApiService {

    /**
     * Simulates a call to an external system (e.g., Payment Gateway).
     * This method is "Flaky" - it fails randomly.
     */
    public String callExternalSystem() {
        // Randomly fail to simulate network issues
        if (new Random().nextInt(10) < 7) { // 70% chance of failure!
            throw new RuntimeException("External System is Down!");
        }
        return "Success: Payment Processed (ID: " + new Random().nextInt(9999) + ")";
    }
}
