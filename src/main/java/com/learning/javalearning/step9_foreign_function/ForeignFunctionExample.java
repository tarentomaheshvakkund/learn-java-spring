package com.learning.javalearning.step9_foreign_function;

import java.lang.foreign.*;
import java.lang.invoke.*;

/**
 * JAVA 21: FOREIGN FUNCTION & MEMORY API (JEP 442 - Final!)
 * 
 * The FFM API allows Java programs to:
 * 1. Call NATIVE CODE (C, Rust, etc.) without JNI
 * 2. Access OFF-HEAP MEMORY safely
 * 
 * This replaces the complex, unsafe, and error-prone JNI!
 * 
 * KEY COMPONENTS:
 * - MemorySegment: A contiguous region of memory
 * - Arena: Manages memory lifecycle (auto-cleanup!)
 * - Linker: Connects Java to native functions
 * - SymbolLookup: Finds native functions in libraries
 * - FunctionDescriptor: Describes native function signatures
 */
public class ForeignFunctionExample {

    public static void main(String[] args) throws Throwable {
        System.out.println("=== JAVA 21: Foreign Function & Memory API ===\n");

        // 1. Off-heap memory management
        demonstrateMemorySegments();

        // 2. Memory layouts for structured data
        demonstrateMemoryLayouts();

        // 3. Calling native C functions
        demonstrateNativeCalls();

        // 4. Compare with JNI
        compareWithJNI();
    }

    /**
     * MemorySegment: Safe off-heap memory access
     */
    static void demonstrateMemorySegments() {
        System.out.println("--- 1. Memory Segments (Off-Heap Memory) ---\n");

        // Arena manages memory lifecycle - auto-closes!
        try (Arena arena = Arena.ofConfined()) {

            // Allocate 100 bytes of off-heap memory
            MemorySegment segment = arena.allocate(100);
            System.out.println("  Allocated: " + segment.byteSize() + " bytes");

            // Write data at specific offsets
            segment.set(ValueLayout.JAVA_INT, 0, 42); // Write int at offset 0
            segment.set(ValueLayout.JAVA_LONG, 8, 123456L); // Write long at offset 8
            segment.set(ValueLayout.JAVA_DOUBLE, 16, 3.14); // Write double at offset 16

            // Read data back
            int intValue = segment.get(ValueLayout.JAVA_INT, 0);
            long longValue = segment.get(ValueLayout.JAVA_LONG, 8);
            double doubleValue = segment.get(ValueLayout.JAVA_DOUBLE, 16);

            System.out.println("  Read back:");
            System.out.println("    int at 0:    " + intValue);
            System.out.println("    long at 8:   " + longValue);
            System.out.println("    double at 16: " + doubleValue);

            // Allocate an array of ints
            MemorySegment intArray = arena.allocate(ValueLayout.JAVA_INT, 5);
            for (int i = 0; i < 5; i++) {
                intArray.setAtIndex(ValueLayout.JAVA_INT, i, i * 10);
            }

            System.out.println("\n  Int array: ");
            System.out.print("    ");
            for (int i = 0; i < 5; i++) {
                System.out.print(intArray.getAtIndex(ValueLayout.JAVA_INT, i) + " ");
            }
            System.out.println();

        } // Memory automatically freed here!

        System.out.println("\n  ✅ Memory auto-freed when Arena closes!\n");
    }

    /**
     * MemoryLayout: Define structured data layouts (like C structs)
     */
    static void demonstrateMemoryLayouts() {
        System.out.println("--- 2. Memory Layouts (C-like Structs) ---\n");

        // Define a Point struct: { int x; int y; }
        StructLayout pointLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("x"),
                ValueLayout.JAVA_INT.withName("y"));

        System.out.println("  Point struct layout:");
        System.out.println("    Size: " + pointLayout.byteSize() + " bytes");

        // Get VarHandles for field access
        VarHandle xHandle = pointLayout.varHandle(MemoryLayout.PathElement.groupElement("x"));
        VarHandle yHandle = pointLayout.varHandle(MemoryLayout.PathElement.groupElement("y"));

        try (Arena arena = Arena.ofConfined()) {
            // Allocate a Point
            MemorySegment point = arena.allocate(pointLayout);

            // Set values using VarHandles
            xHandle.set(point, 0L, 10);
            yHandle.set(point, 0L, 20);

            // Read values
            int x = (int) xHandle.get(point, 0L);
            int y = (int) yHandle.get(point, 0L);

            System.out.println("    Point: (" + x + ", " + y + ")");
        }

