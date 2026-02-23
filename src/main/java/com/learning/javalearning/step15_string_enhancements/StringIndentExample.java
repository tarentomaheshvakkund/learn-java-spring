package com.learning.javalearning.step15_string_enhancements;

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

    public static void main(String[] args) {
        System.out.println("=== String.indent() Examples ===\n");
        
        basicIndentation();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        codeGeneration();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        sqlQueryFormatting();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        dedentation();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        jsonFormatting();
    }

    /**
     * Basic indentation examples
     */
    private static void basicIndentation() {
        System.out.println("1. BASIC INDENTATION");
        
        String text = "Hello\nWorld\nJava";
        
        System.out.println("Original:");
        System.out.println(text);
        
        System.out.println("\nIndent by 4 spaces:");
        String indented4 = text.indent(4);
        System.out.println(indented4);
        
        System.out.println("Indent by 8 spaces:");
        String indented8 = text.indent(8);
        System.out.println(indented8);
    }

    /**
     * Code generation with proper indentation
     */
    private static void codeGeneration() {
        System.out.println("2. CODE GENERATION");
        
        String methodBody = """
            System.out.println("Hello");
            return 42;
            """;
        
        String method = "public int example() {\n" +
                        methodBody.indent(4) +
                        "}";
        
        System.out.println("Generated method:");
        System.out.println(method);
        
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
        
        System.out.println("\nGenerated nested class:");
        System.out.println(innerClass);
    }

    /**
     * SQL query formatting with indentation
     */
    private static void sqlQueryFormatting() {
        System.out.println("3. SQL QUERY FORMATTING");
        
        String selectClause = "SELECT id, name, email";
        String fromClause = "FROM users";
        String whereClause = "WHERE active = true";
        String orderClause = "ORDER BY name ASC";
        
        String query = selectClause + "\n" +
                      fromClause.indent(2) +
                      whereClause.indent(2) +
                      orderClause.indent(2);
        
        System.out.println("Formatted SQL:");
        System.out.println(query);
        
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
        
        System.out.println("\nComplex SQL with subquery:");
        System.out.println(complexQuery);
    }

    /**
     * Removing indentation (dedentation)
     */
    private static void dedentation() {
        System.out.println("4. DEDENTATION (Negative Indent)");
        
        String indentedCode = """
                public void method() {
                    System.out.println("test");
                }
            """;
        
        System.out.println("Original (heavily indented):");
        System.out.println(indentedCode);
        
        System.out.println("\nRemove 4 spaces (indent -4):");
        String dedented = indentedCode.indent(-4);
        System.out.println(dedented);
        
        System.out.println("Remove 8 spaces (indent -8):");
        String moreDedented = indentedCode.indent(-8);
        System.out.println(moreDedented);
    }

    /**
     * JSON formatting example
     */
    private static void jsonFormatting() {
        System.out.println("5. JSON FORMATTING");
        
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
        
        System.out.println("Formatted JSON:");
        System.out.println(json);
    }

    /**
     * Practical utility methods
     */
    public static class IndentUtils {
        
        /**
         * Indent all lines by the specified amount
         */
        public static String indent(String text, int spaces) {
            return text.indent(spaces);
        }
        
        /**
         * Indent a block to match a specific depth level
         */
        public static String indentToLevel(String text, int level) {
            return text.indent(level * 4); // 4 spaces per level
        }
        
        /**
         * Remove all leading whitespace
         */
        public static String removeIndent(String text) {
            // Find minimum indentation
            int minIndent = text.lines()
                .filter(line -> !line.isBlank())
                .mapToInt(line -> line.length() - line.stripLeading().length())
                .min()
                .orElse(0);
            
            return text.indent(-minIndent);
        }
        
        /**
         * Normalize indentation to a specific width
         */
        public static String normalizeIndent(String text, int targetSpaces) {
            return removeIndent(text).indent(targetSpaces);
        }
    }
}
