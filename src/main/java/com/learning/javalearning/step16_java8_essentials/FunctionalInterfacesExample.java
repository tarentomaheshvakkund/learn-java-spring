package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;
import java.util.logging.Logger;

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
@SuppressWarnings("java:S4276") // Intentional: Sections 1-7 teach generic functional interfaces; section 8 covers primitive specializations
public class FunctionalInterfacesExample {

    private static final Logger logger = Logger.getLogger(FunctionalInterfacesExample.class.getName());

    private static final String HEADER_BORDER = "═══════════════════════════════════════════════";
    private static final String SECTION_DIVIDER = "─────────────────────────────────";
    private static final String HELLO = "hello";
    private static final String HELLO_CAPS = "Hello";

    public static void main(String[] args) {
        logger.info(HEADER_BORDER);
        logger.info("  Step 16: Functional Interfaces Deep Dive    ");
        logger.info(() -> HEADER_BORDER + "\n");

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
        logger.info("1️⃣ PREDICATE<T> → T → boolean");
        logger.info(() -> SECTION_DIVIDER + "\n");

        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<String> isNotEmpty = s -> s != null && !s.isEmpty();
        Predicate<String> startsWithJ = s -> s.startsWith("J");

        // Basic usage
        logger.info(() -> "   isEven(4): " + isEven.test(4));         // true
        logger.info(() -> "   isEven(7): " + isEven.test(7));         // false
        logger.info(() -> "   isNotEmpty(\"hi\"): " + isNotEmpty.test("hi")); // true

        // Combining predicates with and(), or(), negate()
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);
        Predicate<Integer> isOdd = isEven.negate();

        logger.info(() -> "   isEvenAndPositive(4): " + isEvenAndPositive.test(4));   // true
        logger.info(() -> "   isEvenAndPositive(-4): " + isEvenAndPositive.test(-4)); // false
        logger.info(() -> "   isOdd(7): " + isOdd.test(7));                          // true
        logger.info(() -> "   isEvenOrPositive(3): " + isEvenOrPositive.test(3));    // true

        // Predicate.isEqual() static method
        Predicate<String> isJava = Predicate.isEqual("Java");
        logger.info(() -> "   isJava(\"Java\"): " + isJava.test("Java"));   // true
        logger.info(() -> "   isJava(\"Python\"): " + isJava.test("Python")); // false

        // Using with collections
        List<String> languages = Arrays.asList("Java", "JavaScript", "Python", "Jython", "C++");
        List<String> jLanguages = new ArrayList<>(languages);
        jLanguages.removeIf(startsWithJ.negate());
        logger.info(() -> "   J-languages: " + jLanguages);

