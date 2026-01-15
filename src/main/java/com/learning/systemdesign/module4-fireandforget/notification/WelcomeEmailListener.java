package com.learning.systemdesign.module4.notification;

import com.learning.systemdesign.module4.event.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * LISTENER (The Consumer)
 * Lists to UserCreatedEvent from Module 2.
 */
@Component
public class WelcomeEmailListener {

    private static final Logger log = LoggerFactory.getLogger(WelcomeEmailListener.class);

    @Async("taskExecutor") // Run in a separate thread
    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        try {
            // Simulate slow SMTP server (3 seconds)
            Thread.sleep(3000);

            log.info("========================================");
            log.info("[Module 4 - ASYNC] Thread: {} | EVENT RECEIVED: User Created", Thread.currentThread().getName());
            log.info("Sending Welcome Email to: {}", event.email());
            log.info("Body: Hello {}, welcome to our System Design course!", event.username());
            log.info("========================================");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
