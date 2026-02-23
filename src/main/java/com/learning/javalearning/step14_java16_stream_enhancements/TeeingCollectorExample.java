package com.learning.javalearning.step14_java16_stream_enhancements;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * STEP 14: TEEING COLLECTOR (Java 12)
 *
 * The Teeing Collector allows processing a stream with two different collectors
 * simultaneously and then merging their results using a BiFunction.
 *
 * SIGNATURE:
 * Collectors.teeing(
 *   Collector<T, ?, R1> downstream1,
 *   Collector<T, ?, R2> downstream2,
 *   BiFunction<R1, R2, R> merger
 * )
 *
 * BENEFITS:
 * 1. Single pass through data (performance)
 * 2. Calculate multiple aggregations simultaneously
 * 3. Cleaner than multiple stream passes
 * 4. Type-safe result combination
 *
 * USE CASES:
 * - Calculate min and max together
 * - Compute average and count
 * - Partition and count simultaneously
 * - Generate summary statistics
 *
 * Java Version: 12 (Standard)
 */
public class TeeingCollectorExample {

  private static final Logger logger = Logger.getLogger(TeeingCollectorExample.class.getName());

  private TeeingCollectorExample() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    demonstrateBasicTeeing();
    demonstrateMinMaxCalculation();
    demonstrateAverageAndCount();
    demonstrateSummaryStatistics();
    demonstrateRealWorldUseCases();
  }

  /**
   * 1. BASIC TEEING
   * Understanding the concept with simple example
   */
  private static void demonstrateBasicTeeing() {
    logger.info("=== Basic Teeing Collector ===");

    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // WITHOUT TEEING: Need multiple passes
    long evenCount = numbers.stream()
        .filter(n -> n % 2 == 0)
        .count();

    long oddCount = numbers.stream()
        .filter(n -> n % 2 != 0)
        .count();

    logger.info(() -> "Without teeing - Even: " + evenCount + ", Odd: " + oddCount);

    // WITH TEEING: Single pass
    var result = numbers.stream()
        .collect(Collectors.teeing(
            Collectors.filtering(n -> n % 2 == 0, Collectors.counting()),
            Collectors.filtering(n -> n % 2 != 0, Collectors.counting()),
            (even, odd) -> new EvenOddCount(even, odd)
        ));

    logger.info(() -> "With teeing - " + result);
    logger.info("");
  }

  /**
   * 2. MIN AND MAX CALCULATION
   * Find minimum and maximum in single pass
   */
  private static void demonstrateMinMaxCalculation() {
    logger.info("=== Min and Max with Teeing ===");

    List<Integer> scores = List.of(85, 92, 78, 95, 88, 76, 90, 82);

    // OLD WAY: Two passes
    int minOld = scores.stream().min(Integer::compare).orElse(0);
    int maxOld = scores.stream().max(Integer::compare).orElse(0);
    logger.info(() -> "Old way - Min: " + minOld + ", Max: " + maxOld);

    // NEW WAY: Single pass with teeing
    MinMax<Integer> minMax = scores.stream()
        .collect(Collectors.teeing(
            Collectors.minBy(Integer::compare),
            Collectors.maxBy(Integer::compare),
            (min, max) -> new MinMax<>(
                min.orElse(null),
                max.orElse(null)
            )
        ));

    logger.info(() -> "New way - " + minMax);
    logger.info("");
  }

  /**
   * 3. AVERAGE AND COUNT
   * Calculate both average and count together
   */
  private static void demonstrateAverageAndCount() {
    logger.info("=== Average and Count with Teeing ===");

    List<Double> salaries = List.of(50000.0, 60000.0, 75000.0, 80000.0, 95000.0);

    AvgCount salaryStats = salaries.stream()
        .collect(Collectors.teeing(
            Collectors.averagingDouble(Double::doubleValue),
            Collectors.counting(),
            AvgCount::new
        ));

    logger.info(() -> "Salary statistics: " + salaryStats);
    logger.info(() -> String.format("Average: $%.2f, Employees: %d",
        salaryStats.average(), salaryStats.count()));

    logger.info("");
  }

  /**
   * 4. SUMMARY STATISTICS
   * Create comprehensive statistics in one pass
   */
  private static void demonstrateSummaryStatistics() {
    logger.info("=== Summary Statistics ===");

    List<Product> products = List.of(
        new Product("Laptop", 999.99, 50),
        new Product("Mouse", 29.99, 200),
        new Product("Keyboard", 79.99, 150),
        new Product("Monitor", 299.99, 75),
        new Product("Webcam", 89.99, 100)
    );

    ProductStats stats = products.stream()
        .collect(Collectors.teeing(
            Collectors.summarizingDouble(Product::price),
            Collectors.summarizingInt(Product::stock),
            ProductStats::new
        ));

    logger.info(() -> "Product Statistics:");
    logger.info(() -> String.format("  Price - Min: $%.2f, Max: $%.2f, Avg: $%.2f",
        stats.priceStats().getMin(),
        stats.priceStats().getMax(),
        stats.priceStats().getAverage()));
    logger.info(() -> String.format("  Stock - Total: %d, Avg: %.2f",
        stats.stockStats().getSum(),
        stats.stockStats().getAverage()));

    logger.info("");
  }

  /**
   * 5. REAL-WORLD USE CASES
   * Practical examples from business scenarios
   */
  private static void demonstrateRealWorldUseCases() {
    logger.info("=== Real-World Use Cases ===");

    // Use Case 1: Employee Performance Analysis
    List<Employee> employees = List.of(
        new Employee("Alice", 85, "Engineering"),
        new Employee("Bob", 92, "Engineering"),
        new Employee("Charlie", 78, "Sales"),
        new Employee("Diana", 95, "Engineering"),
        new Employee("Eve", 88, "Sales"),
        new Employee("Frank", 76, "HR"),
        new Employee("Grace", 90, "Engineering")
    );

    PerformanceReport report = employees.stream()
        .filter(e -> "Engineering".equals(e.department()))
        .collect(Collectors.teeing(
            Collectors.averagingDouble(Employee::performanceScore),
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> list.stream()
                    .filter(e -> e.performanceScore() >= 90)
                    .count()
            ),
            PerformanceReport::new
        ));

    logger.info(() -> "Engineering Department:");
    logger.info(() -> String.format("  Average Score: %.2f", report.averageScore()));
    logger.info(() -> String.format("  High Performers (>=90): %d", report.highPerformers()));

    // Use Case 2: Sales Analysis
    List<Sale> sales = List.of(
        new Sale("Q1", 50000.0),
        new Sale("Q2", 75000.0),
        new Sale("Q3", 60000.0),
        new Sale("Q4", 90000.0)
    );

    SalesReport salesReport = sales.stream()
        .collect(Collectors.teeing(
            Collectors.summingDouble(Sale::amount),
            Collectors.averagingDouble(Sale::amount),
            (total, avg) -> new SalesReport(total, avg, sales.size())
        ));

    logger.info(() -> "\nSales Report:");
    logger.info(() -> String.format("  Total Revenue: $%.2f", salesReport.totalRevenue()));
    logger.info(() -> String.format("  Average per Quarter: $%.2f", salesReport.averageRevenue()));
    logger.info(() -> String.format("  Quarters: %d", salesReport.quarters()));

    // Use Case 3: Grade Distribution
    List<Student> students = List.of(
        new Student("John", 92),
        new Student("Jane", 85),
        new Student("Jim", 78),
        new Student("Jill", 95),
        new Student("Jack", 88),
        new Student("Jenny", 72)
    );

    GradeDistribution gradesDist = students.stream()
        .collect(Collectors.teeing(
            Collectors.partitioningBy(
                s -> s.grade() >= 80,
                Collectors.counting()
            ),
            Collectors.averagingDouble(Student::grade),
            (partition, avg) -> new GradeDistribution(
                partition.get(true),
                partition.get(false),
                avg
            )
        ));

    logger.info(() -> "\nGrade Distribution:");
    logger.info(() -> String.format("  Above 80: %d", gradesDist.passCount()));
    logger.info(() -> String.format("  Below 80: %d", gradesDist.failCount()));
    logger.info(() -> String.format("  Class Average: %.2f", gradesDist.classAverage()));

    logger.info("");
  }

  // Result classes
  record EvenOddCount(long even, long odd) {
    @Override
    public String toString() {
      return String.format("Even: %d, Odd: %d", even, odd);
    }
  }

  record MinMax<T>(T min, T max) {
    @Override
    public String toString() {
      return String.format("Min: %s, Max: %s", min, max);
    }
  }

  record AvgCount(double average, long count) {}

  record ProductStats(
      DoubleSummaryStatistics priceStats,
      java.util.IntSummaryStatistics stockStats
  ) {}

  record PerformanceReport(double averageScore, long highPerformers) {}

  record SalesReport(double totalRevenue, double averageRevenue, int quarters) {}

  record GradeDistribution(long passCount, long failCount, double classAverage) {}

  // Domain classes
  record Product(String name, double price, int stock) {}

  record Employee(String name, int performanceScore, String department) {}

  record Sale(String quarter, double amount) {}

  record Student(String name, int grade) {}
}