        logger.info("");
    }

    // ============================================================
    // 2. Function<T, R> - Transforms input to output
    // ============================================================
    static void functionExamples() {
        logger.info("2️⃣ FUNCTION<T, R> → T → R");
        logger.info(() -> SECTION_DIVIDER + "\n");

        Function<String, Integer> stringLength = String::length;
        Function<String, String> toUpper = String::toUpperCase;
        Function<Integer, String> intToString = n -> "Number: " + n;
        Function<String, String> addBrackets = s -> "[" + s + "]";

        // Basic usage
        logger.info(() -> "   length(\"hello\"): " + stringLength.apply(HELLO));     // 5
        logger.info(() -> "   toUpper(\"hello\"): " + toUpper.apply(HELLO));          // HELLO
        logger.info(() -> "   intToString(42): " + intToString.apply(42));              // Number: 42

        // andThen() - executes after
        Function<String, String> upperThenBrackets = toUpper.andThen(addBrackets);
        logger.info(() -> "   upperThenBrackets(\"hello\"): " + upperThenBrackets.apply(HELLO)); // [HELLO]

        // compose() - executes before
        Function<String, String> bracketsBeforeUpper = toUpper.compose(addBrackets);
        logger.info(() -> "   bracketsBeforeUpper(\"hello\"): " + bracketsBeforeUpper.apply(HELLO)); // [HELLO]

        // Function.identity() - returns input as-is
        Function<String, String> identity = Function.identity();
        logger.info(() -> "   identity(\"same\"): " + identity.apply("same")); // same

        // Chaining functions
        Function<String, String> pipeline = ((Function<String, String>) String::trim)
                .andThen(String::toLowerCase)
                .andThen(s -> s.replace(" ", "-"))
                .andThen(s -> s + ".html");

        logger.info(() -> "   URL slug: " + pipeline.apply("  Hello World  ")); // hello-world.html

        logger.info("");
    }

    // ============================================================
    // 3. Consumer<T> - Accepts input, returns nothing
    // ============================================================
    @SuppressWarnings("java:S2629") // Intentional: Demonstrating Consumer patterns with lambdas
    static void consumerExamples() {
        logger.info("3️⃣ CONSUMER<T> → T → void");
        logger.info(() -> SECTION_DIVIDER + "\n");

        Consumer<String> print = s -> logger.info("   " + s);
        Consumer<String> printUpper = s -> logger.info("   " + s.toUpperCase());
        Consumer<List<String>> clearList = List::clear;

        // Basic usage
        print.accept("Hello Consumer!");

        // Using Consumer to mutate a list
        List<String> tempList = new ArrayList<>(Arrays.asList("x", "y", "z"));
        clearList.accept(tempList);
        logger.info(() -> "   After clearList, size: " + tempList.size()); // 0

        // andThen() - chain consumers
        Consumer<String> printBoth = print.andThen(printUpper);
        printBoth.accept("java");
        // Prints:
        //    java
        //    JAVA

        // BiConsumer - accepts two inputs
        BiConsumer<String, Integer> printRepeat = (s, n) -> {
            StringBuilder sb = new StringBuilder("   ");
            for (int i = 0; i < n; i++) sb.append(s).append(" ");
            logger.info(sb.toString());
        };
        printRepeat.accept("⭐", 5);

        // Using Consumer with forEach
        Map<String, Double> prices = new LinkedHashMap<>();
        prices.put("Coffee", 4.99);
        prices.put("Tea", 3.49);
        prices.put("Juice", 5.99);

        logger.info("   Price list:");
        prices.forEach((item, price) -> logger.info(String.format("   %-10s $%.2f", item, price)));

        logger.info("");
    }

    // ============================================================
    // 4. Supplier<T> - Supplies a value, takes no input
    // ============================================================
    static void supplierExamples() {
        logger.info("4️⃣ SUPPLIER<T> → () → T");
        logger.info(() -> SECTION_DIVIDER + "\n");

        Supplier<String> helloSupplier = () -> "Hello from Supplier!";
        Supplier<Double> randomSupplier = Math::random;
        Supplier<List<String>> listFactory = ArrayList::new;
        Supplier<UUID> uuidGenerator = UUID::randomUUID;

        // Basic usage
        logger.info(() -> "   " + helloSupplier.get());
        logger.info(() -> "   Random: " + randomSupplier.get());
        logger.info(() -> "   UUID: " + uuidGenerator.get());

        // Lazy evaluation - only computed when needed
        logger.info(() -> "   Lazy value: " + getOrDefault(null, FunctionalInterfacesExample::computeExpensiveValue));
        logger.info(() -> "   Cached value: " + getOrDefault("cached", FunctionalInterfacesExample::computeExpensiveValue));

        // Factory pattern
        List<String> newList = createIfNeeded(true, listFactory);
        logger.info(() -> "   Factory list created: " + (newList != null));

        logger.info("");
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
        logger.info("5️⃣ OPERATORS → Specialized Functions");
        logger.info(() -> SECTION_DIVIDER + "\n");

        // UnaryOperator<T> extends Function<T, T> - same input/output type
        UnaryOperator<String> shout = s -> s.toUpperCase() + "!!!";
        UnaryOperator<Integer> doubleIt = n -> n * 2;
        UnaryOperator<String> trim = String::trim;

        logger.info(() -> "   shout(\"hello\"): " + shout.apply(HELLO));     // HELLO!!!
        logger.info(() -> "   doubleIt(21): " + doubleIt.apply(21));            // 42
        logger.info(() -> "   trim(\"  spaces  \"): \"" + trim.apply("  spaces  ") + "\"");

        // BinaryOperator<T> extends BiFunction<T, T, T> - two inputs, same type output
        BinaryOperator<Integer> sum = Integer::sum;
        BinaryOperator<Integer> max = Integer::max;
        BinaryOperator<String> joinWithDash = (a, b) -> a + "-" + b;

        logger.info(() -> "   sum(5, 3): " + sum.apply(5, 3));                 // 8
        logger.info(() -> "   max(10, 20): " + max.apply(10, 20));             // 20
        logger.info(() -> "   join: " + joinWithDash.apply(HELLO_CAPS, "World")); // Hello-World

        // BinaryOperator.minBy() and maxBy()
        BinaryOperator<String> longestString = BinaryOperator.maxBy(Comparator.comparingInt(String::length));
        logger.info(() -> "   longer(\"hi\", \"hello\"): " + longestString.apply("hi", HELLO)); // hello

        // Using UnaryOperator with replaceAll
        List<String> names = new ArrayList<>(Arrays.asList("alice", "bob", "charlie"));
        names.replaceAll(String::toUpperCase);
        logger.info(() -> "   replaceAll toUpper: " + names);

        logger.info("");
    }

    // ============================================================
    // 6. Bi-Functions (Two-Parameter Versions)
    // ============================================================
    @SuppressWarnings("java:S2629") // Intentional: Demonstrating BiConsumer patterns with lambdas
    static void biFunctionExamples() {
        logger.info("6️⃣ BI-FUNCTIONS (Two Parameters)");
        logger.info(() -> SECTION_DIVIDER + "\n");

        BiFunction<String, Integer, String> repeat = String::repeat;
        BiPredicate<String, String> contains = String::contains;
        BiConsumer<String, String> greet = (name, lang) -> 
            logger.info("   " + (lang.equals("EN") ? HELLO_CAPS : "Hola") + ", " + name + "!");

        logger.info(() -> "   repeat(\"Ha\", 3): " + repeat.apply("Ha", 3));       // HaHaHa
        logger.info(() -> "   contains(\"Hello\", \"ell\"): " + contains.test(HELLO_CAPS, "ell")); // true

        greet.accept("Alice", "EN");  // Hello, Alice!
        greet.accept("Carlos", "ES"); // Hola, Carlos!

        // BiFunction with andThen
        BiFunction<Integer, Integer, Integer> add = Integer::sum;
        Function<Integer, String> format = n -> "Result: " + n;

        // BiFunction → andThen → Function
        logger.info(() -> "   " + add.andThen(format).apply(5, 3)); // Result: 8

        logger.info("");
    }

    // ============================================================
    // 7. Function Composition
    // ============================================================
    @SuppressWarnings("java:S2629") // Intentional: Demonstrating Consumer chaining with lambdas
    static void compositionExamples() {
        logger.info("7️⃣ FUNCTION COMPOSITION");
        logger.info(() -> SECTION_DIVIDER + "\n");

        // Build a data processing pipeline
        Function<String, String> normalizeEmail = 
            ((Function<String, String>) String::trim)
            .andThen(String::toLowerCase)
            .andThen(s -> s.replaceAll("\\s+", ""));

        logger.info(() -> "   Normalized: " + normalizeEmail.apply("  User@Example.COM  "));

        // Predicate composition
        Predicate<Integer> between1And100 = 
            ((Predicate<Integer>) n -> n >= 1).and(n -> n <= 100);
        Predicate<Integer> isMultipleOf5 = n -> n % 5 == 0;
        Predicate<Integer> validScore = between1And100.and(isMultipleOf5);

        logger.info(() -> "   Valid score 50: " + validScore.test(50));   // true
        logger.info(() -> "   Valid score 13: " + validScore.test(13));   // false
        logger.info(() -> "   Valid score 150: " + validScore.test(150)); // false

        // Consumer chaining
        Consumer<String> logToConsole = s -> logger.info("   [LOG] " + s);
        Consumer<String> logTimestamp = s -> logger.info("   [TIME] " + s + " @ " + System.currentTimeMillis());
        Consumer<String> fullLogger = logToConsole.andThen(logTimestamp);

        fullLogger.accept("Application started");

        logger.info("");
    }

    // ============================================================
    // 8. Primitive Specializations
    // ============================================================
    @SuppressWarnings({"java:S2629", "java:S2119"}) // Intentional: Demonstrating primitive specializations and IntConsumer patterns
    static void primitiveSpecializations() {
        logger.info("8️⃣ PRIMITIVE SPECIALIZATIONS (No Boxing!)");
        logger.info(() -> SECTION_DIVIDER + "\n");

        // Avoid autoboxing overhead with specialized versions
        IntPredicate isEven = n -> n % 2 == 0;
        IntFunction<String> intToString = n -> "Value: " + n;
        IntUnaryOperator tripleIt = n -> n * 3;
        IntBinaryOperator intMax = Integer::max;
        IntSupplier randomInt = () -> new Random().nextInt(100);
        IntConsumer printInt = n -> logger.info("   Int: " + n);

        // LongXxx, DoubleXxx also available
        LongPredicate isLargeNumber = n -> n > 1_000_000L;
        DoublePredicate isFinite = Double::isFinite;
        DoubleUnaryOperator square = n -> n * n;

        // ToXxxFunction - convert to primitive
        ToIntFunction<String> strLen = String::length;
        ToDoubleFunction<String> parseDouble = Double::parseDouble;

        logger.info(() -> "   isEven(4): " + isEven.test(4));
        logger.info(() -> "   intToString(42): " + intToString.apply(42));
        logger.info(() -> "   tripleIt(7): " + tripleIt.applyAsInt(7));
        logger.info(() -> "   strLen(\"hello\"): " + strLen.applyAsInt(HELLO));
        logger.info(() -> "   square(5.0): " + square.applyAsDouble(5.0));
        logger.info(() -> "   intMax(5, 10): " + intMax.applyAsInt(5, 10));
        logger.info(() -> "   randomInt: " + randomInt.getAsInt());
        printInt.accept(99);
        logger.info(() -> "   isLargeNumber(2_000_000): " + isLargeNumber.test(2_000_000L));
        logger.info(() -> "   isFinite(3.14): " + isFinite.test(3.14));
        logger.info(() -> "   parseDouble(\"3.14\"): " + parseDouble.applyAsDouble("3.14"));

        logger.info("");
        logger.info("   📋 Complete Specialization Table:");
        logger.info("   ┌────────────────────┬──────────┬──────────┬──────────┐");
        logger.info("   │  Interface          │  Int     │  Long    │  Double  │");
        logger.info("   ├────────────────────┼──────────┼──────────┼──────────┤");
        logger.info("   │  Predicate          │  ✅      │  ✅      │  ✅      │");
        logger.info("   │  Function           │  ✅      │  ✅      │  ✅      │");
        logger.info("   │  Consumer           │  ✅      │  ✅      │  ✅      │");
        logger.info("   │  Supplier           │  ✅      │  ✅      │  ✅      │");
        logger.info("   │  UnaryOperator      │  ✅      │  ✅      │  ✅      │");
        logger.info("   │  BinaryOperator     │  ✅      │  ✅      │  ✅      │");
        logger.info("   └────────────────────┴──────────┴──────────┴──────────┘");

        logger.info("");
    }

    // ============================================================
    // 9. Real-World Pipeline
    // ============================================================
    @SuppressWarnings("java:S2629") // Intentional: Demonstrating Consumer patterns in real-world example
    static void realWorldPipeline() {
        logger.info("9️⃣ REAL-WORLD: Data Processing Pipeline");
        logger.info(() -> SECTION_DIVIDER + "\n");

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
        Consumer<String> logValid = s -> logger.info("   ✅ " + s);
        Consumer<String> logInvalid = s -> logger.info("   ❌ " + s);

        logger.info("   User Validation Results:");
        for (User user : users) {
            String formatted = formatUser.apply(user);
            if (isValid.test(user)) {
                logValid.accept(formatted);
            } else {
                logInvalid.accept(formatted);
            }
        }

        logger.info("");
        logger.info(HEADER_BORDER);
        logger.info("  ✅ Functional Interfaces: Complete!         ");
        logger.info(HEADER_BORDER);
    }
}
