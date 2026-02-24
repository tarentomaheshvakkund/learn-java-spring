package com.learning.javalearning.step15_string_enhancements;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(CompactNumberFormattingExample.class.getName());

    public static void main(String[] args) {
        logger.info("=== Compact Number Formatting Examples ===");

        basicFormatting();
        logger.info(() -> "=".repeat(50));

        shortVsLongStyle();
        logger.info(() -> "=".repeat(50));

        localeSpecificFormatting();
        logger.info(() -> "=".repeat(50));

        socialMediaCounters();
        logger.info(() -> "=".repeat(50));

        fileSizeFormatting();
        logger.info(() -> "=".repeat(50));

        parsingCompactNumbers();
    }

    /**
     * Basic compact number formatting
     */
    private static void basicFormatting() {
        logger.info("1. BASIC COMPACT FORMATTING");

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

        logger.info("Number \u2192 Compact Format");
        logger.info(() -> "-".repeat(30));
        for (long num : numbers) {
            logger.info(() -> String.format("%,15d \u2192 %s", num, fmt.format(num)));
        }
    }

    /**
     * Compare SHORT vs LONG styles
     */
    private static void shortVsLongStyle() {
        logger.info("2. SHORT vs LONG STYLES");

        NumberFormat shortFmt = NumberFormat.getCompactNumberInstance(
            Locale.US,
            NumberFormat.Style.SHORT
        );

        NumberFormat longFmt = NumberFormat.getCompactNumberInstance(
            Locale.US,
            NumberFormat.Style.LONG
        );

        long[] numbers = {1_000, 5_000, 1_000_000, 2_500_000, 1_000_000_000};

        logger.info(() -> String.format("%-15s %-15s %-20s", "Number", "SHORT", "LONG"));
        logger.info(() -> "-".repeat(50));

        for (long num : numbers) {
            logger.info(() -> String.format("%-15s %-15s %-20s",
                String.format("%,d", num),
                shortFmt.format(num),
                longFmt.format(num)
            ));
        }
    }

    /**
     * Locale-specific formatting
     */
    private static void localeSpecificFormatting() {
        logger.info("3. LOCALE-SPECIFIC FORMATTING");

        long number = 1_500_000;

        Locale[] locales = {
            Locale.US,
            Locale.UK,
            Locale.FRANCE,
            Locale.GERMANY,
            Locale.JAPAN,
            Locale.CHINA,
            Locale.of("es", "ES"), // Spanish
            Locale.of("hi", "IN")  // Hindi/India
        };

        logger.info(() -> String.format("Number: %,d", number));
        logger.info(() -> String.format("%-20s %-15s %-20s", "Locale", "SHORT", "LONG"));
        logger.info(() -> "-".repeat(55));

        for (Locale locale : locales) {
            NumberFormat shortFmt = NumberFormat.getCompactNumberInstance(
                locale,
                NumberFormat.Style.SHORT
            );
            NumberFormat longFmt = NumberFormat.getCompactNumberInstance(
                locale,
                NumberFormat.Style.LONG
            );

            logger.info(() -> String.format("%-20s %-15s %-20s",
                locale.getDisplayName(),
                shortFmt.format(number),
                longFmt.format(number)
            ));
        }
    }

    /**
     * Social media counters
     */
    private static void socialMediaCounters() {
        logger.info("4. SOCIAL MEDIA COUNTERS");

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

        logger.info(() -> "Post 1: " + post1.title);
        logger.info(() -> "  \uD83D\uDC4D " + fmt.format(post1.likes) + " likes");
        logger.info(() -> "  \uD83D\uDCAC " + fmt.format(post1.comments) + " comments");
        logger.info(() -> "  \uD83D\uDD04 " + fmt.format(post1.shares) + " shares");

        logger.info(() -> "Post 2: " + post2.title);
        logger.info(() -> "  \uD83D\uDC4D " + fmt.format(post2.likes) + " likes");
        logger.info(() -> "  \uD83D\uDCAC " + fmt.format(post2.comments) + " comments");
        logger.info(() -> "  \uD83D\uDD04 " + fmt.format(post2.shares) + " shares");

        // Channel statistics
        logger.info("\uD83D\uDCFA Channel Statistics");
        logger.info(() -> "Subscribers: " + fmt.format(2_345_678));
        logger.info(() -> "Total Views: " + fmt.format(123_456_789));
        logger.info(() -> "Videos: " + fmt.format(1_234));
    }

    /**
     * File size formatting (adapted for compact numbers)
     */
    private static void fileSizeFormatting() {
        logger.info("5. FILE SIZE DISPLAY");

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

        logger.info(() -> String.format("%-20s %15s %12s", "Filename", "Bytes", "Compact"));
        logger.info(() -> "-".repeat(50));

        for (FileInfo file : files) {
            logger.info(() -> String.format("%-20s %,15d %12s",
                file.name,
                file.sizeInBytes,
                fmt.format(file.sizeInBytes) + " bytes"
            ));
        }

        // Better file size formatting (with proper units)
        logger.info("Better file size formatting:");
        for (FileInfo file : files) {
            logger.info(() -> String.format("%-20s %s",
                file.name,
                formatFileSize(file.sizeInBytes)
            ));
        }
    }

    /**
     * Parsing compact numbers back to numeric values
     */
    private static void parsingCompactNumbers() {
        logger.info("6. PARSING COMPACT NUMBERS");

        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
            Locale.US,
            NumberFormat.Style.SHORT
        );

        String[] compactNumbers = {"1K", "2.5M", "1B", "500", "3.14K"};

        logger.info("Compact \u2192 Parsed Value");
        logger.info(() -> "-".repeat(30));

        for (String compact : compactNumbers) {
            try {
                Number parsed = fmt.parse(compact);
                logger.info(() -> String.format("%-10s \u2192 %,.0f", compact, parsed.doubleValue()));
            } catch (ParseException e) {
                logger.info(() -> String.format("%-10s \u2192 ERROR: %s", compact, e.getMessage()));
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
