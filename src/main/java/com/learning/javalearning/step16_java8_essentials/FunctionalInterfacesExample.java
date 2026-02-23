package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;

/**
 * Step 16: Functional Interfaces - The Types Behind Lambdas
 * 
 * A functional interface has exactly ONE abstract method.
 * Java 8 provides many built-in functional interfaces in java.util.function.
 * 
 * Core interfaces:
 * - Predicate<T>      → T → boolean       (test)
 * - Function<T,R>     → T → R             (apply)
 * - Consumer<T>       → T → void          (accept)
 * - Supplier<T>       → () → T            (get)
 * - UnaryOperator<T>  → T → T             (apply)
 * - BinaryOperator<T> → (T, T) → T        (apply)
 * - BiFunction<T,U,R> → (T, U) → R        (apply)
 * - BiPredicate<T,U>  → (T, U) → boolean  (test)
 * - BiConsumer<T,U>   → (T, U) → void     (accept)
 */
public class FunctionalInterfacesExample {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Step 16: Functional Interfaces Deep Dive    ");
        System.out.println("═══════════════════════════════════════════════\n");

        predicateExamples();
        functionExamples();
        consumerExamples();
        supplierExamples();
        operatorExamples();
        biFunctionExamples();
        compositionExamples();
        primitiveSpecializations();
        realWorldPipeline();
    }

    // ============================================================
    // 1. Predicate<T> - Tests a condition, returns boolean
    // ============================================================
    static void predicateExamples() {
        System.out.println("1️⃣ PREDICATE<T> → T → boolean");
        System.out.println("─────────────────────────────────\n");

        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<String> isNotEmpty = s -> s != null && !s.isEmpty();
        Predicate<String> startsWithJ = s -> s.startsWith("J");

        // Basic usage
        System.out.println("   isEven(4): " + isEven.test(4));         // true
        System.out.println("   isEven(7): " + isEven.test(7));         // false
        System.out.println("   isNotEmpty(\"hi\"): " + isNotEmpty.test("hi")); // true

        // Combining predicates with and(), or(), negate()
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);
        Predicate<Integer> isOdd = isEven.negate();

        System.out.println("   isEvenAndPositive(4): " + isEvenAndPositive.test(4));   // true
        System.out.println("   isEvenAndPositive(-4): " + isEvenAndPositive.test(-4)); // false
        System.out.println("   isOdd(7): " + isOdd.test(7));                          // true
        System.out.println("   isEvenOrPositive(3): " + isEvenOrPositive.test(3));    // true

        // Predicate.isEqual() static method
        Predicate<String> isJava = Predicate.isEqual("Java");
        System.out.println("   isJava(\"Java\"): " + isJava.test("Java"));   // true
        System.out.println("   isJava(\"Python\"): " + isJava.test("Python")); // false

        // Using with collections
        List<String> languages = Arrays.asList("Java", "JavaScript", "Python", "Jython", "C++");
        List<String> jLanguages = new ArrayList<>(languages);
        jLanguages.removeIf(startsWithJ.negate());
        System.out.println("   J-languages: " + jLanguages);

        System.out.println();
    }

    // ============================================================
    // 2. Function<T, R> - Transforms input to output
    // ============================================================
    static void functionExamples() {
        System.out.println("2️⃣ FUNCTION<T, R> → T → R");
        System.out.println("─────────────────────────────────\n");

        Function<String, Integer> stringLength = String::length;
        Function<String, String> toUpper = String::toUpperCase;
        Function<Integer, String> intToString = n -> "Number: " + n;
        Function<String, String> addBrackets = s -> "[" + s + "]";

        // Basic usage
        System.out.println("   length(\"hello\"): " + stringLength.apply("hello"));     // 5
        System.out.println("   toUpper(\"hello\"): " + toUpper.apply("hello"));          // HELLO
        System.out.println("   intToString(42): " + intToString.apply(42));              // Number: 42

        // andThen() - executes after
        Function<String, String> upperThenBrackets = toUpper.andThen(addBrackets);
        System.out.println("   upperThenBrackets(\"hello\"): " + upperThenBrackets.apply("hello")); // [HELLO]

        // compose() - executes before
        Function<String, String> bracketsBeforeUpper = toUpper.compose(addBrackets);
        System.out.println("   bracketsBeforeUpper(\"hello\"): " + bracketsBeforeUpper.apply("hello")); // [HELLO]

        // Function.identity() - returns input as-is
        Function<String, String> identity = Function.identity();
        System.out.println("   identity(\"same\"): " + identity.apply("same")); // same

        // Chaining functions
        Function<String, String> pipeline = ((Function<String, String>) String::trim)
                .andThen(String::toLowerCase)
                .andThen(s -> s.replace(" ", "-"))
                .andThen(s -> s + ".html");

        System.out.println("   URL slug: " + pipeline.apply("  Hello World  ")); // hello-world.html

        System.out.println();
    }

    // ============================================================
    // 3. Consumer<T> - Accepts input, returns nothing
    // ============================================================
    static void consumerExamples() {
        System.out.println("3️⃣ CONSUMER<T> → T → void");
        System.out.println("─────────────────────────────────\n");

        Consumer<String> print = s -> System.out.println("   " + s);
        Consumer<String> printUpper = s -> System.out.println("   " + s.toUpperCase());
        Consumer<List<String>> clearList = List::clear;

        // Basic usage
        print.accept("Hello Consumer!");

        // Using Consumer to mutate a list
        List<String> tempList = new ArrayList<>(Arrays.asList("x", "y", "z"));
        clearList.accept(tempList);
        System.out.println("   After clearList, size: " + tempList.size()); // 0

        // andThen() - chain consumers
        Consumer<String> printBoth = print.andThen(printUpper);
        printBoth.accept("java");
        // Prints:
        //    java
        //    JAVA

        // BiConsumer - accepts two inputs
        BiConsumer<String, Integer> printRepeat = (s, n) -> {
            System.out.print("   ");
            for (int i = 0; i < n; i++) System.out.print(s + " ");
            System.out.println();
        };
        printRepeat.accept("⭐", 5);

        // Using Consumer with forEach
        Map<String, Double> prices = new LinkedHashMap<>();
        prices.put("Coffee", 4.99);
        prices.put("Tea", 3.49);
        prices.put("Juice", 5.99);

        System.out.println("   Price list:");
        prices.forEach((item, price) -> System.out.printf("   %-10s $%.2f%n", item, price));

        System.out.println();
    }

    // ============================================================
    // 4. Supplier<T> - Supplies a value, takes no input
    // ============================================================
    static void supplierExamples() {
        System.out.println("4️⃣ SUPPLIER<T> → () → T");
        System.out.println("─────────────────────────────────\n");

        Supplier<String> helloSupplier = () -> "Hello from Supplier!";
        Supplier<Double> randomSupplier = Math::random;
        Supplier<List<String>> listFactory = ArrayList::new;
        Supplier<UUID> uuidGenerator = UUID::randomUUID;

        // Basic usage
        System.out.println("   " + helloSupplier.get());
        System.out.println("   Random: " + randomSupplier.get());
        System.out.println("   UUID: " + uuidGenerator.get());

        // Lazy evaluation - only computed when needed
        System.out.println("   Lazy value: " + getOrDefault(null, FunctionalInterfacesExample::computeExpensiveValue));
        System.out.println("   Cached value: " + getOrDefault("cached", FunctionalInterfacesExample::computeExpensiveValue));

        // Factory pattern
        List<String> newList = createIfNeeded(true, listFactory);
        System.out.println("   Factory list created: " + (newList != null));

        System.out.println();
    }

    static String computeExpensiveValue() {
        // Simulates expensive computation
        return "computed-at-" + System.currentTimeMillis();
    }

    static <T> T getOrDefault(T value, Supplier<T> defaultSupplier) {
        return value != null ? value : defaultSupplier.get();
    }

    static <T> T createIfNeeded(boolean needed, Supplier<T> factory) {
        return needed ? factory.get() : null;
    }

    // ============================================================
    // 5. UnaryOperator & BinaryOperator
    // ============================================================
    static void operatorExamples() {
        System.out.println("5️⃣ OPERATORS → Specialized Functions");
        System.out.println("─────────────────────────────────\n");

        // UnaryOperator<T> extends Function<T, T> - same input/output type
        UnaryOperator<String> shout = s -> s.toUpperCase() + "!!!";
        UnaryOperator<Integer> doubleIt = n -> n * 2;
        UnaryOperator<String> trim = String::trim;

        System.out.println("   shout(\"hello\"): " + shout.apply("hello"));     // HELLO!!!
        System.out.println("   doubleIt(21): " + doubleIt.apply(21));            // 42
        System.out.println("   trim(\"  spaces  \"): \"" + trim.apply("  spaces  ") + "\"");

        // BinaryOperator<T> extends BiFunction<T, T, T> - two inputs, same type output
        BinaryOperator<Integer> sum = Integer::sum;
        BinaryOperator<Integer> max = Integer::max;
        BinaryOperator<String> joinWithDash = (a, b) -> a + "-" + b;

        System.out.println("   sum(5, 3): " + sum.apply(5, 3));                 // 8
        System.out.println("   max(10, 20): " + max.apply(10, 20));             // 20
        System.out.println("   join: " + joinWithDash.apply("Hello", "World")); // Hello-World

        // BinaryOperator.minBy() and maxBy()
        BinaryOperator<String> longestString = BinaryOperator.maxBy(Comparator.comparingInt(String::length));
        System.out.println("   longer(\"hi\", \"hello\"): " + longestString.apply("hi", "hello")); // hello

        // Using UnaryOperator with replaceAll
        List<String> names = new ArrayList<>(Arrays.asList("alice", "bob", "charlie"));
        names.replaceAll(String::toUpperCase);
        System.out.println("   replaceAll toUpper: " + names);

        System.out.println();
    }

    // ============================================================
    // 6. Bi-Functions (Two-Parameter Versions)
    // ============================================================
    static void biFunctionExamples() {
        System.out.println("6️⃣ BI-FUNCTIONS (Two Parameters)");
        System.out.println("─────────────────────────────────\n");

        BiFunction<String, Integer, String> repeat = String::repeat;
        BiPredicate<String, String> contains = String::contains;
        BiConsumer<String, String> greet = (name, lang) -> 
            System.out.println("   " + (lang.equals("EN") ? "Hello" : "Hola") + ", " + name + "!");

        System.out.println("   repeat(\"Ha\", 3): " + repeat.apply("Ha", 3));       // HaHaHa
        System.out.println("   contains(\"Hello\", \"ell\"): " + contains.test("Hello", "ell")); // true

        greet.accept("Alice", "EN");  // Hello, Alice!
        greet.accept("Carlos", "ES"); // Hola, Carlos!

        // BiFunction with andThen
        BiFunction<Integer, Integer, Integer> add = Integer::sum;
        Function<Integer, String> format = n -> "Result: " + n;

        // BiFunction → andThen → Function
        System.out.println("   " + add.andThen(format).apply(5, 3)); // Result: 8

        System.out.println();
    }

    // ============================================================
    // 7. Function Composition
    // ============================================================
    static void compositionExamples() {
        System.out.println("7️⃣ FUNCTION COMPOSITION");
        System.out.println("─────────────────────────────────\n");

        // Build a data processing pipeline
        Function<String, String> normalizeEmail = 
            ((Function<String, String>) String::trim)
            .andThen(String::toLowerCase)
            .andThen(s -> s.replaceAll("\\s+", ""));

        System.out.println("   Normalized: " + normalizeEmail.apply("  User@Example.COM  "));

        // Predicate composition
        Predicate<Integer> between1And100 = 
            ((Predicate<Integer>) n -> n >= 1).and(n -> n <= 100);
        Predicate<Integer> isMultipleOf5 = n -> n % 5 == 0;
        Predicate<Integer> validScore = between1And100.and(isMultipleOf5);

        System.out.println("   Valid score 50: " + validScore.test(50));   // true
        System.out.println("   Valid score 13: " + validScore.test(13));   // false
        System.out.println("   Valid score 150: " + validScore.test(150)); // false

        // Consumer chaining
        Consumer<String> logToConsole = s -> System.out.println("   [LOG] " + s);
        Consumer<String> logTimestamp = s -> System.out.println("   [TIME] " + s + " @ " + System.currentTimeMillis());
        Consumer<String> fullLogger = logToConsole.andThen(logTimestamp);

        fullLogger.accept("Application started");

        System.out.println();
    }

    // ============================================================
    // 8. Primitive Specializations
    // ============================================================
    static void primitiveSpecializations() {
        System.out.println("8️⃣ PRIMITIVE SPECIALIZATIONS (No Boxing!)");
        System.out.println("─────────────────────────────────\n");

        // Avoid autoboxing overhead with specialized versions
        IntPredicate isEven = n -> n % 2 == 0;
        IntFunction<String> intToString = n -> "Value: " + n;
        IntUnaryOperator tripleIt = n -> n * 3;
        IntBinaryOperator intMax = Integer::max;
        IntSupplier randomInt = () -> (int) (Math.random() * 100);
        IntConsumer printInt = n -> System.out.println("   Int: " + n);

        // LongXxx, DoubleXxx also available
        LongPredicate isLargeNumber = n -> n > 1_000_000L;
        DoublePredicate isFinite = Double::isFinite;
        DoubleUnaryOperator square = n -> n * n;

        // ToXxxFunction - convert to primitive
        ToIntFunction<String> strLen = String::length;
        ToDoubleFunction<String> parseDouble = Double::parseDouble;

        System.out.println("   isEven(4): " + isEven.test(4));
        System.out.println("   intToString(42): " + intToString.apply(42));
        System.out.println("   tripleIt(7): " + tripleIt.applyAsInt(7));
        System.out.println("   strLen(\"hello\"): " + strLen.applyAsInt("hello"));
        System.out.println("   square(5.0): " + square.applyAsDouble(5.0));
        System.out.println("   intMax(5, 10): " + intMax.applyAsInt(5, 10));
        System.out.println("   randomInt: " + randomInt.getAsInt());
        printInt.accept(99);
        System.out.println("   isLargeNumber(2_000_000): " + isLargeNumber.test(2_000_000L));
        System.out.println("   isFinite(3.14): " + isFinite.test(3.14));
        System.out.println("   parseDouble(\"3.14\"): " + parseDouble.applyAsDouble("3.14"));

        System.out.println("\n   📋 Complete Specialization Table:");
        System.out.println("   ┌────────────────────┬──────────┬──────────┬──────────┐");
        System.out.println("   │  Interface          │  Int     │  Long    │  Double  │");
        System.out.println("   ├────────────────────┼──────────┼──────────┼──────────┤");
        System.out.println("   │  Predicate          │  ✅      │  ✅      │  ✅      │");
        System.out.println("   │  Function           │  ✅      │  ✅      │  ✅      │");
        System.out.println("   │  Consumer           │  ✅      │  ✅      │  ✅      │");
        System.out.println("   │  Supplier           │  ✅      │  ✅      │  ✅      │");
        System.out.println("   │  UnaryOperator      │  ✅      │  ✅      │  ✅      │");
        System.out.println("   │  BinaryOperator     │  ✅      │  ✅      │  ✅      │");
        System.out.println("   └────────────────────┴──────────┴──────────┴──────────┘");

        System.out.println();
    }

    // ============================================================
    // 9. Real-World Pipeline
    // ============================================================
    static void realWorldPipeline() {
        System.out.println("9️⃣ REAL-WORLD: Data Processing Pipeline");
        System.out.println("─────────────────────────────────\n");

        // Simulate user registration validation
        record User(String name, String email, int age) {}

        List<User> users = List.of(
            new User("Alice", "alice@example.com", 30),
            new User("", "bob@test.com", 25),
            new User("Charlie", "invalid-email", 17),
            new User("Diana", "diana@example.com", 22),
            new User("Eve", "", 35)
        );

        // Define validation rules using Predicates
        Predicate<User> hasName = u -> u.name() != null && !u.name().isBlank();
        Predicate<User> hasEmail = u -> u.email() != null && u.email().contains("@");
        Predicate<User> isAdult = u -> u.age() >= 18;
        Predicate<User> isValid = hasName.and(hasEmail).and(isAdult);

        // Define transformation using Function
        Function<User, String> formatUser = u -> 
            String.format("%-10s %-25s age:%d", u.name(), u.email(), u.age());

        // Define output using Consumer
        Consumer<String> logValid = s -> System.out.println("   ✅ " + s);
        Consumer<String> logInvalid = s -> System.out.println("   ❌ " + s);

        System.out.println("   User Validation Results:");
        for (User user : users) {
            String formatted = formatUser.apply(user);
            if (isValid.test(user)) {
                logValid.accept(formatted);
            } else {
                logInvalid.accept(formatted);
            }
        }

        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("  ✅ Functional Interfaces: Complete!         ");
        System.out.println("═══════════════════════════════════════════════");
    }
}
