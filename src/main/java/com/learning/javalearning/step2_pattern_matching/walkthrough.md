# Step 2 Walkthrough: Advanced Pattern Matching

In this step, we learn how to use the Records and Sealed Classes we created in Step 1 to write more elegant and type-safe logic.

---

## 1. Pattern Matching for `switch`
**File**: [SwitchPatternExample.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/igot/step2/SwitchPatternExample.java)

**Why it matters**: Traditionally, `switch` was limited to primitives, enums, and Strings. Now, you can switch on **types**.

### Key Advantages:
- **Exhaustiveness Check**: When using a `sealed` interface (like `Shape`), the compiler ensures you handle every possible case. You no longer need a `catch-all` default that might hide bugs.
- **Improved Syntax**: No more `if (obj instanceof Circle) { Circle c = (Circle) obj; ... }`.

---

## 2. Record Patterns (Destructuring)
**File**: [RecordPatternExample.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/igot/step2/RecordPatternExample.java)

**Why it matters**: Just like in JavaScript or Python, you can now "destructure" objects in Java.

Instead of:
```java
if (obj instanceof UserRecord user) {
    String email = user.email();
}
```

You can do:
```java
if (obj instanceof UserRecord(var id, var name, String email)) {
    // email is available directly!
}
```

---

## 3. Dedicated `null` Handling in Switch
You can now handle `null` explicitly in a switch label, avoiding the classic `NullPointerException` before the switch begins.

```java
return switch (obj) {
    case null -> "Handling the null case perfectly!";
    ...
};
```

---

### Ready for Step 3?
Next, we will explore the performance side of Java 21: **Step 3: Virtual Threads (Project Loom)**. 
