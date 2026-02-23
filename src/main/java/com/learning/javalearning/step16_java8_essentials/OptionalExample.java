package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.stream.*;

/**
 * Step 16: Optional - Null-Safe Programming
 * 
 * Optional<T> is a container that may or may not contain a non-null value.
 * It forces you to think about the absent case and provides fluent API
 * to handle nullable values without NullPointerException.
 * 
 * Key rules:
 * - NEVER use Optional for fields or method parameters
 * - USE Optional for method return values when result might be absent
 * - NEVER call .get() without checking isPresent() first
 * - Prefer ifPresent(), map(), orElse() over isPresent() + get()
 */
public class OptionalExample {

    record User(String name, String email, Optional<String> phone, Optional<Address> address) {}
    record Address(String street, String city, Optional<String> zipCode) {}

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  Step 16: Optional - Null-Safe Java      ");
        System.out.println("═══════════════════════════════════════════\n");

        creatingOptionals();
        checkingValues();
        transformingOptionals();
        chainingOperations();
        flatMapVsMap();
        optionalWithStreams();
        antiPatterns();
        realWorldExamples();
    }

    // ============================================================
    // 1. Creating Optionals
    // ============================================================
    static void creatingOptionals() {
        System.out.println("1️⃣ CREATING OPTIONALS");
        System.out.println("─────────────────────────────────\n");

        // Optional.of() - value MUST be non-null
        Optional<String> present = Optional.of("Hello");
        System.out.println("   Optional.of(\"Hello\"): " + present);

        // Optional.empty() - represents absent value
        Optional<String> empty = Optional.empty();
        System.out.println("   Optional.empty(): " + empty);

        // Optional.ofNullable() - handles both null and non-null
        String name = "Java";
        String nullName = null;
        Optional<String> withValue = Optional.ofNullable(name);
        Optional<String> withoutValue = Optional.ofNullable(nullName);
        System.out.println("   ofNullable(\"Java\"): " + withValue);
        System.out.println("   ofNullable(null): " + withoutValue);

        // ⚠️ Optional.of(null) throws NullPointerException!
        try {
            Optional<String> willFail = Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("   ⚠️ Optional.of(null) → NullPointerException!");
        }

        System.out.println();
    }

    // ============================================================
    // 2. Checking and Extracting Values
    // ============================================================
    static void checkingValues() {
        System.out.println("2️⃣ CHECKING AND EXTRACTING VALUES");
        System.out.println("─────────────────────────────────\n");

        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();

        // isPresent() / isEmpty() (isEmpty added in Java 11)
        System.out.println("   present.isPresent(): " + present.isPresent());   // true
        System.out.println("   empty.isPresent(): " + empty.isPresent());       // false
        System.out.println("   empty.isEmpty(): " + empty.isEmpty());           // true

        // get() - ⚠️ throws NoSuchElementException if empty
        System.out.println("   present.get(): " + present.get());
        // empty.get(); // Would throw NoSuchElementException!

        // orElse() - provide default value
        System.out.println("   empty.orElse(\"Default\"): " + empty.orElse("Default"));

        // orElseGet() - provide default via Supplier (lazy)
        System.out.println("   empty.orElseGet(() → computed): " + 
            empty.orElseGet(() -> "Computed at " + System.currentTimeMillis()));

        // orElseThrow() - throw custom exception if empty
        try {
            empty.orElseThrow(() -> new IllegalArgumentException("Value required!"));
        } catch (IllegalArgumentException e) {
            System.out.println("   orElseThrow: " + e.getMessage());
        }

        // orElseThrow() - no-arg version (Java 10+)
        try {
            empty.orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.println("   orElseThrow(): NoSuchElementException");
        }

        // or() - return another Optional (Java 9+)
        Optional<String> result = empty.or(() -> Optional.of("Fallback"));
        System.out.println("   empty.or(fallback): " + result);

        // ⚠️ orElse vs orElseGet - IMPORTANT DIFFERENCE
        System.out.println("\n   ⚠️ orElse vs orElseGet:");
        Optional<String> value = Optional.of("Exists");
        // orElse ALWAYS evaluates the default
        String r1 = value.orElse(expensiveComputation("orElse"));
        // orElseGet ONLY evaluates if empty
        String r2 = value.orElseGet(() -> expensiveComputation("orElseGet"));
        System.out.println("   (Notice orElse was called even though value exists!)");

        System.out.println();
    }

    static String expensiveComputation(String source) {
        System.out.println("   💰 Expensive computation called from " + source);
        return "Default";
    }

    // ============================================================
    // 3. Transforming Optionals
    // ============================================================
    static void transformingOptionals() {
        System.out.println("3️⃣ TRANSFORMING OPTIONALS");
        System.out.println("─────────────────────────────────\n");

        Optional<String> name = Optional.of("  Java Programming  ");
        Optional<String> empty = Optional.empty();

        // map() - transform the value if present
        Optional<String> trimmed = name.map(String::trim);
        System.out.println("   map(trim): " + trimmed);

        Optional<Integer> length = name.map(String::trim).map(String::length);
        System.out.println("   map(trim).map(length): " + length);

        Optional<Integer> emptyLength = empty.map(String::length);
        System.out.println("   empty.map(length): " + emptyLength); // Optional.empty

        // filter() - keep value only if condition matches
        Optional<String> longName = name.map(String::trim).filter(n -> n.length() > 10);
        Optional<String> shortName = name.map(String::trim).filter(n -> n.length() < 5);
        System.out.println("   filter(length > 10): " + longName);
        System.out.println("   filter(length < 5): " + shortName);  // empty

        // ifPresent() - execute action if value exists
        name.map(String::trim).ifPresent(n -> System.out.println("   ifPresent: " + n));
        empty.ifPresent(n -> System.out.println("   This won't print"));

        // ifPresentOrElse() - handle both cases (Java 9+)
        name.ifPresentOrElse(
            n -> System.out.println("   ifPresentOrElse present: " + n.trim()),
            () -> System.out.println("   ifPresentOrElse absent")
        );
        empty.ifPresentOrElse(
            n -> System.out.println("   Present"),
            () -> System.out.println("   ifPresentOrElse absent: empty value")
        );

        System.out.println();
    }

    // ============================================================
    // 4. Chaining Operations
    // ============================================================
    static void chainingOperations() {
        System.out.println("4️⃣ CHAINING OPTIONAL OPERATIONS");
        System.out.println("─────────────────────────────────\n");

        // Complex pipeline
        Optional<String> input = Optional.of("   hello@example.com   ");

        String result = input
                .map(String::trim)
                .filter(s -> s.contains("@"))
                .map(String::toLowerCase)
                .map(s -> s.split("@")[0])
                .orElse("unknown");
        System.out.println("   Email → username: " + result);

        // Pipeline with empty
        String noResult = Optional.<String>empty()
                .map(String::trim)
                .filter(s -> s.contains("@"))
                .map(String::toLowerCase)
                .orElse("unknown");
        System.out.println("   Empty → username: " + noResult);

        // Chaining lookups
        Optional<String> config = findConfig("db.url")
                .or(() -> findConfig("database.url"))
                .or(() -> findConfig("DB_URL"))
                .or(() -> Optional.of("jdbc:h2:mem:default"));
        System.out.println("   Config chain: " + config.get());

        System.out.println();
    }

    static Optional<String> findConfig(String key) {
        Map<String, String> configs = Map.of("DB_URL", "jdbc:mysql://localhost/mydb");
        return Optional.ofNullable(configs.get(key));
    }

    // ============================================================
    // 5. FlatMap vs Map
    // ============================================================
    static void flatMapVsMap() {
        System.out.println("5️⃣ FLATMAP vs MAP - Avoiding Optional<Optional<>>");
        System.out.println("─────────────────────────────────\n");

        User user = new User("Alice", "alice@example.com",
                Optional.of("555-1234"),
                Optional.of(new Address("123 Main St", "Springfield", Optional.of("62701"))));

        User noPhone = new User("Bob", "bob@example.com",
                Optional.empty(),
                Optional.of(new Address("456 Oak Ave", "Shelbyville", Optional.empty())));

        // ❌ map() produces Optional<Optional<String>> - bad!
        Optional<Optional<String>> nestedPhone = Optional.of(user).map(User::phone);
        System.out.println("   map → nested: " + nestedPhone);  // Optional[Optional[555-1234]]

        // ✅ flatMap() flattens to Optional<String> - good!
        Optional<String> phone = Optional.of(user).flatMap(User::phone);
        System.out.println("   flatMap → flat: " + phone);      // Optional[555-1234]

        // Deep nested access with flatMap chain
        Optional<String> zipCode = Optional.of(user)
                .flatMap(User::address)
                .flatMap(Address::zipCode);
        System.out.println("   User → Address → Zip: " + zipCode);

        // Same chain with absent data
        Optional<String> noZip = Optional.of(noPhone)
                .flatMap(User::address)
                .flatMap(Address::zipCode);
        System.out.println("   NoPhone → Address → Zip: " + noZip); // empty

        // Real-world: build display string
        String display = Optional.of(user)
                .map(User::name)
                .map(n -> n + " - " +
                    Optional.of(user).flatMap(User::phone).orElse("No phone") + " - " +
                    Optional.of(user).flatMap(User::address)
                            .map(a -> a.city() + " " + a.zipCode().orElse(""))
                            .orElse("No address"))
                .orElse("Unknown user");
        System.out.println("   Display: " + display);

        System.out.println();
    }

    // ============================================================
    // 6. Optional with Streams
    // ============================================================
    static void optionalWithStreams() {
        System.out.println("6️⃣ OPTIONAL WITH STREAMS");
        System.out.println("─────────────────────────────────\n");

        List<Optional<String>> optionals = List.of(
            Optional.of("Alice"),
            Optional.empty(),
            Optional.of("Charlie"),
            Optional.empty(),
            Optional.of("Eve")
        );

        // Extract present values (Java 9+ stream())
        List<String> presentValues = optionals.stream()
                .flatMap(Optional::stream)  // Java 9+
                .toList();
        System.out.println("   Present values: " + presentValues);

        // Pre-Java 9 way
        List<String> presentOldWay = optionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        System.out.println("   Present (old way): " + presentOldWay);

        // Find first user by name
        List<String> userNames = List.of("Alice", "Bob", "Charlie");
        Optional<String> found = userNames.stream()
                .filter(n -> n.startsWith("B"))
                .findFirst();
        System.out.println("   findFirst(B*): " + found);

        // Convert stream result to Optional
        OptionalInt maxLength = userNames.stream()
                .mapToInt(String::length)
                .max();
        System.out.println("   Max name length: " + maxLength.orElse(0));

        System.out.println();
    }

    // ============================================================
    // 7. Anti-Patterns to Avoid
    // ============================================================
    static void antiPatterns() {
        System.out.println("7️⃣ ANTI-PATTERNS ❌ vs BEST PRACTICES ✅");
        System.out.println("─────────────────────────────────\n");

        Optional<String> opt = Optional.of("Hello");
        Optional<String> empty = Optional.empty();

        // ❌ Anti-pattern 1: isPresent() + get()
        System.out.println("   ❌ if (opt.isPresent()) opt.get()");
        System.out.println("   ✅ opt.ifPresent(action) or opt.orElse(default)");

        // ❌ Anti-pattern 2: Optional.of(value) == null check
        System.out.println("   ❌ Optional.of(value) != null");
        System.out.println("   ✅ Optional.ofNullable(value)");

        // ❌ Anti-pattern 3: Returning null instead of Optional.empty()
        System.out.println("   ❌ return null");
        System.out.println("   ✅ return Optional.empty()");

        // ❌ Anti-pattern 4: Optional as field/parameter
        System.out.println("   ❌ private Optional<String> name;");
        System.out.println("   ✅ private String name; // nullable");

        // ❌ Anti-pattern 5: Optional for collections
        System.out.println("   ❌ Optional<List<String>> items");
        System.out.println("   ✅ List<String> items = Collections.emptyList()");

        // ❌ Anti-pattern 6: Nested Optional
        System.out.println("   ❌ Optional<Optional<String>>");
        System.out.println("   ✅ Use flatMap() to flatten");

        System.out.println("\n   📋 When to Use Optional:");
        System.out.println("   ┌────────────────────────────────────────────────┐");
        System.out.println("   │ ✅ Method return type (value might be absent)  │");
        System.out.println("   │ ✅ Stream terminal operations (findFirst, etc) │");
        System.out.println("   │ ❌ Method parameters                           │");
        System.out.println("   │ ❌ Class fields                                │");
        System.out.println("   │ ❌ Collections (use empty collection instead)  │");
        System.out.println("   │ ❌ Serialization (not serializable)            │");
        System.out.println("   └────────────────────────────────────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 8. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        System.out.println("8️⃣ REAL-WORLD OPTIONAL EXAMPLES");
        System.out.println("─────────────────────────────────\n");

        // Example 1: Config lookup chain
        System.out.println("   📌 Config Lookup Chain:");
        String dbUrl = getEnvVar("DB_URL")
                .or(() -> getSystemProperty("db.url"))
                .or(() -> getConfigFile("database.url"))
                .orElse("jdbc:h2:mem:default");
        System.out.println("   DB URL: " + dbUrl);

        // Example 2: User profile display
        System.out.println("\n   📌 User Profile Display:");
        displayUserProfile("Alice", Optional.of("alice@dev.com"), Optional.of("555-0100"));
        displayUserProfile("Bob", Optional.empty(), Optional.empty());

        // Example 3: Safe parsing
        System.out.println("\n   📌 Safe Number Parsing:");
        System.out.println("   parse(\"123\"): " + safeParseInt("123"));
        System.out.println("   parse(\"abc\"): " + safeParseInt("abc"));
        System.out.println("   parse(null): " + safeParseInt(null));

        // Example 4: Default value computation
        System.out.println("\n   📌 Order Processing:");
        double price = getDiscount("VIP")
                .map(discount -> 100.0 * (1 - discount))
                .orElse(100.0);
        System.out.printf("   VIP price: $%.2f%n", price);

        double regularPrice = getDiscount("REGULAR")
                .map(discount -> 100.0 * (1 - discount))
                .orElse(100.0);
        System.out.printf("   Regular price: $%.2f%n", regularPrice);

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  ✅ Optional: Complete!                  ");
        System.out.println("═══════════════════════════════════════════");
    }

    static Optional<String> getEnvVar(String key) { return Optional.empty(); }
    static Optional<String> getSystemProperty(String key) { return Optional.empty(); }
    static Optional<String> getConfigFile(String key) {
        return Optional.of("jdbc:mysql://localhost:3306/mydb");
    }

    static void displayUserProfile(String name, Optional<String> email, Optional<String> phone) {
        System.out.println("   Name: " + name);
        System.out.println("   Email: " + email.orElse("(not provided)"));
        System.out.println("   Phone: " + phone.orElse("(not provided)"));
    }

    static Optional<Integer> safeParseInt(String value) {
        try {
            return Optional.ofNullable(value).map(Integer::parseInt);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static Optional<Double> getDiscount(String customerType) {
        return switch (customerType) {
            case "VIP" -> Optional.of(0.2);
            case "PREMIUM" -> Optional.of(0.1);
            default -> Optional.empty();
        };
    }
}
