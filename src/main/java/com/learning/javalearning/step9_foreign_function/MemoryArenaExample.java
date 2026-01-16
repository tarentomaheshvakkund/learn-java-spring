package com.learning.javalearning.step9_foreign_function;

import java.lang.foreign.*;

/**
 * JAVA 21: MEMORY ARENA TYPES
 * 
 * Different Arena types for different use cases:
 * 
 * 1. Arena.ofConfined() - Single-thread access, explicit close
 * 2. Arena.ofShared() - Multi-thread access, explicit close
 * 3. Arena.ofAuto() - GC-managed cleanup (no try-with-resources needed)
 * 4. Arena.global() - Never freed, lives for JVM lifetime
 */
public class MemoryArenaExample {

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: Memory Arena Types ===\n");

        demonstrateConfinedArena();
        demonstrateSharedArena();
        demonstrateAutoArena();
        demonstrateGlobalArena();
        demonstrateMemorySlicing();
    }

    /**
     * Confined Arena: Single-thread, deterministic cleanup
     */
    static void demonstrateConfinedArena() {
        System.out.println("--- 1. Confined Arena (Single Thread) ---\n");

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(1024);
            segment.set(ValueLayout.JAVA_INT, 0, 42);

            System.out.println("  Created: 1KB confined segment");
            System.out.println("  Value: " + segment.get(ValueLayout.JAVA_INT, 0));
            System.out.println("  Thread: " + Thread.currentThread().getName());

            // Cannot access from another thread - will throw!
            // new Thread(() -> segment.get(ValueLayout.JAVA_INT, 0)).start();
        }
        System.out.println("  ✅ Memory freed on close\n");
    }

    /**
     * Shared Arena: Multi-thread access
     */
    static void demonstrateSharedArena() {
        System.out.println("--- 2. Shared Arena (Multi-Thread) ---\n");

        try (Arena arena = Arena.ofShared()) {
            MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT, 10);

            // Can be accessed from multiple threads
            Thread writer = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    segment.setAtIndex(ValueLayout.JAVA_INT, i, i * 100);
                }
                System.out.println("  Writer thread done");
            });

            writer.start();
            try {
                writer.join();
            } catch (InterruptedException e) {
            }

            System.out.print("  Values: ");
            for (int i = 0; i < 10; i++) {
                System.out.print(segment.getAtIndex(ValueLayout.JAVA_INT, i) + " ");
            }
            System.out.println();
        }
        System.out.println("  ✅ Thread-safe memory access\n");
    }

    /**
     * Auto Arena: GC-managed cleanup
     */
    static void demonstrateAutoArena() {
        System.out.println("--- 3. Auto Arena (GC Managed) ---\n");

        // No try-with-resources needed!
        Arena arena = Arena.ofAuto();
        MemorySegment segment = arena.allocate(512);
        segment.set(ValueLayout.JAVA_LONG, 0, System.currentTimeMillis());

        System.out.println("  Created: 512 bytes, GC will clean up");
        System.out.println("  Timestamp: " + segment.get(ValueLayout.JAVA_LONG, 0));
        System.out.println("  ⚠️ Memory freed when GC runs (non-deterministic)\n");
    }

    /**
     * Global Arena: Never freed
     */
    static void demonstrateGlobalArena() {
        System.out.println("--- 4. Global Arena (JVM Lifetime) ---\n");

        MemorySegment segment = Arena.global().allocate(64);
        segment.set(ValueLayout.JAVA_INT, 0, 12345);

        System.out.println("  Created: 64 bytes in global arena");
        System.out.println("  Value: " + segment.get(ValueLayout.JAVA_INT, 0));
        System.out.println("  ⚠️ Never freed - use for constants only!\n");
    }

    /**
     * Memory Slicing: Views into existing segments
     */
    static void demonstrateMemorySlicing() {
        System.out.println("--- 5. Memory Slicing ---\n");

        try (Arena arena = Arena.ofConfined()) {
            // Allocate 100 bytes
            MemorySegment buffer = arena.allocate(100);

            // Create slices (views into the buffer)
            MemorySegment header = buffer.asSlice(0, 20); // First 20 bytes
            MemorySegment body = buffer.asSlice(20, 60); // Next 60 bytes
            MemorySegment footer = buffer.asSlice(80, 20); // Last 20 bytes

            // Write to slices
            header.set(ValueLayout.JAVA_INT, 0, 1); // Header ID
            body.set(ValueLayout.JAVA_LONG, 0, 999L); // Body data
            footer.set(ValueLayout.JAVA_INT, 0, -1); // Footer marker

            System.out.println("  Buffer: 100 bytes");
            System.out.println("  ├── Header (0-20):  ID = " + header.get(ValueLayout.JAVA_INT, 0));
            System.out.println("  ├── Body (20-80):   Data = " + body.get(ValueLayout.JAVA_LONG, 0));
            System.out.println("  └── Footer (80-100): Marker = " + footer.get(ValueLayout.JAVA_INT, 0));

            System.out.println("\n  ✅ Slices share underlying memory (zero-copy!)\n");
        }
    }
}
