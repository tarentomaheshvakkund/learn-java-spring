package com.learning.javalearning.step6_string_templates;

/**
 * JAVA 21: STRING TEMPLATES (PREVIEW FEATURE - JEP 430)
 * 
 * NOTE: This is a PREVIEW feature. Run with: --enable-preview
 * 
 * String Templates provide a cleaner way to embed expressions in strings,
 * similar to template literals in JavaScript or f-strings in Python.
 * 
 * To run: java --enable-preview --source 21 StringTemplateExample.java
 */
public class StringTemplateExample {

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: String Templates (Preview) ===\n");

        // Since String Templates are a preview feature,
        // here's what the syntax looks like:

        demonstrateStringConcatenation();
        demonstrateStringFormat();
        // String Templates syntax shown in comments

        System.out.println("""
            -------------------------------------------
            STRING TEMPLATES SYNTAX (Preview Feature)
            -------------------------------------------
            
            // OLD: Concatenation
            String msg = "Hello, " + name + "! You are " + age + " years old.";
            
            // OLD: String.format()
            String msg = String.format("Hello, %s! You are %d years old.", name, age);
            
            // NEW: String Template (with STR processor)
            String msg = STR."Hello, \\{name}! You are \\{age} years old.";
            
            // NEW: With expressions
            String msg = STR."Next year you'll be \\{age + 1}";
            
            // NEW: Multi-line with expressions
            String json = STR.\"""
                {
                    "name": "\\{name}",
                    "age": \\{age},
                    "adult": \\{age >= 18}
                }
                \""";
            
            -------------------------------------------
            To enable preview features, run with:
            java --enable-preview --source 21 YourFile.java
            -------------------------------------------
            """);
    }

    static void demonstrateStringConcatenation() {
        System.out.println("--- 1. Traditional Concatenation ---");

        String name = "Alice";
        int age = 25;
        double balance = 1234.56;

        // Old way 1: Concatenation (messy with many variables)
        String message = "Hello, " + name + "! You are " + age + " years old. Balance: $" + balance;
        System.out.println(message);

        System.out.println();
    }

    static void demonstrateStringFormat() {
        System.out.println("--- 2. String.format() ---");

        String name = "Bob";
        int age = 30;
        double balance = 5678.90;

        // Old way 2: String.format (better, but format specifiers are verbose)
        String message = String.format("Hello, %s! You are %d years old. Balance: $%.2f", name, age, balance);
        System.out.println(message);

        // Using formatted() method (Java 15+)
        String message2 = "Hello, %s! Age: %d".formatted(name, age);
        System.out.println(message2);

        System.out.println();
    }
}
