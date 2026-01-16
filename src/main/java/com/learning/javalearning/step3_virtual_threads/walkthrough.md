# Walkthrough: Step 3 - Virtual Threads

Step 3 introduces **Virtual Threads** (Project Loom), a major feature in Java 21 for writing high-throughput concurrent applications.

## Why Virtual Threads?

### The Problem with Platform Threads
Traditionally, Java threads were wrappers around operating system (OS) threads (Platform Threads).
- **Expensive**: OS threads consume significant memory (megabytes) and take time to start.
- **Limited Scalability**: You can only create a few thousand before running out of system resources.
- **The "Thread-per-Request" Bottleneck**: In web servers, assigning one thread per request meant your server's capacity was limited by the thread count, not the CPU.

### The Solution: Virtual Threads
Virtual threads are managed by the JVM, not the OS.
- **Cheap**: You can create **millions** of them.
- **Efficient Blocking**: When a virtual thread blocks (e.g., waiting for a database query), the JVM "unmounts" it from the CPU, allowing another virtual thread to run. The OS thread (Carrier Thread) is never blocked!

## Key Concepts

1.  **Carrier Threads**: The JVM uses a small pool of OS threads (ForkJoinPool) to run virtual threads. A virtual thread is "mounted" on a carrier thread only when it has actual CPU work to do.
2.  **No Pooling**: **Do not pool virtual threads**. Because they are so cheap to create, you should create a new one for every task and let it die when the task is done.

## Example Code

**File**: [VirtualThreadExample.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/learning/step3/VirtualThreadExample.java)

We demonstrate two ways to use them:

### 1. Direct Creation
Use `Thread.ofVirtual()` to start a thread instantly.
```java
Thread.ofVirtual().start(() -> {
    System.out.println("I'm virtual!");
});
```

### 2. Using ExecutorService (Structured Concurrency)
The new `newVirtualThreadPerTaskExecutor` creates a NEW virtual thread for every submitted task. It never reuses threads.
```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> {
        // This runs in its own dedicated virtual thread
        Thread.sleep(1000); 
    });
}
```

## How to Verify
Open [VirtualThreadExample.java](file:///home/maheshrv/Documents/IGOT/sourcecodes-igot/learn-java-spring/src/main/java/com/learning/step3/VirtualThreadExample.java) in IntelliJ and run the `main` method.

You should see output indicating that tasks are running on virtual threads (e.g., `VirtualThread[#...]`).

## Notes
- Ensure your Project SDK in IntelliJ is set to Java 21.
- `pom.xml` is configured for Java 21.
