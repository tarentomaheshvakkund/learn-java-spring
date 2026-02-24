package com.learning.javalearning.step16_java8_essentials;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.logging.Logger;

/**
 * Step 16: Streams API - Functional Data Processing
 *
 * Streams provide a declarative approach to processing collections.
 * They support sequential and parallel operations on data.
 *
 * Key concepts:
 * - Source → Intermediate Operations → Terminal Operation
 * - Streams are LAZY - operations only execute on terminal call
 * - Streams are consumed ONCE - cannot be reused
 * - Streams do NOT modify the source collection
 *
 * Intermediate: filter, map, flatMap, sorted, distinct, peek, limit, skip
 * Terminal: collect, forEach, reduce, count, min, max, anyMatch, allMatch
 */
public class StreamsAPIExample {

    record Employee(String name, String department, double salary, int age) {}

    private static final Logger logger = Logger.getLogger(StreamsAPIExample.class.getName());
    private static final String SECTION_SEP = "─────────────────────────────────\n";
    private static final String NAME_ALICE      = "Alice";
    private static final String NAME_CHARLIE    = "Charlie";
    private static final String NAME_DIANA      = "Diana";
    private static final String DEPT_ENGINEERING = "Engineering";
    private static final String DEPT_MARKETING   = "Marketing";

    public static void main(String[] args) {
        logger.info("═══════════════════════════════════════════");
        logger.info("  Step 16: Streams API Deep Dive          ");
        logger.info("═══════════════════════════════════════════\n");

        creatingStreams();
        intermediateOperations();
        terminalOperations();
        collectorsDeepDive();
        reducingOperations();
        flatMapExamples();
        streamPipelines();
        parallelStreams();
        realWorldExamples();
    }

    // ============================================================
    // 1. Creating Streams
    // ============================================================
    static void creatingStreams() {
        logger.info("1️⃣ CREATING STREAMS");
        logger.info(SECTION_SEP);

        // From Collection
        List<String> list = List.of("A", "B", "C");
        Stream<String> fromList = list.stream();
        logger.info(() -> "   From List: " + fromList.toList());

        // From Array
        String[] array = {"X", "Y", "Z"};
        Stream<String> fromArray = Arrays.stream(array);
        logger.info(() -> "   From Array: " + fromArray.toList());

        // Stream.of()
        Stream<Integer> ofValues = Stream.of(1, 2, 3, 4, 5);
        logger.info(() -> "   Stream.of: " + ofValues.toList());

        // Stream.generate() - infinite stream
        List<Double> randoms = Stream.generate(Math::random).limit(3).toList();
        logger.info(() -> "   Random: " + randoms.stream().map(d -> String.format("%.2f", d)).toList());

        // Stream.iterate() - infinite stream with seed
        List<Integer> powers = Stream.iterate(1, n -> n * 2).limit(8).toList();
        logger.info(() -> "   Powers of 2: " + powers);

        // Stream.iterate() with hasNext predicate (Java 9+)
        Stream<Integer> limited = Stream.iterate(1, n -> n <= 100, n -> n * 2);
        logger.info(() -> "   Powers ≤ 100: " + limited.toList());

        // IntStream, LongStream, DoubleStream - primitive specializations
        IntStream range = IntStream.range(1, 6);       // 1, 2, 3, 4, 5
        IntStream rangeClosed = IntStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5
        logger.info(() -> "   IntStream.range(1,6): " + range.boxed().toList());
        logger.info(() -> "   IntStream.rangeClosed(1,5): " + rangeClosed.boxed().toList());

        // Stream.empty()
        Stream<String> empty = Stream.empty();
        logger.info(() -> "   Empty stream count: " + empty.count());

        // From String
        IntStream chars = "Hello".chars();
        logger.info(() -> "   Char codes: " + chars.boxed().toList());

        logger.info("");
    }

