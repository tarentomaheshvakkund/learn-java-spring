package com.learning.step1;

/**
 * STEP 1: TEXT BLOCKS (Java 15+)
 * 
 * Text blocks allow you to write multi-line strings without messy
 * concatenations (+) or escape characters (\n).
 */
public class TextBlockExample {

  public String getExampleJson() {
    // Notice the triple quotes """
    // The indentation is handled automatically based on the position of the closing
    // """.
    return """
        {
            "id": 1,
            "name": "Java 21 Demo",
            "features": ["Records", "Sealed Classes", "Text Blocks"]
        }
        """;
  }

  public String getExampleSql() {
    return """
        SELECT id, username, email
        FROM users
        WHERE active = true
        ORDER BY username ASC;
        """;
  }
}
