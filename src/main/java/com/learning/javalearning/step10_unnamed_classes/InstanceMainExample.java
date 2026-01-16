package com.learning.javalearning.step10_unnamed_classes;

/**
 * JAVA 21: UNNAMED CLASSES & INSTANCE MAIN METHODS (JEP 445 - Preview)
 * 
 * This feature makes Java more beginner-friendly by:
 * 1. Allowing simpler main() method declarations
 * 2. Allowing unnamed (implicit) classes for simple programs
 * 
 * EVOLUTION OF main() METHOD:
 * 
 * Traditional (required until Java 21):
 * public static void main(String[] args) { ... }
 * 
 * Java 21 Preview options:
 * static void main(String[] args) { ... } // No 'public' needed
 * static void main() { ... } // No args needed
 * void main() { ... } // Instance method!
 * 
 * NOTE: This is a PREVIEW feature. Run with --enable-preview
 */
public class InstanceMainExample {

    // This class demonstrates the TRADITIONAL way
    // See the comments below for the NEW simplified ways

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: Unnamed Classes & Instance Main ===\n");

        demonstrateMainEvolution();
        demonstrateUnnamedClasses();
        demonstrateUseCases();
    }

    static void demonstrateMainEvolution() {
        System.out.println("--- 1. Evolution of main() Method ---\n");

        System.out.println("""
                TRADITIONAL JAVA (Before Java 21):
                ┌─────────────────────────────────────────────────────────────┐
                │ public class HelloWorld {                                   │
                │     public static void main(String[] args) {                │
                │         System.out.println("Hello, World!");                │
                │     }                                                       │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘

                Problems for beginners:
                ❌ Must understand 'public', 'static', 'void', 'String[]'
                ❌ Boilerplate before the first println
                ❌ Scary for new programmers!

                ─────────────────────────────────────────────────────────────────

                JAVA 21 PREVIEW - Simplified main():
                ┌─────────────────────────────────────────────────────────────┐
                │ // Option 1: Drop 'public'                                  │
                │ static void main(String[] args) { ... }                     │
                │                                                             │
                │ // Option 2: Drop 'String[] args' too                       │
                │ static void main() { ... }                                  │
                │                                                             │
                │ // Option 3: Instance method (not static!)                  │
                │ void main() { ... }                                         │
                └─────────────────────────────────────────────────────────────┘

                ✅ Less boilerplate
                ✅ Beginner-friendly
                ✅ Perfect for scripts and learning
                """);
    }

    static void demonstrateUnnamedClasses() {
        System.out.println("--- 2. Unnamed (Implicit) Classes ---\n");

        System.out.println("""
                TRADITIONAL JAVA:
                ┌─────────────────────────────────────────────────────────────┐
                │ // File: HelloWorld.java                                    │
                │ public class HelloWorld {                                   │
                │     public static void main(String[] args) {                │
                │         System.out.println("Hello!");                       │
                │     }                                                       │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘

                Lines of boilerplate: 5
                Lines of actual logic: 1

                ─────────────────────────────────────────────────────────────────

                JAVA 21 PREVIEW - Unnamed Class:
                ┌─────────────────────────────────────────────────────────────┐
                │ // File: HelloWorld.java                                    │
                │ void main() {                                               │
                │     System.out.println("Hello!");                           │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘

                ✅ No class declaration needed!
                ✅ No 'public static void main(String[] args)'
                ✅ Just write your code!

                The compiler creates an unnamed class implicitly.
                """);

        System.out.println("""
                MORE EXAMPLES:

                // Simple script
                ┌─────────────────────────────────────────────────────────────┐
                │ void main() {                                               │
                │     var name = "Alice";                                     │
                │     System.out.println("Hello, " + name);                   │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘

                // With helper methods
                ┌─────────────────────────────────────────────────────────────┐
                │ String greet(String name) {                                 │
                │     return "Hello, " + name + "!";                          │
                │ }                                                           │
                │                                                             │
                │ void main() {                                               │
                │     System.out.println(greet("World"));                     │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘

                // With fields
                ┌─────────────────────────────────────────────────────────────┐
                │ int counter = 0;                                            │
                │                                                             │
                │ void increment() { counter++; }                             │
                │                                                             │
                │ void main() {                                               │
                │     increment();                                            │
                │     increment();                                            │
                │     System.out.println("Counter: " + counter);              │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘
                """);
    }

    static void demonstrateUseCases() {
        System.out.println("--- 3. When to Use This Feature ---\n");

        System.out.println("""
                ✅ GOOD USE CASES:

                1. Learning Java (beginners)
                   - Focus on logic, not boilerplate
                   - Easier first experience

                2. Quick scripts and prototypes
                   - Like Python/JavaScript scripts
                   - Rapid experimentation

                3. Small utilities
                   - One-off tools
                   - Command-line helpers

                4. Teaching and tutorials
                   - Less to explain upfront
                   - Focus on concepts

                ─────────────────────────────────────────────────────────────────

                ❌ NOT RECOMMENDED FOR:

                1. Production applications
                   - Use proper class structure
                   - Clear organization matters

                2. Libraries and APIs
                   - Need proper public classes
                   - Package structure important

                3. Team projects
                   - Consistency matters
                   - Code reviews expect structure

                ─────────────────────────────────────────────────────────────────

                RUNNING UNNAMED CLASS PROGRAMS:

                // Save as: Script.java
                void main() {
                    System.out.println("Quick script!");
                }

                // Run with:
                java --enable-preview --source 21 Script.java
                """);
    }
}