    // ============================================================
    // 2. Intermediate Operations (Lazy - return Stream)
    // ============================================================
    static void intermediateOperations() {
        logger.info("2️⃣ INTERMEDIATE OPERATIONS (Lazy)");
        logger.info(SECTION_SEP);

        List<String> names = List.of(NAME_ALICE, "Bob", NAME_CHARLIE, NAME_ALICE, NAME_DIANA, "Bob", "Eve");

        // filter() - keep elements matching condition
        List<String> longNames = names.stream()
                .filter(n -> n.length() > 3)
                .toList();
        logger.info(() -> "   filter(length > 3): " + longNames);

        // map() - transform each element
        List<Integer> nameLengths = names.stream()
                .map(String::length)
                .toList();
        logger.info(() -> "   map(length): " + nameLengths);

        // distinct() - remove duplicates
        List<String> unique = names.stream()
                .distinct()
                .toList();
        logger.info(() -> "   distinct: " + unique);

        // sorted() - natural order
        List<String> sorted = names.stream()
                .distinct()
                .sorted()
                .toList();
        logger.info(() -> "   sorted: " + sorted);

        // sorted() with comparator
        List<String> sortedByLength = names.stream()
                .distinct()
                .sorted(Comparator.comparingInt(String::length))
                .toList();
        logger.info(() -> "   sorted by length: " + sortedByLength);

        // limit() and skip()
        List<String> page = names.stream()
                .distinct()
                .skip(1)      // skip first
                .limit(3)     // take next 3
                .toList();
        logger.info(() -> "   skip(1).limit(3): " + page);

        // count() and collecting distinct names
        List<String> distinctNames = names.stream()
                .distinct()
                .toList();
        logger.info(() -> "   distinct: " + distinctNames + " \u2192 count: " + distinctNames.size());

        // mapToInt, mapToLong, mapToDouble - to primitive streams
        int totalLength = names.stream()
                .mapToInt(String::length)
                .sum();
        logger.info(() -> "   mapToInt sum of lengths: " + totalLength);

        logger.info("");
    }

    // ============================================================
    // 3. Terminal Operations (Eager - produce result)
    // ============================================================
    static void terminalOperations() {
        logger.info("3️⃣ TERMINAL OPERATIONS (Eager)");
        logger.info(SECTION_SEP);

        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);

        // collect() - gather results into collection
        List<Integer> evenList = numbers.stream()
                .filter(n -> n % 2 == 0)
                .toList();
        logger.info(() -> "   collect(toList): " + evenList);

        // toList() - unmodifiable list (Java 16+)
        List<Integer> immutableList = numbers.stream().filter(n -> n > 3).toList();
        logger.info(() -> "   toList(): " + immutableList);

        // forEach() - perform action on each element
        StringBuilder forEachSb = new StringBuilder("   forEach:");
        numbers.stream().distinct().sorted().forEach(n -> forEachSb.append(" ").append(n));
        logger.info(forEachSb::toString);

        // count()
        long count = numbers.stream().filter(n -> n > 3).count();
        logger.info(() -> "   count(> 3): " + count);

        // min() and max()
        Optional<Integer> min = numbers.stream().min(Comparator.naturalOrder());
        Optional<Integer> max = numbers.stream().max(Comparator.naturalOrder());
        logger.info(() -> "   min: " + min.orElse(-1) + ", max: " + max.orElse(-1));

        // anyMatch, allMatch, noneMatch
        boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        logger.info(() -> "   anyMatch(even): " + hasEven);
        logger.info(() -> "   allMatch(positive): " + allPositive);
        logger.info(() -> "   noneMatch(negative): " + noneNegative);

        // findFirst() and findAny()
        Optional<Integer> first = numbers.stream().filter(n -> n > 4).findFirst();
        logger.info(() -> "   findFirst(> 4): " + first.orElse(-1));

        // toArray()
        Integer[] arr = numbers.stream().distinct().sorted().toArray(Integer[]::new);
        logger.info(() -> "   toArray: " + Arrays.toString(arr));

