package com.learning.javalearning.step8_structured_concurrency;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * JAVA 21: STRUCTURED CONCURRENCY (JEP 453 - Preview)
 * 
 * Structured Concurrency treats groups of related concurrent tasks as a single
 * unit of work. If one subtask fails, others are cancelled. All subtasks
 * complete
 * before the scope exits.
 * 
 * KEY BENEFITS:
 * 1. No orphaned threads - all tasks complete before parent exits
 * 2. Automatic cancellation on failure
 * 3. Clear ownership - parent owns child tasks
 * 4. Error propagation - failures bubble up properly
 * 
 * NOTE: This is a PREVIEW feature. Run with --enable-preview
 */
public class StructuredConcurrencyExample {

    // Simulated services
    record User(String id, String name, String email) {
    }

    record Order(String orderId, String userId, double amount) {
    }

    record UserDashboard(User user, List<Order> orders, String recommendation) {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== JAVA 21: Structured Concurrency ===\n");

        // 1. Basic example: Fetch all or fail
        demonstrateShutdownOnFailure();

        // 2. Best-effort: Get what succeeds
        demonstrateShutdownOnSuccess();

        // 3. Compare with traditional approach
        compareWithTraditional();
    }

    /**
     * ShutdownOnFailure: ALL tasks must succeed, or ALL are cancelled
     * Use case: Building a dashboard that needs ALL data
     */
    static void demonstrateShutdownOnFailure() throws Exception {
        System.out.println("--- 1. ShutdownOnFailure Strategy ---");
        System.out.println("Fetching user dashboard (needs user + orders + recommendation)...\n");

        // In preview Java 21, you would use:
        // try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        // Subtask<User> userTask = scope.fork(() -> fetchUser("user123"));
        // Subtask<List<Order>> ordersTask = scope.fork(() -> fetchOrders("user123"));
        // Subtask<String> recoTask = scope.fork(() -> fetchRecommendation("user123"));
        //
        // scope.join(); // Wait for all
        // scope.throwIfFailed(); // Throw if any failed
        //
        // // All succeeded! Build dashboard
        // UserDashboard dashboard = new UserDashboard(
        // userTask.get(),
        // ordersTask.get(),
        // recoTask.get()
        // );
        // }

        // Simulated version for demonstration
        System.out.println("  [Fork] fetchUser(\"user123\")");
        System.out.println("  [Fork] fetchOrders(\"user123\")");
        System.out.println("  [Fork] fetchRecommendation(\"user123\")");
        System.out.println("  [Join] Waiting for all tasks...");

        User user = fetchUser("user123");
        List<Order> orders = fetchOrders("user123");
        String reco = fetchRecommendation("user123");

        UserDashboard dashboard = new UserDashboard(user, orders, reco);
        System.out.println("\n  ✅ Dashboard built: " + dashboard.user().name());
        System.out.println("     Orders: " + dashboard.orders().size());
        System.out.println("     Recommendation: " + dashboard.recommendation());

        System.out.println("\n  KEY POINT: If ANY task fails, ALL are cancelled automatically!\n");
    }

    /**
     * ShutdownOnSuccess: Return as soon as ONE succeeds
     * Use case: Query multiple replicas, use first response
     */
    static void demonstrateShutdownOnSuccess() throws Exception {
        System.out.println("--- 2. ShutdownOnSuccess Strategy ---");
        System.out.println("Racing multiple data sources (use first response)...\n");

        // In preview Java 21:
        // try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
        // scope.fork(() -> queryPrimaryDB());
        // scope.fork(() -> queryReplicaDB());
        // scope.fork(() -> queryCache());
        //
        // scope.join();
        // String result = scope.result(); // First successful result
        // } // Other tasks are automatically cancelled!

        System.out.println("  [Fork] queryPrimaryDB()   - ~200ms");
        System.out.println("  [Fork] queryReplicaDB()   - ~150ms");
        System.out.println("  [Fork] queryCache()       - ~50ms  ⚡");
        System.out.println("  [Join] Racing...");
        System.out.println("\n  ✅ Cache responded first! Other queries cancelled.");
        System.out.println("  Result: \"cached_user_data\"");

        System.out.println("\n  KEY POINT: First success wins, others are cancelled!\n");
    }

    /**
     * Compare with the OLD way (pre-Java 21)
     */
    static void compareWithTraditional() {
        System.out.println("--- 3. Comparison: Old vs New ---\n");

        System.out.println("""
                ❌ OLD WAY (ExecutorService):
                ┌─────────────────────────────────────────────────────────────┐
                │ ExecutorService executor = Executors.newFixedThreadPool(3); │
                │ try {                                                       │
                │     Future<User> userFuture = executor.submit(fetchUser);   │
                │     Future<Orders> ordersFuture = executor.submit(fetch..); │
                │     Future<Reco> recoFuture = executor.submit(fetchReco);   │
                │                                                             │
                │     // PROBLEM: If userFuture fails, others keep running!   │
                │     User user = userFuture.get();        // May throw      │
                │     Orders orders = ordersFuture.get();  // Still running! │
                │     Reco reco = recoFuture.get();        // Still running! │
                │ } finally {                                                 │
                │     executor.shutdown();  // Must remember to shutdown!     │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘

                ✅ NEW WAY (Structured Concurrency):
                ┌─────────────────────────────────────────────────────────────┐
                │ try (var scope = new ShutdownOnFailure()) {                 │
                │     var user = scope.fork(() -> fetchUser());               │
                │     var orders = scope.fork(() -> fetchOrders());           │
                │     var reco = scope.fork(() -> fetchRecommendation());     │
                │                                                             │
                │     scope.join();           // Wait for all                 │
                │     scope.throwIfFailed();  // Propagate errors             │
                │                                                             │
                │     // ✅ If ANY fails, ALL are cancelled automatically!    │
                │     // ✅ Scope auto-closes, no manual cleanup needed!      │
                │     return new Dashboard(user.get(), orders.get(), ...);    │
                │ }                                                           │
                └─────────────────────────────────────────────────────────────┘
                """);

        System.out.println("  BENEFITS OF STRUCTURED CONCURRENCY:");
        System.out.println("  ✅ No orphaned threads");
        System.out.println("  ✅ Automatic cancellation on failure");
        System.out.println("  ✅ Clear parent-child relationship");
        System.out.println("  ✅ Proper error propagation");
        System.out.println("  ✅ Works beautifully with Virtual Threads!\n");
    }

    // --- Simulated service methods ---

    static User fetchUser(String userId) throws InterruptedException {
        Thread.sleep(100); // Simulate network delay
        return new User(userId, "Alice Johnson", "alice@example.com");
    }

    static List<Order> fetchOrders(String userId) throws InterruptedException {
        Thread.sleep(150);
        return List.of(
                new Order("ORD-001", userId, 99.99),
                new Order("ORD-002", userId, 249.50));
    }

    static String fetchRecommendation(String userId) throws InterruptedException {
        Thread.sleep(80);
        return "Based on your history: Try our new Premium Plan!";
    }
}
