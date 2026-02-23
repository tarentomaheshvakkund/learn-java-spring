package com.learning.javalearning.step13_java14_switch_expressions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Step 13: Switch Expressions (Java 14)")
class SwitchExpressionsTest {

    @Nested
    @DisplayName("Arrow Syntax Switch Expressions")
    class ArrowSyntaxTest {

        @Test
        @DisplayName("Switch expression with arrow syntax returns correct day type")
        void switchArrowSyntaxDayType() {
            String result = switch (DayOfWeek.MONDAY) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
                case SATURDAY, SUNDAY -> "Weekend";
            };
            assertThat(result).isEqualTo("Weekday");
        }

        @Test
        @DisplayName("Switch expression handles weekend correctly")
        void switchArrowSyntaxWeekend() {
            String result = switch (DayOfWeek.SATURDAY) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
                case SATURDAY, SUNDAY -> "Weekend";
            };
            assertThat(result).isEqualTo("Weekend");
        }
    }

    @Nested
    @DisplayName("Yield Keyword in Switch Expressions")
    class YieldKeywordTest {

        @Test
        @DisplayName("Switch expression with yield returns computed value")
        void switchWithYield() {
            int num = 3;
            String result = switch (num) {
                case 1 -> "one";
                case 2 -> "two";
                default -> {
                    String computed = "number " + num;
                    yield computed;
                }
            };
            assertThat(result).isEqualTo("number 3");
        }
    }

    @Nested
    @DisplayName("Exhaustiveness with Enums")
    class ExhaustivenessTest {

        enum Season { SPRING, SUMMER, FALL, WINTER }

        @Test
        @DisplayName("Switch expression covers all enum values without default")
        void exhaustiveEnumSwitch() {
            for (Season season : Season.values()) {
                String activity = switch (season) {
                    case SPRING -> "Planting";
                    case SUMMER -> "Swimming";
                    case FALL -> "Harvesting";
                    case WINTER -> "Skiing";
                };
                assertThat(activity).isNotBlank();
            }
        }
    }

    @Nested
    @DisplayName("Multiple Case Labels")
    class MultipleCaseLabelsTest {

        @Test
        @DisplayName("Multiple case labels share same result")
        void multipleCaseLabels() {
            int month = 3;
            String quarter = switch (month) {
                case 1, 2, 3 -> "Q1";
                case 4, 5, 6 -> "Q2";
                case 7, 8, 9 -> "Q3";
                case 10, 11, 12 -> "Q4";
                default -> "Invalid";
            };
            assertThat(quarter).isEqualTo("Q1");
        }
    }

    @Nested
    @DisplayName("Calculator with Switch Expression")
    class CalculatorTest {

        private double calculate(double a, double b, String op) {
            return switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> {
                    if (b == 0) throw new ArithmeticException("Division by zero");
                    yield a / b;
                }
                default -> throw new IllegalArgumentException("Unknown operator: " + op);
            };
        }

        @Test
        @DisplayName("Calculator handles addition")
        void calculatorAdd() {
            assertThat(calculate(10, 5, "+")).isEqualTo(15.0);
        }

        @Test
        @DisplayName("Calculator handles subtraction")
        void calculatorSubtract() {
            assertThat(calculate(10, 5, "-")).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Calculator handles multiplication")
        void calculatorMultiply() {
            assertThat(calculate(10, 5, "*")).isEqualTo(50.0);
        }

        @Test
        @DisplayName("Calculator handles division")
        void calculatorDivide() {
            assertThat(calculate(10, 5, "/")).isEqualTo(2.0);
        }

        @Test
        @DisplayName("Calculator throws on division by zero")
        void calculatorDivisionByZero() {
            assertThatThrownBy(() -> calculate(10, 0, "/"))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("Calculator throws on unknown operator")
        void calculatorUnknownOperator() {
            assertThatThrownBy(() -> calculate(10, 5, "%"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("RealWorldExample - Restaurant Order Processing")
    class RestaurantOrderTest {

        enum OrderType { DINE_IN, TAKEOUT, DELIVERY, CATERING }

        @Test
        @DisplayName("Service fee varies by order type")
        void serviceFeeByOrderType() {
            double subtotal = 100.0;
            for (OrderType type : OrderType.values()) {
                double fee = switch (type) {
                    case DINE_IN -> subtotal * 0.18;
                    case TAKEOUT -> subtotal * 0.05;
                    case DELIVERY -> subtotal * 0.10;
                    case CATERING -> subtotal * 0.20;
                };
                assertThat(fee).isGreaterThan(0);
            }
        }

        @Test
        @DisplayName("Dine-in service fee is 18%")
        void dineInFee() {
            double subtotal = 100.0;
            double fee = switch (OrderType.DINE_IN) {
                case DINE_IN -> subtotal * 0.18;
                case TAKEOUT -> subtotal * 0.05;
                case DELIVERY -> subtotal * 0.10;
                case CATERING -> subtotal * 0.20;
            };
            assertThat(fee).isEqualTo(18.0);
        }
    }
}
