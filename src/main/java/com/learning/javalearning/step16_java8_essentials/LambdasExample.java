package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(LambdasExample.class.getName());
    private static final String HEADER_BORDER   = "═══════════════════════════════════════════";
    private static final String SECTION_DIVIDER = "─────────────────────────────────";
    private static final String NAME_ALICE   = "Alice";
    private static final String NAME_CHARLIE = "Charlie";

    public static void main(String[] args) {
        logger.info(HEADER_BORDER);
        logger.info("  Step 16: Lambda Expressions Deep Dive   ");
        logger.info(HEADER_BORDER);

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
    @SuppressWarnings("java:S4276") // Boxed BinaryOperator used intentionally to teach lambda basics before primitive specializations
    static void basicLambdaSyntax() {
        logger.info("1️⃣ BASIC LAMBDA SYNTAX");
        logger.info(SECTION_DIVIDER);

        // No parameters
        Runnable noArgs = () -> logger.info("   Hello from lambda!");
        noArgs.run();

        // Single parameter (parentheses optional)
        Consumer<String> singleArg = name -> logger.log(Level.INFO, "   Hello, {0}!", name);
        singleArg.accept("Java 8");

        // Multiple parameters
        BinaryOperator<Integer> add = (a, b) -> a + b;
        logger.info(() -> "   5 + 3 = " + add.apply(5, 3));

        // With explicit types
        BinaryOperator<String> concat = (String a, String b) -> a + " " + b;
        logger.info(() -> "   Concat: " + concat.apply("Hello", "World"));

        // Multi-line lambda (needs curly braces and return)
        BinaryOperator<Integer> max = (a, b) -> {
            if (a >= b) {
                return a;
            } else {
                return b;
            }
        };
        logger.info(() -> "   Max(10, 20) = " + max.apply(10, 20));
    }

    // ============================================================
    // 2. Lambdas with Collections
    // ============================================================
    static void lambdaWithCollections() {
        logger.info("2️⃣ LAMBDAS WITH COLLECTIONS");
        logger.info(SECTION_DIVIDER);

        List<String> names = Arrays.asList(NAME_ALICE, "Bob", NAME_CHARLIE, "Diana", "Eve");

        // forEach with lambda - collect into a single log line
        StringBuilder namesSb = new StringBuilder("   Names:");
        names.forEach(name -> namesSb.append(" ").append(name));
        logger.info(namesSb::toString);

        // Sort with lambda (instead of Comparator anonymous class)
        List<String> sorted = new ArrayList<>(names);
        sorted.sort((a, b) -> a.length() - b.length());
        logger.info(() -> "   Sorted by length: " + sorted);

        // Reverse sort
        sorted.sort((a, b) -> b.compareTo(a));
        logger.info(() -> "   Reverse alphabetical: " + sorted);

        // removeIf with lambda
        List<String> filtered = new ArrayList<>(names);
        filtered.removeIf(name -> name.length() <= 3);
        logger.info(() -> "   Names longer than 3: " + filtered);

        // replaceAll with lambda
        List<String> upper = new ArrayList<>(names);
        upper.replaceAll(String::toUpperCase);
        logger.info(() -> "   Uppercase: " + upper);

        // Map.forEach - collect into a single log line
        Map<String, Integer> ages = Map.of(NAME_ALICE, 30, "Bob", 25, NAME_CHARLIE, 35);
        StringBuilder agesSb = new StringBuilder("   Ages:");
        ages.forEach((name, age) -> agesSb.append(" ").append(name).append("=").append(age));
        logger.info(agesSb::toString);
    }

    // ============================================================
    // 3. Variable Capture (Closures)
    // ============================================================
    static void lambdaVariableCapture() {
        logger.info("3️⃣ VARIABLE CAPTURE (CLOSURES)");
        logger.info(SECTION_DIVIDER);

        // Lambdas can capture 'effectively final' variables
        String greeting = "Hello";  // effectively final
        Consumer<String> greeter = name -> logger.log(Level.INFO, "   {0}, {1}!", new Object[]{greeting, name});
        greeter.accept("World");

        // Instance variables can be modified (they are accessed via 'this')
        int[] counter = {0};  // Array trick - reference is final, contents are mutable
        Runnable incrementer = () -> counter[0]++;
        incrementer.run();
        incrementer.run();
        incrementer.run();
        logger.info(() -> "   Counter after 3 increments: " + counter[0]);

        // Using effectively final loop variable
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int taskNumber = i;  // Must be final or effectively final
            tasks.add(() -> logger.log(Level.INFO, "   Task {0} running", taskNumber));
        }
        tasks.forEach(Runnable::run);

        // Note: Local variables captured by lambdas must be final or effectively final.
        // Attempting to modify a captured variable causes a compile error.
    }

    // ============================================================
    // 4. Lambda as Method Parameter
    // ============================================================
    static void lambdaAsParameter() {
        logger.info("4️⃣ LAMBDA AS METHOD PARAMETER");
        logger.info(SECTION_DIVIDER);

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Pass different lambdas to the same method
        logger.info(() -> "   Even numbers: " + filterList(numbers, n -> n % 2 == 0));
        logger.info(() -> "   Odd numbers:  " + filterList(numbers, n -> n % 2 != 0));
        logger.info(() -> "   Greater > 5:  " + filterList(numbers, n -> n > 5));
        logger.info(() -> "   Squares < 50: " + filterList(numbers, n -> n * n < 50));

        // Compose behaviors dynamically
        List<String> words = Arrays.asList("Apple", "banana", "Cherry", "date", "Elderberry");
        logger.info(() -> "   Starts with upper: " + filterList(words, w -> Character.isUpperCase(w.charAt(0))));
        logger.info(() -> "   Length > 5:        " + filterList(words, w -> w.length() > 5));
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
    @SuppressWarnings("java:S1604") // Anonymous classes kept intentionally to contrast with lambdas
    static void lambdaVsAnonymousClass() {
        logger.info("5️⃣ LAMBDA vs ANONYMOUS CLASS");
        logger.info(SECTION_DIVIDER);

        // ❌ Old way: Anonymous inner class
        Comparator<String> oldComparator = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        };

        // ✅ New way: Lambda expression
        Comparator<String> newComparator = (a, b) -> a.length() - b.length();

        // Both produce identical results
        List<String> cmpList = new ArrayList<>(Arrays.asList(NAME_CHARLIE, "Bob", NAME_ALICE));
        cmpList.sort(oldComparator);
        logger.info(() -> "   Sorted (old style): " + cmpList);
        cmpList.sort(newComparator);
        logger.info(() -> "   Sorted (new style): " + cmpList);

        // ❌ Old way: Runnable anonymous class
        Runnable oldRunnable = new Runnable() {
            @Override
            public void run() {
                logger.info("   Old style runnable");
            }
        };

        // ✅ New way: Lambda
        Runnable newRunnable = () -> logger.info("   Lambda runnable");

        oldRunnable.run();
        newRunnable.run();

        // Key differences:
        logger.info("   Key Differences:");
        logger.info("   ┌──────────────────────┬──────────────────────┐");
        logger.info("   │  Anonymous Class      │  Lambda Expression   │");
        logger.info("   ├──────────────────────┼──────────────────────┤");
        logger.info("   │  Verbose (5+ lines)   │  Concise (1 line)    │");
        logger.info("   │  Has own 'this'       │  'this' = enclosing  │");
        logger.info("   │  Can have state       │  Stateless           │");
        logger.info("   │  Multiple methods OK  │  Single method only  │");
        logger.info("   │  Create .class file   │  invokedynamic       │");
        logger.info("   └──────────────────────┴──────────────────────┘");
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
        logger.info("6️⃣ CUSTOM FUNCTIONAL INTERFACES");
        logger.info(SECTION_DIVIDER);

        // Use custom interface with lambda
        StringProcessor toUpper = String::toUpperCase;
        StringProcessor addExclaim = s -> s + "!!!";
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();

        logger.info(() -> "   toUpper: " + toUpper.process("hello"));
        logger.info(() -> "   addExclaim: " + addExclaim.process("wow"));
        logger.info(() -> "   reverse: " + reverse.process("Java"));

        // Composable validators
        Validator<String> notEmpty = s -> !s.isEmpty();
        Validator<String> minLength5 = s -> s.length() >= 5;
        Validator<String> noSpaces = s -> !s.contains(" ");

        // Compose validators
        Validator<String> passwordValidator = notEmpty.and(minLength5).and(noSpaces);

        logger.info("   Password validation:");
        logger.info(() -> "   'hello123' → " + passwordValidator.validate("hello123")); // true
        logger.info(() -> "   'hi'       → " + passwordValidator.validate("hi"));       // false
        logger.info(() -> "   'no way'   → " + passwordValidator.validate("no way"));   // false
        logger.info(() -> "   ''         → " + passwordValidator.validate(""));          // false
    }

    // ============================================================
    // 7. Real-World Lambda Examples
    // ============================================================
    static void realWorldExamples() {
        logger.info("7️⃣ REAL-WORLD LAMBDA EXAMPLES");
        logger.info(SECTION_DIVIDER);

        // Event handling pattern
        logger.info("   📌 Strategy Pattern with Lambdas:");
        processPayment(100.0, amount -> logger.info(() -> "   💳 Credit card payment: $" + amount));
        processPayment(50.0, amount -> logger.info(() -> "   🏦 Bank transfer: $" + amount));

        // Builder pattern with lambdas
        logger.info("   📌 Configuration with Lambdas:");
        Map<String, Object> config = buildConfig(cfg -> {
            cfg.put("host", "localhost");
            cfg.put("port", 8080);
            cfg.put("debug", true);
        });
        logger.info(() -> "   Config: " + config);

        // Retry logic with lambda
        logger.info("   📌 Retry Pattern:");
        retry(3, () -> {
            logger.info("   Attempting operation...");
            if (Math.random() > 0.5) {
                throw new IllegalStateException("Random failure");
            }
            logger.info("   ✅ Operation succeeded!");
        });

        logger.info(HEADER_BORDER);
        logger.info("  ✅ Lambda Expressions: Complete!        ");
        logger.info(HEADER_BORDER);
    }

    static void processPayment(double amount, DoubleConsumer paymentMethod) {
        paymentMethod.accept(amount);
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
                logger.log(Level.WARNING, "   Attempt {0} failed: {1}",
                        new Object[]{attempt, e.getMessage()});
                if (attempt == maxAttempts) {
                    logger.warning("   All attempts exhausted");
                }
            }
        }
    }
}
