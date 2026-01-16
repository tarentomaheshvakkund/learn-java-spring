package com.learning.javalearning.step5_advanced_patterns;

/**
 * JAVA 21: ADVANCED RECORD PATTERNS (JEP 440)
 * 
 * Record patterns allow you to "destructure" records directly in switch/if
 * statements.
 * This is similar to destructuring in JavaScript or pattern matching in Scala.
 */
public class NestedRecordPatternExample {

    // Define some records
    record Point(int x, int y) {
    }

    record Circle(Point center, int radius) {
    }

    record Rectangle(Point topLeft, Point bottomRight) {
    }

    record ColoredShape(String color, Object shape) {
    }

    public static void main(String[] args) {
        System.out.println("=== JAVA 21: Nested Record Patterns ===\n");

        // 1. Basic record pattern
        basicRecordPattern();

        // 2. Nested record pattern (the power move!)
        nestedRecordPattern();

        // 3. Combining with sealed types
        sealedTypePatterns();
    }

    static void basicRecordPattern() {
        System.out.println("--- 1. Basic Record Pattern ---");

        Point p = new Point(10, 20);

        // OLD WAY: Extract manually
        if (p instanceof Point) {
            Point point = (Point) p;
            int x = point.x();
            int y = point.y();
            System.out.println("Old way: x=" + x + ", y=" + y);
        }

        // JAVA 21 WAY: Destructure directly!
        if (p instanceof Point(int x, int y)) {
            System.out.println("New way: x=" + x + ", y=" + y);
        }

        System.out.println();
    }

    static void nestedRecordPattern() {
        System.out.println("--- 2. Nested Record Pattern ---");

        Circle circle = new Circle(new Point(5, 10), 25);

        // OLD WAY: Multiple steps
        if (circle instanceof Circle c) {
            Point center = c.center();
            int x = center.x();
            int y = center.y();
            int r = c.radius();
            System.out.println("Old: center=(" + x + "," + y + "), radius=" + r);
        }

        // JAVA 21 WAY: Destructure nested records in one shot!
        if (circle instanceof Circle(Point(int x, int y), int r)) {
            System.out.println("New: center=(" + x + "," + y + "), radius=" + r);
        }

        System.out.println();
    }

    static void sealedTypePatterns() {
        System.out.println("--- 3. Switch with Nested Patterns ---");

        Object[] shapes = {
                new Circle(new Point(0, 0), 10),
                new Rectangle(new Point(0, 0), new Point(100, 50)),
                new ColoredShape("red", new Circle(new Point(5, 5), 15))
        };

        for (Object shape : shapes) {
            String description = switch (shape) {
                // Destructure Circle and its nested Point
                case Circle(Point(int x, int y), int r) ->
                    "Circle at (" + x + "," + y + ") with radius " + r;

                // Destructure Rectangle with two Points
                case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) ->
                    "Rectangle from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")";

                // Deep nesting: ColoredShape containing a Circle
                case ColoredShape(String color, Circle(Point(int x, int y), int r)) ->
                    color + " circle at (" + x + "," + y + ") with radius " + r;

                // Catch-all
                default -> "Unknown shape";
            };
            System.out.println(description);
        }

        System.out.println();
    }
}
