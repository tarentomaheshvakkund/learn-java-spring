package com.learning.javalearning.step2_pattern_matching;

import com.learning.javalearning.step1_basics.Shape;
import com.learning.javalearning.step1_basics.UserRecord;

/**
 * STEP 2: PATTERN MATCHING FOR SWITCH (Java 21)
 * 
 * Pattern matching for switch allows you to test variables against
 * types and patterns directly in labels.
 */
public class SwitchPatternExample {

  public String describeShape(Object obj) {
    // We can switch on Object and match against specific types!
    return switch (obj) {
      case Integer i -> String.format("It's a number: %d", i);
      case String s -> String.format("It's a string of length %d", s.length());
      case UserRecord user -> String.format("It's user %s", user.username());
      case null -> "It's a null value"; // Dedicated null handling!
      default -> "It's something else";
    };
  }

  public double getArea(Shape shape) {
    // Because Shape is 'sealed' (from Step 1), the compiler knows
    // there are ONLY 3 possible types.
    // IF we cover all of them, we don't need a 'default' case!
    return switch (shape) {
      case com.learning.javalearning.step1_basics.Circle c -> Math.PI * Math.pow(c.calculateArea() / Math.PI, 1); // Simplistic reuse
      case com.learning.javalearning.step1_basics.Rectangle r -> r.calculateArea();
      case com.learning.javalearning.step1_basics.Square s -> s.calculateArea();
      // No default needed! If we add a new Shape permit, this will fail to compile.
    };
  }
}
