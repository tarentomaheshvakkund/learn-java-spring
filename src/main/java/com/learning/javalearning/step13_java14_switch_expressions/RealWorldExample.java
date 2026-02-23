package com.learning.javalearning.step13_java14_switch_expressions;

import java.time.DayOfWeek;
import java.util.logging.Logger;
import java.time.LocalTime;

/**
 * STEP 13: REAL-WORLD EXAMPLE
 *
 * Complete example: Restaurant Order Processing System
 *
 * This demonstrates how switch expressions improve real production code:
 * - Order pricing with complex business rules
 * - Dynamic delivery time estimation
 * - Tax calculation by location
 * - Discount application
 * - Order validation
 */
public class RealWorldExample {
  private static final Logger logger = Logger.getLogger(RealWorldExample.class.getName());

  private RealWorldExample() {
    // Private constructor to prevent instantiation
    throw new IllegalStateException("Utility class");
  }

  public static void main(String[] args) {
    logger.info("=== Restaurant Order Processing System ===");

    // Create sample orders
    Order order1 = new Order(
        OrderType.DINE_IN,
        MealType.LUNCH,
        Location.DOWNTOWN,
        DayOfWeek.MONDAY,
        CustomerTier.GOLD,
        150.00
    );

    Order order2 = new Order(
        OrderType.DELIVERY,
        MealType.DINNER,
        Location.SUBURBS,
        DayOfWeek.FRIDAY,
        CustomerTier.PLATINUM,
        250.00
    );

    // Process orders
    processOrder(order1);
    logger.info("");
    processOrder(order2);
  }

  private static void processOrder(Order order) {
    logger.info("Processing Order:");
    logger.info("Type: " + order.type);
    logger.info("Subtotal: $" + String.format("%.2f", order.subtotal));

    // Calculate fees using switch expressions
    double serviceFee = calculateServiceFee(order);
    double deliveryFee = calculateDeliveryFee(order);
    double tax = calculateTax(order);
    double discount = calculateDiscount(order);

    double total = order.subtotal + serviceFee + deliveryFee + tax - discount;

    // Display breakdown
    if (serviceFee > 0) {
      logger.info("Service Fee: $" + String.format("%.2f", serviceFee));
    }
    if (deliveryFee > 0) {
      logger.info("Delivery Fee: $" + String.format("%.2f", deliveryFee));
    }
    logger.info("Tax: $" + String.format("%.2f", tax));
    if (discount > 0) {
      logger.info("Discount: -$" + String.format("%.2f", discount));
    }
    logger.info("Total: $" + String.format("%.2f", total));

    // Estimate delivery time
    String eta = estimateDeliveryTime(order);
    logger.info("ETA: " + eta);

    // Validate order
    String validation = validateOrder(order);
    logger.info("Status: " + validation);
  }

  /**
   * Calculate service fee based on order type and meal type
   * Switch expression makes complex pricing rules clear
   */
  private static double calculateServiceFee(Order order) {
    return switch (order.type) {
      case DINE_IN -> switch (order.mealType) {
        case BREAKFAST -> 0.0;  // No service fee for breakfast
        case LUNCH -> order.subtotal * 0.10;  // 10% for lunch
        case DINNER -> order.subtotal * 0.15; // 15% for dinner
      };
      case TAKEOUT -> 0.0;  // No service fee
      case DELIVERY -> switch (order.mealType) {
        case BREAKFAST -> 3.99;
        case LUNCH -> 4.99;
        case DINNER -> 5.99;
      };
      case CATERING -> {
        // Complex calculation for catering
        double baseFee = 50.0;
        double percentageFee = order.subtotal * 0.20;
        yield baseFee + percentageFee;
      }
    };
  }

  /**
   * Calculate delivery fee based on location and day
   */
  private static double calculateDeliveryFee(Order order) {
    if (order.type != OrderType.DELIVERY) {
      return 0.0;
    }

    // Base fee by location
    double baseFee = switch (order.location) {
      case DOWNTOWN -> 2.99;
      case SUBURBS -> 5.99;
      case RURAL -> 9.99;
    };

    // Surge pricing on weekends
    double surgeFee = switch (order.dayOfWeek) {
      case SATURDAY, SUNDAY -> baseFee * 0.50;  // 50% surge
      case FRIDAY -> baseFee * 0.25;  // 25% surge on Friday evenings
      default -> 0.0;
    };

    return baseFee + surgeFee;
  }

