package com.learning.javalearning.step7a_java17_hex_format;

import java.util.HexFormat;

/**
 * JAVA 17: HexFormat Utility Class
 * 
 * HexFormat provides a simple API for converting between bytes and hex strings.
 * Before Java 17, you needed external libraries or manual conversion.
 * 
 * USE CASES:
 * - Debugging binary data
 * - Parsing hex-encoded strings
 * - Generating hex output for logs
 * - MAC addresses, hash values, etc.
 */
public class HexFormatExample {

    public static void main(String[] args) {
        System.out.println("=== JAVA 17: HexFormat Utility ===\n");

        demonstrateBasicConversion();
        demonstrateFormatOptions();
        demonstrateParsing();
        demonstratePracticalExamples();
    }

    static void demonstrateBasicConversion() {
        System.out.println("--- 1. Basic Hex Conversion ---\n");

        HexFormat hex = HexFormat.of();

        // Byte array to hex string
        byte[] bytes = { 0x48, 0x65, 0x6c, 0x6c, 0x6f }; // "Hello"
        String hexString = hex.formatHex(bytes);
        System.out.println("  Bytes to hex: " + hexString);

        // Hex string to byte array
        byte[] parsed = hex.parseHex("48656c6c6f");
        System.out.println("  Hex to string: " + new String(parsed));

        // Single byte
        int byteValue = 0xAB;
        System.out.println("  Single byte 0xAB: " + hex.toHexDigits(byteValue));

        System.out.println();
    }

    static void demonstrateFormatOptions() {
        System.out.println("--- 2. Format Options ---\n");

        byte[] data = { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE };

        // Default: lowercase, no delimiter
        HexFormat basic = HexFormat.of();
        System.out.println("  Default:      " + basic.formatHex(data));

        // Uppercase
        HexFormat upper = HexFormat.of().withUpperCase();
        System.out.println("  Uppercase:    " + upper.formatHex(data));

        // With delimiter
        HexFormat delimited = HexFormat.ofDelimiter(":");
        System.out.println("  Colon delim:  " + delimited.formatHex(data));

        // With prefix and suffix
        HexFormat prefixed = HexFormat.of()
                .withPrefix("0x")
                .withUpperCase();
        System.out.println("  With 0x:      " + prefixed.formatHex(data));

        // Combined
        HexFormat fancy = HexFormat.ofDelimiter(" ")
                .withPrefix("[")
                .withSuffix("]")
                .withUpperCase();
        System.out.println("  Fancy:        " + fancy.formatHex(data));

        System.out.println();
    }

    static void demonstrateParsing() {
        System.out.println("--- 3. Parsing Hex Strings ---\n");

        // Parse simple hex
        HexFormat hex = HexFormat.of();
        byte[] result1 = hex.parseHex("deadbeef");
        System.out.println("  Parsed 'deadbeef': " + hex.formatHex(result1));

        // Parse with delimiter
        HexFormat colonHex = HexFormat.ofDelimiter(":");
        byte[] result2 = colonHex.parseHex("de:ad:be:ef");
        System.out.println("  Parsed 'de:ad:be:ef': " + colonHex.formatHex(result2));

        // Parse MAC address
        byte[] mac = colonHex.parseHex("00:1A:2B:3C:4D:5E");
        System.out.print("  MAC bytes: ");
        for (byte b : mac) {
            System.out.printf("%02X ", b);
        }
        System.out.println("\n");
    }

    static void demonstratePracticalExamples() {
        System.out.println("--- 4. Practical Examples ---\n");

        HexFormat hex = HexFormat.of().withUpperCase();
        HexFormat macFormat = HexFormat.ofDelimiter(":").withUpperCase();

        // Example 1: Hash visualization
        byte[] hashBytes = {
                (byte) 0x5d, (byte) 0x41, (byte) 0x40, (byte) 0x2a,
                (byte) 0xbc, (byte) 0x4b, (byte) 0x2a, (byte) 0x76
        };
        System.out.println("  SHA-256 prefix: " + hex.formatHex(hashBytes));

        // Example 2: MAC address formatting
        byte[] macBytes = { 0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E };
        System.out.println("  MAC address: " + macFormat.formatHex(macBytes));

        // Example 3: Color code
        byte[] rgb = { (byte) 0xFF, (byte) 0x69, (byte) 0xB4 }; // Hot Pink
        System.out.println("  Color (Hot Pink): #" + hex.formatHex(rgb));

        // Example 4: UUID bytes
        byte[] uuidBytes = new byte[16];
        new java.util.Random(42).nextBytes(uuidBytes);
        HexFormat uuidHex = HexFormat.of();
        String uuidHexStr = uuidHex.formatHex(uuidBytes);
        String formatted = uuidHexStr.substring(0, 8) + "-" +
                uuidHexStr.substring(8, 12) + "-" +
                uuidHexStr.substring(12, 16) + "-" +
                uuidHexStr.substring(16, 20) + "-" +
                uuidHexStr.substring(20);
        System.out.println("  UUID format: " + formatted);

        System.out.println();
    }
}
