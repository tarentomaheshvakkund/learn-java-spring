package com.learning.javalearning.step14_java16_stream_enhancements;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * STEP 14: STREAM.TOLIST() (Java 16)
 *
 * Stream.toList() is a convenience method introduced in Java 16 that provides
 * a simpler way to collect stream elements into a List.
 *
 * KEY DIFFERENCES FROM collect(Collectors.toList()):
 * - Shorter, more concise syntax
 * - Returns an unmodifiable List (immutable)
 * - Better performance (optimized implementation)
 * - Clearer intent
 *
 * BENEFITS:
 * 1. Less boilerplate code
 * 2. Immutability by default (safer)
 * 3. Null-safe (doesn't allow null elements)
 * 4. Better readability
 *
 * Java Version: 16 (Standard)
 */
public class StreamToListExample {

  private static final Logger logger = Logger.getLogger(StreamToListExample.class.getName());

  private StreamToListExample() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    demonstrateOldVsNewWay();
    demonstrateImmutability();
    demonstratePerformance();
    demonstrateRealWorldUseCases();
  }

  /**
   * 1. OLD WAY vs NEW WAY
   * Traditional collect() vs modern toList()
   */
  private static void demonstrateOldVsNewWay() {
    logger.info("=== OLD vs NEW Stream Collection ===");

    List<String> fruits = List.of("apple", "banana", "cherry", "date", "elderberry");

    // OLD WAY: Using collect(Collectors.toList())
    List<String> upperCaseOld = fruits.stream()
        .map(String::toUpperCase)
        .collect(Collectors.toList());
    logger.info(() -> "Old way: " + upperCaseOld);

    // NEW WAY: Using toList()
    List<String> upperCaseNew = fruits.stream()
        .map(String::toUpperCase)
        .toList();
    logger.info(() -> "New way: " + upperCaseNew);

    logger.info("");
  }

  /**
   * 2. IMMUTABILITY
   * toList() returns unmodifiable list by default
   */
  private static void demonstrateImmutability() {
    logger.info("=== Immutability Comparison ===");

    List<Integer> numbers = List.of(1, 2, 3, 4, 5);

    // OLD WAY: Returns mutable list
    List<Integer> mutableList = numbers.stream()
        .map(n -> n * 2)
        .collect(Collectors.toList());

    try {
      mutableList.add(12); // This works - mutable
      logger.info(() -> "Mutable list (old way): " + mutableList);
    } catch (UnsupportedOperationException e) {
      logger.info("Old way: Cannot modify (unexpected)");
    }

    // NEW WAY: Returns immutable list
    List<Integer> immutableList = numbers.stream()
        .map(n -> n * 2)
        .toList();

    try {
      immutableList.add(12); // This throws exception - immutable
      logger.info(() -> "Immutable list (new way): " + immutableList);
    } catch (UnsupportedOperationException e) {
      logger.info(() -> "New way: List is immutable (expected) - " + immutableList);
    }

    logger.info("");
  }

  /**
   * 3. PERFORMANCE
   * toList() is optimized for better performance
   */
  private static void demonstratePerformance() {
    logger.info("=== Performance Comparison ===");

    // Generate large dataset
    List<Integer> largeDataset = Stream.iterate(1, n -> n + 1)
        .limit(100000)
        .toList();

    // OLD WAY - Time measurement
    long startOld = System.nanoTime();
    List<Integer> resultOld = largeDataset.stream()
        .filter(n -> n % 2 == 0)
        .map(n -> n * n)
        .collect(Collectors.toList());
    long endOld = System.nanoTime();
    long durationOld = (endOld - startOld) / 1_000_000; // Convert to milliseconds

    // NEW WAY - Time measurement
    long startNew = System.nanoTime();
    List<Integer> resultNew = largeDataset.stream()
        .filter(n -> n % 2 == 0)
        .map(n -> n * n)
        .toList();
    long endNew = System.nanoTime();
    long durationNew = (endNew - startNew) / 1_000_000; // Convert to milliseconds

    logger.info(() -> "Old way (collect): " + durationOld + " ms, Size: " + resultOld.size());
    logger.info(() -> "New way (toList): " + durationNew + " ms, Size: " + resultNew.size());

    double improvement = ((double) (durationOld - durationNew) / durationOld) * 100;
    logger.info(() -> String.format("Performance improvement: %.2f%%", improvement));

    logger.info("");
  }

  /**
   * 4. REAL-WORLD USE CASES
   * Practical examples of using toList()
   */
  private static void demonstrateRealWorldUseCases() {
    logger.info("=== Real-World Use Cases ===");

    // Use Case 1: Filter and collect user data
    List<User> users = List.of(
        new User(1, "Alice", 25, true),
        new User(2, "Bob", 30, false),
        new User(3, "Charlie", 35, true),
        new User(4, "Diana", 28, true),
        new User(5, "Eve", 32, false)
    );

    List<String> activeUserNames = users.stream()
        .filter(User::active)
        .map(User::name)
        .toList();
    logger.info(() -> "Active users: " + activeUserNames);

    // Use Case 2: Transform and collect prices
    List<Product> products = List.of(
        new Product("Laptop", 999.99),
        new Product("Mouse", 29.99),
        new Product("Keyboard", 79.99),
        new Product("Monitor", 299.99)
    );

    List<Double> discountedPrices = products.stream()
        .map(p -> p.price * 0.9) // 10% discount
        .toList();
    logger.info(() -> "Discounted prices: " + discountedPrices);

    // Use Case 3: Flatten nested collections
    List<Department> departments = List.of(
        new Department("Engineering", List.of("Alice", "Bob", "Charlie")),
        new Department("Sales", List.of("Diana", "Eve")),
        new Department("HR", List.of("Frank", "Grace"))
    );

    List<String> allEmployees = departments.stream()
        .flatMap(dept -> dept.employees.stream())
        .toList();
    logger.info(() -> "All employees: " + allEmployees);

    // Use Case 4: Range operations
    List<Integer> evenNumbersInRange = Stream.iterate(1, n -> n <= 20, n -> n + 1)
        .filter(n -> n % 2 == 0)
        .toList();
    logger.info(() -> "Even numbers (1-20): " + evenNumbersInRange);

    // Use Case 5: String operations
    List<String> sentences = List.of(
        "Java 16 is great",
        "Stream API is powerful",
        "toList() is convenient"
    );

    List<String> words = sentences.stream()
        .flatMap(sentence -> Stream.of(sentence.split(" ")))
        .map(String::toLowerCase)
        .distinct()
        .sorted()
        .toList();
    logger.info(() -> "All unique words: " + words);

    logger.info("");
  }

  // Domain classes
  record User(int id, String name, int age, boolean active) {}

  record Product(String name, double price) {}

  record Department(String name, List<String> employees) {}
}
