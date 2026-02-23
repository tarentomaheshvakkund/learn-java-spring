package com.learning.javalearning.step13_java14_switch_expressions;

import java.util.logging.Logger;

/**
 * STEP 13: ADVANCED SWITCH EXPRESSIONS
 *
 * Advanced patterns and real-world use cases for switch expressions
 *
 * COVERED TOPICS:
 * 1. Switch expressions in method returns
 * 2. Nested switch expressions
 * 3. Switch with Strings
 * 4. Calculator example (practical use case)
 * 5. State machine implementation
 */
public class SwitchExpressionAdvanced {

  private static final Logger logger = Logger.getLogger(SwitchExpressionAdvanced.class.getName());
  private static final String PROCESSING_PAYMENT_MSG = "Processing payment: ";

  private SwitchExpressionAdvanced() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    demonstrateMethodReturns();
    demonstrateNestedSwitch();
    demonstrateStringSwitch();
    demonstrateCalculator();
    demonstrateStateMachine();
  }

  /**
   * 1. SWITCH IN METHOD RETURNS
   * Clean, single-expression method bodies
   */
  private static void demonstrateMethodReturns() {
    logger.info("=== Switch in Method Returns ===");

    logger.info(() -> PROCESSING_PAYMENT_MSG + processPayment(PaymentMethod.CREDIT_CARD));
    logger.info(() -> PROCESSING_PAYMENT_MSG + processPayment(PaymentMethod.PAYPAL));
    logger.info(() -> PROCESSING_PAYMENT_MSG + processPayment(PaymentMethod.CRYPTO));

    logger.info("");
  }

  // Entire method is single switch expression!
  private static String processPayment(PaymentMethod method) {
    return switch (method) {
      case CREDIT_CARD -> {
        // Complex processing
        double fee = 2.5;
        yield "Processed via credit card (fee: " + fee + "%)";
      }
      case DEBIT_CARD -> "Processed via debit card (no fee)";
      case PAYPAL -> "Redirecting to PayPal...";
      case CRYPTO -> "Waiting for blockchain confirmation...";
      case CASH -> "Payment received in cash";
    };
  }

  /**
   * 2. NESTED SWITCH EXPRESSIONS
   * Switch inside switch for complex decision trees
   */
  private static void demonstrateNestedSwitch() {
    logger.info("=== Nested Switch Expressions ===");

    UserRole role = UserRole.ADMIN;
    Action action = Action.DELETE;

    boolean allowed = switch (role) {
      case ADMIN -> switch (action) {
        case READ, WRITE, DELETE -> true;
      };
      case EDITOR -> switch (action) {
        case READ, WRITE -> true;
        case DELETE -> false;
      };
      case VIEWER -> switch (action) {
        case READ -> true;
        case WRITE, DELETE -> false;
      };
    };

    logger.info(() -> role + " attempting " + action + ": " +
        (allowed ? "ALLOWED" : "DENIED"));

    logger.info("");
  }

  /**
   * 3. SWITCH WITH STRINGS
   * Pattern matching-like behavior with strings
   */
  private static void demonstrateStringSwitch() {
    logger.info("=== Switch with Strings ===");

    String command = "start";

    String response = switch (command.toLowerCase()) {
      case "start", "begin", "go" -> "Starting the process...";
      case "stop", "end", "quit" -> "Stopping the process...";
      case "pause", "wait" -> "Pausing the process...";
      case "resume", "continue" -> "Resuming the process...";
      case "status", "info" -> "Process is running";
      default -> "Unknown command: " + command;
    };

    logger.info(() -> "Command '" + command + "': " + response);

    logger.info("");
  }

  /**
   * 4. CALCULATOR EXAMPLE
   * Practical use case: mathematical operations
   */
  private static void demonstrateCalculator() {
    logger.info("=== Calculator with Switch Expression ===");

    logger.info(() -> "10 + 5 = " + calculate(10, 5, Operator.ADD));
    logger.info(() -> "10 - 5 = " + calculate(10, 5, Operator.SUBTRACT));
    logger.info(() -> "10 * 5 = " + calculate(10, 5, Operator.MULTIPLY));
    logger.info(() -> "10 / 5 = " + calculate(10, 5, Operator.DIVIDE));
    logger.info(() -> "10 ^ 2 = " + calculate(10, 2, Operator.POWER));

    try {
      calculate(10, 0, Operator.DIVIDE);
    } catch (ArithmeticException e) {
      logger.info(() -> "Error: " + e.getMessage());
    }

    logger.info("");
  }

  private static double calculate(double a, double b, Operator op) {
    return switch (op) {
      case ADD -> a + b;
      case SUBTRACT -> a - b;
      case MULTIPLY -> a * b;
      case DIVIDE -> {
        if (b == 0) {
          throw new ArithmeticException("Division by zero");
        }
        yield a / b;
      }
      case MODULO -> {
        if (b == 0) {
          throw new ArithmeticException("Modulo by zero");
        }
        yield a % b;
      }
      case POWER -> Math.pow(a, b);
    };
  }

  /**
   * 5. STATE MACHINE
   * Implement state transitions with switch expressions
   */
  private static void demonstrateStateMachine() {
    logger.info("=== State Machine with Switch Expression ===");

    OrderState currentState = OrderState.CREATED;

    // Simulate state transitions
    currentState = transition(currentState, Event.PAYMENT_RECEIVED);
    OrderState finalCurrentState = currentState;
    logger.info(() -> "After payment: " + finalCurrentState);

    currentState = transition(currentState, Event.ITEMS_SHIPPED);
    OrderState finalCurrentState1 = currentState;
    logger.info(() -> "After shipping: " + finalCurrentState1);

    currentState = transition(currentState, Event.DELIVERED);
    OrderState finalCurrentState2 = currentState;
    logger.info(() -> "After delivery: " + finalCurrentState2);

    // Try invalid transition
    try {
      transition(currentState, Event.PAYMENT_RECEIVED);
    } catch (IllegalStateException e) {
      logger.info(() -> "Error: " + e.getMessage());
    }

    logger.info("");
  }

  private static OrderState transition(OrderState current, Event event) {
    return switch (current) {
      case CREATED -> switch (event) {
        case PAYMENT_RECEIVED -> OrderState.PAID;
        case CANCELLED -> OrderState.CANCELLED;
        default -> throw new IllegalStateException(
            "Invalid transition from " + current + " with event " + event);
      };
      case PAID -> switch (event) {
        case ITEMS_SHIPPED -> OrderState.SHIPPED;
        case CANCELLED -> OrderState.CANCELLED;
        default -> throw new IllegalStateException(
            "Invalid transition from " + current + " with event " + event);
      };
      case SHIPPED -> switch (event) {
        case DELIVERED -> OrderState.DELIVERED;
        default -> throw new IllegalStateException(
            "Invalid transition from " + current + " with event " + event);
      };
      case DELIVERED, CANCELLED -> throw new IllegalStateException(
          "Order in terminal state: " + current);
    };
  }

  // Enums for examples
  enum PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, PAYPAL, CRYPTO, CASH
  }

  enum UserRole {
    ADMIN, EDITOR, VIEWER
  }

  enum Action {
    READ, WRITE, DELETE
  }

  enum Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, POWER
  }

  enum OrderState {
    CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
  }

  enum Event {
    PAYMENT_RECEIVED, ITEMS_SHIPPED, DELIVERED, CANCELLED
  }
}
