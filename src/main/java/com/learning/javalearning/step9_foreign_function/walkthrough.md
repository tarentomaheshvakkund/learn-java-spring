# Step 9: Foreign Function & Memory API (Java 21 - Final!)

> ✅ **Final Feature**: No preview flags needed!

## What is FFM API?

The Foreign Function & Memory (FFM) API replaces JNI with a **pure Java** solution for:
1. **Calling native code** (C, Rust, etc.)
2. **Managing off-heap memory** safely

## Key Components

| Component | Purpose |
|-----------|---------|
| `MemorySegment` | Contiguous region of memory (on/off heap) |
| `Arena` | Manages memory lifecycle (auto-cleanup!) |
| `Linker` | Connects Java to native functions |
| `SymbolLookup` | Finds functions in native libraries |
| `FunctionDescriptor` | Describes native function signatures |
| `MemoryLayout` | Defines data structures (like C structs) |

---

## Memory Management

### Arena Types

```java
// 1. Confined: Single thread, explicit close
try (Arena arena = Arena.ofConfined()) {
    MemorySegment seg = arena.allocate(1024);
} // Auto-freed

// 2. Shared: Multi-thread access
try (Arena arena = Arena.ofShared()) { ... }

// 3. Auto: GC-managed (no try-with-resources)
Arena arena = Arena.ofAuto();

// 4. Global: Never freed (JVM lifetime)
MemorySegment seg = Arena.global().allocate(64);
```

### Read/Write Memory

```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment seg = arena.allocate(100);
    
    seg.set(ValueLayout.JAVA_INT, 0, 42);      // Write int
    seg.set(ValueLayout.JAVA_LONG, 8, 123L);   // Write long
    
    int val = seg.get(ValueLayout.JAVA_INT, 0); // Read
}
```

---

## Calling Native Functions

```java
// Get linker and lookup
Linker linker = Linker.nativeLinker();
SymbolLookup stdlib = linker.defaultLookup();

// Find strlen in C library
MemorySegment strlenAddr = stdlib.find("strlen").get();

// Define signature: size_t strlen(char*)
FunctionDescriptor desc = FunctionDescriptor.of(
    ValueLayout.JAVA_LONG,  // Return type
    ValueLayout.ADDRESS     // Parameter
);

// Create handle and call!
MethodHandle strlen = linker.downcallHandle(strlenAddr, desc);

try (Arena arena = Arena.ofConfined()) {
    long len = (long) strlen.invoke(
        arena.allocateFrom("Hello!")
    );
}
```

---

## FFM vs JNI

| Aspect | JNI | FFM API |
|--------|-----|---------|
| Code | Java + C | Pure Java ✅ |
| Build | Complex (.so/.dll) | None needed ✅ |
| Memory | Manual, error-prone | Arena (auto-cleanup) ✅ |
| Type Safety | None | FunctionDescriptor ✅ |
| Performance | Good | Great ✅ |

---

## Run Examples

```bash
cd src/main/java
# FFM API is final - no preview flags!
java com/learning/javalearning/step9_foreign_function/ForeignFunctionExample.java
java com/learning/javalearning/step9_foreign_function/MemoryArenaExample.java
```

## Back to: [Step 8 - Structured Concurrency](../step8_structured_concurrency/walkthrough.md)
