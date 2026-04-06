package com.learning.javalearning.step17_datetime_enhancements;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Advanced DateTimeFormatter Patterns - Java 16+
 *
 * <p>This class demonstrates advanced {@link DateTimeFormatter} usage including 
 * the Java 16 day period 'B' pattern, {@link DateTimeFormatterBuilder} for 
 * complex patterns, optional sections, and locale-sensitive formatting.</p>
 *
 * <h2>Topics Covered:</h2>
 * <ul>
 *   <li>DateTimeFormatterBuilder for custom formatters</li>
 *   <li>Optional sections with appendOptional</li>
 *   <li>Default values and fallbacks</li>
 *   <li>Localized patterns with FormatStyle</li>
 *   <li>Custom field padding and text styles</li>
 *   <li>Duration and Period formatting improvements</li>
 * </ul>
 *
 * @see java.time.format.DateTimeFormatter
 * @see java.time.format.DateTimeFormatterBuilder
 */
public class DateTimeFormatterExample {

    private static final Logger LOGGER = Logger.getLogger(DateTimeFormatterExample.class.getName());

    private static final String SEPARATOR = "─".repeat(50);
    private static final String SECTION_SEPARATOR = "═".repeat(50);

    private DateTimeFormatterExample() {
        // Utility class - prevent instantiation
    }

    public static void main(String[] args) {
        LOGGER.info(SECTION_SEPARATOR);
        LOGGER.info("  Advanced DateTimeFormatter Patterns");
        LOGGER.info(SECTION_SEPARATOR);

        demonstrateFormatterBuilder();
        demonstrateOptionalSections();
        demonstrateLocalizedFormats();
        demonstratePaddingAndTextStyles();
        demonstrateDurationFormatting();
        demonstrateCustomPatterns();
    }

