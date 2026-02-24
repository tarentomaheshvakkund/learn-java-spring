package com.learning.javalearning.step15_string_enhancements;

import java.util.logging.Logger;

/**
 * String.indent(int n) - Java 12
 *
 * Adjusts the indentation of each line in a multi-line string.
 * - Positive n: adds n spaces to each line
 * - Negative n: removes up to n spaces from each line
 * - Zero: normalizes line terminators without changing indentation
 *
 * Use Cases:
 * - Code generation with proper formatting
 * - SQL query building
 * - JSON/XML pretty printing
 * - Dynamic template rendering
 */
public class StringIndentExample {

    private StringIndentExample() {
        // Utility class - prevent instantiation
    }

    private static final Logger logger = Logger.getLogger(StringIndentExample.class.getName());
    private static final String SEPARATOR = "=".repeat(50);

    public static void main(String[] args) {
        logger.info("=== String.indent() Examples ===\n");

        basicIndentation();
        logger.info(SEPARATOR);

        codeGeneration();
        logger.info(SEPARATOR);

        sqlQueryFormatting();
        logger.info(SEPARATOR);

        dedentation();
        logger.info(SEPARATOR);

        jsonFormatting();
    }

    /**
     * Basic indentation examples
     */
    private static void basicIndentation() {
        logger.info("1. BASIC INDENTATION");

        String text = "Hello\nWorld\nJava";

        logger.info("Original:");
        logger.info(text);

        logger.info("Indent by 4 spaces:");
        String indented4 = text.indent(4);
        logger.info(indented4);

        logger.info("Indent by 8 spaces:");
        String indented8 = text.indent(8);
        logger.info(indented8);
    }

    /**
     * Code generation with proper indentation
     */
    private static void codeGeneration() {
        logger.info("2. CODE GENERATION");

        String methodBody = """
            System.out.println("Hello");
            return 42;
            """;

        String method = "public int example() {\n" +
                        methodBody.indent(4) +
                        "}";

        logger.info("Generated method:");
        logger.info(method);

        // Nested class generation
        String innerMethod = """
            int x = 10;
            return x * 2;
            """;

        String innerClass = """
            private static class Inner {
            """ +
            "    private int compute() {\n" +
            innerMethod.indent(8) +
            "    }\n" +
            "}";

        logger.info("Generated nested class:");
        logger.info(innerClass);
    }

    /**
     * SQL query formatting with indentation
     */
    private static void sqlQueryFormatting() {
        logger.info("3. SQL QUERY FORMATTING");

        String selectClause = "SELECT id, name, email";
        String fromClause = "FROM users";
        String whereClause = "WHERE active = true";
        String orderClause = "ORDER BY name ASC";

        String query = selectClause + "\n" +
                      fromClause.indent(2) +
                      whereClause.indent(2) +
                      orderClause.indent(2);

        logger.info("Formatted SQL:");
        logger.info(query);

        // Complex query with subquery
        String subquery = """
            SELECT user_id
            FROM orders
            WHERE total > 100
            """;

        String complexQuery = """
            SELECT *
            FROM users
            WHERE id IN (
            """ +
            subquery.indent(4) +
            ")";

        logger.info("Complex SQL with subquery:");
        logger.info(complexQuery);
    }

    /**
     * Removing indentation (dedentation)
     */
    private static void dedentation() {
        logger.info("4. DEDENTATION (Negative Indent)");

        String indentedCode = """
                public void method() {
                    System.out.println("test");
                }
            """;

        logger.info("Original (heavily indented):");
        logger.info(indentedCode);

        logger.info("Remove 4 spaces (indent -4):");
        String dedented = indentedCode.indent(-4);
        logger.info(dedented);

        logger.info("Remove 8 spaces (indent -8):");
        String moreDedented = indentedCode.indent(-8);
        logger.info(moreDedented);
    }

    /**
     * JSON formatting example
     */
    private static void jsonFormatting() {
        logger.info("5. JSON FORMATTING");

        String userFields = """
            "id": 123,
            "name": "John Doe",
            "email": "john@example.com"
            """;

        String addressFields = """
            "street": "123 Main St",
            "city": "Springfield",
            "zip": "12345"
            """;

        String json = "{\n" +
                     userFields.indent(2) +
                     "  \"address\": {\n" +
                     addressFields.indent(4) +
                     "  }\n" +
                     "}";

        logger.info("Formatted JSON:");
        logger.info(json);
    }
}