  /**
   * Calculate tax based on location
   * Different tax rates for different jurisdictions
   */
  private static double calculateTax(Order order) {
    double taxRate = switch (order.location) {
      case DOWNTOWN -> 0.095;   // 9.5% city tax
      case SUBURBS -> 0.085;    // 8.5% county tax
      case RURAL -> 0.075;      // 7.5% state tax
    };

    return order.subtotal * taxRate;
  }

  /**
   * Calculate discount based on customer tier and order type
   * Nested switch expressions for complex discount rules
   */
  private static double calculateDiscount(Order order) {
    return switch (order.customerTier) {
      case BRONZE -> switch (order.type) {
        case DINE_IN, TAKEOUT -> order.subtotal * 0.05;  // 5%
        case DELIVERY -> 0.0;  // No discount on delivery
        case CATERING -> order.subtotal * 0.10;  // 10% for catering
      };
      case SILVER -> switch (order.type) {
        case DINE_IN, TAKEOUT -> order.subtotal * 0.10;  // 10%
        case DELIVERY -> order.subtotal * 0.05;  // 5%
        case CATERING -> order.subtotal * 0.15;  // 15%
      };
      case GOLD -> switch (order.type) {
        case DINE_IN, TAKEOUT -> order.subtotal * 0.15;  // 15%
        case DELIVERY -> order.subtotal * 0.10;  // 10%
        case CATERING -> order.subtotal * 0.20;  // 20%
      };
      case PLATINUM -> {
        // Premium customers get best discounts plus bonuses
        double baseDiscount = order.subtotal * 0.20;  // 20% base
        double bonusDiscount = order.subtotal > 200 ? 25.0 : 0.0;
        yield baseDiscount + bonusDiscount;
      }
    };
  }

  /**
   * Estimate delivery time based on multiple factors
   */
  private static String estimateDeliveryTime(Order order) {
    if (order.type != OrderType.DELIVERY) {
      return switch (order.type) {
        case DINE_IN -> "Served at table";
        case TAKEOUT -> "15-20 minutes";
        case CATERING -> "Contact catering manager";
        default -> "N/A";
      };
    }

    // Calculate delivery time
    int baseMinutes = switch (order.location) {
      case DOWNTOWN -> 20;
      case SUBURBS -> 35;
      case RURAL -> 50;
    };

    // Add time for peak hours
    int peakMinutes = switch (order.mealType) {
      case BREAKFAST -> 0;
      case LUNCH -> 10;  // Lunch rush
      case DINNER -> 15; // Dinner rush
    };

    // Weekend delays
    int weekendMinutes = switch (order.dayOfWeek) {
      case SATURDAY, SUNDAY -> 10;
      default -> 0;
    };

    int totalMinutes = baseMinutes + peakMinutes + weekendMinutes;
    return totalMinutes + "-" + (totalMinutes + 10) + " minutes";
  }

  /**
   * Validate order and return status message
   */
  private static String validateOrder(Order order) {
    // Minimum order check by type
    double minimumOrder = switch (order.type) {
      case DINE_IN -> 0.0;     // No minimum
      case TAKEOUT -> 10.0;     // $10 minimum
      case DELIVERY -> switch (order.location) {
        case DOWNTOWN -> 15.0;
        case SUBURBS -> 25.0;
        case RURAL -> 40.0;
      };
      case CATERING -> 200.0;   // $200 minimum
    };

    if (order.subtotal < minimumOrder) {
      return "REJECTED - Minimum order: $" + String.format("%.2f", minimumOrder);
    }

    // Service availability check
    boolean available = switch (order.type) {
      case DINE_IN, TAKEOUT -> true;  // Always available
      case DELIVERY -> switch (order.location) {
        case DOWNTOWN -> true;
        case SUBURBS -> true;
        case RURAL -> !isLateNight();  // No late night rural delivery
      };
      case CATERING -> !isLateNight() && order.subtotal >= 200;
    };

    if (!available) {
      return "REJECTED - Service not available";
    }

    return "APPROVED";
  }

  private static boolean isLateNight() {
    LocalTime now = LocalTime.now();
    return now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
  }

  // Domain classes
  record Order(
      OrderType type,
      MealType mealType,
      Location location,
      DayOfWeek dayOfWeek,
      CustomerTier customerTier,
      double subtotal
  ) {}

  enum OrderType {
    DINE_IN, TAKEOUT, DELIVERY, CATERING
  }

  enum MealType {
    BREAKFAST, LUNCH, DINNER
  }

  enum Location {
    DOWNTOWN, SUBURBS, RURAL
  }

  enum CustomerTier {
    BRONZE, SILVER, GOLD, PLATINUM
  }
}
