package com.learning.javalearning.step1_basics;

public final class Rectangle implements Shape {
  private final double length, width;

  public Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }

  @Override
  public double calculateArea() {
    return length * width;
  }
}
