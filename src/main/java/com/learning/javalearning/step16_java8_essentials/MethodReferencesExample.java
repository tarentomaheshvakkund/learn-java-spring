package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  Step 16: Method References              ");
        System.out.println("═══════════════════════════════════════════\n");

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
    static void staticMethodRef() {
        System.out.println("1️⃣ STATIC METHOD REFERENCE (ClassName::staticMethod)");
        System.out.println("─────────────────────────────────\n");

        // Lambda vs Method Reference
        // ❌ Lambda
        Function<String, Integer> parseIntLambda = s -> Integer.parseInt(s);
        // ✅ Method Reference
        Function<String, Integer> parseIntRef = Integer::parseInt;

        System.out.println("   parseInt(\"42\"): " + parseIntRef.apply("42"));

        // More examples
        List<String> numbers = List.of("1", "2", "3", "4", "5");

        // Lambda
        List<Integer> parsed1 = numbers.stream()
                .map(s -> Integer.parseInt(s))
                .toList();

        // Method Reference (cleaner)
        List<Integer> parsed2 = numbers.stream()
                .map(Integer::parseInt)
                .toList();

        System.out.println("   Parsed numbers: " + parsed2);

        // Math static methods
        List<Double> values = List.of(-3.5, 2.1, -1.7, 4.9);
        List<Double> absolutes = values.stream().map(Math::abs).toList();
        System.out.println("   Absolutes: " + absolutes);

        // Static method with two args
        BinaryOperator<Integer> maxLambda = (a, b) -> Integer.max(a, b);
        BinaryOperator<Integer> maxRef = Integer::max;
        System.out.println("   max(10, 20): " + maxRef.apply(10, 20));

        // Custom static method
        List<String> names = List.of("alice", "bob", "charlie");
        List<String> upper = names.stream().map(MethodReferencesExample::toUpperCase).toList();
        System.out.println("   Uppercased: " + upper);

        System.out.println();
    }

    static String toUpperCase(String s) {
        return s.toUpperCase();
    }

    // ============================================================
    // 2. Instance Method Reference (instance::method)
    // ============================================================
    static void instanceMethodRef() {
        System.out.println("2️⃣ INSTANCE METHOD REFERENCE (instance::method)");
        System.out.println("─────────────────────────────────\n");

        // Reference to a specific instance's method
        String prefix = "Hello, ";

        // Lambda
        Function<String, String> greeterLambda = name -> prefix.concat(name);
        // Method Reference
        Function<String, String> greeterRef = prefix::concat;

        System.out.println("   " + greeterRef.apply("World"));

        // Printer example
        List<String> fruits = List.of("Apple", "Banana", "Cherry");

        // Lambda
        fruits.forEach(f -> System.out.println("   Lambda: " + f));

        // Method Reference on PrintStream instance
        // System.out is the instance, println is the method
        System.out.print("   Method ref: ");
        fruits.forEach(System.out::println); // tricky - prints on new lines

        // Custom instance
        StringProcessor processor = new StringProcessor(">>>");
        List<String> processed = fruits.stream()
                .map(processor::process)
                .toList();
        System.out.println("   Processed: " + processed);

        // Instance method on a captured variable
        Comparator<String> comp = String.CASE_INSENSITIVE_ORDER;
        List<String> sorted = fruits.stream().sorted(comp::compare).toList();
        System.out.println("   Sorted: " + sorted);

        System.out.println();
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
    static void arbitraryObjectMethodRef() {
        System.out.println("3️⃣ ARBITRARY OBJECT METHOD REF (ClassName::instanceMethod)");
        System.out.println("─────────────────────────────────\n");

        // The first parameter becomes the object the method is called on
        // String::toLowerCase is equivalent to (String s) -> s.toLowerCase()

        List<String> names = List.of("ALICE", "BOB", "CHARLIE");

        // Lambda
        List<String> lower1 = names.stream().map(s -> s.toLowerCase()).toList();
        // Method Reference - called on each element
        List<String> lower2 = names.stream().map(String::toLowerCase).toList();
        System.out.println("   Lowercased: " + lower2);

        // String::length as Function<String, Integer>
        List<Integer> lengths = names.stream().map(String::length).toList();
        System.out.println("   Lengths: " + lengths);

        // String::compareTo as Comparator
        // (s1, s2) -> s1.compareTo(s2)  →  String::compareTo
        List<String> sorted = names.stream().sorted(String::compareTo).toList();
        System.out.println("   Sorted: " + sorted);

        // String::isEmpty as Predicate<String>
        List<String> mixed = List.of("hello", "", "world", "", "!");
        List<String> nonEmpty = mixed.stream().filter(Predicate.not(String::isEmpty)).toList();
        System.out.println("   Non-empty: " + nonEmpty);

        // Understanding the difference:
        System.out.println("\n   📋 Instance vs Arbitrary Object:");
        System.out.println("   ┌───────────────────────┬────────────────────────────────┐");
        System.out.println("   │ instance::method      │ Bound to specific instance     │");
        System.out.println("   │                       │ e.g., str::length              │");
        System.out.println("   ├───────────────────────┼────────────────────────────────┤");
        System.out.println("   │ ClassName::method     │ Called on first parameter      │");
        System.out.println("   │                       │ e.g., String::length           │");
        System.out.println("   └───────────────────────┴────────────────────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 4. Constructor Reference (ClassName::new)
    // ============================================================
    static void constructorRef() {
        System.out.println("4️⃣ CONSTRUCTOR REFERENCE (ClassName::new)");
        System.out.println("─────────────────────────────────\n");

        // Lambda vs Constructor Reference
        // ❌ Lambda
        Supplier<List<String>> listLambda = () -> new ArrayList<>();
        // ✅ Constructor Reference
        Supplier<List<String>> listRef = ArrayList::new;

        List<String> newList = listRef.get();
        newList.add("Hello");
        System.out.println("   Created list: " + newList);

        // With parameters
        // Function<String, StringBuilder>
        Function<String, StringBuilder> sbCreator = StringBuilder::new;
        StringBuilder sb = sbCreator.apply("Initial");
        System.out.println("   StringBuilder: " + sb);

        // Convert strings to records
        record Person(String name) {}

        List<String> names = List.of("Alice", "Bob", "Charlie");

        // Lambda
        List<Person> people1 = names.stream().map(n -> new Person(n)).toList();
        // Constructor Reference
        List<Person> people2 = names.stream().map(Person::new).toList();
        System.out.println("   People: " + people2);

        // Array constructor reference
        // IntFunction<String[]> creates array of given size
        String[] nameArray = names.stream().toArray(String[]::new);
        System.out.println("   Array: " + Arrays.toString(nameArray));

        // Two-arg constructor
        record Employee(String name, String dept) {}
        BiFunction<String, String, Employee> empCreator = Employee::new;
        Employee emp = empCreator.apply("Alice", "Engineering");
        System.out.println("   Employee: " + emp);

        System.out.println();
    }

    // ============================================================
    // 5. Method References with Streams
    // ============================================================
    static void methodRefWithStreams() {
        System.out.println("5️⃣ METHOD REFERENCES WITH STREAMS");
        System.out.println("─────────────────────────────────\n");

        record Product(String name, double price, String category) {}

        List<Product> products = List.of(
            new Product("Laptop", 999.99, "Electronics"),
            new Product("Book", 29.99, "Education"),
            new Product("Phone", 699.99, "Electronics"),
            new Product("Pen", 2.99, "Office"),
            new Product("Tablet", 449.99, "Electronics")
        );

        // map with method refs
        List<String> names = products.stream().map(Product::name).toList();
        System.out.println("   Product names: " + names);

        // Sorting with method ref comparator
        List<Product> byPrice = products.stream()
                .sorted(Comparator.comparing(Product::price))
                .toList();
        System.out.println("   By price: " + byPrice.stream().map(Product::name).toList());

        // Grouping
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::category));
        byCategory.forEach((cat, prods) ->
            System.out.println("   " + cat + ": " + prods.stream().map(Product::name).toList()));

        // Statistics
        DoubleSummaryStatistics stats = products.stream()
                .mapToDouble(Product::price)
                .summaryStatistics();
        System.out.printf("   Price stats: min=%.2f, max=%.2f, avg=%.2f%n",
            stats.getMin(), stats.getMax(), stats.getAverage());

        // Chaining
        String catalog = products.stream()
                .sorted(Comparator.comparing(Product::price).reversed())
                .map(Product::name)
                .collect(Collectors.joining(", "));
        System.out.println("   Catalog (expensive first): " + catalog);

        System.out.println();
    }

    // ============================================================
    // 6. When to Use Method References
    // ============================================================
    static void whenToUseMethodRefs() {
        System.out.println("6️⃣ WHEN TO USE (and NOT use) METHOD REFS");
        System.out.println("─────────────────────────────────\n");

        // ✅ USE: Simple, single-method lambdas
        System.out.println("   ✅ USE when lambda just delegates to a single method:");
        System.out.println("      s -> s.toLowerCase()       →  String::toLowerCase");
        System.out.println("      s -> Integer.parseInt(s)   →  Integer::parseInt");
        System.out.println("      () -> new ArrayList<>()    →  ArrayList::new");
        System.out.println("      s -> System.out.println(s) →  System.out::println");

        // ❌ DON'T USE: Complex logic, multiple operations
        System.out.println("\n   ❌ DON'T USE when lambda has logic:");
        System.out.println("      s -> s.trim().toLowerCase()        (multiple calls)");
        System.out.println("      s -> s.length() > 5                (comparison)");
        System.out.println("      (a, b) -> a + \"-\" + b             (concatenation)");
        System.out.println("      s -> { log(s); return process(s); } (multi-statement)");

        // Quick reference
        System.out.println("\n   📋 Complete Reference:");
        System.out.println("   ┌────────────────────┬───────────────────────┬──────────────────────┐");
        System.out.println("   │ Type               │ Lambda                │ Method Reference     │");
        System.out.println("   ├────────────────────┼───────────────────────┼──────────────────────┤");
        System.out.println("   │ Static method      │ x -> Class.method(x) │ Class::method        │");
        System.out.println("   │ Bound instance     │ x -> obj.method(x)   │ obj::method          │");
        System.out.println("   │ Unbound instance   │ x -> x.method()      │ Class::method        │");
        System.out.println("   │ Constructor        │ x -> new Class(x)    │ Class::new           │");
        System.out.println("   └────────────────────┴───────────────────────┴──────────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 7. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        System.out.println("7️⃣ REAL-WORLD METHOD REFERENCE EXAMPLES");
        System.out.println("─────────────────────────────────\n");

        // Example 1: Event handler registration
        System.out.println("   📌 Event Handler Pattern:");
        EventBus bus = new EventBus();
        bus.register("click", MethodReferencesExample::handleClick);
        bus.register("hover", MethodReferencesExample::handleHover);
        bus.emit("click", "button-1");
        bus.emit("hover", "menu-item");

        // Example 2: Validation pipeline
        System.out.println("\n   📌 Validation Pipeline:");
        List<String> emails = List.of("alice@example.com", "", "invalid", "bob@test.com", null);
        List<String> valid = emails.stream()
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isEmpty))
                .filter(MethodReferencesExample::isValidEmail)
                .toList();
        System.out.println("   Valid emails: " + valid);

        // Example 3: Map operations
        System.out.println("\n   📌 Map Operations:");
        Map<String, Integer> scores = Map.of("Alice", 95, "Bob", 82, "Charlie", 91);
        scores.forEach((name, score) ->
            System.out.printf("   %s: %d (%s)%n", name, score, score >= 90 ? "A" : "B"));

        // Example 4: Collector to custom type
        System.out.println("\n   📌 Collecting to Custom Types:");
        List<String> words = List.of("hello", "world", "java", "rocks");
        String result = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining(" | "));
        System.out.println("   Result: " + result);

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  ✅ Method References: Complete!          ");
        System.out.println("═══════════════════════════════════════════");
    }

    static void handleClick(String target) {
        System.out.println("   Clicked: " + target);
    }

    static void handleHover(String target) {
        System.out.println("   Hovered: " + target);
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
