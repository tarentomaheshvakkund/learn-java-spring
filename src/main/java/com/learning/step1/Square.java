package com.learning.step1;

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
