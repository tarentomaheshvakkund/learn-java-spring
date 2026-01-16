package com.learning.javalearning.step1_basics;

public final class Square implements Shape {
  private final double side;

  public Square(double side) {
    this.side = side;
  }

  @Override
  public double calculateArea() {
    return side * side;
  }
}
