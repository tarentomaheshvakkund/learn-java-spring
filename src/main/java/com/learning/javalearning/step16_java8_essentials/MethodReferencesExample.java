package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.logging.Logger;

/**
 * Step 16: Method References - Shorthand for Lambda Expressions
 *
 * Method references are a compact way to refer to an existing method.
 * They replace simple lambdas that only call a single method.
 *
 * Four types:
 * 1. Static method:     ClassName::staticMethod
 * 2. Instance method:   instance::instanceMethod
 * 3. Arbitrary object:  ClassName::instanceMethod
 * 4. Constructor:       ClassName::new
 */
public class MethodReferencesExample {

    private static final Logger logger = Logger.getLogger(MethodReferencesExample.class.getName());
    private static final String SECTION_SEP = "─────────────────────────────────\n";
    private static final String LAMBDA_RESULT = "   (lambda result): ";
    private static final String NAME_ALICE       = "Alice";
    private static final String CAT_ELECTRONICS  = "Electronics";

    public static void main(String[] args) {
        logger.info("═══════════════════════════════════════════");
        logger.info("  Step 16: Method References              ");
        logger.info("═══════════════════════════════════════════\n");

        staticMethodRef();
        instanceMethodRef();
        arbitraryObjectMethodRef();
        constructorRef();
        methodRefWithStreams();
        whenToUseMethodRefs();
        realWorldExamples();
    }

    // ============================================================
    // 1. Static Method Reference (ClassName::staticMethod)
    // ============================================================
    // S1612/S4276: lambdas and boxed types kept intentionally to contrast lambda vs method reference
    @SuppressWarnings({"java:S1612", "java:S4276"})
    static void staticMethodRef() {
        logger.info("1️⃣ STATIC METHOD REFERENCE (ClassName::staticMethod)");
        logger.info(SECTION_SEP);

        // Lambda vs Method Reference
        // ❌ Lambda
        Function<String, Integer> parseIntLambda = s -> Integer.parseInt(s);
        logger.info(() -> LAMBDA_RESULT + parseIntLambda.apply("42"));
        // ✅ Method Reference
        Function<String, Integer> parseIntRef = Integer::parseInt;

        logger.info(() -> "   parseInt(\"42\"): " + parseIntRef.apply("42"));

        // More examples
        List<String> numbers = List.of("1", "2", "3", "4", "5");

        // Lambda
        List<Integer> parsed1 = numbers.stream()
                .map(s -> Integer.parseInt(s))
                .toList();
        logger.info(() -> LAMBDA_RESULT + parsed1);

        // Method Reference (cleaner)
        List<Integer> parsed2 = numbers.stream()
                .map(Integer::parseInt)
                .toList();

        logger.info(() -> "   Parsed numbers: " + parsed2);

        // Math static methods
        List<Double> values = List.of(-3.5, 2.1, -1.7, 4.9);
        List<Double> absolutes = values.stream().map(Math::abs).toList();
        logger.info(() -> "   Absolutes: " + absolutes);

        // Static method with two args
        BinaryOperator<Integer> maxLambda = (a, b) -> Integer.max(a, b);
        logger.info(() -> LAMBDA_RESULT + maxLambda.apply(10, 20));
        BinaryOperator<Integer> maxRef = Integer::max;
        logger.info(() -> "   max(10, 20): " + maxRef.apply(10, 20));

        // Custom static method
        List<String> names = List.of("alice", "bob", "charlie");
        List<String> upper = names.stream().map(MethodReferencesExample::toUpperCase).toList();
        logger.info(() -> "   Uppercased: " + upper);

        logger.info("");
    }

    static String toUpperCase(String s) {
        return s.toUpperCase();
    }

