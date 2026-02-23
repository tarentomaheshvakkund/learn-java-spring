package com.learning.javalearning.step13_java14_switch_expressions;

import java.util.logging.Logger;

/**
 * STEP 13: SWITCH EXPRESSIONS (Java 14)
 *
 * Switch expressions are a major evolution of the traditional switch statement.
 *
 * KEY DIFFERENCES:
 * - Switch STATEMENT: Executes code, doesn't return a value
 * - Switch EXPRESSION: Returns a value, can be assigned to a variable
 *
 * BENEFITS:
 * 1. No fall-through bugs (no need for break statements with arrow syntax)
 * 2. Returns a value directly
 * 3. Exhaustiveness checking (compiler ensures all cases covered)
 * 4. Can use in expressions (assignments, return statements, method arguments)
 *
 * Java Version: 14 (Preview in 12-13, Standard in 14)
 */
public class SwitchExpressionBasics {

  private static final Logger logger = Logger.getLogger(SwitchExpressionBasics.class.getName());

  private SwitchExpressionBasics() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    demonstrateOldVsNewSwitch();
    demonstrateArrowSyntax();
    demonstrateYieldKeyword();
    demonstrateMultipleLabels();
    demonstrateExhaustiveness();
  }

  /**
   * 1. OLD WAY vs NEW WAY
   * Traditional switch statement vs modern switch expression
   */
  private static void demonstrateOldVsNewSwitch() {
    logger.info("=== OLD vs NEW Switch ===");

    Day day = Day.MONDAY;

    // OLD WAY: Switch Statement (verbose, error-prone)
    String typeOfDayOld;
    switch (day) {
      case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY:
        typeOfDayOld = "Weekday";
        break;  // Forget this? Bug!
      case SATURDAY, SUNDAY:
        typeOfDayOld = "Weekend";
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + day);
    }
    logger.info(() -> "Old way: " + typeOfDayOld);

    // NEW WAY: Switch Expression (concise, safe)
    String typeOfDayNew = switch (day) {
      case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
      case SATURDAY, SUNDAY -> "Weekend";
    };
    logger.info(() -> "New way: " + typeOfDayNew);
    logger.info("");
  }

  /**
   * 2. ARROW SYNTAX (->)
   * No fall-through, no break needed
   * Can have single expression or block
   */
  private static void demonstrateArrowSyntax() {
    logger.info("=== Arrow Syntax ===");

    Month month = Month.FEBRUARY;

    // Single expression (no braces needed)
    int days = switch (month) {
      case JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER -> 31;
      case APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30;
      case FEBRUARY -> 28; // Simplified for demo
    };
    logger.info(() -> month + " has " + days + " days");

    // Block with multiple statements (braces required)
    String description = switch (month) {
      case DECEMBER, JANUARY, FEBRUARY -> {
        String season = "Winter";
        String temp = "Cold";
        yield season + " - " + temp; // yield returns the value
      }
      case MARCH, APRIL, MAY -> {
        String season = "Spring";
        String temp = "Mild";
        yield season + " - " + temp;
      }
      case JUNE, JULY, AUGUST -> {
        String season = "Summer";
        String temp = "Hot";
        yield season + " - " + temp;
      }
      case SEPTEMBER, OCTOBER, NOVEMBER -> {
        String season = "Fall";
        String temp = "Cool";
        yield season + " - " + temp;
      }
    };
    logger.info(() -> month + ": " + description);
    logger.info("");
  }

  /**
   * 3. YIELD KEYWORD
   * Used to return a value from a block in switch expression
   * Similar to return, but for switch expressions
   */
  private static void demonstrateYieldKeyword() {
    logger.info("=== Yield Keyword ===");

    int score = 85;

    // Complex logic with yield
    String grade = switch (score / 10) {
      case 10, 9 -> "A";  // Simple expression, no yield needed
      case 8 -> "B";
      case 7 -> "C";
      case 6 -> "D";
      default -> {
        // Complex block logic
        if (score < 0 || score > 100) {
          logger.info("Invalid score detected!");
          yield "Invalid";
        } else {
          logger.info("Score is below passing grade");
          yield "F";
        }
      }
    };
    logger.info(() -> "Score " + score + " = Grade " + grade);
    logger.info("");
  }

  /**
   * 4. MULTIPLE LABELS
   * Combine multiple case labels with comma separator
   */
  private static void demonstrateMultipleLabels() {
    logger.info("=== Multiple Labels ===");

    char grade = 'B';

    String message = switch (grade) {
      case 'A', 'a' -> "Excellent!";
      case 'B', 'b' -> "Good job!";
      case 'C', 'c' -> "Satisfactory";
      case 'D', 'd' -> "Needs improvement";
      case 'F', 'f' -> "Failed";
      default -> "Invalid grade";
    };
    logger.info(() -> "Grade " + grade + ": " + message);
    logger.info("");
  }

  /**
   * 5. EXHAUSTIVENESS
   * Compiler ensures all possible values are covered
   * Especially useful with enums
   */
  private static void demonstrateExhaustiveness() {
    logger.info("=== Exhaustiveness ===");

    Status status = Status.PENDING;

    // With enums, if you cover all cases, no default needed!
    String action = switch (status) {
      case PENDING -> "Waiting for approval";
      case APPROVED -> "Processing order";
      case REJECTED -> "Contact customer";
      case COMPLETED -> "Archive record";
      // No default needed - compiler knows all cases covered
    };
    logger.info(() -> "Status " + status + ": " + action);

    // If enum changes (new value added), compiler will force you to handle it!
    // This prevents bugs from unhandled cases
    logger.info("");
  }

  // Enums for examples
  enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
  }

  enum Month {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
    JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
  }

  enum Status {
    PENDING, APPROVED, REJECTED, COMPLETED
  }
}
