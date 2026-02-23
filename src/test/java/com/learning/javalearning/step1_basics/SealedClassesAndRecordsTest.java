package com.learning.javalearning.step1_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Step 1: Sealed Classes, Records & Text Blocks")
class SealedClassesAndRecordsTest {

    @Nested
    @DisplayName("Sealed Classes - Shape Hierarchy")
    class SealedClassesTest {

        @Test
        @DisplayName("Circle area calculation is correct")
        void circleAreaCalculation() {
            Shape circle = new Circle(5.0);
            assertThat(circle.calculateArea()).isCloseTo(Math.PI * 25, within(0.001));
        }

        @Test
        @DisplayName("Rectangle area calculation is correct")
        void rectangleAreaCalculation() {
            Shape rectangle = new Rectangle(4.0, 6.0);
            assertThat(rectangle.calculateArea()).isEqualTo(24.0);
        }

        @Test
        @DisplayName("Square area calculation is correct")
        void squareAreaCalculation() {
            Shape square = new Square(7.0);
            assertThat(square.calculateArea()).isEqualTo(49.0);
        }

        @Test
        @DisplayName("Shape interface is sealed - only permits Circle, Rectangle, Square")
        void shapeIsSealed() {
            assertThat(Shape.class.isSealed()).isTrue();
            Class<?>[] permitted = Shape.class.getPermittedSubclasses();
            assertThat(permitted).hasSize(3);
        }

        @Test
        @DisplayName("Circle is a final class")
        void circleIsFinal() {
            assertThat(java.lang.reflect.Modifier.isFinal(Circle.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("All shapes implement Shape interface")
        void allShapesImplementShape() {
            assertThat(new Circle(1)).isInstanceOf(Shape.class);
            assertThat(new Rectangle(1, 1)).isInstanceOf(Shape.class);
            assertThat(new Square(1)).isInstanceOf(Shape.class);
        }

        @Test
        @DisplayName("Circle with zero radius has zero area")
        void circleZeroRadius() {
            assertThat(new Circle(0).calculateArea()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Rectangle with zero dimension has zero area")
        void rectangleZeroDimension() {
            assertThat(new Rectangle(0, 5).calculateArea()).isEqualTo(0.0);
            assertThat(new Rectangle(5, 0).calculateArea()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Records - UserRecord")
    class RecordsTest {

        @Test
        @DisplayName("UserRecord stores data correctly")
        void userRecordStoresData() {
            UserRecord user = new UserRecord(1L, "john", "john@example.com");
            assertThat(user.id()).isEqualTo(1L);
            assertThat(user.username()).isEqualTo("john");
            assertThat(user.email()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("UserRecord compact constructor rejects blank username")
        void userRecordRejectsBlankUsername() {
            assertThatThrownBy(() -> new UserRecord(1L, "", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Username cannot be empty");
        }

        @Test
        @DisplayName("UserRecord compact constructor rejects null username")
        void userRecordRejectsNullUsername() {
            assertThatThrownBy(() -> new UserRecord(1L, null, "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Username cannot be empty");
        }

        @Test
        @DisplayName("UserRecord getDisplayName formats correctly")
        void userRecordGetDisplayName() {
            UserRecord user = new UserRecord(1L, "john", "john@example.com");
            assertThat(user.getDisplayName()).isEqualTo("john (john@example.com)");
        }

        @Test
        @DisplayName("UserRecord equals works correctly for records")
        void userRecordEquals() {
            UserRecord user1 = new UserRecord(1L, "john", "john@example.com");
            UserRecord user2 = new UserRecord(1L, "john", "john@example.com");
            assertThat(user1).isEqualTo(user2);
        }

        @Test
        @DisplayName("UserRecord hashCode is consistent for equal records")
        void userRecordHashCode() {
            UserRecord user1 = new UserRecord(1L, "john", "john@example.com");
            UserRecord user2 = new UserRecord(1L, "john", "john@example.com");
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("UserRecord toString contains all fields")
        void userRecordToString() {
            UserRecord user = new UserRecord(1L, "john", "john@example.com");
            String str = user.toString();
            assertThat(str).contains("john", "john@example.com");
        }

        @Test
        @DisplayName("UserRecord is immutable - fields are final")
        void userRecordIsImmutable() {
            assertThat(UserRecord.class.isRecord()).isTrue();
        }
    }

    @Nested
    @DisplayName("Text Blocks")
    class TextBlocksTest {

        @Test
        @DisplayName("Text blocks preserve multi-line content")
        void textBlockMultiLine() {
            String textBlock = """
                    Hello,
                    World!
                    """;
            assertThat(textBlock).contains("Hello,");
            assertThat(textBlock).contains("World!");
            assertThat(textBlock.lines().count()).isEqualTo(2);
        }

        @Test
        @DisplayName("Text blocks handle JSON formatting")
        void textBlockJson() {
            String json = """
                    {
                        "name": "Java",
                        "version": 21
                    }
                    """;
            assertThat(json).contains("\"name\": \"Java\"");
            assertThat(json).contains("\"version\": 21");
        }
    }
}
