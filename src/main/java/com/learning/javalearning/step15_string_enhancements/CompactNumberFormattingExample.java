package com.learning.javalearning.step15_string_enhancements;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Compact Number Formatting - Java 12
 * 
 * Format numbers in a compact, human-readable form (1K, 1M, 1B).
 * Ideal for:
 * - Social media counters (likes, views, followers)
 * - File size displays
 * - Financial dashboards
 * - Analytics metrics
 * 
 * Styles:
 * - SHORT: "1K", "1M", "1B"
 * - LONG: "1 thousand", "1 million", "1 billion"
 */
public class CompactNumberFormattingExample {

    public static void main(String[] args) {
        System.out.println("=== Compact Number Formatting Examples ===\n");
        
        basicFormatting();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        shortVsLongStyle();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        localeSpecificFormatting();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        socialMediaCounters();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        fileSizeFormatting();
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        parsingCompactNumbers();
    }

    /**
     * Basic compact number formatting
     */
    private static void basicFormatting() {
        System.out.println("1. BASIC COMPACT FORMATTING");
        
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
            Locale.US, 
            NumberFormat.Style.SHORT
        );
        
        long[] numbers = {
            100,
            1_000,
            10_000,
            100_000,
            1_000_000,
            10_000_000,
            100_000_000,
            1_000_000_000,
            1_000_000_000_000L
        };
        
