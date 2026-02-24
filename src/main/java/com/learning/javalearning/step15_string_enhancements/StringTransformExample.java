package com.learning.javalearning.step15_string_enhancements;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * String.transform(Function<String, R>) - Java 12
 *
 * Applies a function to transform the string and returns the result.
 * Enables fluent, chainable string operations.
 *
 * Benefits:
 * - Method chaining for string operations
 * - Cleaner than nested function calls
 * - Reusable transformation functions
 * - Functional programming style
 */
public class StringTransformExample {

    private static final Logger logger = Logger.getLogger(StringTransformExample.class.getName());
    private static final String ARROW = " \u2192 ";

    public static void main(String[] args) {
        logger.info("=== String.transform() Examples ===");

        basicTransform();
        logger.info(() -> "=".repeat(50));

        chainingTransforms();
        logger.info(() -> "=".repeat(50));

        reusableTransformers();
        logger.info(() -> "=".repeat(50));

        conditionalTransform();
        logger.info(() -> "=".repeat(50));

        realWorldExamples();
    }

    /**
     * Basic transform examples
     */
    private static void basicTransform() {
        logger.info("1. BASIC TRANSFORM");

        String original = "hello world";

        // Transform to uppercase
        String upper = original.transform(String::toUpperCase);
        logger.info(() -> "Uppercase: " + upper);

        // Transform to get length
        Integer length = original.transform(String::length);
        logger.info(() -> "Length: " + length);

        // Transform with lambda
        String reversed = original.transform(s -> new StringBuilder(s).reverse().toString());
        logger.info(() -> "Reversed: " + reversed);

        // Transform to boolean
        Boolean isEmpty = original.transform(String::isEmpty);
        logger.info(() -> "Is empty: " + isEmpty);
    }

    /**
     * Chaining multiple transforms
     */
    private static void chainingTransforms() {
        logger.info("2. CHAINING TRANSFORMS");

        String input = "  hello world  ";

        // Old way - nested function calls (hard to read)
        String oldWay = String.format("Result: %s",
                        input.strip().toUpperCase().replace(" ", "_"));
        logger.info(() -> "Old way: " + oldWay);

        // New way - fluent chaining with transform
        String newWay = input
            .transform(String::strip)
            .transform(String::toUpperCase)
            .transform(s -> s.replace(" ", "_"))
            .transform(s -> "Result: " + s);
        logger.info(() -> "New way: " + newWay);

        // Complex transformation chain
        String email = "John.Doe@EXAMPLE.COM";
        String normalized = email
            .transform(String::toLowerCase)
            .transform(s -> s.split("@")[0])
            .transform(s -> s.replace(".", " "))
            .transform(StringTransformExample::capitalize);

        logger.info(() -> "Email to name: " + email + ARROW + normalized);
    }

    /**
     * Reusable transformation functions
     */
    private static void reusableTransformers() {
        logger.info("3. REUSABLE TRANSFORMERS");

        // Define reusable transformers
        Function<String, String> normalize = s -> s.strip().toLowerCase();
        Function<String, String> toSnakeCase = s -> s.replace(" ", "_");
        Function<String, String> addPrefix = s -> "user_" + s;
        Function<String, String> truncate = s -> s.length() > 10 ? s.substring(0, 10) + "..." : s;

        String input1 = "  John Doe  ";
        String input2 = "  Jane Smith  ";

        String result1 = input1
            .transform(normalize)
            .transform(toSnakeCase)
            .transform(addPrefix);

        String result2 = input2
            .transform(normalize)
            .transform(toSnakeCase)
            .transform(addPrefix);

        logger.info(() -> "Transformed: " + input1.strip() + ARROW + result1);
        logger.info(() -> "Transformed: " + input2.strip() + ARROW + result2);

        // With truncation
        String longName = "Christopher Alexander Montgomery";
        String truncated = longName
            .transform(normalize)
            .transform(truncate);
        logger.info(() -> "Truncated: " + longName + ARROW + truncated);
    }

