# Step 12: Java 21 Best Practices & Migration Guide

## 🎯 Feature Summary

### Final Features (Production Ready)

| Feature | When to Use |
|---------|-------------|
| **Records** | DTOs, API responses, value objects |
| **Sealed Classes** | Controlled hierarchies, exhaustive switches |
| **Pattern Matching** | Type checking + casting, switch expressions |
| **Text Blocks** | JSON, SQL, HTML, multi-line strings |
| **Virtual Threads** | I/O-bound apps, high concurrency |
| **Sequenced Collections** | First/last access, reversing |
| **FFM API** | Native calls, off-heap memory |
| **KEM API** | Secure key exchange |

### Preview Features (Use with Caution)

| Feature | Status | Notes |
|---------|--------|-------|
| String Templates | Preview | May change; use for prototyping |
| Structured Concurrency | Preview | Great for new projects |
| Scoped Values | Preview | Replace ThreadLocal carefully |
| Unnamed Classes | Preview | Scripts and learning only |

---

## 🔄 Migration Guide

### From Java 11

```java
// ❌ Java 11: POJO with boilerplate
public class User {
    private final Long id;
    private final String name;
    public User(Long id, String name) {...}
    public Long getId() {...}
    public String getName() {...}
    @Override public boolean equals(Object o) {...}
    @Override public int hashCode() {...}
    @Override public String toString() {...}
}

// ✅ Java 21: Record
public record User(Long id, String name) {}
```

```java
// ❌ Java 11: instanceof + cast
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// ✅ Java 21: Pattern matching
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

```java
// ❌ Java 11: String concatenation
String json = "{\n" +
    "  \"name\": \"" + name + "\",\n" +
    "  \"age\": " + age + "\n" +
    "}";

// ✅ Java 21: Text blocks (+ String templates in preview)
String json = """
    {
      "name": "%s",
      "age": %d
    }
    """.formatted(name, age);
```

### From Java 17

```java
// ❌ Java 17: Manual first/last
String first = list.get(0);
String last = list.get(list.size() - 1);

// ✅ Java 21: Sequenced collections
String first = list.getFirst();
String last = list.getLast();
```

```java
// ❌ Java 17: Platform threads for I/O
ExecutorService executor = Executors.newFixedThreadPool(200);

// ✅ Java 21: Virtual threads
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
```

---

## 📋 Best Practices Checklist

### Records
- ✅ Use for immutable data carriers (DTOs, events)
- ✅ Use compact constructors for validation
- ❌ Don't use if you need mutable state
- ❌ Don't extend records (they're final)

### Sealed Classes
- ✅ Use for fixed set of implementations
- ✅ Combine with pattern matching switches
- ✅ Use for domain modeling (Payment types, States)
- ❌ Don't seal if extensibility is needed

### Pattern Matching
- ✅ Use with instanceof for type narrowing
- ✅ Use guarded patterns (`when`) for conditions
- ✅ Use underscore `_` for ignored values
- ❌ Don't over-nest patterns (readability)

### Virtual Threads
- ✅ Use for I/O-bound workloads
- ✅ Use `Executors.newVirtualThreadPerTaskExecutor()`
- ❌ Don't use for CPU-bound tasks
- ❌ Avoid `synchronized` blocks (prefer `ReentrantLock`)
- ❌ Don't pool virtual threads

### Sequenced Collections
- ✅ Use `getFirst()`/`getLast()` instead of index tricks
- ✅ Use `reversed()` for reverse iteration
- ✅ Remember reversed() returns a VIEW, not copy

### FFM API
- ✅ Use confined arena for single-thread access
- ✅ Use shared arena for multi-thread access
- ✅ Prefer FFM over JNI for new projects
- ❌ Don't forget to close arenas (use try-with-resources)

---

## 🚀 Quick Reference

### Create a Virtual Thread
```java
Thread.startVirtualThread(() -> {
    // Your code here
});
```

### Pattern Matching Switch
```java
String result = switch (shape) {
    case Circle c -> "Circle with radius " + c.radius();
    case Rectangle r when r.width() == r.height() -> "Square!";
    case Rectangle r -> "Rectangle";
    case null -> "No shape";
};
```

### Record with Validation
```java
public record Email(String value) {
    public Email {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
```

### Off-Heap Memory
```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment segment = arena.allocate(1024);
    segment.set(ValueLayout.JAVA_INT, 0, 42);
}
```

---

## 📊 Decision Matrix

| Need | Use |
|------|-----|
| Immutable data holder | Record |
| Fixed set of types | Sealed class/interface |
| Type check + extract | Pattern matching |
| Multi-line string | Text block |
| High concurrency I/O | Virtual threads |
| First/last element | Sequenced collections |
| Call native code | FFM API |
| Secure key exchange | KEM API |
| Concurrent task groups | Structured Concurrency |
| Thread context | Scoped Values |

---

## Next: [Step 13 - Capstone Project](../step13_capstone_project/walkthrough.md)

Build a real project combining all these features! 🎉
