package com.learning.systemdesign.common.exception;

/**
 * Custom Exception for Domain Logic.
 * <p>
 * SYSTEM DESIGN PRINCIPLE: Semantic Exceptions.
 * Instead of throwing generic RuntimeExceptions, we throw domain-specific exceptions.
 * This allows our GlobalExceptionHandler to know exactly what HTTP Status to return.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
