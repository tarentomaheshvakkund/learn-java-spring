package com.learning.javalearning.step13_java14_switch_expressions;
import java.util.logging.Logger;

/**
 * STEP 13: SWITCH STATEMENT vs SWITCH EXPRESSION
 *
 * Understanding when to use traditional colon syntax vs arrow syntax
 *
 * COLON SYNTAX (Traditional):
 * - Can still be used in switch expressions
 * - Requires 'yield' to return values
 * - Has fall-through behavior (need 'break')
 * - Use when migration from old code or fall-through needed
 *
 * ARROW SYNTAX (Modern):
 * - No fall-through, cleaner code
 * - No 'break' needed
 * - Preferred for new code
 *
 * This file shows BOTH styles so you understand the complete picture
 */
public class SwitchStatementVsExpression {
  private static final Logger logger = Logger.getLogger(SwitchStatementVsExpression.class.getName());

  private SwitchStatementVsExpression() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    demonstrateColonVsArrow();
    demonstrateWhenToUseFallThrough();
    demonstrateMixedSyntax();
    demonstrateYieldInColonSyntax();
  }

  /**
   * 1. COLON SYNTAX vs ARROW SYNTAX
   * Side-by-side comparison
   */
  private static void demonstrateColonVsArrow() {
    logger.info("=== Colon vs Arrow Syntax ===");

    TrafficLight light = TrafficLight.YELLOW;

    // COLON SYNTAX (traditional, still works in expressions)
    String actionColon = switch (light) {
      case RED:
        yield "Stop";  // Must use 'yield' in expression
      case YELLOW:
        yield "Caution";
      case GREEN:
        yield "Go";
    };
    logger.info(() -> "Colon syntax: " + actionColon);

    // ARROW SYNTAX (modern, preferred)
    String actionArrow = switch (light) {
      case RED -> "Stop";     // No 'yield' needed for simple expression
      case YELLOW -> "Caution";
      case GREEN -> "Go";
    };
    logger.info(() -> "Arrow syntax: " + actionArrow);

    logger.info("");
  }

  /**
   * 2. WHEN TO USE FALL-THROUGH
   * Rare cases where fall-through is intentional
   */
  private static void demonstrateWhenToUseFallThrough() {
    logger.info("=== Intentional Fall-Through ===");

    int month = 2; // February

    // Traditional switch statement with intentional fall-through
    // (This is now RARE but sometimes useful for side effects)
    switch (month) {
      case 1:
        logger.info("January sales report generated");
        // Fall-through to common year-end processing
      case 12:
        logger.info("Year-end bonus calculations");
        logger.info("Tax document preparation");
        break;
      case 2:
        logger.info("February budget review");
        break;
      default:
        logger.info("Regular monthly processing");
    }

    // Modern approach: Be explicit, no fall-through
    switch (month) {
      case 1 -> {
        logger.info("\nJanuary sales report generated");
        yearEndProcessing();
      }
      case 12 -> yearEndProcessing();
      case 2 -> logger.info("February budget review");
      default -> logger.info("Regular monthly processing");
    }

    logger.info("");
  }

  private static void yearEndProcessing() {
    logger.info("Year-end bonus calculations");
    logger.info("Tax document preparation");
  }

  /**
   * 3. MIXED SYNTAX (Not Recommended but Legal)
   * You CAN'T mix colon and arrow in same switch
   */
  private static void demonstrateMixedSyntax() {
    logger.info("=== Consistent Syntax Required ===");

    Priority priority = Priority.HIGH;

    // CORRECT: All arrow
    String messageArrow = switch (priority) {
      case CRITICAL -> "Handle immediately";
      case HIGH -> "Handle today";
      case MEDIUM -> "Handle this week";
      case LOW -> "Handle when possible";
    };
    logger.info(() -> "All arrow: " + messageArrow);

    // CORRECT: All colon
    String messageColon = switch (priority) {
      case CRITICAL:
        yield "Handle immediately";
      case HIGH:
        yield "Handle today";
      case MEDIUM:
        yield "Handle this week";
      case LOW:
        yield "Handle when possible";
    };
    logger.info(() -> "All colon: " + messageColon);

    logger.info("");
  }

  /**
   * 4. YIELD IN COLON SYNTAX
   * When using colon syntax in expressions, MUST use yield
   */
  private static void demonstrateYieldInColonSyntax() {
    logger.info("=== Yield in Colon Syntax ===");

    Season season = Season.SUMMER;

    // Colon syntax with complex logic
    String activities = switch (season) {
      case SPRING:
        String activity1 = "Planting gardens";
        String activity2 = "Spring cleaning";
        yield activity1 + ", " + activity2;  // Must use 'yield'

      case SUMMER:
        String activity3 = "Beach trips";
        String activity4 = "Outdoor sports";
        // Complex logic allowed
        if (IS_WEEKEND) {
          yield activity3 + ", " + activity4 + ", BBQ parties";
        } else {
          yield activity3 + ", " + activity4;
        }

      case FALL:
        yield "Leaf raking, Harvest festivals";

      case WINTER:
        yield "Skiing, Holiday shopping";
    };

    logger.info(() -> String.format("Season %s: %s", season, activities));

    // Same logic with arrow syntax (cleaner)
    String activitiesArrow = switch (season) {
      case SPRING -> "Planting gardens, Spring cleaning";
      case SUMMER -> {
        String activity3 = "Beach trips";
        String activity4 = "Outdoor sports";
        if (IS_WEEKEND) {
          yield activity3 + ", " + activity4 + ", BBQ parties";
        } else {
          yield activity3 + ", " + activity4;
        }
      }
      case FALL -> "Leaf raking, Harvest festivals";
      case WINTER -> "Skiing, Holiday shopping";
    };

    logger.info(() -> String.format("Arrow version: %s", activitiesArrow));

    logger.info("");
  }

  private static final boolean IS_WEEKEND = true; // Simplified for demo

  // Enums
  enum TrafficLight {
    RED, YELLOW, GREEN
  }

  enum Priority {
    CRITICAL, HIGH, MEDIUM, LOW
  }

  enum Season {
    SPRING, SUMMER, FALL, WINTER
  }
}
