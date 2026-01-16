# Step 6: String Templates (Java 21 Preview)

> ⚠️ **Preview Feature**: String Templates require `--enable-preview` to run.

## What's New?

String Templates provide cleaner string interpolation, similar to JavaScript template literals or Python f-strings.

## Syntax Comparison

| Method | Example |
|--------|---------|
| Concatenation | `"Hello, " + name + "!"` |
| String.format() | `String.format("Hello, %s!", name)` |
| **String Template** | `STR."Hello, \{name}!"` |

## STR Processor

```java
String name = "Alice";
int age = 25;

// NEW: String Template with STR processor
String msg = STR."Hello, \{name}! You are \{age} years old.";

// Expressions work too!
String msg2 = STR."Next year you'll be \{age + 1}";
```

## Multi-line Templates

```java
String json = STR."""
    {
        "name": "\{name}",
        "age": \{age},
        "adult": \{age >= 18}
    }
    """;
```

## Running with Preview

```bash
# Compile
javac --enable-preview --source 21 StringTemplateExample.java

# Run
java --enable-preview StringTemplateExample
```

## Key Points

- **STR** is a template processor that interpolates values
- Use `\{expression}` to embed any Java expression
- Works with text blocks for multi-line templates
- Preview feature - syntax may change in future releases

## Run the Example

```bash
cd src/main/java
java --enable-preview --source 21 com/learning/javalearning/step6_string_templates/StringTemplateExample.java
```

## Back to: [Step 5 - Advanced Patterns](../step5_advanced_patterns/walkthrough.md)