        System.out.println("Number → Compact Format");
        System.out.println("-".repeat(30));
        for (long num : numbers) {
            System.out.printf("%,15d → %s%n", num, fmt.format(num));
        }
    }

    /**
     * Compare SHORT vs LONG styles
     */
    private static void shortVsLongStyle() {
        System.out.println("2. SHORT vs LONG STYLES");
        
        NumberFormat shortFmt = NumberFormat.getCompactNumberInstance(
            Locale.US, 
            NumberFormat.Style.SHORT
        );
        
        NumberFormat longFmt = NumberFormat.getCompactNumberInstance(
            Locale.US, 
            NumberFormat.Style.LONG
        );
        
        long[] numbers = {1_000, 5_000, 1_000_000, 2_500_000, 1_000_000_000};
        
        System.out.printf("%-15s %-15s %-20s%n", "Number", "SHORT", "LONG");
        System.out.println("-".repeat(50));
        
        for (long num : numbers) {
            System.out.printf("%-15s %-15s %-20s%n",
                String.format("%,d", num),
                shortFmt.format(num),
                longFmt.format(num)
            );
        }
    }

    /**
     * Locale-specific formatting
     */
    private static void localeSpecificFormatting() {
        System.out.println("3. LOCALE-SPECIFIC FORMATTING");
        
        long number = 1_500_000;
        
        Locale[] locales = {
            Locale.US,
            Locale.UK,
            Locale.FRANCE,
            Locale.GERMANY,
            Locale.JAPAN,
            Locale.CHINA,
            new Locale("es", "ES"), // Spanish
            new Locale("hi", "IN")  // Hindi/India
        };
        
        System.out.printf("Number: %,d%n%n", number);
        System.out.printf("%-20s %-15s %-20s%n", "Locale", "SHORT", "LONG");
        System.out.println("-".repeat(55));
        
        for (Locale locale : locales) {
            NumberFormat shortFmt = NumberFormat.getCompactNumberInstance(
                locale, 
                NumberFormat.Style.SHORT
            );
            NumberFormat longFmt = NumberFormat.getCompactNumberInstance(
                locale, 
                NumberFormat.Style.LONG
            );
            
            System.out.printf("%-20s %-15s %-20s%n",
                locale.getDisplayName(),
                shortFmt.format(number),
                longFmt.format(number)
            );
        }
    }

    /**
     * Social media counters
     */
    private static void socialMediaCounters() {
        System.out.println("4. SOCIAL MEDIA COUNTERS");
        
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
            Locale.US, 
            NumberFormat.Style.SHORT
        );
        
        // Simulate social media metrics
        SocialMediaPost post1 = new SocialMediaPost(
            "Amazing sunset photo!",
            15_234,      // likes
            2_456,       // comments
            8_901        // shares
        );
        
        SocialMediaPost post2 = new SocialMediaPost(
            "Viral video compilation",
            5_234_567,   // likes
            123_456,     // comments
            987_654      // shares
        );
        
        System.out.println("Post 1: " + post1.title);
        System.out.println("  👍 " + fmt.format(post1.likes) + " likes");
        System.out.println("  💬 " + fmt.format(post1.comments) + " comments");
        System.out.println("  🔄 " + fmt.format(post1.shares) + " shares");
        
        System.out.println("\nPost 2: " + post2.title);
        System.out.println("  👍 " + fmt.format(post2.likes) + " likes");
        System.out.println("  💬 " + fmt.format(post2.comments) + " comments");
        System.out.println("  🔄 " + fmt.format(post2.shares) + " shares");
        
        // Channel statistics
        System.out.println("\n📺 Channel Statistics");
        System.out.println("Subscribers: " + fmt.format(2_345_678));
        System.out.println("Total Views: " + fmt.format(123_456_789));
        System.out.println("Videos: " + fmt.format(1_234));
    }

    /**
     * File size formatting (adapted for compact numbers)
     */
    private static void fileSizeFormatting() {
        System.out.println("5. FILE SIZE DISPLAY");
        
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
            Locale.US, 
            NumberFormat.Style.SHORT
        );
        
        // Note: For actual file sizes, you'd typically use binary units (KiB, MiB)
        // This demonstrates compact formatting for byte counts
        
        FileInfo[] files = {
            new FileInfo("document.pdf", 52_428),           // ~51 KB
            new FileInfo("photo.jpg", 2_097_152),           // ~2 MB
            new FileInfo("video.mp4", 524_288_000),         // ~500 MB
            new FileInfo("dataset.zip", 5_368_709_120L),    // ~5 GB
            new FileInfo("backup.tar", 1_099_511_627_776L)  // ~1 TB
        };
        
        System.out.printf("%-20s %15s %12s%n", "Filename", "Bytes", "Compact");
        System.out.println("-".repeat(50));
        
        for (FileInfo file : files) {
            System.out.printf("%-20s %,15d %12s%n",
                file.name,
                file.sizeInBytes,
                fmt.format(file.sizeInBytes) + " bytes"
            );
        }
        
        // Better file size formatting (with proper units)
        System.out.println("\nBetter file size formatting:");
        for (FileInfo file : files) {
            System.out.printf("%-20s %s%n", 
                file.name, 
                formatFileSize(file.sizeInBytes)
            );
        }
    }

    /**
     * Parsing compact numbers back to numeric values
     */
    private static void parsingCompactNumbers() {
        System.out.println("6. PARSING COMPACT NUMBERS");
        
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
            Locale.US, 
            NumberFormat.Style.SHORT
        );
        
        String[] compactNumbers = {"1K", "2.5M", "1B", "500", "3.14K"};
        
        System.out.println("Compact → Parsed Value");
        System.out.println("-".repeat(30));
        
        for (String compact : compactNumbers) {
            try {
                Number parsed = fmt.parse(compact);
                System.out.printf("%-10s → %,.0f%n", compact, parsed.doubleValue());
            } catch (ParseException e) {
                System.out.printf("%-10s → ERROR: %s%n", compact, e.getMessage());
            }
        }
    }

    /**
     * Helper method for proper file size formatting
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Helper classes for examples
     */
    static class SocialMediaPost {
        String title;
        long likes;
        long comments;
        long shares;
        
        SocialMediaPost(String title, long likes, long comments, long shares) {
            this.title = title;
            this.likes = likes;
            this.comments = comments;
            this.shares = shares;
        }
    }
    
    static class FileInfo {
        String name;
        long sizeInBytes;
        
        FileInfo(String name, long sizeInBytes) {
            this.name = name;
            this.sizeInBytes = sizeInBytes;
        }
    }

    /**
     * Utility class for compact number formatting
     */
    public static class CompactFormatter {
        private final NumberFormat shortFormat;
        private final NumberFormat longFormat;
        
        public CompactFormatter(Locale locale) {
            this.shortFormat = NumberFormat.getCompactNumberInstance(
                locale, 
                NumberFormat.Style.SHORT
            );
            this.longFormat = NumberFormat.getCompactNumberInstance(
                locale, 
                NumberFormat.Style.LONG
            );
        }
        
        public String formatShort(long number) {
            return shortFormat.format(number);
        }
        
        public String formatLong(long number) {
            return longFormat.format(number);
        }
        
        public String formatShort(double number) {
            return shortFormat.format(number);
        }
        
        public String formatLong(double number) {
            return longFormat.format(number);
        }
        
        public Number parse(String compactNumber) throws ParseException {
            return shortFormat.parse(compactNumber);
        }
        
        /**
         * Format with custom suffix
         */
        public String formatWithSuffix(long number, String suffix) {
            return formatShort(number) + " " + suffix;
        }
        
        /**
         * US locale instance
         */
        public static CompactFormatter us() {
            return new CompactFormatter(Locale.US);
        }
        
        /**
         * UK locale instance
         */
        public static CompactFormatter uk() {
            return new CompactFormatter(Locale.UK);
        }
    }
}
