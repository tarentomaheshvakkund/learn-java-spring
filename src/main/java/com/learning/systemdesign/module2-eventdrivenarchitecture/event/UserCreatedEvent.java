package com.learning.systemdesign.module2.event;

/**
 * EVENT (The Contract)
 * <p>
 * This record represents "What Happened".
 * It is immutable and contains only the data needed by listeners.
 */
public record UserCreatedEvent(Long userId, String email, String username) {
}
