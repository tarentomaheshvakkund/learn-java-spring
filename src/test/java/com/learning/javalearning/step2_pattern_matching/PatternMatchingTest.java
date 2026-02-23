package com.learning.javalearning.step2_pattern_matching;

import com.learning.javalearning.step1_basics.Circle;
import com.learning.javalearning.step1_basics.Rectangle;
import com.learning.javalearning.step1_basics.Square;
import com.learning.javalearning.step1_basics.UserRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Step 2: Pattern Matching")
class PatternMatchingTest {

    @Nested
    @DisplayName("Switch Pattern Matching")
    class SwitchPatternTest {

        private final SwitchPatternExample example = new SwitchPatternExample();

        @Test
        @DisplayName("describeShape matches Integer correctly")
        void describeShapeInteger() {
            assertThat(example.describeShape(42)).isEqualTo("It's a number: 42");
        }

        @Test
        @DisplayName("describeShape matches String correctly")
        void describeShapeString() {
            assertThat(example.describeShape("hello")).isEqualTo("It's a string of length 5");
        }

        @Test
        @DisplayName("describeShape matches UserRecord correctly")
        void describeShapeUserRecord() {
            UserRecord user = new UserRecord(1L, "alice", "alice@example.com");
            assertThat(example.describeShape(user)).isEqualTo("It's user alice");
        }

        @Test
        @DisplayName("describeShape handles null")
        void describeShapeNull() {
            assertThat(example.describeShape(null)).isEqualTo("It's a null value");
        }

        @Test
        @DisplayName("describeShape handles unknown types with default")
        void describeShapeDefault() {
            assertThat(example.describeShape(3.14)).isEqualTo("It's something else");
        }

        @Test
        @DisplayName("getArea returns correct area for Circle")
        void getAreaCircle() {
            Circle circle = new Circle(5.0);
            double area = example.getArea(circle);
            assertThat(area).isCloseTo(Math.PI * 25.0, within(0.001));
        }

        @Test
        @DisplayName("getArea returns correct area for Rectangle")
        void getAreaRectangle() {
            Rectangle rect = new Rectangle(3.0, 4.0);
            assertThat(example.getArea(rect)).isEqualTo(12.0);
        }

        @Test
        @DisplayName("getArea returns correct area for Square")
        void getAreaSquare() {
            Square square = new Square(6.0);
            assertThat(example.getArea(square)).isEqualTo(36.0);
        }
    }

    @Nested
    @DisplayName("Record Pattern Matching")
    class RecordPatternTest {

        private final RecordPatternExample example = new RecordPatternExample();

        @Test
        @DisplayName("getEmailDomain extracts domain from UserRecord")
        void getEmailDomainFromUserRecord() {
            UserRecord user = new UserRecord(1L, "john", "john@example.com");
            assertThat(example.getEmailDomain(user)).isEqualTo("example.com");
        }

        @Test
        @DisplayName("getEmailDomain extracts domain from Gmail address")
        void getEmailDomainGmail() {
            UserRecord user = new UserRecord(2L, "jane", "jane@gmail.com");
            assertThat(example.getEmailDomain(user)).isEqualTo("gmail.com");
        }

        @Test
        @DisplayName("getEmailDomain returns unknown for non-UserRecord")
        void getEmailDomainUnknownType() {
            assertThat(example.getEmailDomain("not a user")).isEqualTo("unknown");
            assertThat(example.getEmailDomain(42)).isEqualTo("unknown");
        }
    }
}
