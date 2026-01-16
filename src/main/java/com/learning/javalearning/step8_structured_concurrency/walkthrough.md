# Step 8: Structured Concurrency & Scoped Values (Java 21 Preview)

> ⚠️ **Preview Features**: Require `--enable-preview` to run.

## Structured Concurrency (JEP 453)

Treats groups of concurrent tasks as a **single unit of work**.

### Key Strategies

| Strategy | Use Case | Behavior |
|----------|----------|----------|
| `ShutdownOnFailure` | Need ALL results | If any fails, cancel all |
| `ShutdownOnSuccess` | Need FIRST result | First success wins, cancel others |

### Example: Dashboard (All or Nothing)

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<User> user = scope.fork(() -> fetchUser());
    Subtask<Orders> orders = scope.fork(() -> fetchOrders());
    
    scope.join();           // Wait for all
    scope.throwIfFailed();  // Propagate errors
    
    // If ANY fails, ALL are cancelled!
    return new Dashboard(user.get(), orders.get());
}
```

### Example: Race (First Wins)

```java
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<>()) {
    scope.fork(() -> queryPrimaryDB());
    scope.fork(() -> queryCache());  // Faster!
    
    scope.join();
    return scope.result();  // Cache wins, DB cancelled!
}
```

---

## Scoped Values (JEP 446)

Modern replacement for `ThreadLocal` - immutable, inheritable, auto-cleanup.

### Basic Usage

```java
static final ScopedValue<String> USER = ScopedValue.newInstance();

ScopedValue.where(USER, "alice").run(() -> {
    String user = USER.get();  // "alice"
    processRequest();
});
// Outside: USER.get() throws NoSuchElementException
```

### Why Not ThreadLocal?

| Problem | ThreadLocal | ScopedValue |
|---------|------------|-------------|
| Memory leaks | Common (forget `remove()`) | Impossible |
| Virtual Threads | Poor performance | Optimized |
| Child threads | Not inherited | Inherited ✅ |

---

## Run Examples

```bash
cd src/main/java
java --enable-preview --source 21 com/learning/javalearning/step8_structured_concurrency/StructuredConcurrencyExample.java
java --enable-preview --source 21 com/learning/javalearning/step8_structured_concurrency/ScopedValuesExample.java
```

## Back to: [Step 3 - Virtual Threads](../step3_virtual_threads/walkthrough.md)