    // ============================================================
    // 1. DateTimeFormatterBuilder
    // ============================================================
    private static void demonstrateFormatterBuilder() {
        LOGGER.info("1️⃣ DATETIMEFORMATTERBUILDER");
        LOGGER.info(SEPARATOR);

        // Build a complex formatter step-by-step
        DateTimeFormatter customFormatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL)
                .appendLiteral(", ")
                .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL)
                .appendLiteral(" ")
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(", ")
                .appendValue(ChronoField.YEAR)
                .appendLiteral(" at ")
                .appendValue(ChronoField.HOUR_OF_AMPM)
                .appendLiteral(":")
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(" ")
                .appendText(ChronoField.AMPM_OF_DAY, TextStyle.SHORT)
                .toFormatter(Locale.US);

        LocalDateTime now = LocalDateTime.now();
        LOGGER.info(() -> "   Custom format: " + now.format(customFormatter));

        // Builder with case-insensitive parsing
        DateTimeFormatter flexibleParser = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MMM-dd")
                .toFormatter(Locale.US);

        LocalDate parsedUpper = LocalDate.parse("2026-FEB-24", flexibleParser);
        LocalDate parsedLower = LocalDate.parse("2026-feb-24", flexibleParser);
        LOGGER.info(() -> "   Parsed (upper): " + parsedUpper);
        LOGGER.info(() -> "   Parsed (lower): " + parsedLower);
        LOGGER.info("");
    }

    // ============================================================
    // 2. Optional Sections
    // ============================================================
    private static void demonstrateOptionalSections() {
        LOGGER.info("2️⃣ OPTIONAL SECTIONS IN FORMATTERS");
        LOGGER.info(SEPARATOR);

        // Formatter that handles optional time zone
        DateTimeFormatter withOptionalZone = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendLiteral(" [")
                .appendZoneId()
                .appendLiteral("]")
                .optionalEnd()
                .toFormatter();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 24, 15, 30, 0);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("America/New_York"));

        LOGGER.info(() -> "   Without zone: " + localDateTime.format(withOptionalZone));
        LOGGER.info(() -> "   With zone   : " + zonedDateTime.format(withOptionalZone));

        // Formatter with optional milliseconds
        DateTimeFormatter withOptionalNanos = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .toFormatter();

        LocalTime withNanos = LocalTime.of(14, 30, 45, 123_000_000);
        LocalTime withoutNanos = LocalTime.of(14, 30, 45);

        LOGGER.info(() -> "   With millis   : " + withNanos.format(withOptionalNanos));
        LOGGER.info(() -> "   Without millis: " + withoutNanos.format(withOptionalNanos));
        LOGGER.info("");
    }

    // ============================================================
    // 3. Localized Formats
    // ============================================================
    private static void demonstrateLocalizedFormats() {
        LOGGER.info("3️⃣ LOCALIZED DATE/TIME FORMATS");
        LOGGER.info(SEPARATOR);

        LocalDateTime dateTime = LocalDateTime.of(2026, 2, 24, 15, 30, 0);

        // FormatStyle options: FULL, LONG, MEDIUM, SHORT
        List<FormatStyle> styles = List.of(
                FormatStyle.SHORT,
                FormatStyle.MEDIUM,
                FormatStyle.LONG,
                FormatStyle.FULL
        );

        // Show different styles in US locale
        LOGGER.info("   US Locale:");
        for (FormatStyle style : styles) {
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(style)
                    .withLocale(Locale.US);
            String formatted = dateTime.toLocalDate().format(formatter);
            LOGGER.info(() -> String.format("   %-8s: %s", style, formatted));
        }
        LOGGER.info("");

        // Compare same date across locales with FULL style
        LOGGER.info("   FULL style across locales:");
        List<Locale> locales = List.of(
                Locale.US,
                Locale.FRANCE,
                Locale.GERMANY,
                Locale.JAPAN,
                Locale.CHINA
        );

        for (Locale locale : locales) {
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                    .withLocale(locale);
            String formatted = dateTime.toLocalDate().format(formatter);
            String display = String.format("   %-10s: %s",
                    locale.getDisplayLanguage(Locale.US), formatted);
            LOGGER.info(display);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 4. Padding and Text Styles
    // ============================================================
    private static void demonstratePaddingAndTextStyles() {
        LOGGER.info("4️⃣ PADDING AND TEXT STYLES");
        LOGGER.info(SEPARATOR);

        // Padded day of month (always 2 digits)
        DateTimeFormatter paddedFormatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4)
                .appendLiteral('-')
                .padNext(2, '0')
                .appendValue(ChronoField.MONTH_OF_YEAR)
                .appendLiteral('-')
                .padNext(2, '0')
                .appendValue(ChronoField.DAY_OF_MONTH)
                .toFormatter();

        LocalDate singleDigitDate = LocalDate.of(2026, 3, 5);
        LOGGER.info(() -> "   Padded date: " + singleDigitDate.format(paddedFormatter));

        // Different text styles for month
        LocalDate date = LocalDate.of(2026, 2, 24);

        List<TextStyle> textStyles = List.of(
                TextStyle.FULL,
                TextStyle.SHORT,
                TextStyle.NARROW,
                TextStyle.FULL_STANDALONE,
                TextStyle.SHORT_STANDALONE,
                TextStyle.NARROW_STANDALONE
        );

        LOGGER.info("   Month text styles (February):");
        for (TextStyle style : textStyles) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendText(ChronoField.MONTH_OF_YEAR, style)
                    .toFormatter(Locale.US);
            String formatted = date.format(formatter);
            LOGGER.info(() -> String.format("   %-20s: %s", style, formatted));
        }
        LOGGER.info("");
    }

    // ============================================================
    // 5. Duration and Period Formatting
    // ============================================================
    private static void demonstrateDurationFormatting() {
        LOGGER.info("5️⃣ DURATION AND PERIOD FORMATTING");
        LOGGER.info(SEPARATOR);

        // Duration formatting
        Duration duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(45);
        LOGGER.info(() -> "   Duration ISO    : " + duration);
        LOGGER.info(() -> "   Duration hours  : " + duration.toHours() + "h "
                + duration.toMinutesPart() + "m " + duration.toSecondsPart() + "s");

        // Large duration
        Duration largeDuration = Duration.ofDays(5).plusHours(3).plusMinutes(20);
        LOGGER.info(() -> "   Large duration  : " + formatDuration(largeDuration));

        // Period formatting
        Period period = Period.of(2, 6, 15);
        LOGGER.info(() -> "   Period ISO      : " + period);
        LOGGER.info(() -> "   Period readable : " + formatPeriod(period));

        // Duration between two instants
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-02-24T15:30:00Z");
        Duration between = Duration.between(start, end);
        LOGGER.info(() -> "   Between Jan 1 and Feb 24: " + formatDuration(between));

        // Period between two dates
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 24);
        Period periodBetween = Period.between(startDate, endDate);
        LOGGER.info(() -> "   Period between: " + formatPeriod(periodBetween));
        LOGGER.info("");
    }

    // ============================================================
    // 6. Custom Patterns for Real-World Use
    // ============================================================
    private static void demonstrateCustomPatterns() {
        LOGGER.info("6️⃣ CUSTOM PATTERNS FOR REAL-WORLD USE");
        LOGGER.info(SEPARATOR);

        LocalDateTime dateTime = LocalDateTime.of(2026, 2, 24, 15, 30, 45, 123_456_789);

        // ISO-8601 with offset
        DateTimeFormatter isoWithOffset = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("Asia/Kolkata"));
        LOGGER.info(() -> "   ISO with offset : " + zdt.format(isoWithOffset));

        // REST API friendly format
        DateTimeFormatter restApi = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LOGGER.info(() -> "   REST API format : " + dateTime.format(restApi));

        // Log timestamp format
        DateTimeFormatter logFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LOGGER.info(() -> "   Log format      : " + dateTime.format(logFormat));

        // Human-friendly with day period (Java 16+)
        DateTimeFormatter humanFriendly = DateTimeFormatter.ofPattern(
                "EEEE, MMMM d, yyyy 'at' h:mm B", Locale.US);
        LOGGER.info(() -> "   Human-friendly  : " + dateTime.format(humanFriendly));

        // File name safe format
        DateTimeFormatter fileNameSafe = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LOGGER.info(() -> "   Filename safe   : " + dateTime.format(fileNameSafe));

        // Relative date display
        DateTimeFormatter relativeStyle = DateTimeFormatter.ofPattern(
                "EEE, MMM d 'at' h:mm a", Locale.US);
        LOGGER.info(() -> "   Relative style  : " + dateTime.format(relativeStyle));
        LOGGER.info("");
    }

    /**
     * Formats a {@link Duration} into a human-readable string.
     *
     * @param duration the duration to format
     * @return formatted string like "5d 3h 20m 15s"
     */
    static String formatDuration(Duration duration) {
        long days = duration.toDays();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        var sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s");
        }
        String result = sb.toString().strip();
        return result.isEmpty() ? "0s" : result;
    }

    /**
     * Formats a {@link Period} into a human-readable string.
     *
     * @param period the period to format
     * @return formatted string like "2 years, 6 months, 15 days"
     */
    static String formatPeriod(Period period) {
        var parts = new java.util.ArrayList<String>();
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        if (years > 0) {
            parts.add(years + (years == 1 ? " year" : " years"));
        }
        if (months > 0) {
            parts.add(months + (months == 1 ? " month" : " months"));
        }
        if (days > 0) {
            parts.add(days + (days == 1 ? " day" : " days"));
        }
        return parts.isEmpty() ? "0 days" : String.join(", ", parts);
    }
}
