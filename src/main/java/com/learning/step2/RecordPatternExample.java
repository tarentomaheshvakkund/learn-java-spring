package com.learning.step2;

import com.learning.step1.UserRecord;

/**
 * STEP 2: RECORD PATTERNS (Java 21)
 * 
 * Record patterns allow you to "destructure" a record directly in
 * instanceof or switch labels, extracting its components instantly.
 */
public class RecordPatternExample {

  public void processObject(Object obj) {
    // Traditional instanceof (Java 16+)
    if (obj instanceof UserRecord user) {
      System.out.println("User is: " + user.username());
    }

    // Record Pattern (Java 21)
    // We "unpack" the record components directly!
    if (obj instanceof UserRecord(Long id, String username, String email)) {
      System.out.println("Processing ID " + id + " for " + username);
    }
  }

  public String getEmailDomain(Object obj) {
    return switch (obj) {
      // Destructuring in switch matching!
      case UserRecord(var id, var name, String email) -> {
        yield email.substring(email.indexOf("@") + 1);
      }
      default -> "unknown";
    };
  }
}
