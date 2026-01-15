package com.learning.systemdesign.module2.notification;

import com.learning.systemdesign.module2.event.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * LISTENER (The Consumer)
 * Lists to UserCreatedEvent from Module 2.
 */
@Service
public class WelcomeEmailListener {

    private static final Logger log = LoggerFactory.getLogger(WelcomeEmailListener.class);

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("========================================");
        log.info("[Module 2] EVENT RECEIVED: User Created");
        log.info("Sending Welcome Email to: {}", event.email());
        log.info("Body: Hello {}, welcome to our System Design course!", event.username());
        log.info("========================================");
    }
}