    /**
     * Conditional transformations
     */
    private static void conditionalTransform() {
        logger.info("4. CONDITIONAL TRANSFORM");

        Function<String, String> sanitizeHtml = s ->
            s.replace("<", "&lt;")
             .replace(">", "&gt;")
             .replace("&", "&amp;");

        Function<String, String> conditionalSanitize = s ->
            s.contains("<") || s.contains(">") ?
                s.transform(sanitizeHtml) : s;

        String safe = "Hello World";
        String unsafe = "<script>alert('xss')</script>";

        logger.info(() -> "Safe input: " + safe);
        logger.info(() -> "After transform: " + safe.transform(conditionalSanitize));

        logger.info(() -> "Unsafe input: " + unsafe);
        logger.info(() -> "After transform: " + unsafe.transform(conditionalSanitize));
    }

    /**
     * Real-world use cases
     */
    private static void realWorldExamples() {
        logger.info("5. REAL-WORLD EXAMPLES");

        // URL slug generation
        String title = "How to Learn Java 21 Features!";
        String slug = title
            .transform(String::toLowerCase)
            .transform(s -> s.replaceAll("[^a-z0-9\\s-]", ""))
            .transform(String::strip)
            .transform(s -> s.replaceAll("\\s+", "-"));
        logger.info(() -> "Title: " + title);
        logger.info(() -> "Slug: " + slug);

        // Phone number formatting
        String rawPhone = "1234567890";
        String formattedPhone = rawPhone
            .transform(s -> String.format("(%s) %s-%s",
                s.substring(0, 3),
                s.substring(3, 6),
                s.substring(6)));
        logger.info(() -> "Raw phone: " + rawPhone);
        logger.info(() -> "Formatted: " + formattedPhone);

        // Password strength indicator
        String password = "MyP@ssw0rd!";
        String strength = password
            .transform(StringTransformExample::calculatePasswordStrength);
        logger.info(() -> "Password: " + password);
        logger.info(() -> "Strength: " + strength);

        // CSV field escaping
        String csvField = "Hello, \"World\"";
        String escaped = csvField
            .transform(s -> s.replace("\"", "\"\""))
            .transform(s -> "\"" + s + "\"");
        logger.info(() -> "CSV field: " + csvField);
        logger.info(() -> "Escaped: " + escaped);
    }

    /**
     * Utility methods for transformations
     */

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private static String calculatePasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;

        return switch (score) {
            case 0, 1, 2 -> "Weak";
            case 3, 4 -> "Medium";
            case 5, 6 -> "Strong";
            default -> "Very Strong";
        };
    }

    /**
     * Transformation library - reusable transformers
     */
    public static class StringTransformers {

        public static final Function<String, String> NORMALIZE =
            s -> s.strip().toLowerCase();

        public static final Function<String, String> TO_SNAKE_CASE =
            s -> s.replaceAll("\\s+", "_").toLowerCase();

        public static final Function<String, String> TO_KEBAB_CASE =
            s -> s.replaceAll("\\s+", "-").toLowerCase();

        public static final Function<String, String> TO_CAMEL_CASE = s -> {
            String[] words = s.split("\\s+");
            if (words.length == 0) return s;

            StringBuilder result = new StringBuilder(words[0].toLowerCase());
            for (int i = 1; i < words.length; i++) {
                result.append(capitalize(words[i]));
            }
            return result.toString();
        };

        public static final Function<String, String> REMOVE_SPECIAL_CHARS =
            s -> s.replaceAll("[^a-zA-Z0-9\\s]", "");

        public static Function<String, String> truncate(int maxLength) {
            return s -> s.length() > maxLength ?
                s.substring(0, maxLength) + "..." : s;
        }

        public static Function<String, String> wrap(String prefix, String suffix) {
            return s -> prefix + s + suffix;
        }
    }

    /**
     * Builder pattern with transform
     */
    public static class StringProcessor {
        private String value;

        public StringProcessor(String value) {
            this.value = value;
        }

        public StringProcessor apply(Function<String, String> transformer) {
            this.value = value.transform(transformer);
            return this;
        }

        public String build() {
            return value;
        }

        public static StringProcessor of(String value) {
            return new StringProcessor(value);
        }
    }
}