    // ============================================================
    // 2. Instance Method Reference (instance::method)
    // ============================================================
    // S1612/S4276: lambdas and boxed types kept intentionally to contrast lambda vs method reference
    @SuppressWarnings({"java:S1612", "java:S4276"})
    static void instanceMethodRef() {
        logger.info("2️⃣ INSTANCE METHOD REFERENCE (instance::method)");
        logger.info(SECTION_SEP);

        // Reference to a specific instance's method
        String prefix = "Hello, ";

        // Lambda
        Function<String, String> greeterLambda = name -> prefix.concat(name);
        logger.info(() -> LAMBDA_RESULT + greeterLambda.apply("World"));
        // Method Reference
        Function<String, String> greeterRef = prefix::concat;

        logger.info(() -> "   " + greeterRef.apply("World"));

        // Printer example
        List<String> fruits = List.of("Apple", "Banana", "Cherry");

        // Lambda
        fruits.forEach(f -> logger.info(() -> "   Lambda: " + f));

        // Method Reference on logger instance
        // logger is the instance, info is the method
        logger.info("   Method ref (via logger::info method reference):");
        fruits.forEach(logger::info);  // logger::info is a method reference!

        // Custom instance
        StringProcessor processor = new StringProcessor(">>>");
        List<String> processed = fruits.stream()
                .map(processor::process)
                .toList();
        logger.info(() -> "   Processed: " + processed);

        // Instance method on a captured variable
        Comparator<String> comp = String.CASE_INSENSITIVE_ORDER;
        List<String> sorted = fruits.stream().sorted(comp::compare).toList();
        logger.info(() -> "   Sorted: " + sorted);

        logger.info("");
    }

    static class StringProcessor {
        private final String prefix;

        StringProcessor(String prefix) {
            this.prefix = prefix;
        }

        String process(String input) {
            return prefix + " " + input;
        }
    }

    // ============================================================
    // 3. Arbitrary Object Instance Method (ClassName::instanceMethod)
    // ============================================================
    @SuppressWarnings("java:S1612") // lambda kept intentionally to contrast with method reference
    static void arbitraryObjectMethodRef() {
        logger.info("3️⃣ ARBITRARY OBJECT METHOD REF (ClassName::instanceMethod)");
        logger.info(SECTION_SEP);

        // The first parameter becomes the object the method is called on
        // String::toLowerCase is equivalent to (String s) -> s.toLowerCase()

        List<String> names = List.of("ALICE", "BOB", "CHARLIE");

        // Lambda
        List<String> lower1 = names.stream().map(s -> s.toLowerCase()).toList();
        logger.info(() -> LAMBDA_RESULT + lower1);
        // Method Reference - called on each element
        List<String> lower2 = names.stream().map(String::toLowerCase).toList();
        logger.info(() -> "   Lowercased: " + lower2);

        // String::length as Function<String, Integer>
        List<Integer> lengths = names.stream().map(String::length).toList();
        logger.info(() -> "   Lengths: " + lengths);

        // String::compareTo as Comparator
        // (s1, s2) -> s1.compareTo(s2)  →  String::compareTo
        List<String> sorted = names.stream().sorted(String::compareTo).toList();
        logger.info(() -> "   Sorted: " + sorted);

        // String::isEmpty as Predicate<String>
        List<String> mixed = List.of("hello", "", "world", "", "!");
        List<String> nonEmpty = mixed.stream().filter(Predicate.not(String::isEmpty)).toList();
        logger.info(() -> "   Non-empty: " + nonEmpty);

        // Understanding the difference:
        logger.info("\n   📋 Instance vs Arbitrary Object:");
        logger.info("   ┌───────────────────────┬────────────────────────────────┐");
        logger.info("   │ instance::method      │ Bound to specific instance     │");
        logger.info("   │                       │ e.g., str::length              │");
        logger.info("   ├───────────────────────┼────────────────────────────────┤");
        logger.info("   │ ClassName::method     │ Called on first parameter      │");
        logger.info("   │                       │ e.g., String::length           │");
        logger.info("   └───────────────────────┴────────────────────────────────┘");

        logger.info("");
    }

