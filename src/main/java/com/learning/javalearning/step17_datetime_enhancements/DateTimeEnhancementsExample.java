package com.learning.javalearning.step17_datetime_enhancements;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Comprehensive DateTime Enhancements - Java 16+
 *
 * <p>This class demonstrates a wide range of date/time features and enhancements,
 * bringing together core Java 8 Date/Time API concepts with newer improvements from
 * Java 12-16+.</p>
 *
 * <h2>Topics Covered:</h2>
 * <ul>
 *   <li>Temporal adjusters for complex date calculations</li>
 *   <li>Time zone conversions and daylight saving handling</li>
 *   <li>Epoch-based operations and conversions</li>
 *   <li>Date streams for generating date ranges</li>
 *   <li>Year/YearMonth utilities</li>
 *   <li>Business date calculations</li>
 * </ul>
 *
 * @see java.time.temporal.TemporalAdjusters
 * @see java.time.ZonedDateTime
 * @see java.time.LocalDate#datesUntil
 */
public class DateTimeEnhancementsExample {

    private static final Logger LOGGER = Logger.getLogger(DateTimeEnhancementsExample.class.getName());

    private static final String SEPARATOR = "─".repeat(50);
    private static final String SECTION_SEPARATOR = "═".repeat(50);

    private DateTimeEnhancementsExample() {
        // Utility class - prevent instantiation
    }

    public static void main(String[] args) {
        LOGGER.info(SECTION_SEPARATOR);
        LOGGER.info("  Comprehensive DateTime Enhancements");
        LOGGER.info(SECTION_SEPARATOR);

        demonstrateTemporalAdjusters();
        demonstrateTimeZoneConversions();
        demonstrateDateStreams();
        demonstrateEpochOperations();
        demonstrateYearAndYearMonth();
        demonstrateBusinessDateCalculations();
    }