        logger.info("");
    }

    // ============================================================
    // 4. Collectors Deep Dive
    // ============================================================
    static void collectorsDeepDive() {
        logger.info("4️⃣ COLLECTORS - Powerful Result Gathering");
        logger.info(SECTION_SEP);

        List<Employee> employees = List.of(
            new Employee(NAME_ALICE, DEPT_ENGINEERING, 90000, 30),
            new Employee("Bob", DEPT_ENGINEERING, 85000, 28),
            new Employee(NAME_CHARLIE, DEPT_MARKETING, 75000, 35),
            new Employee(NAME_DIANA, DEPT_MARKETING, 72000, 32),
            new Employee("Eve", "HR", 65000, 27),
            new Employee("Frank", "HR", 68000, 40),
            new Employee("Grace", DEPT_ENGINEERING, 110000, 45)
        );

        // toSet()
        Set<String> departments = employees.stream()
                .map(Employee::department)
                .collect(Collectors.toSet());
        logger.info(() -> "   Departments: " + departments);

        // joining()
        String nameList = employees.stream()
                .map(Employee::name)
                .collect(Collectors.joining(", "));
        logger.info(() -> "   Names: " + nameList);

        String formatted = employees.stream()
                .map(Employee::name)
                .collect(Collectors.joining(", ", "[", "]"));
        logger.info(() -> "   Formatted: " + formatted);

        // groupingBy()
        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department));
        byDept.forEach((dept, emps) ->
            logger.info(() -> "   " + dept + ": " + emps.stream().map(Employee::name).toList()));

        // groupingBy with downstream collector
        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        logger.info(() -> "   Count by dept: " + countByDept);

        Map<String, Double> avgSalaryByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.averagingDouble(Employee::salary)));
        logger.info(() -> "   Avg salary by dept: " + avgSalaryByDept);

        // partitioningBy() - split into true/false groups
        Map<Boolean, List<Employee>> highEarners = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() > 80000));
        logger.info(() -> "   High earners: " + highEarners.get(true).stream().map(Employee::name).toList());
        logger.info(() -> "   Others: " + highEarners.get(false).stream().map(Employee::name).toList());

        // summarizingDouble() - statistics in one pass
        DoubleSummaryStatistics stats = employees.stream()
                .collect(Collectors.summarizingDouble(Employee::salary));
        logger.info(() -> "   Salary stats: count=" + stats.getCount() +
                ", avg=" + String.format("%.0f", stats.getAverage()) +
                ", min=" + String.format("%.0f", stats.getMin()) +
                ", max=" + String.format("%.0f", stats.getMax()));

        // toMap()
        Map<String, Double> nameSalary = employees.stream()
                .collect(Collectors.toMap(Employee::name, Employee::salary));
        logger.info(() -> "   Name→Salary: " + nameSalary);

        // toList() - unmodifiable list (Java 16+) - preferred over toUnmodifiableList
        List<String> immutableNames = employees.stream()
                .map(Employee::name)
                .toList();
        logger.info(() -> "   Immutable names: " + immutableNames);

        logger.info("");
    }

    // ============================================================
    // 5. Reduce Operations
    // ============================================================
    static void reducingOperations() {
        logger.info("5️⃣ REDUCE - Aggregate to Single Value");
        logger.info(SECTION_SEP);

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // reduce with identity and accumulator
        int sum = numbers.stream().reduce(0, Integer::sum);
        logger.info(() -> "   Sum: " + sum);

        int product = numbers.stream().reduce(1, (a, b) -> a * b);
        logger.info(() -> "   Product: " + product);

        // reduce without identity (returns Optional)
        Optional<Integer> maxOpt = numbers.stream().reduce(Integer::max);
        logger.info(() -> "   Max: " + maxOpt.orElse(-1));

        // String concatenation with reduce
        List<String> words = List.of("Java", "Streams", "Are", "Powerful");
        String sentence = words.stream().reduce("", (a, b) -> a + " " + b).trim();
        logger.info(() -> "   Sentence: " + sentence);

        // Custom reduce - find longest string
        Optional<String> longest = words.stream()
                .reduce((a, b) -> a.length() >= b.length() ? a : b);
        logger.info(() -> "   Longest word: " + longest.orElse(""));

        // Reduce with combiner (for parallel streams)
        int parallelSum = numbers.parallelStream()
                .reduce(0, Integer::sum, Integer::sum);
        logger.info(() -> "   Parallel sum: " + parallelSum);

        logger.info("");
    }

    // ============================================================
    // 6. FlatMap - Flatten Nested Structures
    // ============================================================
    static void flatMapExamples() {
        logger.info("6️⃣ FLATMAP - Flatten Nested Streams");
        logger.info(SECTION_SEP);

        // Flatten list of lists
        List<List<Integer>> nested = List.of(
            List.of(1, 2, 3),
            List.of(4, 5),
            List.of(6, 7, 8, 9)
        );

        List<Integer> flat = nested.stream()
                .flatMap(Collection::stream)
                .toList();
        logger.info(() -> "   Flat: " + flat);

        // Split words from sentences
        List<String> sentences = List.of("Hello World", "Java Streams", "Are Great");
        List<String> allWords = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .toList();
        logger.info(() -> "   Words: " + allWords);

        // Unique characters from words
        List<String> words = List.of("Hello", "World");
        List<String> uniqueChars = words.stream()
                .flatMap(w -> w.chars().mapToObj(c -> String.valueOf((char) c)))
                .distinct()
                .sorted()
                .toList();
        logger.info(() -> "   Unique chars: " + uniqueChars);

        // Cartesian product
        List<String> colors = List.of("Red", "Blue");
        List<String> sizes = List.of("S", "M", "L");
        List<String> combinations = colors.stream()
                .flatMap(color -> sizes.stream().map(size -> color + "-" + size))
                .toList();
        logger.info(() -> "   Combinations: " + combinations);

        logger.info("");
    }

    // ============================================================
    // 7. Stream Pipelines - Complete Examples
    // ============================================================
    static void streamPipelines() {
        logger.info("7️⃣ COMPLETE STREAM PIPELINES");
        logger.info(SECTION_SEP);

        List<Employee> employees = List.of(
            new Employee(NAME_ALICE, DEPT_ENGINEERING, 95000, 30),
            new Employee("Bob", DEPT_ENGINEERING, 85000, 28),
            new Employee(NAME_CHARLIE, DEPT_MARKETING, 75000, 35),
            new Employee(NAME_DIANA, DEPT_MARKETING, 72000, 32),
            new Employee("Eve", "HR", 65000, 27),
            new Employee("Frank", "HR", 68000, 40),
            new Employee("Grace", DEPT_ENGINEERING, 110000, 45)
        );

        // Pipeline 1: Top 3 highest paid names
        List<String> top3 = employees.stream()
                .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                .limit(3)
                .map(Employee::name)
                .toList();
        logger.info(() -> "   Top 3 earners: " + top3);

        // Pipeline 2: Average salary of engineers
        double avgEngineerSalary = employees.stream()
                .filter(e -> e.department().equals(DEPT_ENGINEERING))
                .mapToDouble(Employee::salary)
                .average()
                .orElse(0);
        logger.info(() -> String.format("   Avg engineer salary: $%.0f", avgEngineerSalary));

        // Pipeline 3: Department with highest total salary
        Optional<Map.Entry<String, Double>> richestDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.summingDouble(Employee::salary)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
        richestDept.ifPresent(e ->
            logger.info(() -> String.format("   Richest dept: %s ($%.0f)", e.getKey(), e.getValue())));

        // Pipeline 4: Name → Department mapping, sorted
        String directory = employees.stream()
                .sorted(Comparator.comparing(Employee::name))
                .map(e -> e.name() + " (" + e.department() + ")")
                .collect(Collectors.joining(", "));
        logger.info(() -> "   Directory: " + directory);

        // Pipeline 5: Statistics per department
        logger.info("   Department Stats:");
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.summarizingDouble(Employee::salary)))
                .forEach((dept, stats) ->
                    logger.info(() -> String.format("     %s: avg=$%.0f, count=%d",
                            dept, stats.getAverage(), stats.getCount())));

        logger.info("");
    }

    // ============================================================
    // 8. Parallel Streams
    // ============================================================
    static void parallelStreams() {
        logger.info("8️⃣ PARALLEL STREAMS");
        logger.info(SECTION_SEP);

        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000).boxed().toList();

        // Sequential vs Parallel timing
        long startSeq = System.nanoTime();
        long seqSum = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long seqTime = System.nanoTime() - startSeq;

        long startPar = System.nanoTime();
        long parSum = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        long parTime = System.nanoTime() - startPar;

        logger.info(() -> String.format("   Sequential: sum=%d, time=%dms", seqSum, seqTime / 1_000_000));
        logger.info(() -> String.format("   Parallel:   sum=%d, time=%dms", parSum, parTime / 1_000_000));

        // ⚠️ Parallel stream pitfalls
        logger.info("\n   ⚠️ Parallel Stream Guidelines:");
        logger.info("   ┌────────────────────────────────────────────────┐");
        logger.info("   │ ✅ Use for: CPU-intensive work, large datasets │");
        logger.info("   │ ❌ Avoid with: I/O operations, small datasets  │");
        logger.info("   │ ❌ Avoid with: Shared mutable state            │");
        logger.info("   │ ❌ Avoid with: Order-dependent operations      │");
        logger.info("   │ 💡 Always measure before using parallel!       │");
        logger.info("   └────────────────────────────────────────────────┘");

        logger.info("");
    }

    // ============================================================
    // 9. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        logger.info("9️⃣ REAL-WORLD STREAM EXAMPLES");
        logger.info(SECTION_SEP);

        // Example 1: Word frequency counter
        String text = "the quick brown fox jumps over the lazy dog the fox";
        Map<String, Long> wordFreq = Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        logger.info(() -> "   Word frequency: " + wordFreq);

        // Example 2: CSV-like data processing
        List<String> csvLines = List.of(
            "Product,Price,Quantity",
            "Laptop,999.99,5",
            "Phone,699.99,10",
            "Tablet,449.99,8",
            "Watch,299.99,15"
        );

        double totalRevenue = csvLines.stream()
                .skip(1) // skip header
                .map(line -> line.split(","))
                .mapToDouble(parts -> Double.parseDouble(parts[1]) * Integer.parseInt(parts[2]))
                .sum();
        logger.info(() -> String.format("   Total revenue: $%.2f", totalRevenue));

        // Example 3: Finding duplicates
        List<String> items = List.of("A", "B", "C", "A", "D", "B", "E");
        Set<String> duplicates = items.stream()
                .filter(i -> Collections.frequency(items, i) > 1)
                .collect(Collectors.toSet());
        logger.info(() -> "   Duplicates: " + duplicates);

        // Example 4: Grouping and transforming
        List<String> emails = List.of(
            "alice@gmail.com", "bob@yahoo.com", "charlie@gmail.com",
            "diana@yahoo.com", "eve@outlook.com"
        );
        Map<String, List<String>> byDomain = emails.stream()
                .collect(Collectors.groupingBy(
                    e -> e.substring(e.indexOf("@") + 1),
                    Collectors.mapping(e -> e.substring(0, e.indexOf("@")), Collectors.toList())
                ));
        logger.info(() -> "   Emails by domain: " + byDomain);

        logger.info("\n═══════════════════════════════════════════");
        logger.info("  ✅ Streams API: Complete!               ");
        logger.info("═══════════════════════════════════════════");
    }
}
