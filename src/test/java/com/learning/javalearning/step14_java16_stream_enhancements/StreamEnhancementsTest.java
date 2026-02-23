package com.learning.javalearning.step14_java16_stream_enhancements;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Step 14: Stream Enhancements (Java 12-16)")
class StreamEnhancementsTest {

    @Nested
    @DisplayName("Stream.toList() (Java 16)")
    class StreamToListTest {

        @Test
        @DisplayName("toList() returns correct elements")
        void toListReturnsCorrectElements() {
            List<String> fruits = List.of("apple", "banana", "cherry");
            List<String> upperCase = fruits.stream()
                    .map(String::toUpperCase)
                    .toList();
            assertThat(upperCase).containsExactly("APPLE", "BANANA", "CHERRY");
        }

        @Test
        @DisplayName("toList() returns unmodifiable list")
        void toListIsImmutable() {
            List<Integer> numbers = List.of(1, 2, 3).stream()
                    .map(n -> n * 2)
                    .toList();

            assertThatThrownBy(() -> numbers.add(8))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Collectors.toList() returns mutable list")
        void collectorsToListIsMutable() {
            List<Integer> numbers = List.of(1, 2, 3).stream()
                    .map(n -> n * 2)
                    .collect(Collectors.toList());

            numbers.add(8);
            assertThat(numbers).containsExactly(2, 4, 6, 8);
        }

        @Test
        @DisplayName("toList() with filter produces correct results")
        void toListWithFilter() {
            List<Integer> evenNumbers = Stream.iterate(1, n -> n + 1)
                    .limit(10)
                    .filter(n -> n % 2 == 0)
                    .toList();
            assertThat(evenNumbers).containsExactly(2, 4, 6, 8, 10);
        }

        @Test
        @DisplayName("toList() with flatMap flattens collections")
        void toListWithFlatMap() {
            record Department(String name, List<String> employees) {}

            List<Department> departments = List.of(
                    new Department("Eng", List.of("Alice", "Bob")),
                    new Department("Sales", List.of("Charlie"))
            );

            List<String> allEmployees = departments.stream()
                    .flatMap(d -> d.employees().stream())
                    .toList();
            assertThat(allEmployees).containsExactly("Alice", "Bob", "Charlie");
        }
    }

    @Nested
    @DisplayName("Collectors.teeing() (Java 12)")
    class TeeingCollectorTest {

        @Test
        @DisplayName("Teeing collector computes even and odd counts in single pass")
        void teeingEvenOddCount() {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            record EvenOddCount(long even, long odd) {}

            EvenOddCount result = numbers.stream()
                    .collect(Collectors.teeing(
                            Collectors.filtering(n -> n % 2 == 0, Collectors.counting()),
                            Collectors.filtering(n -> n % 2 != 0, Collectors.counting()),
                            EvenOddCount::new
                    ));

            assertThat(result.even()).isEqualTo(5);
            assertThat(result.odd()).isEqualTo(5);
        }

        @Test
        @DisplayName("Teeing collector finds min and max in single pass")
        void teeingMinMax() {
            List<Integer> scores = List.of(85, 92, 78, 95, 88);

            record MinMax(int min, int max) {}

            MinMax result = scores.stream()
                    .collect(Collectors.teeing(
                            Collectors.minBy(Integer::compare),
                            Collectors.maxBy(Integer::compare),
                            (min, max) -> new MinMax(min.orElse(0), max.orElse(0))
                    ));

            assertThat(result.min()).isEqualTo(78);
            assertThat(result.max()).isEqualTo(95);
        }

        @Test
        @DisplayName("Teeing collector computes average and count together")
        void teeingAverageAndCount() {
            List<Double> salaries = List.of(50000.0, 60000.0, 75000.0, 80000.0, 95000.0);

            record AvgCount(double average, long count) {}

            AvgCount result = salaries.stream()
                    .collect(Collectors.teeing(
                            Collectors.averagingDouble(Double::doubleValue),
                            Collectors.counting(),
                            AvgCount::new
                    ));

            assertThat(result.average()).isCloseTo(72000.0, within(0.01));
            assertThat(result.count()).isEqualTo(5);
        }

        @Test
        @DisplayName("Teeing collector partitions and averages simultaneously")
        void teeingPartitionAndAverage() {
            record Student(String name, int grade) {}

            List<Student> students = List.of(
                    new Student("John", 92),
                    new Student("Jane", 85),
                    new Student("Jim", 78),
                    new Student("Jill", 95)
            );

            record GradeDistribution(long passCount, long failCount, double average) {}

            GradeDistribution result = students.stream()
                    .collect(Collectors.teeing(
                            Collectors.partitioningBy(s -> s.grade() >= 80, Collectors.counting()),
                            Collectors.averagingDouble(Student::grade),
                            (partition, avg) -> new GradeDistribution(
                                    partition.get(true), partition.get(false), avg
                            )
                    ));

            assertThat(result.passCount()).isEqualTo(3);
            assertThat(result.failCount()).isEqualTo(1);
            assertThat(result.average()).isCloseTo(87.5, within(0.01));
        }
    }

    @Nested
    @DisplayName("Stream Comparison")
    class StreamComparisonTest {

        @Test
        @DisplayName("toList() and collect(Collectors.toList()) produce same elements")
        void sameElements() {
            List<Integer> source = List.of(1, 2, 3, 4, 5);

            List<Integer> viaToList = source.stream().filter(n -> n > 2).toList();
            List<Integer> viaCollect = source.stream().filter(n -> n > 2).collect(Collectors.toList());

            assertThat(viaToList).isEqualTo(viaCollect);
        }

        @Test
        @DisplayName("Large dataset filtering with toList() produces correct size")
        void largeDatasetToList() {
            List<Integer> result = Stream.iterate(1, n -> n + 1)
                    .limit(100_000)
                    .filter(n -> n % 2 == 0)
                    .toList();
            assertThat(result).hasSize(50_000);
        }
    }
}
