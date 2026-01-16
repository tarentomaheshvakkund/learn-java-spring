# Step 5: Advanced Record Patterns (Java 21)

## Nested Record Patterns

Destructure nested records in a single expression:

```java
record Point(int x, int y) {}
record Circle(Point center, int radius) {}

Circle c = new Circle(new Point(5, 10), 25);

// Destructure EVERYTHING in one shot
if (c instanceof Circle(Point(int x, int y), int r)) {
    System.out.println("Center: " + x + "," + y + " Radius: " + r);
}
```

## Guarded Patterns

Add conditions with `when`:

```java
record Person(String name, int age) {}

String category = switch (person) {
    case Person(String n, int a) when a < 18 -> n + " is a minor";
    case Person(String n, int a) when a < 65 -> n + " is an adult";
    case Person(String n, int _) -> n + " is a senior";
};
```

## Underscore Pattern (`_`)

Use `_` when you don't need a variable:

```java
case Person(String name, int _) -> name + " (age ignored)";
```

## Run Examples

```bash
cd src/main/java
java com/learning/javalearning/step5_advanced_patterns/NestedRecordPatternExample.java
java com/learning/javalearning/step5_advanced_patterns/GuardedPatternExample.java
```

## Next: [Step 6 - String Templates](../step6_string_templates/walkthrough.md)
