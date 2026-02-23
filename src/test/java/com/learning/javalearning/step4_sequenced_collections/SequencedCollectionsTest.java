package com.learning.javalearning.step4_sequenced_collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Step 4: Sequenced Collections (Java 21)")
class SequencedCollectionsTest {

    @Nested
    @DisplayName("SequencedCollection - ArrayList")
    class SequencedCollectionTest {

        @Test
        @DisplayName("getFirst and getLast return correct elements")
        void getFirstAndLast() {
            List<String> list = new ArrayList<>(List.of("A", "B", "C"));
            assertThat(list.getFirst()).isEqualTo("A");
            assertThat(list.getLast()).isEqualTo("C");
        }

        @Test
        @DisplayName("addFirst inserts at the beginning")
        void addFirst() {
            List<String> list = new ArrayList<>(List.of("B", "C"));
            list.addFirst("A");
            assertThat(list).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("addLast appends at the end")
        void addLast() {
            List<String> list = new ArrayList<>(List.of("A", "B"));
            list.addLast("C");
            assertThat(list).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("removeFirst removes and returns the first element")
        void removeFirst() {
            List<String> list = new ArrayList<>(List.of("A", "B", "C"));
            String removed = list.removeFirst();
            assertThat(removed).isEqualTo("A");
            assertThat(list).containsExactly("B", "C");
        }

        @Test
        @DisplayName("removeLast removes and returns the last element")
        void removeLast() {
            List<String> list = new ArrayList<>(List.of("A", "B", "C"));
            String removed = list.removeLast();
            assertThat(removed).isEqualTo("C");
            assertThat(list).containsExactly("A", "B");
        }
    }

    @Nested
    @DisplayName("SequencedSet - LinkedHashSet")
    class SequencedSetTest {

        @Test
        @DisplayName("LinkedHashSet maintains insertion order with getFirst/getLast")
        void maintainsOrder() {
            SequencedSet<String> set = new LinkedHashSet<>();
            set.add("Spring");
            set.add("Hibernate");
            set.add("Quarkus");

            assertThat(set.getFirst()).isEqualTo("Spring");
            assertThat(set.getLast()).isEqualTo("Quarkus");
        }

        @Test
        @DisplayName("reversed() returns a reversed view")
        void reversedView() {
            SequencedSet<String> set = new LinkedHashSet<>(List.of("A", "B", "C"));
            SequencedSet<String> reversed = set.reversed();

            assertThat(reversed.getFirst()).isEqualTo("C");
            assertThat(reversed.getLast()).isEqualTo("A");
        }
    }

    @Nested
    @DisplayName("SequencedMap - LinkedHashMap")
    class SequencedMapTest {

        @Test
        @DisplayName("firstEntry and lastEntry return correct entries")
        void firstAndLastEntry() {
            SequencedMap<String, Integer> map = new LinkedHashMap<>();
            map.put("Alice", 95);
            map.put("Bob", 87);
            map.put("Charlie", 92);

            assertThat(map.firstEntry()).isEqualTo(Map.entry("Alice", 95));
            assertThat(map.lastEntry()).isEqualTo(Map.entry("Charlie", 92));
        }

        @Test
        @DisplayName("putFirst places entry at the beginning")
        void putFirst() {
            SequencedMap<String, Integer> map = new LinkedHashMap<>();
            map.put("Alice", 95);
            map.put("Bob", 87);
            map.putFirst("Zara", 99);

            assertThat(map.firstEntry().getKey()).isEqualTo("Zara");
        }

        @Test
        @DisplayName("pollLastEntry removes and returns the last entry")
        void pollLastEntry() {
            SequencedMap<String, Integer> map = new LinkedHashMap<>();
            map.put("Alice", 95);
            map.put("Bob", 87);

            Map.Entry<String, Integer> polled = map.pollLastEntry();
            assertThat(polled.getKey()).isEqualTo("Bob");
            assertThat(map).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Reversed Views")
    class ReversedViewsTest {

        @Test
        @DisplayName("reversed view is a live view, not a copy")
        void reversedIsLiveView() {
            List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
            List<Integer> reversed = numbers.reversed();

            reversed.set(0, 100); // Sets last element of original
            assertThat(numbers.getLast()).isEqualTo(100);
            assertThat(numbers).containsExactly(1, 2, 3, 4, 100);
        }

        @Test
        @DisplayName("reversed view iterates in reverse order")
        void reversedIteratesCorrectly() {
            List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3));
            List<Integer> reversed = numbers.reversed();
            assertThat(reversed).containsExactly(3, 2, 1);
        }
    }
}
