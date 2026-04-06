package com.learning.javalearning.step17_datetime_enhancements;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Day Period Support - Java 16 (JEP 396 / CLDR update)
 *
 * <p>Java 16 introduced the 'B' pattern symbol for day period formatting, which provides
 * more natural, human-friendly time descriptions such as "in the morning", "in the afternoon",
 * "at night", etc. This is a CLDR (Common Locale Data Repository) feature exposed through
 * {@link DateTimeFormatter}.</p>
 *
 * <h2>Key Points:</h2>
 * <ul>
 *   <li>'B' pattern produces locale-aware day period names</li>
 *   <li>More natural than AM/PM for many locales</li>
 *   <li>Day period boundaries vary by locale (cultural convention)</li>
 *   <li>Supports abbreviated ('B'), wide ('BBBB'), and narrow ('BBBBB') formats</li>
 * </ul>
 *
 * <h2>Day Period Boundaries (en_US):</h2>
 * <ul>
 *   <li>midnight: 00:00</li>
 *   <li>in the morning: 06:00 - 11:59</li>
 *   <li>noon: 12:00</li>
 *   <li>in the afternoon: 12:01 - 17:59</li>
 *   <li>in the evening: 18:00 - 20:59</li>
 *   <li>at night: 21:00 - 05:59</li>
 * </ul>
 *
 * @see java.time.format.DateTimeFormatter
 * @see java.time.LocalTime
 */
public class DayPeriodExample {

    private static final Logger LOGGER = Logger.getLogger(DayPeriodExample.class.getName());

    private static final String SEPARATOR = "─".repeat(50);
    private static final String SECTION_SEPARATOR = "═".repeat(50);
    private static final String TIME_PERIOD_PATTERN = "h:mm B";
    private static final String TIME_LABEL_PREFIX = "   Time: ";

    private DayPeriodExample() {
        // Utility class - prevent instantiation
    }

    public static void main(String[] args) {
        LOGGER.info(SECTION_SEPARATOR);
        LOGGER.info("  Day Period Support (Java 16) - 'B' Pattern");
        LOGGER.info(SECTION_SEPARATOR);

        demonstrateBasicDayPeriods();
        demonstrateDayPeriodFormats();
        demonstrateTimesOfDay();
        demonstrateLocaleSpecificPeriods();
    }

    // ============================================================
    // 1. Basic Day Period Usage
    // ============================================================
    private static void demonstrateBasicDayPeriods() {
        LOGGER.info("1️⃣ BASIC DAY PERIOD USAGE");
        LOGGER.info(SEPARATOR);

        // 'B' pattern gives day period names like "in the morning", "in the afternoon"
        DateTimeFormatter dayPeriodFormatter = DateTimeFormatter.ofPattern(TIME_PERIOD_PATTERN, Locale.US);

        LocalTime morning = LocalTime.of(9, 30);
        LocalTime afternoon = LocalTime.of(14, 15);
        LocalTime evening = LocalTime.of(19, 45);

        LOGGER.info(() -> "   Morning  : " + morning.format(dayPeriodFormatter));
        LOGGER.info(() -> "   Afternoon: " + afternoon.format(dayPeriodFormatter));
        LOGGER.info(() -> "   Evening  : " + evening.format(dayPeriodFormatter));

        // Compare with traditional AM/PM
        DateTimeFormatter amPmFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
        LOGGER.info("");
        LOGGER.info("   Compare with AM/PM:");
        LOGGER.info(() -> "   Morning   (AM/PM): " + morning.format(amPmFormatter));
        LOGGER.info(() -> "   Afternoon (AM/PM): " + afternoon.format(amPmFormatter));
        LOGGER.info(() -> "   Evening   (AM/PM): " + evening.format(amPmFormatter));
        LOGGER.info("");
    }

