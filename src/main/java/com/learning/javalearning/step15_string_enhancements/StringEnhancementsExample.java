package com.learning.javalearning.step15_string_enhancements;

/**
 * String.stripIndent() and String.translateEscapes() - Java 13
 * 
 * stripIndent():
 * - Removes incidental whitespace from multi-line strings
 * - Primarily designed for text blocks
 * - Normalizes line endings
 * 
 * translateEscapes():
 * - Processes escape sequences in strings
 * - Converts \\n to actual newline, \\t to tab, etc.
 * - Useful for dynamic string processing
 */
public class StringEnhancementsExample {

    public static void main(String[] args) {
        System.out.println("=== String Enhancements Examples ===\n");
        
        stripIndentExamples();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        translateEscapesExamples();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        combinedExamples();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        realWorldUseCases();
    }

    /**
     * String.stripIndent() examples
     */
    private static void stripIndentExamples() {
        System.out.println("1. STRIP INDENT EXAMPLES");
        
        // Basic stripIndent with text block
        String code = """
                public void method() {
                    System.out.println("Hello");
                    return 42;
                }
                """;
        
        System.out.println("Original (with indentation):");
        System.out.println(code);
        
        String stripped = code.stripIndent();
        System.out.println("After stripIndent():");
        System.out.println(stripped);
        
        // stripIndent finds the minimum common indentation
        String inconsistent = """
            Line 1
                Line 2 (extra indent)
            Line 3
                Line 4 (extra indent)
        """;
        
        System.out.println("\nInconsistent indentation:");
        System.out.println(inconsistent);
        
        System.out.println("After stripIndent():");
        System.out.println(inconsistent.stripIndent());
        
        // With manual string (not text block)
        String manual = "    Line 1\n" +
                       "        Line 2\n" +
                       "    Line 3";
        
        System.out.println("\nManual multi-line string:");
        System.out.println(manual);
        
        System.out.println("After stripIndent():");
        System.out.println(manual.stripIndent());
    }

    /**
     * String.translateEscapes() examples
     */
    private static void translateEscapesExamples() {
        System.out.println("2. TRANSLATE ESCAPES EXAMPLES");
        
        // Basic escape processing
        String escaped = "Line 1\\nLine 2\\tTabbed\\nLine 3";
        System.out.println("Original string with escape sequences:");
        System.out.println(escaped);
        
        String translated = escaped.translateEscapes();
        System.out.println("\nAfter translateEscapes():");
        System.out.println(translated);
        
        // Common escape sequences
        String allEscapes = "\\n (newline)\\n" +
                           "\\t (tab)\\n" +
                           "\\r (carriage return)\\n" +
                           "\\\\ (backslash)\\n" +
                           "\\\" (double quote)\\n" +
                           "\\' (single quote)";
        
        System.out.println("\n--- Common Escape Sequences ---");
        System.out.println("Before:");
        System.out.println(allEscapes);
        System.out.println("\nAfter translateEscapes():");
        System.out.println(allEscapes.translateEscapes());
        
        // Unicode escapes
        String unicode = "\\u0048\\u0065\\u006C\\u006C\\u006F";
        System.out.println("\nUnicode escapes: " + unicode);
        System.out.println("Translated: " + unicode.translateEscapes());
        
        // Octal escapes
        String octal = "\\101\\102\\103"; // ABC
        System.out.println("\nOctal escapes: " + octal);
        System.out.println("Translated: " + octal.translateEscapes());
    }

    /**
     * Combining stripIndent() and translateEscapes()
     */
    private static void combinedExamples() {
        System.out.println("3. COMBINED USAGE");
        
        // Code template with escape sequences
        String template = """
                public void log(String message) {
                    System.out.println("\\tLog: " + message);
                    System.out.println("\\tTime: " + System.currentTimeMillis());
                }
                """;
        
        System.out.println("Original template:");
        System.out.println(template);
        
        // First strip indent, then translate escapes
        String processed = template.stripIndent().translateEscapes();
        System.out.println("\nAfter stripIndent() + translateEscapes():");
        System.out.println(processed);
        
        // Dynamic string construction
        String dynamicCode = """
            if (condition) {
                System.out.println("True\\nValue: " + x);
            } else {
                System.out.println("False\\nValue: " + y);
            }
        """.stripIndent();
        
        System.out.println("\nDynamic code generation:");
        System.out.println(dynamicCode.translateEscapes());
    }

