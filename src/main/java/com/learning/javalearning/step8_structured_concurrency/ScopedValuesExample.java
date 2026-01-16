package com.learning.javalearning.step8_structured_concurrency;

/**
 * JAVA 21: SCOPED VALUES (JEP 446 - Preview)
 * 
 * Scoped Values are a modern alternative to ThreadLocal for sharing immutable
 * data within a thread and with child threads (especially Virtual Threads).
 * 
 * KEY DIFFERENCES FROM THREADLOCAL:
 * 1. Immutable - value cannot be changed once bound
 * 2. Bounded lifetime - automatically cleaned up when scope exits
 * 3. Inherited by child threads - works with Virtual Threads
 * 4. More performant - especially with many virtual threads
 * 
 * USE CASES:
 * - Request context (user ID, tenant ID, trace ID)
 * - Transaction context
 * - Security context
 * 
 * NOTE: This is a PREVIEW feature. Run with --enable-preview
 */
public class ScopedValuesExample {

    // Declare scoped values (like constants)
    // private static final ScopedValue<String> CURRENT_USER =
    // ScopedValue.newInstance();
    // private static final ScopedValue<String> TRACE_ID =
    // ScopedValue.newInstance();

    record RequestContext(String userId, String traceId, String tenantId) {
    }

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: Scoped Values ===\n");

        // 1. Basic usage
        demonstrateBasicUsage();

        // 2. Compare with ThreadLocal
        compareWithThreadLocal();

        // 3. Integration with Structured Concurrency
        demonstrateWithStructuredConcurrency();
    }

    static void demonstrateBasicUsage() {
        System.out.println("--- 1. Basic Scoped Values Usage ---\n");

        // In preview Java 21:
        // ScopedValue.where(CURRENT_USER, "alice")
        // .where(TRACE_ID, "trace-12345")
        // .run(() -> {
        // processRequest(); // Can access CURRENT_USER and TRACE_ID
        // });
        // // Outside the scope, values are NOT accessible!

        System.out.println("""
                // Declare as static final (like constants)
                static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();
                static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();

                // Bind values for a scope
                ScopedValue.where(CURRENT_USER, "alice")
                    .where(TRACE_ID, "trace-12345")
                    .run(() -> {
                        // Inside scope: values are accessible
                        String user = CURRENT_USER.get();  // "alice"
                        String trace = TRACE_ID.get();     // "trace-12345"

                        processRequest();
                        callService();
                        logAction();
                    });

                // Outside scope: values are NOT accessible!
                // CURRENT_USER.get() -> throws NoSuchElementException
                """);

        System.out.println("  KEY POINT: Values are automatically cleaned up when scope exits!\n");
    }

    static void compareWithThreadLocal() {
        System.out.println("--- 2. Scoped Values vs ThreadLocal ---\n");

        System.out.println("""
                ┌─────────────────────────────────────────────────────────────────────────┐
                │                    ThreadLocal (Old)    vs    ScopedValue (New)         │
                ├─────────────────────────────────────────────────────────────────────────┤
                │ Mutability          │ Mutable ❌            │ Immutable ✅              │
                │ Cleanup             │ Manual remove() ❌    │ Automatic ✅              │
                │ Memory Leaks        │ Common problem ❌     │ Not possible ✅           │
                │ Virtual Threads     │ Poor performance ❌   │ Optimized ✅              │
                │ Child Threads       │ Not inherited ❌      │ Inherited ✅              │
                │ Rebinding           │ Anywhere ❌           │ Only in nested scope ✅   │
                └─────────────────────────────────────────────────────────────────────────┘
                """);

        System.out.println("""
                ❌ THREADLOCAL PROBLEMS:

                static final ThreadLocal<String> USER = new ThreadLocal<>();

                void handleRequest(String userId) {
                    USER.set(userId);
                    try {
                        processRequest();
                    } finally {
                        USER.remove();  // Easy to forget! Memory leak!
                    }
                }

                // With thread pools, forgetting remove() means:
                // - Next request sees previous user's data! 💀
                // - Memory leaks in long-running applications

                ─────────────────────────────────────────────────────────

                ✅ SCOPED VALUES SOLUTION:

                static final ScopedValue<String> USER = ScopedValue.newInstance();

                void handleRequest(String userId) {
                    ScopedValue.where(USER, userId).run(() -> {
                        processRequest();
                    });
                    // Automatic cleanup! No memory leaks possible!
                }
                """);
    }

    static void demonstrateWithStructuredConcurrency() {
        System.out.println("--- 3. Scoped Values + Structured Concurrency ---\n");

        System.out.println("""
                // Perfect combination for request handling!

                static final ScopedValue<RequestContext> REQUEST = ScopedValue.newInstance();

                void handleRequest(HttpRequest req) {
                    var ctx = new RequestContext(
                        req.getUserId(),
                        generateTraceId(),
                        req.getTenantId()
                    );

                    ScopedValue.where(REQUEST, ctx).run(() -> {
                        // Context available everywhere in this scope!

                        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                            // Child tasks INHERIT the scoped value! ✅
                            var userTask = scope.fork(() -> {
                                // REQUEST.get() works here!
                                return fetchUser(REQUEST.get().userId());
                            });

                            var ordersTask = scope.fork(() -> {
                                // REQUEST.get() works here too!
                                return fetchOrders(REQUEST.get().userId());
                            });

                            scope.join();
                            scope.throwIfFailed();

                            return buildResponse(userTask.get(), ordersTask.get());
                        }
                    });
                }
                """);

        System.out.println("  BENEFITS:");
        System.out.println("  ✅ No need to pass context through every method");
        System.out.println("  ✅ Child virtual threads automatically inherit context");
        System.out.println("  ✅ Perfect for distributed tracing");
        System.out.println("  ✅ Clean, automatic resource management\n");
    }
}
