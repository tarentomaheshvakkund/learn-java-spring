package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Step 16: Default & Static Interface Methods
 *
 * Java 8 added two new method types in interfaces:
 * - default methods: provide implementation, inheritable, enable interface evolution
 * - static methods: utility methods tied to the interface, not inheritable
 *
 * This solved the "interface evolution problem" - adding new methods to existing
 * interfaces without breaking all implementations.
 */
public class DefaultStaticInterfaceExample {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  Step 16: Default & Static Interfaces    ");
        System.out.println("═══════════════════════════════════════════\n");

        defaultMethodBasics();
        staticMethodBasics();
        interfaceEvolution();
        multipleInheritanceDiamond();
        functionalInterfaceDefaults();
        compositionWithDefaults();
        realWorldExamples();
    }

    // ============================================================
    // 1. Default Method Basics
    // ============================================================

    interface Greeter {
        String greet(String name); // abstract - must implement

        // Default method - has implementation
        default String greetAll(String... names) {
            return Arrays.stream(names)
                    .map(this::greet)
                    .collect(Collectors.joining(", "));
        }

        // Another default method
        default String shout(String name) {
            return greet(name).toUpperCase() + "!";
        }
    }

    static void defaultMethodBasics() {
        System.out.println("1️⃣ DEFAULT METHOD BASICS");
        System.out.println("─────────────────────────────────\n");

        // Implement only the abstract method - get defaults for free
        Greeter formal = name -> "Good day, " + name;
        Greeter casual = name -> "Hey " + name;

        System.out.println("   " + formal.greet("Alice"));
        System.out.println("   " + casual.greet("Bob"));

        // Default methods work automatically
        System.out.println("   " + formal.greetAll("Alice", "Bob", "Charlie"));
        System.out.println("   " + casual.shout("World"));

        // Override default method
        Greeter enthusiastic = new Greeter() {
            @Override
            public String greet(String name) {
                return "OMG hi " + name;
            }

            @Override
            public String shout(String name) {
                return "🎉 " + greet(name).toUpperCase() + " 🎉";
            }
        };
        System.out.println("   " + enthusiastic.shout("Java"));

        System.out.println();
    }

    // ============================================================
    // 2. Static Method Basics
    // ============================================================

    interface MathUtils {
        // Static methods - utility functions on the interface
        static int add(int a, int b) { return a + b; }
        static int multiply(int a, int b) { return a * b; }
        static boolean isEven(int n) { return n % 2 == 0; }
        static boolean isPositive(int n) { return n > 0; }

        // Static factory method pattern
        static Comparator<String> caseInsensitiveComparator() {
            return String::compareToIgnoreCase;
        }
    }

    interface StringUtils {
        static String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        static boolean isNullOrEmpty(String s) {
            return s == null || s.isEmpty();
        }

        static String repeat(String s, int times) {
            return s.repeat(times); // Java 11+ but concept is Java 8
        }
    }

    static void staticMethodBasics() {
        System.out.println("2️⃣ STATIC INTERFACE METHODS");
        System.out.println("─────────────────────────────────\n");

        // Call static methods on the interface
        System.out.println("   add(3, 5): " + MathUtils.add(3, 5));
        System.out.println("   multiply(4, 7): " + MathUtils.multiply(4, 7));
        System.out.println("   isEven(6): " + MathUtils.isEven(6));

        // Unlike default methods, static methods are NOT inherited
        // class MyMath implements MathUtils { }
        // MyMath.add(1, 2); // ❌ WON'T COMPILE - static methods not inherited

        // String utilities
        System.out.println("   capitalize(\"hello\"): " + StringUtils.capitalize("hello"));
        System.out.println("   isNullOrEmpty(\"\"): " + StringUtils.isNullOrEmpty(""));
        System.out.println("   isNullOrEmpty(\"hi\"): " + StringUtils.isNullOrEmpty("hi"));

        // Factory method pattern
        List<String> names = List.of("Charlie", "alice", "Bob");
        List<String> sorted = names.stream()
                .sorted(MathUtils.caseInsensitiveComparator())
                .toList();
        System.out.println("   Sorted case-insensitive: " + sorted);

        System.out.println("\n   📋 Static vs Default Methods:");
        System.out.println("   ┌─────────────────┬──────────────────┬──────────────────┐");
        System.out.println("   │ Feature         │ default          │ static           │");
        System.out.println("   ├─────────────────┼──────────────────┼──────────────────┤");
        System.out.println("   │ Inherited?      │ ✅ Yes           │ ❌ No            │");
        System.out.println("   │ Override?       │ ✅ Yes           │ ❌ No            │");
        System.out.println("   │ Access 'this'?  │ ✅ Yes           │ ❌ No            │");
        System.out.println("   │ Called via       │ instance.method  │ Interface.method │");
        System.out.println("   └─────────────────┴──────────────────┴──────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 3. Interface Evolution (The "Why")
    // ============================================================

    // Before Java 8: Adding a method to an interface broke ALL implementors
    interface CollectionV1 {
        void add(Object item);
        int size();
        // If we add stream() here, ALL existing classes break! 😱
    }

    // After Java 8: default methods let us evolve interfaces safely
    interface CollectionV2 {
        void add(Object item);
        int size();

        // NEW! Existing implementations DON'T break
        default boolean isEmpty() {
            return size() == 0;
        }

        // This is exactly what Java did with List, Collection, Map etc.
        // Added: forEach, stream, removeIf, spliterator etc.
    }

    static void interfaceEvolution() {
        System.out.println("3️⃣ INTERFACE EVOLUTION (Why Default Methods Exist)");
        System.out.println("─────────────────────────────────\n");

        System.out.println("   The Problem (Pre-Java 8):");
        System.out.println("   • Adding a method to java.util.List would break");
        System.out.println("     EVERY class implementing List worldwide!");
        System.out.println();
        System.out.println("   The Solution (Java 8+):");
        System.out.println("   • default methods provide implementation");
        System.out.println("   • Existing classes inherit them automatically");
        System.out.println();
        System.out.println("   Real methods added via default in Java 8:");
        System.out.println("   • Iterable.forEach(Consumer)");
        System.out.println("   • Collection.stream()");
        System.out.println("   • Collection.removeIf(Predicate)");
        System.out.println("   • List.sort(Comparator)");
        System.out.println("   • List.replaceAll(UnaryOperator)");
        System.out.println("   • Map.forEach(BiConsumer)");
        System.out.println("   • Map.getOrDefault(key, defaultValue)");
        System.out.println("   • Map.putIfAbsent(key, value)");
        System.out.println("   • Map.computeIfAbsent/Present/compute");
        System.out.println("   • Comparator.thenComparing(...)");

        // Demonstrate some
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.putIfAbsent("b", 2);     // default method
        map.computeIfAbsent("c", k -> 3); // default method
        int val = map.getOrDefault("z", 0); // default method
        System.out.println("\n   Map with defaults: " + map + ", getOrDefault(z): " + val);

        List<String> list = new ArrayList<>(List.of("banana", "apple", "cherry"));
        list.sort(Comparator.naturalOrder()); // default method on List
        list.replaceAll(String::toUpperCase); // default method on List
        System.out.println("   List after sort+replaceAll: " + list);

        System.out.println();
    }

    // ============================================================
    // 4. Multiple Inheritance - Diamond Problem
    // ============================================================

    interface Flyable {
        default String move() { return "Flying"; }
        default String describe() { return "I can fly"; }
    }

    interface Swimmable {
        default String move() { return "Swimming"; }
        default String describe() { return "I can swim"; }
    }

    // When implementing two interfaces with same default method → MUST override
    static class Duck implements Flyable, Swimmable {
        @Override
        public String move() {
            // Can choose which parent's implementation to use
            return Flyable.super.move() + " and " + Swimmable.super.move();
        }

        @Override
        public String describe() {
            // Or provide completely new implementation
            return "I'm a duck - " + Flyable.super.describe() + " and " + Swimmable.super.describe();
        }
    }

    // No conflict if only one interface has the default
    interface Walkable {
        default String terrain() { return "Land"; }
    }

    static class Human implements Walkable, Swimmable {
        @Override
        public String move() {
            return "Walking"; // Only override conflicting methods if needed
        }
        // terrain() is automatically inherited from Walkable (no conflict)
    }

    static void multipleInheritanceDiamond() {
        System.out.println("4️⃣ DIAMOND PROBLEM (Multiple Inheritance)");
        System.out.println("─────────────────────────────────\n");

        Duck duck = new Duck();
        System.out.println("   Duck.move(): " + duck.move());
        System.out.println("   Duck.describe(): " + duck.describe());

        Human human = new Human();
        System.out.println("   Human.move(): " + human.move());
        System.out.println("   Human.terrain(): " + human.terrain());

        System.out.println("\n   📋 Diamond Problem Resolution Rules:");
        System.out.println("   1. Class methods always win over interface defaults");
        System.out.println("   2. More specific interface wins (sub-interface > parent)");
        System.out.println("   3. If ambiguous, class MUST override and choose");
        System.out.println("   4. Use InterfaceName.super.method() to call specific parent");

        System.out.println();
    }

    // ============================================================
    // 5. Functional Interface Default Methods
    // ============================================================
    static void functionalInterfaceDefaults() {
        System.out.println("5️⃣ FUNCTIONAL INTERFACE DEFAULTS");
        System.out.println("─────────────────────────────────\n");

        // Predicate combining with default methods
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isSmall = n -> n < 100;

        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);
        Predicate<Integer> isOdd = isEven.negate();
        Predicate<Integer> isEvenPositiveSmall = isEven.and(isPositive).and(isSmall);

        List<Integer> numbers = List.of(-4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 200);

        System.out.println("   Even AND Positive: " + filter(numbers, isEvenAndPositive));
        System.out.println("   Even OR Positive: " + filter(numbers, isEvenOrPositive));
        System.out.println("   Odd: " + filter(numbers, isOdd));
        System.out.println("   Even+Positive+Small: " + filter(numbers, isEvenPositiveSmall));

        // Function composition with default methods
        Function<String, String> trim = String::trim;
        Function<String, String> lower = String::toLowerCase;
        Function<String, String> exclaim = s -> s + "!";

        Function<String, String> pipeline = trim.andThen(lower).andThen(exclaim);
        System.out.println("\n   Pipeline(\"  HELLO  \"): " + pipeline.apply("  HELLO  "));

        // compose vs andThen
        Function<Integer, Integer> multiplyBy2 = x -> x * 2;
        Function<Integer, Integer> add10 = x -> x + 10;

        System.out.println("   andThen: multiply then add: " + multiplyBy2.andThen(add10).apply(5)); // (5*2)+10=20
        System.out.println("   compose: add then multiply: " + multiplyBy2.compose(add10).apply(5)); // (5+10)*2=30

        // Consumer chaining
        Consumer<String> print = s -> System.out.print("   → " + s);
        Consumer<String> println = s -> System.out.println(" (length: " + s.length() + ")");
        Consumer<String> printWithLength = print.andThen(println);

        printWithLength.accept("Hello");
        printWithLength.accept("Java 8");

        // Comparator chaining with default methods
        System.out.println();
        record Student(String name, int grade, double gpa) {}
        List<Student> students = List.of(
            new Student("Alice", 12, 3.8),
            new Student("Bob", 11, 3.9),
            new Student("Charlie", 12, 3.8),
            new Student("Diana", 11, 3.7)
        );

        List<Student> sorted = students.stream()
                .sorted(Comparator.comparing(Student::grade)
                        .thenComparing(Student::gpa, Comparator.reverseOrder())
                        .thenComparing(Student::name))
                .toList();
        System.out.println("   Sorted students (grade → GPA desc → name):");
        sorted.forEach(s -> System.out.printf("   %s: Grade %d, GPA %.1f%n",
            s.name(), s.grade(), s.gpa()));

        System.out.println();
    }

    static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).toList();
    }

    // ============================================================
    // 6. Composition with Default Methods
    // ============================================================

    @FunctionalInterface
    interface Validator<T> {
        boolean validate(T t);

        default Validator<T> and(Validator<T> other) {
            return t -> this.validate(t) && other.validate(t);
        }

        default Validator<T> or(Validator<T> other) {
            return t -> this.validate(t) || other.validate(t);
        }

        default Validator<T> negate() {
            return t -> !this.validate(t);
        }

        // Static factory methods
        static <T> Validator<T> of(Predicate<T> predicate) {
            return predicate::test;
        }

        static <T> Validator<T> alwaysTrue() {
            return t -> true;
        }
    }

    @FunctionalInterface
    interface Transformer<T> {
        T transform(T input);

        default Transformer<T> andThen(Transformer<T> after) {
            return input -> after.transform(this.transform(input));
        }

        static <T> Transformer<T> identity() {
            return t -> t;
        }

        static <T> Transformer<T> chain(List<Transformer<T>> transformers) {
            return transformers.stream()
                    .reduce(identity(), Transformer::andThen);
        }
    }

    static void compositionWithDefaults() {
        System.out.println("6️⃣ BUILDING COMPOSABLE APIs WITH DEFAULTS");
        System.out.println("─────────────────────────────────\n");

        // Composable validators
        Validator<String> notEmpty = s -> !s.isEmpty();
        Validator<String> hasAt = s -> s.contains("@");
        Validator<String> hasDot = s -> s.contains(".");
        Validator<String> noSpaces = s -> !s.contains(" ");

        Validator<String> emailValidator = notEmpty.and(hasAt).and(hasDot).and(noSpaces);

        List<String> emails = List.of("good@email.com", "bad", "no@dot", "has @space.com", "");
        emails.forEach(e -> System.out.println("   " + e + " → " +
            (emailValidator.validate(e) ? "✅ valid" : "❌ invalid")));

        // Composable transformers
        System.out.println();
        Transformer<String> trim = String::trim;
        Transformer<String> lower = String::toLowerCase;
        Transformer<String> removeSpecial = s -> s.replaceAll("[^a-z0-9]", "");

        Transformer<String> slugify = trim.andThen(lower).andThen(removeSpecial);
        System.out.println("   Slugify(\"  Hello World! \"): " + slugify.transform("  Hello World! "));

        // Chain from list
        List<Transformer<String>> transforms = List.of(trim, lower, s -> s.replace(" ", "-"));
        Transformer<String> pipeline = Transformer.chain(transforms);
        System.out.println("   Pipeline(\"  Hello World \"): " + pipeline.transform("  Hello World "));

        System.out.println();
    }

    // ============================================================
    // 7. Real-World Examples
    // ============================================================

    // Plugin system with defaults
    interface Plugin {
        String name();
        void execute(Map<String, Object> context);

        default int priority() { return 0; } // Default priority
        default boolean isEnabled() { return true; } // Enabled by default
        default void initialize() { /* no-op by default */ }
        default void shutdown() { /* no-op by default */ }

        // Static factory
        static Plugin simple(String name, Consumer<Map<String, Object>> action) {
            return new Plugin() {
                @Override public String name() { return name; }
                @Override public void execute(Map<String, Object> ctx) { action.accept(ctx); }
            };
        }
    }

    // Repository pattern with defaults
    interface Repository<T> {
        List<T> findAll();
        Optional<T> findById(String id);
        void save(T entity);

        default T findByIdOrThrow(String id) {
            return findById(id).orElseThrow(() ->
                new NoSuchElementException("Entity not found: " + id));
        }

        default boolean exists(String id) {
            return findById(id).isPresent();
        }

        default long count() {
            return findAll().size();
        }
    }

    static void realWorldExamples() {
        System.out.println("7️⃣ REAL-WORLD EXAMPLES");
        System.out.println("─────────────────────────────────\n");

        // Plugin system
        System.out.println("   📌 Plugin System:");
        Plugin logger = Plugin.simple("Logger",
            ctx -> System.out.println("   [LOG] Processing: " + ctx));

        Plugin timer = new Plugin() {
            @Override public String name() { return "Timer"; }
            @Override public int priority() { return 10; } // Override default
            @Override public void execute(Map<String, Object> ctx) {
                long start = System.nanoTime();
                System.out.println("   [TIMER] Execution tracked");
                ctx.put("elapsed_ns", System.nanoTime() - start);
            }
        };

        List<Plugin> plugins = List.of(logger, timer);
        Map<String, Object> context = new HashMap<>(Map.of("data", "test"));

        // Execute plugins sorted by priority (using default priority)
        plugins.stream()
                .filter(Plugin::isEnabled) // default method
                .sorted(Comparator.comparing(Plugin::priority))
                .forEach(p -> {
                    System.out.println("   Running: " + p.name() + " (priority: " + p.priority() + ")");
                    p.execute(context);
                });

        // Builder pattern with interface defaults
        System.out.println("\n   📌 Fluent API with Defaults:");
        interface Configurable<T> {
            T withConfig(String key, String value);
            default T withName(String name) { return withConfig("name", name); }
            default T withTimeout(int ms) { return withConfig("timeout", String.valueOf(ms)); }
            default T enabled() { return withConfig("enabled", "true"); }
        }

        System.out.println("   (Configurable interface supports fluent API with defaults)");

        // Summary
        System.out.println("\n   📋 Default & Static Interface Methods Summary:");
        System.out.println("   ┌─────────────────────────────────────────────────────────┐");
        System.out.println("   │ Default Methods:                                        │");
        System.out.println("   │ • Enable interface evolution without breaking code       │");
        System.out.println("   │ • Provide shared behavior across implementations        │");
        System.out.println("   │ • Support composition (and, or, andThen, compose)       │");
        System.out.println("   │ • Can be overridden by implementing classes             │");
        System.out.println("   │                                                         │");
        System.out.println("   │ Static Methods:                                         │");
        System.out.println("   │ • Replace utility classes (Collections → Collection)    │");
        System.out.println("   │ • Factory methods (Comparator.comparing())              │");
        System.out.println("   │ • Helper/validation methods                             │");
        System.out.println("   │ • NOT inherited by implementing classes                 │");
        System.out.println("   └─────────────────────────────────────────────────────────┘");

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  ✅ Default & Static Interfaces: Complete!");
        System.out.println("═══════════════════════════════════════════");
    }
}
