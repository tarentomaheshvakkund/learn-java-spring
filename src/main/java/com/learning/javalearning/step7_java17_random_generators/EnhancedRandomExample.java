package com.learning.javalearning.step7_java17_random_generators;

import java.util.random.*;
import java.util.stream.*;

/**
 * JAVA 17: ENHANCED RANDOM GENERATORS (JEP 356)
 * 
 * Java 17 introduced a new random number generation framework:
 * - RandomGenerator interface (unifies all random classes)
 * - New algorithms: L64X128MixRandom, Xoshiro256PlusPlus, etc.
 * - Splittable, Jumpable, LeapableGenerator for parallel processing
 * - RandomGeneratorFactory for algorithm discovery
 * 
 * BENEFITS:
 * - Better API design
 * - More algorithms with different trade-offs
 * - Stream-friendly
 * - Better parallelism support
 */
public class EnhancedRandomExample {

    public static void main(String[] args) {
        System.out.println("=== JAVA 17: Enhanced Random Generators ===\n");

        // 1. Basic usage with RandomGenerator interface
        demonstrateRandomGenerator();

        // 2. Factory pattern for algorithm selection
        demonstrateRandomFactory();

        // 3. Stream support
        demonstrateStreamSupport();

        // 4. Parallel random with SplittableGenerator
        demonstrateSplittableGenerator();

        // 5. List all available algorithms
        listAvailableAlgorithms();
    }

    /**
     * RandomGenerator: The new unified interface
     */
    static void demonstrateRandomGenerator() {
        System.out.println("--- 1. RandomGenerator Interface ---\n");

        // Old way still works
        java.util.Random oldRandom = new java.util.Random();
        System.out.println("  Old Random: " + oldRandom.nextInt(100));

        // New way: Use RandomGenerator interface
        RandomGenerator random = RandomGenerator.getDefault();
        System.out.println("  Default algorithm: " + random.getClass().getSimpleName());
        System.out.println("  Random int: " + random.nextInt(100));
        System.out.println("  Random double: " + random.nextDouble());
        System.out.println("  Random bounded: " + random.nextInt(10, 50));

        // Create with specific algorithm
        RandomGenerator xoshiro = RandomGenerator.of("Xoshiro256PlusPlus");
        System.out.println("\n  Xoshiro256++: " + xoshiro.nextLong());

        System.out.println();
    }

    /**
     * RandomGeneratorFactory: Discover and create generators
     */
    static void demonstrateRandomFactory() {
        System.out.println("--- 2. RandomGeneratorFactory ---\n");

        // Get factory for specific algorithm
        RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("L64X128MixRandom");

        System.out.println("  Algorithm: " + factory.name());
        System.out.println("  Splittable: " + factory.isSplittable());
        System.out.println("  Jumpable: " + factory.isJumpable());
        System.out.println("  State bits: " + factory.stateBits());

        // Create generator from factory
        RandomGenerator gen = factory.create();
        System.out.println("  Generated value: " + gen.nextInt());

        // Create with seed
        RandomGenerator seeded = factory.create(12345L);
        System.out.println("  Seeded value: " + seeded.nextInt());

        System.out.println();
    }

    /**
     * Stream support for bulk random generation
     */
    static void demonstrateStreamSupport() {
        System.out.println("--- 3. Stream Support ---\n");

        RandomGenerator random = RandomGenerator.getDefault();

        // Generate stream of random ints
        System.out.print("  5 random ints (0-100): ");
        random.ints(5, 0, 100)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Generate stream of random doubles
        System.out.print("  5 random doubles: ");
        random.doubles(5)
                .mapToObj(d -> String.format("%.2f", d))
                .forEach(s -> System.out.print(s + " "));
        System.out.println();

        // Sum of random values
        double sum = random.doubles(1000, 0, 1).sum();
        System.out.println("  Sum of 1000 random doubles: " + String.format("%.2f", sum));
        System.out.println("  (Expected ~500 for uniform distribution)");

        // Generate Gaussian (normal) distribution
        System.out.print("\n  5 Gaussian values: ");
        random.doubles(5)
                .map(d -> random.nextGaussian() * 10 + 50) // mean=50, std=10
                .mapToObj(d -> String.format("%.1f", d))
                .forEach(s -> System.out.print(s + " "));
        System.out.println("\n");
    }

    /**
     * SplittableGenerator: Perfect for parallel processing
     */
    static void demonstrateSplittableGenerator() {
        System.out.println("--- 4. Splittable Generator (Parallelism) ---\n");

        // Get a splittable generator
        RandomGenerator.SplittableGenerator splittable = (RandomGenerator.SplittableGenerator) RandomGenerator.of("L64X128MixRandom");

        System.out.println("""
                SplittableGenerator allows you to split a generator into
                independent generators for parallel processing.

                Each split produces a NEW generator that:
                - Is statistically independent
                - Produces non-overlapping sequences
                - Safe for use in separate threads
                """);

        // Split into multiple generators
        RandomGenerator.SplittableGenerator gen1 = splittable.split();
        RandomGenerator.SplittableGenerator gen2 = splittable.split();
        RandomGenerator.SplittableGenerator gen3 = splittable.split();

        System.out.println("  Generator 1: " + gen1.nextInt(100));
        System.out.println("  Generator 2: " + gen2.nextInt(100));
        System.out.println("  Generator 3: " + gen3.nextInt(100));

        // Parallel stream with splits
        System.out.print("\n  Parallel random sum: ");
        long sum = splittable.splits(4)
                .parallel()
                .mapToLong(g -> g.longs(1000).sum())
                .sum();
        System.out.println(sum);

        System.out.println();
    }

    /**
     * List all available random algorithms
     */
    static void listAvailableAlgorithms() {
        System.out.println("--- 5. Available Algorithms ---\n");

        System.out.println("  Algorithm                    | Splittable | Jumpable | State Bits");
        System.out.println("  -----------------------------|------------|----------|------------");

        RandomGeneratorFactory.all()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(factory -> {
                    System.out.printf("  %-30s| %-10s | %-8s | %d%n",
                            factory.name(),
                            factory.isSplittable() ? "Yes" : "No",
                            factory.isJumpable() ? "Yes" : "No",
                            factory.stateBits());
                });

        System.out.println("\n  RECOMMENDATIONS:");
        System.out.println("  - General purpose: L64X128MixRandom or Xoshiro256PlusPlus");
        System.out.println("  - Parallel processing: Use SplittableGenerator");
        System.out.println("  - Cryptographic: Use SecureRandom (not shown here)");
        System.out.println();
    }
}
