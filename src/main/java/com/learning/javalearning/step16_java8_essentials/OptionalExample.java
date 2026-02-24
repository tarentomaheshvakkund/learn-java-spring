package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(OptionalExample.class.getName());

    private static final String SEPARATOR = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n";
    private static final String GREETING_HELLO = "Hello";
    private static final String DEFAULT_UNKNOWN = "unknown";
    private static final String CONFIG_DB_URL   = "DB_URL";
    private static final String CONFIG_DB_DOT_URL = "db.url";
    private static final String CONFIG_DATABASE_URL = "database.url";
    private static final String NAME_ALICE      = "Alice";

    record User(String name, String email, Optional<String> phone, Optional<Address> address) {}
    record Address(String street, String city, Optional<String> zipCode) {}

    public static void main(String[] args) {
        logger.info("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        logger.info("  Step 16: Optional - Null-Safe Java      ");
        logger.info("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");

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
        logger.info("1\uFE0F\u20E3 CREATING OPTIONALS");
        logger.info(SEPARATOR);

        // Optional.of() - value MUST be non-null
        Optional<String> present = Optional.of(GREETING_HELLO);
        logger.log(Level.INFO, "   Optional.of(\"Hello\"): {0}", present);

        // Optional.empty() - represents absent value
        Optional<String> empty = Optional.empty();
        logger.log(Level.INFO, "   Optional.empty(): {0}", empty);

        // Optional.ofNullable() - handles both null and non-null
        String name = "Java";
        String nullName = null;
        Optional<String> withValue    = Optional.ofNullable(name);
        Optional<String> withoutValue = Optional.ofNullable(nullName);
        logger.log(Level.INFO, "   ofNullable(\"Java\"): {0}", withValue);
        logger.log(Level.INFO, "   ofNullable(null): {0}", withoutValue);

        // ⚠️ Optional.of(null) throws NullPointerException!
        try {
            Optional<String> willFail = Optional.of(null);
            // This line is never reached; willFail exists to satisfy the compiler
            logger.log(Level.INFO, "   (unexpected value: {0})", willFail);
        } catch (NullPointerException e) {
            logger.info("   \u26A0\uFE0F Optional.of(null) \u2192 NullPointerException!");
        }

        logger.info("");
    }

    // ============================================================
    // 2. Checking and Extracting Values
    // ============================================================
    static void checkingValues() {
        logger.info("2\uFE0F\u20E3 CHECKING AND EXTRACTING VALUES");
        logger.info(SEPARATOR);

        Optional<String> present = Optional.of(GREETING_HELLO);
        Optional<String> empty = Optional.empty();

        // isPresent() / isEmpty() (isEmpty added in Java 11)
        logger.log(Level.INFO, "   present.isPresent(): {0}", present.isPresent());   // true
        logger.log(Level.INFO, "   empty.isPresent(): {0}",   empty.isPresent());     // false
        logger.log(Level.INFO, "   empty.isEmpty(): {0}",     empty.isEmpty());       // true

        // get() - ⚠️ throws NoSuchElementException if empty
        logger.log(Level.INFO, "   present.get(): {0}", present.get());
        // empty.get(); // Would throw NoSuchElementException!

        // orElse() - provide default value
        logger.log(Level.INFO, "   empty.orElse(\"Default\"): {0}", empty.orElse("Default"));

        // orElseGet() - provide default via Supplier (lazy)
        logger.log(Level.INFO, "   empty.orElseGet(() \u2192 computed): {0}",
            empty.orElseGet(() -> "Computed at " + System.currentTimeMillis()));

        // orElseThrow() - throw custom exception if empty
        try {
            empty.orElseThrow(() -> new IllegalArgumentException("Value required!"));
        } catch (IllegalArgumentException e) {
            logger.log(Level.INFO, "   orElseThrow: {0}", e.getMessage());
        }

        // orElseThrow() - no-arg version (Java 10+)
        try {
            empty.orElseThrow();
        } catch (NoSuchElementException e) {
            logger.info("   orElseThrow(): NoSuchElementException");
        }

        // or() - return another Optional (Java 9+)
        Optional<String> result = empty.or(() -> Optional.of("Fallback"));
        logger.log(Level.INFO, "   empty.or(fallback): {0}", result);

        // ⚠️ orElse vs orElseGet - IMPORTANT DIFFERENCE
        logger.info("\n   \u26A0\uFE0F orElse vs orElseGet:");
        Optional<String> value = Optional.of("Exists");
        // orElse ALWAYS evaluates the default — assign to show the side-effect output
        String r1 = value.orElse(expensiveComputation("orElse"));
        // orElseGet ONLY evaluates if empty — assign to show the side-effect output
        String r2 = value.orElseGet(() -> expensiveComputation("orElseGet"));
        logger.log(Level.INFO, "   (orElse returned: {0}, orElseGet returned: {1})", new Object[]{r1, r2});
        logger.info("   (Notice orElse was called even though value exists!)");

        logger.info("");
    }

    static String expensiveComputation(String source) {
        logger.log(Level.INFO, "   \uD83D\uDCB0 Expensive computation called from {0}", source);
        return "Default";
    }

    // ============================================================
    // 3. Transforming Optionals
    // ============================================================
    static void transformingOptionals() {
        logger.info("3\uFE0F\u20E3 TRANSFORMING OPTIONALS");
        logger.info(SEPARATOR);

        Optional<String> name  = Optional.of("  Java Programming  ");
        Optional<String> empty = Optional.empty();

        // map() - transform the value if present
        Optional<String> trimmed = name.map(String::trim);
        logger.log(Level.INFO, "   map(trim): {0}", trimmed);

        Optional<Integer> length = name.map(String::trim).map(String::length);
        logger.log(Level.INFO, "   map(trim).map(length): {0}", length);

        Optional<Integer> emptyLength = empty.map(String::length);
        logger.log(Level.INFO, "   empty.map(length): {0}", emptyLength); // Optional.empty

        // filter() - keep value only if condition matches
        Optional<String> longName  = name.map(String::trim).filter(n -> n.length() > 10);
        Optional<String> shortName = name.map(String::trim).filter(n -> n.length() < 5);
        logger.log(Level.INFO, "   filter(length > 10): {0}", longName);
        logger.log(Level.INFO, "   filter(length < 5): {0}",  shortName); // empty

        // ifPresent() - execute action if value exists
        name.map(String::trim).ifPresent(n -> logger.log(Level.INFO, "   ifPresent: {0}", n));
        empty.ifPresent(n -> logger.log(Level.INFO, "   This won''t print: {0}", n));

        // ifPresentOrElse() - handle both cases (Java 9+)
        name.ifPresentOrElse(
            n -> logger.log(Level.INFO, "   ifPresentOrElse present: {0}", n.trim()),
            () -> logger.info("   ifPresentOrElse absent")
        );
        empty.ifPresentOrElse(
            n -> logger.log(Level.INFO, "   Present: {0}", n),
            () -> logger.info("   ifPresentOrElse absent: empty value")
        );

        logger.info("");
    }

    // ============================================================
    // 4. Chaining Operations
    // ============================================================
    static void chainingOperations() {
        logger.info("4\uFE0F\u20E3 CHAINING OPTIONAL OPERATIONS");
        logger.info(SEPARATOR);

        // Complex pipeline
        Optional<String> input = Optional.of("   hello@example.com   ");

        String result = input
                .map(String::trim)
                .filter(s -> s.contains("@"))
                .map(String::toLowerCase)
                .map(s -> s.split("@")[0])
                .orElse(DEFAULT_UNKNOWN);
        logger.log(Level.INFO, "   Email \u2192 username: {0}", result);

        // Pipeline with empty
        String noResult = Optional.<String>empty()
                .map(String::trim)
                .filter(s -> s.contains("@"))
                .map(String::toLowerCase)
                .orElse(DEFAULT_UNKNOWN);
        logger.log(Level.INFO, "   Empty \u2192 username: {0}", noResult);

        // Chaining lookups
        Optional<String> config = findConfig(CONFIG_DB_DOT_URL)
                .or(() -> findConfig(CONFIG_DATABASE_URL))
                .or(() -> findConfig(CONFIG_DB_URL))
                .or(() -> Optional.of("jdbc:h2:mem:default"));
        logger.log(Level.INFO, "   Config chain: {0}", config.orElse(DEFAULT_UNKNOWN));

        logger.info("");
    }

    static Optional<String> findConfig(String key) {
        Map<String, String> configs = Map.of(CONFIG_DB_URL, "jdbc:mysql://localhost/mydb");
        return Optional.ofNullable(configs.get(key));
    }

    // ============================================================
    // 5. FlatMap vs Map
    // ============================================================
    static void flatMapVsMap() {
        logger.info("5\uFE0F\u20E3 FLATMAP vs MAP - Avoiding Optional<Optional<>>");
        logger.info(SEPARATOR);

        User user = new User(NAME_ALICE, "alice@example.com",
                Optional.of("555-1234"),
                Optional.of(new Address("123 Main St", "Springfield", Optional.of("62701"))));

        User noPhone = new User("Bob", "bob@example.com",
                Optional.empty(),
                Optional.of(new Address("456 Oak Ave", "Shelbyville", Optional.empty())));

        // ❌ map() produces Optional<Optional<String>> - bad!
        Optional<Optional<String>> nestedPhone = Optional.of(user).map(User::phone);
        logger.log(Level.INFO, "   map \u2192 nested: {0}", nestedPhone); // Optional[Optional[555-1234]]

        // ✅ flatMap() flattens to Optional<String> - good!
        Optional<String> phone = Optional.of(user).flatMap(User::phone);
        logger.log(Level.INFO, "   flatMap \u2192 flat: {0}", phone);     // Optional[555-1234]

        // Deep nested access with flatMap chain
        Optional<String> zipCode = Optional.of(user)
                .flatMap(User::address)
                .flatMap(Address::zipCode);
        logger.log(Level.INFO, "   User \u2192 Address \u2192 Zip: {0}", zipCode);

        // Same chain with absent data
        Optional<String> noZip = Optional.of(noPhone)
                .flatMap(User::address)
                .flatMap(Address::zipCode);
        logger.log(Level.INFO, "   NoPhone \u2192 Address \u2192 Zip: {0}", noZip); // empty

        // Real-world: build display string
        String display = Optional.of(user)
                .map(User::name)
                .map(n -> n + " - " +
                    Optional.of(user).flatMap(User::phone).orElse("No phone") + " - " +
                    Optional.of(user).flatMap(User::address)
                            .map(a -> a.city() + " " + a.zipCode().orElse(""))
                            .orElse("No address"))
                .orElse("Unknown user");
        logger.log(Level.INFO, "   Display: {0}", display);

        logger.info("");
    }

    // ============================================================
    // 6. Optional with Streams
    // ============================================================
    static void optionalWithStreams() {
        logger.info("6\uFE0F\u20E3 OPTIONAL WITH STREAMS");
        logger.info(SEPARATOR);

        List<Optional<String>> optionals = List.of(
            Optional.of(NAME_ALICE),
            Optional.empty(),
            Optional.of("Charlie"),
            Optional.empty(),
            Optional.of("Eve")
        );

        // Extract present values (Java 9+ stream())
        List<String> presentValues = optionals.stream()
                .flatMap(Optional::stream)  // Java 9+
                .toList();
        logger.log(Level.INFO, "   Present values: {0}", presentValues);

        // Pre-Java 9 way
        List<String> presentOldWay = optionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        logger.log(Level.INFO, "   Present (old way): {0}", presentOldWay);

        // Find first user by name
        List<String> userNames = List.of(NAME_ALICE, "Bob", "Charlie");
        Optional<String> found = userNames.stream()
                .filter(n -> n.startsWith("B"))
                .findFirst();
        logger.log(Level.INFO, "   findFirst(B*): {0}", found);

        // Convert stream result to Optional
        OptionalInt maxLength = userNames.stream()
                .mapToInt(String::length)
                .max();
        logger.log(Level.INFO, "   Max name length: {0}", maxLength.orElse(0));

        logger.info("");
    }

    // ============================================================
    // 7. Anti-Patterns to Avoid
    // ============================================================
    static void antiPatterns() {
        logger.info("7\uFE0F\u20E3 ANTI-PATTERNS \u274C vs BEST PRACTICES \u2705");
        logger.info(SEPARATOR);

        // ❌ Anti-pattern 1: isPresent() + get()
        logger.info("   \u274C if (opt.isPresent()) opt.get()");
        logger.info("   \u2705 opt.ifPresent(action) or opt.orElse(default)");

        // ❌ Anti-pattern 2: Optional.of(value) == null check
        logger.info("   \u274C Optional.of(value) != null");
        logger.info("   \u2705 Optional.ofNullable(value)");

        // ❌ Anti-pattern 3: Returning null instead of Optional.empty()
        logger.info("   \u274C return null");
        logger.info("   \u2705 return Optional.empty()");

        // ❌ Anti-pattern 4: Optional as field/parameter
        logger.info("   \u274C private Optional<String> name;");
        logger.info("   \u2705 private String name; // nullable");

        // ❌ Anti-pattern 5: Optional for collections
        logger.info("   \u274C Optional<List<String>> items");
        logger.info("   \u2705 List<String> items = Collections.emptyList()");

        // ❌ Anti-pattern 6: Nested Optional
        logger.info("   \u274C Optional<Optional<String>>");
        logger.info("   \u2705 Use flatMap() to flatten");

        logger.info("\n   \uD83D\uDCCB When to Use Optional:");
        logger.info("   \u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510");
        logger.info("   \u2502 \u2705 Method return type (value might be absent)  \u2502");
        logger.info("   \u2502 \u2705 Stream terminal operations (findFirst, etc) \u2502");
        logger.info("   \u2502 \u274C Method parameters                           \u2502");
        logger.info("   \u2502 \u274C Class fields                                \u2502");
        logger.info("   \u2502 \u274C Collections (use empty collection instead)  \u2502");
        logger.info("   \u2502 \u274C Serialization (not serializable)            \u2502");
        logger.info("   \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518");

        logger.info("");
    }

    // ============================================================
    // 8. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        logger.info("8\uFE0F\u20E3 REAL-WORLD OPTIONAL EXAMPLES");
        logger.info(SEPARATOR);

        // Example 1: Config lookup chain
        logger.info("   \uD83D\uDCCC Config Lookup Chain:");
        String dbUrl = getEnvVar(CONFIG_DB_URL)
                .or(() -> getSystemProperty(CONFIG_DB_DOT_URL))
                .or(() -> getConfigFile(CONFIG_DATABASE_URL))
                .orElse("jdbc:h2:mem:default");
        logger.log(Level.INFO, "   DB URL: {0}", dbUrl);

        // Example 2: User profile display
        logger.info("\n   \uD83D\uDCCC User Profile Display:");
        displayUserProfile(NAME_ALICE, Optional.of("alice@dev.com"), Optional.of("555-0100"));
        displayUserProfile("Bob", Optional.empty(), Optional.empty());

        // Example 3: Safe parsing
        logger.info("\n   \uD83D\uDCCC Safe Number Parsing:");
        logger.log(Level.INFO, "   parse(\"123\"): {0}", safeParseInt("123"));
        logger.log(Level.INFO, "   parse(\"abc\"): {0}", safeParseInt("abc"));
        logger.log(Level.INFO, "   parse(null): {0}",  safeParseInt(null));

        // Example 4: Default value computation
        logger.info("\n   \uD83D\uDCCC Order Processing:");
        double price = getDiscount("VIP")
                .map(discount -> 100.0 * (1 - discount))
                .orElse(100.0);
        logger.log(Level.INFO, "   VIP price: ${0,number,0.00}", price);

        double regularPrice = getDiscount("REGULAR")
                .map(discount -> 100.0 * (1 - discount))
                .orElse(100.0);
        logger.log(Level.INFO, "   Regular price: ${0,number,0.00}", regularPrice);

        logger.info("\n\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        logger.info("  \u2705 Optional: Complete!                  ");
        logger.info("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
    }

    static Optional<String> getEnvVar(String key) {
        // Simulate environment variable lookup
        Map<String, String> envVars = Map.of();
        return Optional.ofNullable(envVars.get(key));
    }
    static Optional<String> getSystemProperty(String key) {
        // Simulate system property lookup
        Map<String, String> sysProps = Map.of();
        return Optional.ofNullable(sysProps.get(key));
    }
    static Optional<String> getConfigFile(String key) {
        // Simulate config file lookup
        Map<String, String> configEntries = Map.of(
            CONFIG_DATABASE_URL, "jdbc:mysql://localhost:3306/mydb"
        );
        return Optional.ofNullable(configEntries.get(key));
    }

    static void displayUserProfile(String name, Optional<String> email, Optional<String> phone) {
        logger.log(Level.INFO, "   Name: {0}",  name);
        logger.log(Level.INFO, "   Email: {0}", email.orElse("(not provided)"));
        logger.log(Level.INFO, "   Phone: {0}", phone.orElse("(not provided)"));
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
            case "VIP"     -> Optional.of(0.2);
            case "PREMIUM" -> Optional.of(0.1);
            default        -> Optional.empty();
        };
    }
}
