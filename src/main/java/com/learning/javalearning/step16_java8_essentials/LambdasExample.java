package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;

/**
 * Step 16: Lambda Expressions - The Heart of Java 8
 * 
 * Lambda expressions enable you to treat functionality as a method argument,
 * or code as data. They make instances of anonymous classes easier to write.
 * 
 * Syntax: (parameters) -> expression
 *     or: (parameters) -> { statements; }
 * 
 * Key concepts:
 * - Lambdas are anonymous functions
 * - They can be assigned to variables
 * - They can capture variables from enclosing scope (effectively final)
 * - They implement functional interfaces (single abstract method)
 */
public class LambdasExample {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  Step 16: Lambda Expressions Deep Dive   ");
        System.out.println("═══════════════════════════════════════════\n");

        basicLambdaSyntax();
        lambdaWithCollections();
        lambdaVariableCapture();
        lambdaAsParameter();
        lambdaVsAnonymousClass();
        customFunctionalInterface();
        realWorldExamples();
    }

    // ============================================================
    // 1. Basic Lambda Syntax
    // ============================================================
    static void basicLambdaSyntax() {
        System.out.println("1️⃣ BASIC LAMBDA SYNTAX");
        System.out.println("─────────────────────────────────\n");

        // No parameters
        Runnable noArgs = () -> System.out.println("   Hello from lambda!");
        noArgs.run();

        // Single parameter (parentheses optional)
        Consumer<String> singleArg = name -> System.out.println("   Hello, " + name + "!");
        singleArg.accept("Java 8");

        // Multiple parameters
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("   5 + 3 = " + add.apply(5, 3));

        // With explicit types
        BiFunction<String, String, String> concat = (String a, String b) -> a + " " + b;
        System.out.println("   Concat: " + concat.apply("Hello", "World"));

        // Multi-line lambda (needs curly braces and return)
        BiFunction<Integer, Integer, Integer> max = (a, b) -> {
            if (a >= b) {
                return a;
            } else {
                return b;
            }
        };
        System.out.println("   Max(10, 20) = " + max.apply(10, 20));

        System.out.println();
    }

    // ============================================================
    // 2. Lambdas with Collections
    // ============================================================
    static void lambdaWithCollections() {
        System.out.println("2️⃣ LAMBDAS WITH COLLECTIONS");
        System.out.println("─────────────────────────────────\n");

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");

        // forEach with lambda
        System.out.print("   Names: ");
        names.forEach(name -> System.out.print(name + " "));
        System.out.println();

        // Sort with lambda (instead of Comparator anonymous class)
        List<String> sorted = new ArrayList<>(names);
        sorted.sort((a, b) -> a.length() - b.length());
        System.out.println("   Sorted by length: " + sorted);

        // Reverse sort
        sorted.sort((a, b) -> b.compareTo(a));
        System.out.println("   Reverse alphabetical: " + sorted);

        // removeIf with lambda
        List<String> filtered = new ArrayList<>(names);
        filtered.removeIf(name -> name.length() <= 3);
        System.out.println("   Names longer than 3: " + filtered);

        // replaceAll with lambda
        List<String> upper = new ArrayList<>(names);
        upper.replaceAll(name -> name.toUpperCase());
        System.out.println("   Uppercase: " + upper);

        // Map.forEach
        Map<String, Integer> ages = Map.of("Alice", 30, "Bob", 25, "Charlie", 35);
        System.out.print("   Ages: ");
        ages.forEach((name, age) -> System.out.print(name + "=" + age + " "));
        System.out.println("\n");
    }

    // ============================================================
    // 3. Variable Capture (Closures)
    // ============================================================
    static void lambdaVariableCapture() {
        System.out.println("3️⃣ VARIABLE CAPTURE (CLOSURES)");
        System.out.println("─────────────────────────────────\n");

        // Lambdas can capture 'effectively final' variables
        String greeting = "Hello";  // effectively final
        Consumer<String> greeter = name -> System.out.println("   " + greeting + ", " + name + "!");
        greeter.accept("World");

        // Instance variables can be modified (they are accessed via 'this')
        int[] counter = {0};  // Array trick - reference is final, contents are mutable
        Runnable incrementer = () -> counter[0]++;
        incrementer.run();
        incrementer.run();
        incrementer.run();
        System.out.println("   Counter after 3 increments: " + counter[0]);

        // Using effectively final loop variable
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int taskNumber = i;  // Must be final or effectively final
            tasks.add(() -> System.out.println("   Task " + taskNumber + " running"));
        }
        tasks.forEach(Runnable::run);

        // COMPILE ERROR: local variable must be final or effectively final
        // int x = 10;
        // Runnable r = () -> System.out.println(x);
        // x = 20;  // This would cause compile error

        System.out.println();
    }

    // ============================================================
    // 4. Lambda as Method Parameter
    // ============================================================
    static void lambdaAsParameter() {
        System.out.println("4️⃣ LAMBDA AS METHOD PARAMETER");
        System.out.println("─────────────────────────────────\n");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Pass different lambdas to the same method
        System.out.println("   Even numbers: " + filterList(numbers, n -> n % 2 == 0));
        System.out.println("   Odd numbers:  " + filterList(numbers, n -> n % 2 != 0));
        System.out.println("   Greater > 5:  " + filterList(numbers, n -> n > 5));
        System.out.println("   Squares < 50: " + filterList(numbers, n -> n * n < 50));

        // Compose behaviors dynamically
        List<String> words = Arrays.asList("Apple", "banana", "Cherry", "date", "Elderberry");
        System.out.println("   Starts with upper: " + filterList(words, w -> Character.isUpperCase(w.charAt(0))));
        System.out.println("   Length > 5:        " + filterList(words, w -> w.length() > 5));

        System.out.println();
    }

    // Generic filter method that accepts a Predicate lambda
    static <T> List<T> filterList(List<T> list, Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (condition.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    // ============================================================
    // 5. Lambda vs Anonymous Class Comparison
    // ============================================================
    static void lambdaVsAnonymousClass() {
        System.out.println("5️⃣ LAMBDA vs ANONYMOUS CLASS");
        System.out.println("─────────────────────────────────\n");

        // ❌ Old way: Anonymous inner class
        Comparator<String> oldComparator = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        };

        // ✅ New way: Lambda expression
        Comparator<String> newComparator = (a, b) -> a.length() - b.length();

        // ❌ Old way: Runnable anonymous class
        Runnable oldRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("   Old style runnable");
            }
        };

        // ✅ New way: Lambda
        Runnable newRunnable = () -> System.out.println("   Lambda runnable");

        oldRunnable.run();
        newRunnable.run();

        // Key differences:
        System.out.println("\n   Key Differences:");
        System.out.println("   ┌──────────────────────┬──────────────────────┐");
        System.out.println("   │  Anonymous Class      │  Lambda Expression   │");
        System.out.println("   ├──────────────────────┼──────────────────────┤");
        System.out.println("   │  Verbose (5+ lines)   │  Concise (1 line)    │");
        System.out.println("   │  Has own 'this'       │  'this' = enclosing  │");
        System.out.println("   │  Can have state       │  Stateless           │");
        System.out.println("   │  Multiple methods OK  │  Single method only  │");
        System.out.println("   │  Create .class file   │  invokedynamic       │");
        System.out.println("   └──────────────────────┴──────────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 6. Custom Functional Interface
    // ============================================================

    @FunctionalInterface
    interface StringProcessor {
        String process(String input);
    }

    @FunctionalInterface
    interface Validator<T> {
        boolean validate(T value);
        
        // Can have default methods
        default Validator<T> and(Validator<T> other) {
            return value -> validate(value) && other.validate(value);
        }
        
        default Validator<T> or(Validator<T> other) {
            return value -> validate(value) || other.validate(value);
        }
        
        default Validator<T> negate() {
            return value -> !validate(value);
        }
    }

    static void customFunctionalInterface() {
        System.out.println("6️⃣ CUSTOM FUNCTIONAL INTERFACES");
        System.out.println("─────────────────────────────────\n");

        // Use custom interface with lambda
        StringProcessor toUpper = s -> s.toUpperCase();
        StringProcessor addExclaim = s -> s + "!!!";
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();

        System.out.println("   toUpper: " + toUpper.process("hello"));
        System.out.println("   addExclaim: " + addExclaim.process("wow"));
        System.out.println("   reverse: " + reverse.process("Java"));

        // Composable validators
        Validator<String> notEmpty = s -> !s.isEmpty();
        Validator<String> minLength5 = s -> s.length() >= 5;
        Validator<String> noSpaces = s -> !s.contains(" ");

        // Compose validators
        Validator<String> passwordValidator = notEmpty.and(minLength5).and(noSpaces);

        System.out.println("\n   Password validation:");
        System.out.println("   'hello123' → " + passwordValidator.validate("hello123")); // true
        System.out.println("   'hi'       → " + passwordValidator.validate("hi"));       // false
        System.out.println("   'no way'   → " + passwordValidator.validate("no way"));   // false
        System.out.println("   ''         → " + passwordValidator.validate(""));          // false

        System.out.println();
    }

    // ============================================================
    // 7. Real-World Lambda Examples
    // ============================================================
    static void realWorldExamples() {
        System.out.println("7️⃣ REAL-WORLD LAMBDA EXAMPLES");
        System.out.println("─────────────────────────────────\n");

        // Event handling pattern
        System.out.println("   📌 Strategy Pattern with Lambdas:");
        processPayment(100.0, amount -> {
            System.out.println("   💳 Credit card payment: $" + amount);
            return true;
        });
        processPayment(50.0, amount -> {
            System.out.println("   🏦 Bank transfer: $" + amount);
            return true;
        });

        // Builder pattern with lambdas
        System.out.println("\n   📌 Configuration with Lambdas:");
        Map<String, Object> config = buildConfig(cfg -> {
            cfg.put("host", "localhost");
            cfg.put("port", 8080);
            cfg.put("debug", true);
        });
        System.out.println("   Config: " + config);

        // Retry logic with lambda
        System.out.println("\n   📌 Retry Pattern:");
        retry(3, () -> {
            System.out.println("   Attempting operation...");
            if (Math.random() > 0.5) {
                throw new RuntimeException("Random failure");
            }
            System.out.println("   ✅ Operation succeeded!");
        });

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  ✅ Lambda Expressions: Complete!        ");
        System.out.println("═══════════════════════════════════════════");
    }

    static boolean processPayment(double amount, Function<Double, Boolean> paymentMethod) {
        return paymentMethod.apply(amount);
    }

    static Map<String, Object> buildConfig(Consumer<Map<String, Object>> configurator) {
        Map<String, Object> config = new HashMap<>();
        configurator.accept(config);
        return config;
    }

    static void retry(int maxAttempts, Runnable operation) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                operation.run();
                return;
            } catch (Exception e) {
                System.out.println("   ❌ Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxAttempts) {
                    System.out.println("   ⚠️ All attempts exhausted");
                }
            }
        }
    }
}