    // ============================================================
    // 4. Constructor Reference (ClassName::new)
    // ============================================================
    @SuppressWarnings("java:S1612") // lambda kept intentionally to contrast with constructor reference
    static void constructorRef() {
        logger.info("4️⃣ CONSTRUCTOR REFERENCE (ClassName::new)");
        logger.info(SECTION_SEP);

        // Lambda vs Constructor Reference
        // ❌ Lambda
        Supplier<List<String>> listLambda = () -> new ArrayList<>();
        // ✅ Constructor Reference
        Supplier<List<String>> listRef = ArrayList::new;

        List<String> newList = listRef.get();
        newList.add("Hello");
        logger.info(() -> "   Created list: " + newList);
        logger.info(() -> "   (lambda also creates list): " + listLambda.get());

        // With parameters
        // Function<String, StringBuilder>
        Function<String, StringBuilder> sbCreator = StringBuilder::new;
        StringBuilder sb = sbCreator.apply("Initial");
        logger.info(() -> "   StringBuilder: " + sb);

        // Convert strings to records
        record Person(String name) {}

        List<String> names = List.of(NAME_ALICE, "Bob", "Charlie");

        // Lambda
        List<Person> people1 = names.stream().map(n -> new Person(n)).toList();
        logger.info(() -> "   (lambda people): " + people1);
        // Constructor Reference
        List<Person> people2 = names.stream().map(Person::new).toList();
        logger.info(() -> "   People: " + people2);

        // Array constructor reference
        // IntFunction<String[]> creates array of given size
        String[] nameArray = names.stream().toArray(String[]::new);
        logger.info(() -> "   Array: " + Arrays.toString(nameArray));

        // Two-arg constructor
        record Employee(String name, String dept) {}
        BiFunction<String, String, Employee> empCreator = Employee::new;
        Employee emp = empCreator.apply(NAME_ALICE, "Engineering");
        logger.info(() -> "   Employee: " + emp);

        logger.info("");
    }

