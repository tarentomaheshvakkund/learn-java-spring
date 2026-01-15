package com.learning.systemdesign.common.exception;

import java.time.LocalDateTime;

/**
 * Standard Error Response DTO.
 * <p>
 * This defines the contract for ALL API errors in our system.
 * Clients can rely on this structure to parse error messages.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
