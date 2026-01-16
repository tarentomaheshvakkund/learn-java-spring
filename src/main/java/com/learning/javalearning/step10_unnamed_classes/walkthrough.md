# Step 10: Unnamed Classes & Instance Main (Java 21 Preview)

> ⚠️ **Preview Feature**: Requires `--enable-preview` to run.

## The Problem

Traditional Java requires too much boilerplate for beginners:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello!");
    }
}
```

5 lines of ceremony for 1 line of logic! 😓

---

## Java 21 Solution

### Simplified main() Methods

```java
// Option 1: Drop 'public'
static void main(String[] args) { ... }

// Option 2: Drop 'String[] args'
static void main() { ... }

// Option 3: Instance method!
void main() { ... }
```

### Unnamed Classes

```java
// File: Hello.java - No class declaration needed!
void main() {
    System.out.println("Hello, World!");
}
```

### With Helper Methods

```java
// File: Greeter.java
String greet(String name) {
    return "Hello, " + name + "!";
}

void main() {
    System.out.println(greet("Alice"));
}
```

---

## When to Use

| ✅ Good For | ❌ Not For |
|-------------|-----------|
| Learning Java | Production apps |
| Quick scripts | Libraries/APIs |
| Prototyping | Team projects |
| Teaching | Large codebases |

---

## Running

```bash
# Save as Script.java
java --enable-preview --source 21 Script.java
```

## Back to: [Step 9 - Foreign Function API](../step9_foreign_function/walkthrough.md)