    /**
     * Real-world use cases
     */
    private static void realWorldUseCases() {
        System.out.println("4. REAL-WORLD USE CASES");
        
        // Use Case 1: JSON template processing
        System.out.println("📦 Use Case 1: JSON Template");
        String jsonTemplate = """
            {
              "name": "{{name}}",
              "message": "Line 1\\nLine 2\\nLine 3",
              "timestamp": {{timestamp}}
            }
            """.stripIndent();
        
        String processedJson = jsonTemplate
            .replace("{{name}}", "John Doe")
            .replace("{{timestamp}}", String.valueOf(System.currentTimeMillis()))
            .translateEscapes();
        
        System.out.println(processedJson);
        
        // Use Case 2: SQL query with formatting
        System.out.println("\n🗄️ Use Case 2: SQL Query");
        String sqlTemplate = """
                SELECT 
                    id,
                    name,
                    email
                FROM users
                WHERE active = true
                ORDER BY name ASC
                """.stripIndent();
        
        System.out.println(sqlTemplate);
        
        // Use Case 3: Log message formatting
        System.out.println("📝 Use Case 3: Log Messages");
        String logTemplate = "ERROR\\n\\tFile: {{file}}\\n\\tLine: {{line}}\\n\\tMessage: {{message}}";
        
        String logMessage = logTemplate
            .replace("{{file}}", "Main.java")
            .replace("{{line}}", "42")
            .replace("{{message}}", "Null pointer exception")
            .translateEscapes();
        
        System.out.println(logMessage);
        
        // Use Case 4: Email template
        System.out.println("\n✉️ Use Case 4: Email Template");
        String emailTemplate = """
            Dear {{name}},
            
            Thank you for your registration.\\n\\n
            Your account details:\\n
            \\tUsername: {{username}}\\n
            \\tEmail: {{email}}\\n\\n
            
            Best regards,\\n
            The Team
            """.stripIndent();
        
        String email = emailTemplate
            .replace("{{name}}", "Alice")
            .replace("{{username}}", "alice123")
            .replace("{{email}}", "alice@example.com")
            .translateEscapes();
        
        System.out.println(email);
        
        // Use Case 5: Code generation
        System.out.println("💻 Use Case 5: Code Generation");
        String methodTemplate = """
            public {{returnType}} {{methodName}}({{params}}) {
                // Method body
                {{body}}
                return {{returnValue}};
            }
            """.stripIndent();
        
        String generatedMethod = methodTemplate
            .replace("{{returnType}}", "String")
            .replace("{{methodName}}", "getName")
            .replace("{{params}}", "")
            .replace("{{body}}", "System.out.println(\\\"Getting name\\\");")
            .replace("{{returnValue}}", "\\\"John\\\"")
            .translateEscapes();
        
        System.out.println(generatedMethod);
    }

    /**
     * Utility class for string processing
     */
    public static class StringProcessor {
        
        /**
         * Normalize a multi-line string: strip indent and translate escapes
         */
        public static String normalize(String text) {
            return text.stripIndent().translateEscapes();
        }
        
        /**
         * Process a template: strip indent, replace placeholders, translate escapes
         */
        public static String processTemplate(String template, 
                                            java.util.Map<String, String> replacements) {
            String processed = template.stripIndent();
            
            for (var entry : replacements.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                processed = processed.replace(placeholder, entry.getValue());
            }
            
            return processed.translateEscapes();
        }
        
        /**
         * Clean code block: remove indentation and normalize whitespace
         */
        public static String cleanCodeBlock(String code) {
            return code.stripIndent()
                      .lines()
                      .map(String::strip)
                      .filter(line -> !line.isEmpty())
                      .reduce((a, b) -> a + "\n" + b)
                      .orElse("");
        }
        
        /**
         * Escape special characters for Java string literals
         */
        public static String escapeJavaString(String input) {
            return input.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("\n", "\\n")
                       .replace("\t", "\\t")
                       .replace("\r", "\\r");
        }
        
        /**
         * Unescape Java string literals
         */
        public static String unescapeJavaString(String input) {
            return input.translateEscapes();
        }
    }

    /**
     * Template engine example
     */
    public static class SimpleTemplateEngine {
        private final String template;
        
        public SimpleTemplateEngine(String template) {
            this.template = template.stripIndent();
        }
        
        public String render(java.util.Map<String, String> context) {
            String result = template;
            
            for (var entry : context.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                result = result.replace(placeholder, entry.getValue());
            }
            
            return result.translateEscapes();
        }
        
        public static SimpleTemplateEngine compile(String template) {
            return new SimpleTemplateEngine(template);
        }
    }

    /**
     * Example: Using the template engine
     */
    public static void demonstrateTemplateEngine() {
        var engine = SimpleTemplateEngine.compile("""
            Name: {{name}}
            Age: {{age}}
            Email: {{email}}
            \\nNotes:\\n\\t{{notes}}
            """);
        
        var context = java.util.Map.of(
            "name", "Bob",
            "age", "30",
            "email", "bob@example.com",
            "notes", "Premium customer"
        );
        
        String output = engine.render(context);
        System.out.println(output);
    }
}
