# Step 7b: Deserialization Filters (Java 17)

> ☕ **Java 17 Feature** - Security enhancement for deserialization!

## Why This Matters

Deserializing untrusted data is a **major security risk**:
- Remote Code Execution
- Denial of Service
- Data tampering

Java 17 enhanced filtering with context-specific filter factories.

---

## Basic Filter

```java
ObjectInputStream ois = new ObjectInputStream(input);
ois.setObjectInputFilter(filterInfo -> {
    Class<?> clazz = filterInfo.serialClass();
    if (clazz != null) {
        if (clazz.getName().startsWith("com.myapp.")) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return ObjectInputFilter.Status.REJECTED;
    }
    return ObjectInputFilter.Status.UNDECIDED;
});
```

---

## JVM-Wide Filter (System Property)

```bash
java -Djdk.serialFilter="maxdepth=5;maxrefs=500;com.myapp.**;java.lang.*;!*"
```

| Option | Meaning |
|--------|---------|
| `maxdepth=5` | Max 5 levels of nesting |
| `maxrefs=500` | Max 500 object references |
| `com.myapp.**` | Allow com.myapp package |
| `!*` | Block everything else |

---

## Filter Factory (Java 17+)

```java
ObjectInputFilter.Config.setSerialFilterFactory(
    (currentFilter, streamFilter) -> {
        // Combine or customize filters based on context
        return ObjectInputFilter.merge(currentFilter, streamFilter);
    }
);
```

---

## Best Practices

1. ✅ **Avoid deserializing untrusted data** - use JSON instead
2. ✅ **Use allowlist**, not blocklist
3. ✅ **Set limits**: maxdepth, maxrefs, maxbytes
4. ✅ **Test filters** with your serialized objects

---

## Run Example

```bash
cd src/main/java
java com/learning/javalearning/step7b_java17_deserialization_filters/DeserializationFilterExample.java
```
