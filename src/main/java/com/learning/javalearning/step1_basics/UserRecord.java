package com.learning.javalearning.step1_basics;

/**
 * STEP 1: RECORDS (Java 16+)
 * 
 * Records are a concise way to create "data carrier" classes.
 * They automatically provide:
 * - Private final fields
 * - Constructor
 * - Getters (named after the field, e.g., name())
 * - equals(), hashCode(), and toString()
 * 
 * Compare this to a traditional Java 11 class where you'd need ~50 lines of
 * code!
 */
public record UserRecord(Long id, String username, String email) {

  // You can add validation in a "Compact Constructor"
  public UserRecord {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
  }

  // You can still add instance methods
  public String getDisplayName() {
    return username + " (" + email + ")";
  }
}
