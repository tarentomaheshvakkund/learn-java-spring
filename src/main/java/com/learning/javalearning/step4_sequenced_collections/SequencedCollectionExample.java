package com.learning.javalearning.step4_sequenced_collections;

import java.util.*;

/**
 * JAVA 21: SEQUENCED COLLECTIONS (JEP 431)
 * 
 * Before Java 21, getting the first/last element from different collections was
 * inconsistent:
 * - List: list.get(0) / list.get(list.size()-1)
 * - Deque: deque.getFirst() / deque.getLast()
 * - SortedSet: sortedSet.first() / sortedSet.last()
 * 
 * Java 21 introduces unified interfaces:
 * - SequencedCollection: getFirst(), getLast(), addFirst(), addLast(),
 * reversed()
 * - SequencedSet: extends SequencedCollection
 * - SequencedMap: firstEntry(), lastEntry(), reversed()
 */
public class SequencedCollectionExample {

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: Sequenced Collections ===\n");

        // 1. SequencedCollection with ArrayList
        demonstrateSequencedCollection();

        // 2. SequencedSet with LinkedHashSet
        demonstrateSequencedSet();

        // 3. SequencedMap with LinkedHashMap
        demonstrateSequencedMap();

        // 4. Reversed views
        demonstrateReversedViews();
    }

    static void demonstrateSequencedCollection() {
        System.out.println("--- 1. SequencedCollection (ArrayList) ---");

        List<String> languages = new ArrayList<>(List.of("Java", "Python", "Go", "Rust"));

        // NEW in Java 21: Uniform first/last access
        System.out.println("First: " + languages.getFirst()); // Java
        System.out.println("Last:  " + languages.getLast()); // Rust

        // NEW: Add at beginning/end
        languages.addFirst("C++");
        languages.addLast("Kotlin");
        System.out.println("After add: " + languages);

        // NEW: Remove first/last
        String removed = languages.removeFirst();
        System.out.println("Removed first: " + removed);
        System.out.println("After remove: " + languages);

        System.out.println();
    }

    static void demonstrateSequencedSet() {
        System.out.println("--- 2. SequencedSet (LinkedHashSet) ---");

        // LinkedHashSet maintains insertion order
        SequencedSet<String> frameworks = new LinkedHashSet<>();
        frameworks.add("Spring");
        frameworks.add("Hibernate");
        frameworks.add("Quarkus");

        System.out.println("Frameworks: " + frameworks);
        System.out.println("First: " + frameworks.getFirst()); // Spring
        System.out.println("Last:  " + frameworks.getLast()); // Quarkus

        // Get reversed view (no copy, just a view!)
        SequencedSet<String> reversed = frameworks.reversed();
        System.out.println("Reversed: " + reversed);

        System.out.println();
    }

    static void demonstrateSequencedMap() {
        System.out.println("--- 3. SequencedMap (LinkedHashMap) ---");

        SequencedMap<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);

        // NEW: Access first/last entries directly
        System.out.println("First entry: " + scores.firstEntry()); // Alice=95
        System.out.println("Last entry:  " + scores.lastEntry()); // Charlie=92

        // NEW: Put at beginning (shifts others)
        scores.putFirst("Zara", 99);
        System.out.println("After putFirst: " + scores);

        // NEW: Poll (remove and return) first/last
        var removed = scores.pollLastEntry();
        System.out.println("Polled last: " + removed);
        System.out.println("After poll:  " + scores);

        System.out.println();
    }

    static void demonstrateReversedViews() {
        System.out.println("--- 4. Reversed Views (Zero Copy!) ---");

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("Original: " + numbers);

        // reversed() returns a VIEW, not a copy
        List<Integer> reversedView = numbers.reversed();
        System.out.println("Reversed view: " + reversedView);

        // Modifying the view affects the original!
        reversedView.set(0, 100); // Sets the last element of original
        System.out.println("After modifying reversed view:");
        System.out.println("  Original: " + numbers); // [1, 2, 3, 4, 100]
        System.out.println("  Reversed: " + reversedView); // [100, 4, 3, 2, 1]

        // Iterate in reverse order easily
        System.out.println("Iterating reversed:");
        for (int n : numbers.reversed()) {
            System.out.print(n + " ");
        }
        System.out.println("\n");
    }
}
