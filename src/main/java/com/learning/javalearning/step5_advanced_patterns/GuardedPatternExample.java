package com.learning.javalearning.step5_advanced_patterns;

/**
 * JAVA 21: GUARDED PATTERNS
 * 
 * Guarded patterns add a boolean condition (guard) to a pattern.
 * Syntax: case Pattern when condition -> ...
 */
public class GuardedPatternExample {

    record Person(String name, int age) {
    }

    record Order(String item, double amount, String status) {
    }

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: Guarded Patterns ===\n");

        // 1. Basic guards with records
        demonstrateBasicGuards();

        // 2. Guards with multiple conditions
        demonstrateComplexGuards();

        // 3. Practical example: Order processing
        demonstrateOrderProcessing();
    }

    static void demonstrateBasicGuards() {
        System.out.println("--- 1. Basic Guards ---");

        Person[] people = {
                new Person("Alice", 25),
                new Person("Bob", 17),
                new Person("Charlie", 65),
                new Person("Diana", 8)
        };

        for (Person p : people) {
            String category = switch (p) {
                case Person(String name, int age) when age < 13 -> name + " is a child";
                case Person(String name, int age) when age < 20 -> name + " is a teenager";
                case Person(String name, int age) when age < 60 -> name + " is an adult";
                case Person(String name, int age) -> name + " is a senior";
            };
            System.out.println(category);
        }

        System.out.println();
    }

    static void demonstrateComplexGuards() {
        System.out.println("--- 2. Complex Guards ---");

        record Student(String name, int grade, boolean scholarship) {
        }

        Student[] students = {
                new Student("Alice", 95, true),
                new Student("Bob", 45, false),
                new Student("Charlie", 75, false),
                new Student("Diana", 85, true)
        };

        for (Student s : students) {
            String status = switch (s) {
                // Top performer with scholarship
                case Student(String n, int g, boolean sc) when g >= 90 && sc ->
                    n + ": Dean's List (Scholarship)";

                // Top performer without scholarship
                case Student(String n, int g, boolean _) when g >= 90 ->
                    n + ": Dean's List";

                // Passing with scholarship
                case Student(String n, int g, boolean sc) when g >= 50 && sc ->
                    n + ": Pass (Scholarship)";

                // Just passing
                case Student(String n, int g, boolean _) when g >= 50 ->
                    n + ": Pass";

                // Failing
                case Student(String n, int _, boolean _) ->
                    n + ": Needs Improvement";
            };
            System.out.println(status);
        }

        System.out.println();
    }

    static void demonstrateOrderProcessing() {
        System.out.println("--- 3. Practical: Order Processing ---");

        Order[] orders = {
                new Order("Laptop", 1500.00, "pending"),
                new Order("Mouse", 25.00, "pending"),
                new Order("Keyboard", 75.00, "shipped"),
                new Order("Monitor", 300.00, "cancelled")
        };

        for (Order order : orders) {
            String action = switch (order) {
                // High-value pending orders need approval
                case Order(String item, double amt, String s) when s.equals("pending") && amt > 1000 ->
                    "APPROVAL REQUIRED: " + item + " ($" + amt + ")";

                // Low-value pending orders auto-approve
                case Order(String item, double amt, String s) when s.equals("pending") ->
                    "AUTO-APPROVED: " + item + " ($" + amt + ")";

                // Shipped orders
                case Order(String item, double _, String s) when s.equals("shipped") ->
                    "TRACKING: " + item + " is on the way";

                // Cancelled orders
                case Order(String item, double amt, String s) when s.equals("cancelled") ->
                    "REFUND: " + item + " - $" + amt + " returned";

                // Unknown status
                case Order(String item, double _, String s) ->
                    "UNKNOWN STATUS for " + item + ": " + s;
            };
            System.out.println(action);
        }

        System.out.println();
    }
}
