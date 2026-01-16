package com.learning.javalearning.step1_basics;

/**
 * STEP 1: SEALED CLASSES (Java 17+)
 * 
 * Sealed classes and interfaces allow you to restrict which classes/interfaces
 * can extend or implement them.
 * 
 * This provides better control over your domain model and allows the compiler
 * to check for "exhaustiveness" in switch expressions (Step 2).
 */
public sealed interface Shape permits Circle, Rectangle, Square {
  double calculateArea();
}