        // More complex struct: Person { char name[32]; int age; double salary; }
        System.out.println("\n  Complex struct example:");
        System.out.println("""
                    StructLayout personLayout = MemoryLayout.structLayout(
                        MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("name"),
                        ValueLayout.JAVA_INT.withName("age"),
                        MemoryLayout.paddingLayout(4),  // Alignment padding
                        ValueLayout.JAVA_DOUBLE.withName("salary")
                    );
                """);
        System.out.println();
    }

    /**
     * Calling native C library functions
     */
    static void demonstrateNativeCalls() throws Throwable {
        System.out.println("--- 3. Calling Native Functions ---\n");

        // Get the native linker
        Linker linker = Linker.nativeLinker();

        // Look up standard C library
        SymbolLookup stdlib = linker.defaultLookup();

        // Find the 'strlen' function
        MemorySegment strlenAddr = stdlib.find("strlen")
                .orElseThrow(() -> new RuntimeException("strlen not found"));

        // Define function signature: size_t strlen(const char* str)
        FunctionDescriptor strlenDesc = FunctionDescriptor.of(
                ValueLayout.JAVA_LONG, // Return type: size_t (long)
                ValueLayout.ADDRESS // Parameter: char* (pointer)
        );

        // Create a MethodHandle to call strlen
        MethodHandle strlen = linker.downcallHandle(strlenAddr, strlenDesc);

        try (Arena arena = Arena.ofConfined()) {
            // Allocate a C string
            MemorySegment cString = arena.allocateUtf8String("Hello, FFM API!");

            // Call strlen!
            long length = (long) strlen.invoke(cString);
            System.out.println("  Called C strlen(\"Hello, FFM API!\")");
            System.out.println("  Result: " + length + " characters");
        }

        // Another example: calling 'abs'
        MemorySegment absAddr = stdlib.find("abs")
                .orElseThrow(() -> new RuntimeException("abs not found"));

        FunctionDescriptor absDesc = FunctionDescriptor.of(
                ValueLayout.JAVA_INT, // Return: int
                ValueLayout.JAVA_INT // Param: int
        );

        MethodHandle abs = linker.downcallHandle(absAddr, absDesc);
        int result = (int) abs.invoke(-42);
        System.out.println("\n  Called C abs(-42)");
        System.out.println("  Result: " + result);

        System.out.println("\n  ✅ No JNI boilerplate needed!\n");
    }

    /**
     * Compare FFM API with traditional JNI
     */
    static void compareWithJNI() {
        System.out.println("--- 4. FFM API vs JNI Comparison ---\n");

        System.out.println("""
                ❌ JNI WAY (Complex, Error-Prone):
                ┌─────────────────────────────────────────────────────────────────┐
                │ 1. Write Java native method declaration                         │
                │    public native int add(int a, int b);                         │
                │                                                                 │
                │ 2. Generate C header with javah/javac -h                        │
                │    JNIEXPORT jint JNICALL Java_MyClass_add(JNIEnv*, jobject,   │
                │                                            jint, jint);         │
                │                                                                 │
                │ 3. Write C implementation                                       │
                │    JNIEXPORT jint JNICALL Java_MyClass_add(JNIEnv *env,        │
                │        jobject obj, jint a, jint b) { return a + b; }          │
                │                                                                 │
                │ 4. Compile to shared library (.so/.dll/.dylib)                  │
                │ 5. Load library with System.loadLibrary()                       │
                │                                                                 │
                │ Problems:                                                       │
                │   - Error-prone C code                                          │
                │   - Complex build process                                       │
                │   - Memory management nightmares                                │
                │   - No compile-time type checking                               │
                └─────────────────────────────────────────────────────────────────┘

                ✅ FFM API WAY (Simple, Safe):
                ┌─────────────────────────────────────────────────────────────────┐
                │ // Everything in pure Java!                                     │
                │ Linker linker = Linker.nativeLinker();                          │
                │ SymbolLookup lookup = linker.defaultLookup();                   │
                │                                                                 │
                │ MethodHandle strlen = linker.downcallHandle(                    │
                │     lookup.find("strlen").get(),                                │
                │     FunctionDescriptor.of(JAVA_LONG, ADDRESS)                   │
                │ );                                                              │
                │                                                                 │
                │ try (Arena arena = Arena.ofConfined()) {                        │
                │     long len = (long) strlen.invoke(arena.allocateFrom("Hi!")); │
                │ }                                                               │
                │                                                                 │
                │ Benefits:                                                       │
                │   ✅ Pure Java - no C code needed                               │
                │   ✅ Safe memory management with Arena                          │
                │   ✅ Type-safe function descriptors                             │
                │   ✅ No complex build process                                   │
                └─────────────────────────────────────────────────────────────────┘
                """);

        System.out.println("  USE CASES:");
        System.out.println("  • Call existing C/C++/Rust libraries");
        System.out.println("  • High-performance computing (SIMD, GPU)");
        System.out.println("  • System-level programming");
        System.out.println("  • Database drivers, crypto libraries");
        System.out.println("  • Game engines, media processing\n");
    }
}
