package com.learning.javalearning.step7b_java17_deserialization_filters;

import java.io.*;

/**
 * JAVA 17: CONTEXT-SPECIFIC DESERIALIZATION FILTERS (JEP 415)
 * 
 * Java 17 enhanced the deserialization filtering API to allow context-specific
 * filters that can be applied based on the deserialization context.
 * 
 * WHY THIS MATTERS:
 * - Deserialization is a common attack vector
 * - Malicious serialized data can execute arbitrary code
 * - Filters help prevent deserialization of dangerous classes
 * 
 * FILTER TYPES:
 * 1. Static filter: JVM-wide, set via system property
 * 2. Stream filter: Per ObjectInputStream
 * 3. Filter factory: Context-aware filter creation (Java 17+)
 */
public class DeserializationFilterExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== JAVA 17: Deserialization Filters ===\n");

        explainDeserializationRisks();
        demonstrateBasicFilter();
        demonstrateFilterFactory();
        showBestPractices();
    }

    static void explainDeserializationRisks() {
        System.out.println("--- 1. Why Filters Matter ---\n");

        System.out.println("""
                ⚠️ DESERIALIZATION RISKS:

                Deserializing untrusted data can lead to:
                - Remote Code Execution (RCE)
                - Denial of Service (DoS)
                - Data tampering

                FAMOUS VULNERABILITIES:
                - Apache Commons Collections (CVE-2015-7501)
                - Spring Framework (CVE-2016-1000027)
                - Java RMI attacks

                SOLUTION: Filter what classes can be deserialized!
                """);
    }

    static void demonstrateBasicFilter() throws Exception {
        System.out.println("--- 2. Basic Deserialization Filter ---\n");

        // Create a simple serializable object
        record User(String name, int age) implements Serializable {
        }

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(new User("Alice", 25));
        }
        byte[] data = baos.toByteArray();
        System.out.println("  Serialized User object (" + data.length + " bytes)");

        // Deserialize WITH filter
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {

            // Set a filter that only allows User class
            ois.setObjectInputFilter(filterInfo -> {
                Class<?> clazz = filterInfo.serialClass();
                if (clazz != null) {
                    // Only allow our User record and java.lang classes
                    String name = clazz.getName();
                    if (name.startsWith("com.learning.") ||
                            name.startsWith("java.lang.")) {
                        System.out.println("  ✅ Allowed: " + name);
                        return ObjectInputFilter.Status.ALLOWED;
                    } else {
                        System.out.println("  ❌ Rejected: " + name);
                        return ObjectInputFilter.Status.REJECTED;
                    }
                }
                return ObjectInputFilter.Status.UNDECIDED;
            });

            User user = (User) ois.readObject();
            System.out.println("  Deserialized: " + user);
        }

        System.out.println();
    }

    static void demonstrateFilterFactory() {
        System.out.println("--- 3. Context-Specific Filter Factory (Java 17+) ---\n");

        System.out.println("""
                Java 17 added ObjectInputFilter.Config.setSerialFilterFactory()

                This allows you to create filters based on CONTEXT:
                - Different filters for different streams
                - Combine global and stream-specific filters
                - Dynamic filter creation

                Example:
                ┌─────────────────────────────────────────────────────────────┐
                │ ObjectInputFilter.Config.setSerialFilterFactory(           │
                │     (currentFilter, nextFilter) -> {                        │
                │         // Create a combined filter                         │
                │         return ObjectInputFilter.merge(currentFilter,       │
                │                                        nextFilter);         │
                │     }                                                       │
                │ );                                                          │
                └─────────────────────────────────────────────────────────────┘

                The factory receives:
                - currentFilter: The existing JVM-wide filter
                - nextFilter: The stream-specific filter being set

                You can:
                - Merge them
                - Override one with the other
                - Create a completely new filter based on context
                """);

        // Note: setSerialFilterFactory can only be called once per JVM,
        // so we just demonstrate the concept here

        System.out.println();
    }

    static void showBestPractices() {
        System.out.println("--- 4. Best Practices ---\n");

        System.out.println("""
                ✅ DESERIALIZATION SECURITY BEST PRACTICES:

                1. AVOID deserialization of untrusted data if possible
                   - Use JSON/XML instead of Java serialization

                2. SET GLOBAL FILTER via system property:
                   -Djdk.serialFilter=!*  (block all by default)
                   -Djdk.serialFilter=com.myapp.**;!*  (allowlist)

                3. USE ALLOWLIST, not blocklist:
                   ❌ Block dangerous classes (attackers find new ones)
                   ✅ Allow only known-safe classes

                4. LIMIT object graph:
                   maxdepth=5;maxrefs=1000;maxbytes=500000

                5. USE FILTER FACTORY for complex scenarios:
                   - Different rules for different contexts
                   - Logging and monitoring

                ─────────────────────────────────────────────────────────────────

                EXAMPLE FILTER PATTERN:

                jdk.serialFilter=\\
                    maxdepth=5;\\
                    maxrefs=500;\\
                    maxbytes=100000;\\
                    com.mycompany.dto.**;\\
                    java.lang.*;\\
                    java.util.*;\\
                    !*

                This allows:
                - Max 5 levels of nesting
                - Max 500 object references
                - Max 100KB of data
                - Only com.mycompany.dto, java.lang, java.util classes
                - Blocks everything else
                """);
    }
}