    // ============================================================
    // 1. Temporal Adjusters
    // ============================================================
    private static void demonstrateTemporalAdjusters() {
        LOGGER.info("1️⃣ TEMPORAL ADJUSTERS");
        LOGGER.info(SEPARATOR);

        LocalDate today = LocalDate.of(2026, 2, 24);
        LOGGER.info(() -> "   Today: " + today);

        // Built-in adjusters
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate previousFriday = today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        LocalDate firstSunday = today.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
        LocalDate lastFriday = today.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));

        LOGGER.info(() -> "   First day of month  : " + firstDayOfMonth);
        LOGGER.info(() -> "   Last day of month   : " + lastDayOfMonth);
        LOGGER.info(() -> "   First day of year   : " + firstDayOfYear);
        LOGGER.info(() -> "   Last day of year    : " + lastDayOfYear);
        LOGGER.info(() -> "   Next Monday         : " + nextMonday);
        LOGGER.info(() -> "   Previous Friday     : " + previousFriday);
        LOGGER.info(() -> "   First Sunday in Feb : " + firstSunday);
        LOGGER.info(() -> "   Last Friday in Feb  : " + lastFriday);

        // Custom adjuster: next business day
        LocalDate friday = LocalDate.of(2026, 2, 27); // Friday
        LocalDate nextBusinessDay = getNextBusinessDay(friday);
        LOGGER.info(() -> "   Next business day after " + friday + " (" +
                friday.getDayOfWeek() + "): " + nextBusinessDay);
        LOGGER.info("");
    }

    // ============================================================
    // 2. Time Zone Conversions
    // ============================================================
    private static void demonstrateTimeZoneConversions() {
        LOGGER.info("2️⃣ TIME ZONE CONVERSIONS");
        LOGGER.info(SEPARATOR);

        LocalDateTime eventTime = LocalDateTime.of(2026, 2, 24, 10, 0);
        ZoneId sourceZone = ZoneId.of("America/New_York");
        ZonedDateTime sourceDateTime = eventTime.atZone(sourceZone);

        LOGGER.info(() -> "   Meeting in New York: " + sourceDateTime);

        // Convert to other time zones
        List<ZoneId> targetZones = List.of(
                ZoneId.of("America/Los_Angeles"),
                ZoneId.of("Europe/London"),
                ZoneId.of("Europe/Paris"),
                ZoneId.of("Asia/Kolkata"),
                ZoneId.of("Asia/Tokyo"),
                ZoneId.of("Australia/Sydney")
        );

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern(
                "HH:mm (z, O) EEE", Locale.US);

        for (ZoneId zone : targetZones) {
            ZonedDateTime converted = sourceDateTime.withZoneSameInstant(zone);
            String formatted = converted.format(displayFormatter);
            String display = String.format("   %-25s: %s", zone, formatted);
            LOGGER.info(display);
        }
        LOGGER.info("");

        // OffsetDateTime for precise timestamps
        OffsetDateTime utcNow = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime istNow = utcNow.withOffsetSameInstant(ZoneOffset.ofHoursMinutes(5, 30));
        LOGGER.info(() -> "   UTC now            : " + utcNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        LOGGER.info(() -> "   IST (same instant) : " + istNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        LOGGER.info("");
    }

    // ============================================================
    // 3. Date Streams (Java 9+)
    // ============================================================
    private static void demonstrateDateStreams() {
        LOGGER.info("3️⃣ DATE STREAMS");
        LOGGER.info(SEPARATOR);

        // datesUntil - generate a stream of dates
        LocalDate start = LocalDate.of(2026, 2, 24);
        LocalDate end = LocalDate.of(2026, 3, 3);

        LOGGER.info(() -> "   Dates from " + start + " to " + end + ":");
        List<LocalDate> dateRange = start.datesUntil(end).toList();
        dateRange.forEach(date -> LOGGER.info(() -> "   " + date + " ("
                + date.getDayOfWeek() + ")"));
        LOGGER.info("");

        // datesUntil with step (every 3 days)
        LOGGER.info(() -> "   Every 3 days from " + start + ":");
        start.datesUntil(end.plusWeeks(2), java.time.Period.ofDays(3))
                .forEach(date -> LOGGER.info(() -> "   " + date));
        LOGGER.info("");

        // Business days only
        LOGGER.info("   Business days only:");
        List<LocalDate> businessDays = start.datesUntil(end)
                .filter(DateTimeEnhancementsExample::isBusinessDay)
                .toList();
        businessDays.forEach(date -> LOGGER.info(() -> "   " + date
                + " (" + date.getDayOfWeek() + ")"));
        LOGGER.info("");

        // Monthly sequence
        LOGGER.info("   First of each month in 2026:");
        YearMonth startMonth = YearMonth.of(2026, 1);
        Stream.iterate(startMonth, ym -> ym.isBefore(YearMonth.of(2027, 1)), ym -> ym.plusMonths(1))
                .forEach(ym -> LOGGER.info(() -> "   " + ym.atDay(1)
                        + " (" + ym.lengthOfMonth() + " days)"));
        LOGGER.info("");
    }

    // ============================================================
    // 4. Epoch Operations
    // ============================================================
    private static void demonstrateEpochOperations() {
        LOGGER.info("4️⃣ EPOCH OPERATIONS");
        LOGGER.info(SEPARATOR);

        // Epoch seconds and millis
        Instant now = Instant.now();
        long epochSecond = now.getEpochSecond();
        long epochMilli = now.toEpochMilli();

        LOGGER.info(() -> "   Current instant    : " + now);
        LOGGER.info(() -> "   Epoch seconds      : " + epochSecond);
        LOGGER.info(() -> "   Epoch milliseconds : " + epochMilli);

        // Convert from epoch
        Instant fromSeconds = Instant.ofEpochSecond(epochSecond);
        Instant fromMillis = Instant.ofEpochMilli(epochMilli);
        LOGGER.info(() -> "   From epoch seconds : " + fromSeconds);
        LOGGER.info(() -> "   From epoch millis  : " + fromMillis);

        // Epoch day for LocalDate
        LocalDate date = LocalDate.of(2026, 2, 24);
        long epochDay = date.toEpochDay();
        LocalDate fromEpochDay = LocalDate.ofEpochDay(epochDay);
        LOGGER.info(() -> "   Date epoch day     : " + epochDay);
        LOGGER.info(() -> "   From epoch day     : " + fromEpochDay);

        // Instant to LocalDateTime (needs zone)
        LocalDateTime localFromInstant = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        LOGGER.info(() -> "   Instant -> LocalDT : " + localFromInstant);

        // Duration between instants
        Instant past = Instant.parse("2000-01-01T00:00:00Z");
        long daysBetween = ChronoUnit.DAYS.between(past, now);
        LOGGER.info(() -> "   Days since Y2K     : " + daysBetween);
        LOGGER.info("");
    }

    // ============================================================
    // 5. Year and YearMonth Utilities
    // ============================================================
    private static void demonstrateYearAndYearMonth() {
        LOGGER.info("5️⃣ YEAR AND YEARMONTH UTILITIES");
        LOGGER.info(SEPARATOR);

        // Year utilities
        Year currentYear = Year.of(2026);
        LOGGER.info(() -> "   Year             : " + currentYear);
        LOGGER.info(() -> "   Is leap year     : " + currentYear.isLeap());
        LOGGER.info(() -> "   Length            : " + currentYear.length());

        // Check multiple years for leap
        LOGGER.info("   Leap year check (2020-2032):");
        Stream.iterate(Year.of(2020), y -> y.isBefore(Year.of(2033)), y -> y.plusYears(1))
                .filter(Year::isLeap)
                .forEach(y -> LOGGER.info(() -> "   " + y + " is a leap year"));
        LOGGER.info("");

        // YearMonth utilities
        YearMonth currentMonth = YearMonth.of(2026, 2);
        LOGGER.info(() -> "   YearMonth        : " + currentMonth);
        LOGGER.info(() -> "   Days in month    : " + currentMonth.lengthOfMonth());
        LOGGER.info(() -> "   Is valid day 29  : " + currentMonth.isValidDay(29));
        LOGGER.info(() -> "   Is valid day 30  : " + currentMonth.isValidDay(30));
        LOGGER.info(() -> "   At end of month  : " + currentMonth.atEndOfMonth());

        // All months with their lengths
        LOGGER.info("   2026 months overview:");
        for (Month month : Month.values()) {
            YearMonth ym = Year.of(2026).atMonth(month);
            String display = String.format("   %-10s: %d days", month, ym.lengthOfMonth());
            LOGGER.info(display);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 6. Business Date Calculations
    // ============================================================
    private static void demonstrateBusinessDateCalculations() {
        LOGGER.info("6️⃣ BUSINESS DATE CALCULATIONS");
        LOGGER.info(SEPARATOR);

        LocalDate projectStart = LocalDate.of(2026, 2, 24);

        // Add business days
        int businessDaysToAdd = 10;
        LocalDate deadline = addBusinessDays(projectStart, businessDaysToAdd);
        LOGGER.info(() -> "   Project start      : " + projectStart + " (" +
                projectStart.getDayOfWeek() + ")");
        LOGGER.info(() -> "   Add " + businessDaysToAdd + " business days: " +
                deadline + " (" + deadline.getDayOfWeek() + ")");

        // Count business days between dates
        LocalDate quarterEnd = LocalDate.of(2026, 3, 31);
        long businessDayCount = countBusinessDays(projectStart, quarterEnd);
        LOGGER.info(() -> "   Business days until " + quarterEnd + ": " + businessDayCount);

        // Group dates by week
        LOGGER.info("");
        LOGGER.info("   February 2026 weeks:");
        LocalDate monthStart = LocalDate.of(2026, 2, 1);
        LocalDate monthEnd = LocalDate.of(2026, 2, 28);

        Map<Integer, List<LocalDate>> weeks = monthStart.datesUntil(monthEnd.plusDays(1))
                .collect(Collectors.groupingBy(
                        d -> d.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                        TreeMap::new,
                        Collectors.toList()
                ));

        weeks.forEach((weekNum, dates) -> {
            String dateStr = dates.stream()
                    .map(d -> String.valueOf(d.getDayOfMonth()))
                    .collect(Collectors.joining(", "));
            LOGGER.info(() -> "   Week " + weekNum + ": " + dateStr);
        });
        LOGGER.info("");
    }

    // ============================================================
    // Utility Methods
    // ============================================================

    /**
     * Returns the next business day (skipping weekends).
     *
     * @param date the starting date
     * @return the next business day after the given date
     */
    static LocalDate getNextBusinessDay(LocalDate date) {
        LocalDate next = date.plusDays(1);
        while (!isBusinessDay(next)) {
            next = next.plusDays(1);
        }
        return next;
    }

    /**
     * Checks if a given date is a business day (Monday to Friday).
     *
     * @param date the date to check
     * @return {@code true} if the date is a weekday
     */
    static boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    /**
     * Adds the specified number of business days to a date.
     *
     * @param startDate    the starting date
     * @param businessDays the number of business days to add (must be non-negative)
     * @return the resulting date after adding business days
     */
    static LocalDate addBusinessDays(LocalDate startDate, int businessDays) {
        if (businessDays < 0) {
            throw new IllegalArgumentException("businessDays must be non-negative: " + businessDays);
        }
        LocalDate result = startDate;
        int added = 0;
        while (added < businessDays) {
            result = result.plusDays(1);
            if (isBusinessDay(result)) {
                added++;
            }
        }
        return result;
    }

    /**
     * Counts the number of business days between two dates (exclusive of end date).
     *
     * @param start the start date (inclusive)
     * @param end   the end date (exclusive)
     * @return the number of business days in the range
     */
    static long countBusinessDays(LocalDate start, LocalDate end) {
        return start.datesUntil(end)
                .filter(DateTimeEnhancementsExample::isBusinessDay)
                .count();
    }
}
