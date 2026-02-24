package com.learning.javalearning.step14_java16_stream_enhancements;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * STEP 14: STREAM ENHANCEMENTS COMPARISON
 *
 * Side-by-side comparison of old and new stream collection methods
 * demonstrating when and why to use each approach.
 *
 * DECISION GUIDE:
 *
 * Use toList() when:
 * - You need an immutable result
 * - Simple collection without further modifications
 * - Want concise, readable code
 * - Don't need null elements
 *
 * Use collect(Collectors.toList()) when:
 * - You need a mutable list
 * - Will modify the list after creation
 * - Need to add/remove elements
 * - Working with legacy code expecting mutable lists
 *
 * Use Teeing Collector when:
 * - Need multiple aggregations from same stream
 * - Want to avoid multiple passes over data
 * - Combining different collector results
 * - Performance is critical for large datasets
 *
 * Java Version: 12-16
 */
public class StreamComparisonExample {

  private static final Logger logger = Logger.getLogger(StreamComparisonExample.class.getName());
  private static final String CREDIT = "CREDIT";
  private static final String DEBIT = "DEBIT";

  private StreamComparisonExample() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    demonstrateMutabilityDifferences();
    demonstratePerformanceComparison();
    demonstrateWhenToUseWhat();
    demonstrateMigrationStrategies();
  }

  /**
   * 1. MUTABILITY DIFFERENCES
   * Understanding mutable vs immutable results
   */
  private static void demonstrateMutabilityDifferences() {
    logger.info("=== Mutability Comparison ===");

    List<String> fruits = List.of("apple", "banana", "cherry");

    // Collectors.toList() - Mutable
    List<String> mutableList = fruits.stream()
        .map(String::toUpperCase)
        .collect(Collectors.toList());

    logger.info(() -> "Mutable list before: " + mutableList);
    mutableList.add("DATE"); // Works fine
    logger.info(() -> "Mutable list after add: " + mutableList);

    // toList() - Immutable
    List<String> immutableList = fruits.stream()
        .map(String::toUpperCase)
        .toList();

    logger.info(() -> "Immutable list: " + immutableList);
    try {
      immutableList.add("DATE"); // Throws exception
    } catch (UnsupportedOperationException e) {
      logger.info("Cannot modify immutable list (as expected)");
    }

    // If you need mutable from toList()
    List<String> convertedToMutable = new ArrayList<>(immutableList);
    convertedToMutable.add("DATE");
    logger.info(() -> "Converted to mutable: " + convertedToMutable);

    logger.info("");
  }

  /**
   * 2. PERFORMANCE COMPARISON
   * Benchmarking different approaches
   */
  @SuppressWarnings("java:S6204") // Intentionally comparing old vs new approach
  private static void demonstratePerformanceComparison() {
    logger.info("=== Performance Comparison ===");

    int dataSize = 1000000;
    List<Integer> largeDataset = java.util.stream.IntStream.range(0, dataSize)
        .boxed()
        .toList();

    // Test 1: Simple collection
    long start1 = System.nanoTime();
    List<Integer> result1 = largeDataset.stream()
        .filter(n -> n % 2 == 0)
        .collect(Collectors.toList());
    long duration1 = (System.nanoTime() - start1) / 1_000_000;

    long start2 = System.nanoTime();
    List<Integer> result2 = largeDataset.stream()
        .filter(n -> n % 2 == 0)
        .toList();
    long duration2 = (System.nanoTime() - start2) / 1_000_000;

    logger.info(() -> String.format("Collectors.toList(): %d ms (size: %d)",
        duration1, result1.size()));
    logger.info(() -> String.format("toList(): %d ms (size: %d)",
        duration2, result2.size()));

    // Test 2: Multiple passes vs Teeing
    long start3 = System.nanoTime();
    long evenCount = largeDataset.stream().filter(n -> n % 2 == 0).count();
    long oddCount = largeDataset.stream().filter(n -> n % 2 != 0).count();
    long duration3 = (System.nanoTime() - start3) / 1_000_000;

    long start4 = System.nanoTime();
    var counts = largeDataset.stream()
        .collect(Collectors.teeing(
            Collectors.filtering(n -> n % 2 == 0, Collectors.counting()),
            Collectors.filtering(n -> n % 2 != 0, Collectors.counting()),
            (even, odd) -> new long[]{even, odd}
        ));
    long duration4 = (System.nanoTime() - start4) / 1_000_000;

    logger.info(() -> String.format("%nMultiple passes: %d ms (even: %d, odd: %d)",
        duration3, evenCount, oddCount));
    logger.info(() -> String.format("Teeing collector: %d ms (even: %d, odd: %d)",
        duration4, counts[0], counts[1]));

    logger.info("");
  }

  /**
   * 3. WHEN TO USE WHAT
   * Decision guide with examples
   */
  private static void demonstrateWhenToUseWhat() {
    logger.info("=== When to Use What ===");

    List<Order> orders = List.of(
        new Order(1, "Laptop", 999.99, "PAID"),
        new Order(2, "Mouse", 29.99, "PENDING"),
        new Order(3, "Keyboard", 79.99, "PAID"),
        new Order(4, "Monitor", 299.99, "SHIPPED")
    );

    // Scenario 1: Need immutable result for API response
    logger.info("Scenario 1: API Response (use toList())");
    List<String> productNames = orders.stream()
        .map(Order::product)
        .toList();
    logger.info(() -> "Products (immutable): " + productNames);

    // Scenario 2: Need to modify list after creation
    logger.info("\nScenario 2: Building dynamic list (use Collectors.toList())");
    List<Double> prices = orders.stream()
        .map(Order::price)
        .collect(Collectors.toList());
    prices.add(0.0); // Add default price
    logger.info(() -> "Prices (mutable): " + prices);

    // Scenario 3: Need multiple statistics
    logger.info("\nScenario 3: Multiple statistics (use teeing())");
    OrderStats stats = orders.stream()
        .filter(o -> "PAID".equals(o.status()))
        .collect(Collectors.teeing(
            Collectors.summingDouble(Order::price),
            Collectors.counting(),
            OrderStats::new
        ));
    logger.info(() -> String.format("Paid orders - Total: $%.2f, Count: %d",
        stats.totalAmount(), stats.count()));

    // Scenario 4: Chaining operations after collection
    logger.info("\nScenario 4: Post-collection processing");
    List<String> statuses = orders.stream()
        .map(Order::status)
        .distinct()
        .collect(Collectors.toList());
    statuses.sort(String::compareTo); // Need mutable for sorting
    logger.info(() -> "Sorted statuses: " + statuses);

    logger.info("");
  }

  /**
   * 4. MIGRATION STRATEGIES
   * How to migrate from old to new patterns
   */
  private static void demonstrateMigrationStrategies() {
    logger.info("=== Migration Strategies ===");

    List<Transaction> transactions = List.of(
        new Transaction("2024-01-01", 1000.0, CREDIT),
        new Transaction("2024-01-02", 500.0, DEBIT),
        new Transaction("2024-01-03", 750.0, CREDIT),
        new Transaction("2024-01-04", 250.0, DEBIT)
    );

    // Strategy 1: Direct replacement (immutable OK)
    logger.info("Strategy 1: Direct replacement");
    // NEW:
    List<String> datesNew = transactions.stream()
        .map(Transaction::date)
        .toList();
    logger.info(() -> "Dates: " + datesNew);

    // Strategy 2: Wrap in ArrayList if mutability needed
    logger.info("\nStrategy 2: Wrap for mutability");
    List<Double> amounts = new ArrayList<>(
        transactions.stream()
            .map(Transaction::amount)
            .toList()
    );
    amounts.add(0.0); // Now mutable
    logger.info(() -> "Amounts (with default): " + amounts);

    // Strategy 3: Combine multiple collectors with teeing
    logger.info("\nStrategy 3: Combine collectors");
    TransactionSummary summary = transactions.stream()
        .collect(Collectors.teeing(
            Collectors.filtering(
                t -> CREDIT.equals(t.type()),
                Collectors.summingDouble(Transaction::amount)
            ),
            Collectors.filtering(
                t -> DEBIT.equals(t.type()),
                Collectors.summingDouble(Transaction::amount)
            ),
            TransactionSummary::new
        ));
    logger.info(() -> String.format("Credits: $%.2f, Debits: $%.2f, Balance: $%.2f",
        summary.totalCredits(), summary.totalDebits(), summary.balance()));

    logger.info("");
  }

  // Domain classes
  record Order(int id, String product, double price, String status) {}

  record OrderStats(double totalAmount, long count) {}

  record Transaction(String date, double amount, String type) {}

  record TransactionSummary(double totalCredits, double totalDebits) {
    public double balance() {
      return totalCredits - totalDebits;
    }
  }
}
