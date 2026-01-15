# Step 1 Walkthrough: Data Carriers & Hierarchy Control

This document explains the core Java features introduced after Java 11 that are essential for modern Spring development.

---

## 1. Java Records (Java 16+)
**File**: [UserRecord.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/igot/step1/UserRecord.java)

### The Problem in Java 11
To create a simple data holder for a User, you had to write:
```java
public class User {
    private final Long id;
    private final String name;
    public User(Long id, String name) { this.id = id; this.name = name; }
    public Long getId() { return id; }
    public String getName() { return name; }
    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { ... }
    @Override public String toString() { ... }
}
```

### The Solution: Records
A record reduces this to a single line. It is **immutable** by default and automatically generates the constructor, accessors, `equals`, `hashCode`, and `toString`.

**Key Features**:
- **Compact Constructor**: In [UserRecord.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/igot/step1/UserRecord.java#L19-L23), we added validation without repeating the field names.
- **Accessors**: Instead of `getId()`, you use `id()`.

---

## 2. Sealed Classes & Interfaces (Java 17+)
**File**: [Shape.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/igot/step1/Shape.java)

### What are they?
`sealed` allows you to control which classes can implement your interface. In our example, the `Shape` interface only allows `Circle`, `Rectangle`, and `Square`.

```java
public sealed interface Shape permits Circle, Rectangle, Square { ... }
```

### Why use them?
1. **Domain Integrity**: You prevent third parties from adding unexpected implementations (e.g., a "Triangle" where it doesn't belong).
2. **Exhaustiveness**: The compiler knows all possible subclasses. This is critical for **Pattern Matching** (Step 2), where a `switch` expression won't require a `default` case if all permitted types are covered.

---

## 3. Text Blocks (Java 15+)
**File**: [TextBlockExample.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/igot/step1/TextBlockExample.java)

### Before (Java 11)
```java
String query = "SELECT * FROM users " +
               "WHERE id = 1 " +
               "AND active = true;";
```

### After (Java 21)
Text blocks use triple quotes `"""` to preserve formatting and indentation.

```java
String query = """
    SELECT * FROM users
    WHERE id = 1
    AND active = true;
    """;
```

### Pro Tip:
The indentation of the text block is relative to the position of the closing `"""`. If you move the closing quotes to the left, the resulting string will have more leading whitespace.

---

### Summary Checklist
- [x] Use `record` for immutable DTOs.
- [x] Use `sealed` for controlled polymorphism.
- [x] Use `"""` for JSON, SQL, or multi-line strings.

**Ready for Step 2?** We will see how to combine these features using **Pattern Matching**.