    // ============================================================
    // 5. Method References with Streams
    // ============================================================
    static void methodRefWithStreams() {
        logger.info("5️⃣ METHOD REFERENCES WITH STREAMS");
        logger.info(SECTION_SEP);

        record Product(String name, double price, String category) {}

        List<Product> products = List.of(
            new Product("Laptop", 999.99, CAT_ELECTRONICS),
            new Product("Book", 29.99, "Education"),
            new Product("Phone", 699.99, CAT_ELECTRONICS),
            new Product("Pen", 2.99, "Office"),
            new Product("Tablet", 449.99, CAT_ELECTRONICS)
        );

        // map with method refs
        List<String> names = products.stream().map(Product::name).toList();
        logger.info(() -> "   Product names: " + names);

        // Sorting with method ref comparator
        List<Product> byPrice = products.stream()
                .sorted(Comparator.comparing(Product::price))
                .toList();
        logger.info(() -> "   By price: " + byPrice.stream().map(Product::name).toList());

        // Grouping
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::category));
        byCategory.forEach((cat, prods) ->
            logger.info(() -> "   " + cat + ": " + prods.stream().map(Product::name).toList()));

        // Statistics
        DoubleSummaryStatistics stats = products.stream()
                .mapToDouble(Product::price)
                .summaryStatistics();
        logger.info(() -> String.format("   Price stats: min=%.2f, max=%.2f, avg=%.2f",
            stats.getMin(), stats.getMax(), stats.getAverage()));

        // Chaining
        String catalog = products.stream()
                .sorted(Comparator.comparing(Product::price).reversed())
                .map(Product::name)
                .collect(Collectors.joining(", "));
        logger.info(() -> "   Catalog (expensive first): " + catalog);

        logger.info("");
    }

    // ============================================================
    // 6. When to Use Method References
    // ============================================================
    static void whenToUseMethodRefs() {
        logger.info("6️⃣ WHEN TO USE (and NOT use) METHOD REFS");
        logger.info(SECTION_SEP);

        // ✅ USE: Simple, single-method lambdas
        logger.info("   ✅ USE when lambda just delegates to a single method:");
        logger.info("      s -> s.toLowerCase()       →  String::toLowerCase");
        logger.info("      s -> Integer.parseInt(s)   →  Integer::parseInt");
        logger.info("      () -> new ArrayList<>()    →  ArrayList::new");
        logger.info("      s -> System.out.println(s) →  System.out::println");

        // ❌ DON'T USE: Complex logic, multiple operations
        logger.info("\n   ❌ DON'T USE when lambda has logic:");
        logger.info("      s -> s.trim().toLowerCase()        (multiple calls)");
        logger.info("      s -> s.length() > 5                (comparison)");
        logger.info("      (a, b) -> a + \"-\" + b             (concatenation)");
        logger.info("      s -> { log(s); return process(s); } (multi-statement)");

        // Quick reference
        logger.info("\n   📋 Complete Reference:");
        logger.info("   ┌────────────────────┬───────────────────────┬──────────────────────┐");
        logger.info("   │ Type               │ Lambda                │ Method Reference     │");
        logger.info("   ├────────────────────┼───────────────────────┼──────────────────────┤");
        logger.info("   │ Static method      │ x -> Class.method(x) │ Class::method        │");
        logger.info("   │ Bound instance     │ x -> obj.method(x)   │ obj::method          │");
        logger.info("   │ Unbound instance   │ x -> x.method()      │ Class::method        │");
        logger.info("   │ Constructor        │ x -> new Class(x)    │ Class::new           │");
        logger.info("   └────────────────────┴───────────────────────┴──────────────────────┘");

        logger.info("");
    }

    // ============================================================
    // 7. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        logger.info("7️⃣ REAL-WORLD METHOD REFERENCE EXAMPLES");
        logger.info(SECTION_SEP);

        // Example 1: Event handler registration
        logger.info("   📌 Event Handler Pattern:");
        EventBus bus = new EventBus();
        bus.register("click", MethodReferencesExample::handleClick);
        bus.register("hover", MethodReferencesExample::handleHover);
        bus.emit("click", "button-1");
        bus.emit("hover", "menu-item");

        // Example 2: Validation pipeline
        logger.info("\n   📌 Validation Pipeline:");
        List<String> emails = List.of("alice@example.com", "", "invalid", "bob@test.com", null);
        List<String> valid = emails.stream()
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isEmpty))
                .filter(MethodReferencesExample::isValidEmail)
                .toList();
        logger.info(() -> "   Valid emails: " + valid);

        // Example 3: Map operations
        logger.info("\n   📌 Map Operations:");
        Map<String, Integer> scores = Map.of(NAME_ALICE, 95, "Bob", 82, "Charlie", 91);
        scores.forEach((name, score) ->
            logger.info(() -> String.format("   %s: %d (%s)", name, score, score >= 90 ? "A" : "B")));

        // Example 4: Collector to custom type
        logger.info("\n   📌 Collecting to Custom Types:");
        List<String> words = List.of("hello", "world", "java", "rocks");
        String result = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining(" | "));
        logger.info(() -> "   Result: " + result);

        logger.info("\n═══════════════════════════════════════════");
        logger.info("  ✅ Method References: Complete!          ");
        logger.info("═══════════════════════════════════════════");
    }

    static void handleClick(String target) {
        logger.info(() -> "   Clicked: " + target);
    }

    static void handleHover(String target) {
        logger.info(() -> "   Hovered: " + target);
    }

    static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    // Simple event bus for method reference demo
    static class EventBus {
        private final Map<String, Consumer<String>> handlers = new HashMap<>();

        void register(String event, Consumer<String> handler) {
            handlers.put(event, handler);
        }

        void emit(String event, String data) {
            Optional.ofNullable(handlers.get(event)).ifPresent(h -> h.accept(data));
        }
    }
}
