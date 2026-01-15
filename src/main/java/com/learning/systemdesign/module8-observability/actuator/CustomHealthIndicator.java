package com.learning.systemdesign.module8.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Custom Health Check for System Design Module 8.
 * Simulates checking an external service (like a Database or Payment Gateway).
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Simulation: 90% chance of being UP, 10% chance of being DOWN
        boolean isServiceUp = checkExternalService();

        if (isServiceUp) {
            return Health.up()
                    .withDetail("Database Service", "Available")
                    .withDetail("Latency", "45ms")
                    .build();
        } else {
            return Health.down()
                    .withDetail("Database Service", "Unreachable")
                    .withDetail("Error", "Connection Refused")
                    .build();
        }
    }

    private boolean checkExternalService() {
        // Randomly simulate failure to demonstrate "DOWN" status
        return new Random().nextInt(10) > 1; // 80-90% success rate
    }
}
