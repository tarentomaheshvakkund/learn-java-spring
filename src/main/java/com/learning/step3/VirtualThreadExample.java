package com.learning.step3;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * STEP 3: VIRTUAL THREADS (Java 21)
 *
 * Virtual threads are lightweight threads that significantly reduce the effort
 * of writing, maintaining, and observing high-throughput concurrent
 * applications.
 *
 * They are managed by the JVM, not the OS, so you can create millions of them!
 */
public class VirtualThreadExample {

  public void runVirtualThreadDemo() {
    System.out.println("=== Virtual Thread Demo ===");

    // 1. Create a single virtual thread
    Thread vThread = Thread.ofVirtual().start(() -> {
      System.out.println("Running in a virtual thread: " + Thread.currentThread());
    });

    try {
      vThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // 2. Using ExecutorService with Virtual Threads
    // try-with-resources ensures the executor is closed (and all tasks finished)
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, 10).forEach(i -> {
        executor.submit(() -> {
          try {
            // Simulate some blocking IO (sleeping)
            // Virtual threads are great for blocking tasks!
            Thread.sleep(Duration.ofMillis(100));
            System.out.println("Task " + i + " completed on " + Thread.currentThread());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        });
      });
    } // Executor waits for all tasks to complete here

    System.out.println("All virtual thread tasks completed.");
  }

  public static void main(String[] args) {
    new VirtualThreadExample().runVirtualThreadDemo();
  }
}