    // ============================================================
    // 2. Day Period Format Variations (B, BBBB, BBBBB)
    // ============================================================
    private static void demonstrateDayPeriodFormats() {
        LOGGER.info("2️⃣ DAY PERIOD FORMAT VARIATIONS");
        LOGGER.info(SEPARATOR);

        LocalTime sampleTime = LocalTime.of(15, 30);

        // B    = abbreviated ("in the afternoon")
        DateTimeFormatter abbreviated = DateTimeFormatter.ofPattern(TIME_PERIOD_PATTERN, Locale.US);
        // BBBB = wide/full ("in the afternoon")
        DateTimeFormatter wide = DateTimeFormatter.ofPattern("h:mm BBBB", Locale.US);
        // BBBBB = narrow ("in the afternoon")
        DateTimeFormatter narrow = DateTimeFormatter.ofPattern("h:mm BBBBB", Locale.US);

        LOGGER.info(() -> TIME_LABEL_PREFIX + sampleTime);
        LOGGER.info(() -> "   Abbreviated (B)    : " + sampleTime.format(abbreviated));
        LOGGER.info(() -> "   Wide (BBBB)        : " + sampleTime.format(wide));
        LOGGER.info(() -> "   Narrow (BBBBB)     : " + sampleTime.format(narrow));
        LOGGER.info("");

        // With full date-time pattern
        DateTimeFormatter fullPattern = DateTimeFormatter.ofPattern("h:mm:ss B", Locale.US);
        LOGGER.info(() -> "   With seconds: " + sampleTime.format(fullPattern));
        LOGGER.info("");
    }

    // ============================================================
    // 3. All Times of Day
    // ============================================================
    private static void demonstrateTimesOfDay() {
        LOGGER.info("3️⃣ DAY PERIODS ACROSS 24 HOURS");
        LOGGER.info(SEPARATOR);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm -> " + TIME_PERIOD_PATTERN, Locale.US);

        // Show representative times for each day period
        List<LocalTime> representativeTimes = List.of(
                LocalTime.MIDNIGHT,
                LocalTime.of(3, 0),
                LocalTime.of(6, 0),
                LocalTime.of(9, 0),
                LocalTime.NOON,
                LocalTime.of(15, 0),
                LocalTime.of(18, 0),
                LocalTime.of(21, 0)
        );

        for (LocalTime time : representativeTimes) {
            String formatted = time.format(formatter);
            LOGGER.info(() -> "   " + formatted);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 4. Locale-Specific Day Periods
    // ============================================================
    private static void demonstrateLocaleSpecificPeriods() {
        LOGGER.info("4️⃣ LOCALE-SPECIFIC DAY PERIODS");
        LOGGER.info(SEPARATOR);

        LocalTime afternoonTime = LocalTime.of(15, 30);

        List<Locale> locales = List.of(
                Locale.US,
                Locale.UK,
                Locale.FRANCE,
                Locale.GERMANY,
                Locale.JAPAN,
                Locale.CHINA,
                Locale.ITALY,
                Locale.of("es", "ES"),
                Locale.of("pt", "BR"),
                Locale.of("hi", "IN")
        );

        LOGGER.info(() -> TIME_LABEL_PREFIX + afternoonTime + " across locales:");
        LOGGER.info("");

        for (Locale locale : locales) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PERIOD_PATTERN, locale);
            String formatted = afternoonTime.format(formatter);
            String localeDisplay = String.format("   %-20s: %s", locale.getDisplayName(Locale.US), formatted);
            LOGGER.info(localeDisplay);
        }
        LOGGER.info("");

        // Show morning across locales
        LocalTime morningTime = LocalTime.of(8, 0);
        LOGGER.info(() -> TIME_LABEL_PREFIX + morningTime + " across locales:");
        LOGGER.info("");

        for (Locale locale : locales) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PERIOD_PATTERN, locale);
            String formatted = morningTime.format(formatter);
            String localeDisplay = String.format("   %-20s: %s", locale.getDisplayName(Locale.US), formatted);
            LOGGER.info(localeDisplay);
        }
        LOGGER.info("");
    }
}
