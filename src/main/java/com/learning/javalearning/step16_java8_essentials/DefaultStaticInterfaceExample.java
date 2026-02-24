package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(DefaultStaticInterfaceExample.class.getName());

    // S1192: repeated string literals extracted as constants
    private static final String SECTION_DIVIDER = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
    private static final String HEADER_BORDER   = "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550";
    private static final String TABLE_BORDER    = "   +------------------+------------------+------------------+";
    private static final String FMT_ONE_ARG     = "   {0}";
    private static final String NAME_ALICE      = "Alice";
    private static final String NAME_CHARLIE    = "Charlie";

    public static void main(String[] args) {
        logger.info(HEADER_BORDER);
        logger.info("  Step 16: Default & Static Interfaces    ");
        logger.info(HEADER_BORDER);

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
        logger.info("1\uFE0F\u20E3 DEFAULT METHOD BASICS");
        logger.info(SECTION_DIVIDER);

        // Implement only the abstract method - get defaults for free
        Greeter formal = name -> "Good day, " + name;
        Greeter casual = name -> "Hey " + name;

        // S2629: use JUL parameterised form so the argument is never evaluated
        // unconditionally - the logger checks the level first
        logger.log(Level.INFO, FMT_ONE_ARG, formal.greet(NAME_ALICE));
        logger.log(Level.INFO, FMT_ONE_ARG, casual.greet("Bob"));

        // Default methods work automatically
        logger.log(Level.INFO, FMT_ONE_ARG, formal.greetAll(NAME_ALICE, "Bob", NAME_CHARLIE));
        logger.log(Level.INFO, FMT_ONE_ARG, casual.shout("World"));

        // Override default method
        Greeter enthusiastic = new Greeter() {
            @Override
            public String greet(String name) {
                return "OMG hi " + name;
            }

            @Override
            public String shout(String name) {
                return "\uD83C\uDF89 " + greet(name).toUpperCase() + " \uD83C\uDF89";
            }
        };
        logger.log(Level.INFO, FMT_ONE_ARG, enthusiastic.shout("Java"));

        logger.info("");
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
        logger.info("2\uFE0F\u20E3 STATIC INTERFACE METHODS");
        logger.info(SECTION_DIVIDER);

        // Call static methods on the interface
        logger.log(Level.INFO, "   add(3, 5): {0}", MathUtils.add(3, 5));
        logger.log(Level.INFO, "   multiply(4, 7): {0}", MathUtils.multiply(4, 7));
        logger.log(Level.INFO, "   isEven(6): {0}", MathUtils.isEven(6));

        // Unlike default methods, static methods are NOT inherited
        // (MyMath.add(1,2) would not compile - static methods not inherited)

        // String utilities
        logger.log(Level.INFO, "   capitalize(\"hello\"): {0}", StringUtils.capitalize("hello"));
        logger.log(Level.INFO, "   isNullOrEmpty(\"\"): {0}", StringUtils.isNullOrEmpty(""));
        logger.log(Level.INFO, "   isNullOrEmpty(\"hi\"): {0}", StringUtils.isNullOrEmpty("hi"));

        // Factory method pattern
        List<String> names = List.of(NAME_CHARLIE, "alice", "Bob");
        List<String> sorted = names.stream()
                .sorted(MathUtils.caseInsensitiveComparator())
                .toList();
        logger.log(Level.INFO, "   Sorted case-insensitive: {0}", sorted);

        logger.info("   Static vs Default Methods:");
        logger.info(TABLE_BORDER);
        logger.info("   | Feature          | default          | static           |");
        logger.info(TABLE_BORDER);
        logger.info("   | Inherited?       | Yes              | No               |");
        logger.info("   | Override?        | Yes              | No               |");
        logger.info("   | Access 'this'?   | Yes              | No               |");
        logger.info("   | Called via       | instance.method  | Interface.method |");
        logger.info(TABLE_BORDER);

        logger.info("");
    }

    // ============================================================
    // 3. Interface Evolution (The "Why")
    // ============================================================

    // Before Java 8: Adding a method to an interface broke ALL implementors
    interface CollectionV1 {
        void add(Object item);
        int size();
        // If we add stream() here, ALL existing classes break!
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
        logger.info("3\uFE0F\u20E3 INTERFACE EVOLUTION (Why Default Methods Exist)");
        logger.info(SECTION_DIVIDER);

        logger.info("   The Problem (Pre-Java 8):");
        logger.info("   - Adding a method to java.util.List would break");
        logger.info("     EVERY class implementing List worldwide!");
        logger.info("");
        logger.info("   The Solution (Java 8+):");
        logger.info("   - default methods provide implementation");
        logger.info("   - Existing classes inherit them automatically");
        logger.info("");
        logger.info("   Real methods added via default in Java 8:");
        logger.info("   - Iterable.forEach(Consumer)");
        logger.info("   - Collection.stream()");
        logger.info("   - Collection.removeIf(Predicate)");
        logger.info("   - List.sort(Comparator)");
        logger.info("   - List.replaceAll(UnaryOperator)");
        logger.info("   - Map.forEach(BiConsumer)");
        logger.info("   - Map.getOrDefault(key, defaultValue)");
        logger.info("   - Map.putIfAbsent(key, value)");
        logger.info("   - Map.computeIfAbsent/Present/compute");
        logger.info("   - Comparator.thenComparing(...)");

        // Demonstrate some
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.putIfAbsent("b", 2);          // default method
        map.computeIfAbsent("c", k -> 3); // default method
        int val = map.getOrDefault("z", 0); // default method
        logger.log(Level.INFO, "   Map with defaults: {0}, getOrDefault(z): {1}", new Object[]{map, val});

        List<String> list = new ArrayList<>(List.of("banana", "apple", "cherry"));
        list.sort(Comparator.naturalOrder()); // default method on List
        list.replaceAll(String::toUpperCase); // default method on List
        logger.log(Level.INFO, "   List after sort+replaceAll: {0}", list);

        logger.info("");
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

    // When implementing two interfaces with same default method - MUST override
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
        logger.info("4\uFE0F\u20E3 DIAMOND PROBLEM (Multiple Inheritance)");
        logger.info(SECTION_DIVIDER);

        Duck duck = new Duck();
        logger.log(Level.INFO, "   Duck.move(): {0}", duck.move());
        logger.log(Level.INFO, "   Duck.describe(): {0}", duck.describe());

        Human human = new Human();
        logger.log(Level.INFO, "   Human.move(): {0}", human.move());
        logger.log(Level.INFO, "   Human.terrain(): {0}", human.terrain());

        logger.info("   Diamond Problem Resolution Rules:");
        logger.info("   1. Class methods always win over interface defaults");
        logger.info("   2. More specific interface wins (sub-interface > parent)");
        logger.info("   3. If ambiguous, class MUST override and choose");
        logger.info("   4. Use InterfaceName.super.method() to call specific parent");

        logger.info("");
    }

    // ============================================================
    // 5. Functional Interface Default Methods
    // ============================================================
    @SuppressWarnings("java:S4276") // Section intentionally uses boxed Predicate<Integer> and
    // Function<String,String> to demonstrate default-method composition (.and/.or/.negate,
    // .andThen/.compose) - switching to IntPredicate/UnaryOperator would remove .compose demo
    static void functionalInterfaceDefaults() {
        logger.info("5\uFE0F\u20E3 FUNCTIONAL INTERFACE DEFAULTS");
        logger.info(SECTION_DIVIDER);

        // Predicate combining with default methods
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isSmall = n -> n < 100;

        Predicate<Integer> isEvenAndPositive   = isEven.and(isPositive);
        Predicate<Integer> isEvenOrPositive    = isEven.or(isPositive);
        Predicate<Integer> isOdd               = isEven.negate();
        Predicate<Integer> isEvenPositiveSmall = isEven.and(isPositive).and(isSmall);

        List<Integer> numbers = List.of(-4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 200);

        logger.log(Level.INFO, "   Even AND Positive: {0}", filter(numbers, isEvenAndPositive));
        logger.log(Level.INFO, "   Even OR Positive: {0}",  filter(numbers, isEvenOrPositive));
        logger.log(Level.INFO, "   Odd: {0}",               filter(numbers, isOdd));
        logger.log(Level.INFO, "   Even+Positive+Small: {0}", filter(numbers, isEvenPositiveSmall));

        // Function composition with default methods
        // Function<String,String> used deliberately (not UnaryOperator) to show .compose()
        Function<String, String> trim    = String::trim;
        Function<String, String> lower   = String::toLowerCase;
        Function<String, String> exclaim = s -> s + "!";

        Function<String, String> pipeline = trim.andThen(lower).andThen(exclaim);
        logger.log(Level.INFO, "   Pipeline(\"  HELLO  \"): {0}", pipeline.apply("  HELLO  "));

        // compose vs andThen - Function used to show both directions
        Function<Integer, Integer> multiplyBy2 = x -> x * 2;
        Function<Integer, Integer> add10       = x -> x + 10;

        logger.log(Level.INFO, "   andThen: multiply then add: {0}", multiplyBy2.andThen(add10).apply(5)); // (5*2)+10=20
        logger.log(Level.INFO, "   compose: add then multiply: {0}", multiplyBy2.compose(add10).apply(5)); // (5+10)*2=30

        // Consumer chaining
        Consumer<String> print           = s -> logger.log(Level.INFO, "   -> {0}", s);
        Consumer<String> println         = s -> logger.log(Level.INFO, " (length: {0})", s.length());
        Consumer<String> printWithLength = print.andThen(println);

        printWithLength.accept("Hello");
        printWithLength.accept("Java 8");

        // Comparator chaining with default methods
        logger.info("");
        record Student(String name, int grade, double gpa) {}
        List<Student> students = List.of(
            new Student(NAME_ALICE,   12, 3.8),
            new Student("Bob",        11, 3.9),
            new Student(NAME_CHARLIE, 12, 3.8),
            new Student("Diana",      11, 3.7)
        );

        List<Student> sorted = students.stream()
                .sorted(Comparator.comparing(Student::grade)
                        .thenComparing(Student::gpa, Comparator.reverseOrder())
                        .thenComparing(Student::name))
                .toList();
        logger.info("   Sorted students (grade -> GPA desc -> name):");
        sorted.forEach(s -> logger.log(Level.INFO, "   {0}: Grade {1}, GPA {2}",
            new Object[]{s.name(), s.grade(), s.gpa()}));

        logger.info("");
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
        logger.info("6\uFE0F\u20E3 BUILDING COMPOSABLE APIs WITH DEFAULTS");
        logger.info(SECTION_DIVIDER);

        // Composable validators
        Validator<String> notEmpty  = s -> !s.isEmpty();
        Validator<String> hasAt     = s -> s.contains("@");
        Validator<String> hasDot    = s -> s.contains(".");
        Validator<String> noSpaces  = s -> !s.contains(" ");

        Validator<String> emailValidator = notEmpty.and(hasAt).and(hasDot).and(noSpaces);

        List<String> emails = List.of("good@email.com", "bad", "no@dot", "has @space.com", "");
        emails.forEach(e -> logger.log(Level.INFO, "   {0} -> {1}",
            new Object[]{e, emailValidator.validate(e) ? "valid" : "invalid"}));

        // Composable transformers
        logger.info("");
        Transformer<String> trim          = String::trim;
        Transformer<String> lower         = String::toLowerCase;
        Transformer<String> removeSpecial = s -> s.replaceAll("[^a-z0-9]", "");

        Transformer<String> slugify = trim.andThen(lower).andThen(removeSpecial);
        logger.log(Level.INFO, "   Slugify(\"  Hello World! \"): {0}", slugify.transform("  Hello World! "));

        // Chain from list
        List<Transformer<String>> transforms = List.of(trim, lower, s -> s.replace(" ", "-"));
        Transformer<String> pipeline = Transformer.chain(transforms);
        logger.log(Level.INFO, "   Pipeline(\"  Hello World \"): {0}", pipeline.transform("  Hello World "));

        logger.info("");
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
        logger.info("7\uFE0F\u20E3 REAL-WORLD EXAMPLES");
        logger.info(SECTION_DIVIDER);

        // Plugin system
        logger.info("   Plugin System:");
        Plugin loggingPlugin = Plugin.simple("Logger",
            ctx -> logger.log(Level.INFO, "   [LOG] Processing: {0}", ctx));

        Plugin timer = new Plugin() {
            @Override public String name() { return "Timer"; }
            @Override public int priority() { return 10; } // Override default
            @Override public void execute(Map<String, Object> ctx) {
                long start = System.nanoTime();
                logger.info("   [TIMER] Execution tracked");
                ctx.put("elapsed_ns", System.nanoTime() - start);
            }
        };

        List<Plugin> plugins = List.of(loggingPlugin, timer);
        Map<String, Object> context = new HashMap<>(Map.of("data", "test"));

        // Execute plugins sorted by priority (using default priority)
        plugins.stream()
                .filter(Plugin::isEnabled) // default method
                .sorted(Comparator.comparing(Plugin::priority))
                .forEach(p -> {
                    logger.log(Level.INFO, "   Running: {0} (priority: {1})",
                        new Object[]{p.name(), p.priority()});
                    p.execute(context);
                });

        // Builder pattern with interface defaults
        logger.info("   Fluent API with Defaults:");
        interface Configurable<T> {
            T withConfig(String key, String value);
            default T withName(String name) { return withConfig("name", name); }
            default T withTimeout(int ms) { return withConfig("timeout", String.valueOf(ms)); }
            default T enabled() { return withConfig("enabled", "true"); }
        }

        logger.info("   (Configurable interface supports fluent API with defaults)");

        // Summary
        logger.info("   Default & Static Interface Methods Summary:");
        logger.info("   Default Methods:");
        logger.info("   - Enable interface evolution without breaking code");
        logger.info("   - Provide shared behavior across implementations");
        logger.info("   - Support composition (and, or, andThen, compose)");
        logger.info("   - Can be overridden by implementing classes");
        logger.info("   Static Methods:");
        logger.info("   - Replace utility classes (Collections -> Collection)");
        logger.info("   - Factory methods (Comparator.comparing())");
        logger.info("   - Helper/validation methods");
        logger.info("   - NOT inherited by implementing classes");

        logger.info(HEADER_BORDER);
        logger.info("  Default & Static Interfaces: Complete!");
        logger.info(HEADER_BORDER);
    }
}
