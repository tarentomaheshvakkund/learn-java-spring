package com.learning.javalearning.step16_java8_essentials;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Step 16: Java 8 Date/Time API (java.time package)
 *
 * The new Date/Time API (JSR 310) replaces the problematic java.util.Date
 * and java.util.Calendar with immutable, thread-safe classes.
 *
 * Key classes:
 * - LocalDate, LocalTime, LocalDateTime - no timezone
 * - ZonedDateTime, OffsetDateTime - with timezone
 * - Instant - machine timestamp
 * - Duration - time-based amount
 * - Period - date-based amount
 * - DateTimeFormatter - parsing and formatting
 */
public class DateTimeAPIExample {

    private static final Logger logger = Logger.getLogger(DateTimeAPIExample.class.getName());

    private static final String SEPARATOR = "─────────────────────────────────\n";
    private static final String HH_MM_Z   = "HH:mm z";
    private static final String FMT_PARSED = "   Parsed: {0}";
    private static final String FMT_NOW    = "   Now: {0}";
    private static final String ZONE_TOKYO     = "Asia/Tokyo";
    private static final String ZONE_NEW_YORK  = "America/New_York";
    private static final String ZONE_KOLKATA   = "Asia/Kolkata";

    public static void main(String[] args) {
        logger.info("═══════════════════════════════════════════");
        logger.info("  Step 16: Date/Time API (java.time)      ");
        logger.info("═══════════════════════════════════════════\n");

        localDateExamples();
        localTimeExamples();
        localDateTimeExamples();
        zonedDateTimeExamples();
        instantExamples();
        durationAndPeriod();
        dateTimeFormatting();
        temporalAdjusters();
        legacyConversion();
        realWorldExamples();
    }

    // ============================================================
    // 1. LocalDate - Date without Time or Timezone
    // ============================================================
    static void localDateExamples() {
        logger.info("1\uFE0F\u20E3 LOCAL DATE (Date without Time)");
        logger.info(SEPARATOR);

        // Creating LocalDate
        LocalDate today = LocalDate.now();
        LocalDate specific = LocalDate.of(2024, 3, 15);
        LocalDate parsed = LocalDate.parse("2024-06-20");
        LocalDate fromDayOfYear = LocalDate.ofYearDay(2024, 100); // 100th day of 2024

        logger.log(Level.INFO, "   Today: {0}", today);
        logger.log(Level.INFO, "   Specific: {0}", specific);
        logger.log(Level.INFO, FMT_PARSED, parsed);
        logger.log(Level.INFO, "   100th day of 2024: {0}", fromDayOfYear);

        // Extracting components
        logger.log(Level.INFO, "\n   Components of {0}:", today);
        logger.log(Level.INFO, "   Year: {0}", today.getYear());
        logger.log(Level.INFO, "   Month: {0} ({1})", new Object[]{today.getMonth(), today.getMonthValue()});
        logger.log(Level.INFO, "   Day: {0}", today.getDayOfMonth());
        logger.log(Level.INFO, "   Day of Week: {0}", today.getDayOfWeek());
        logger.log(Level.INFO, "   Day of Year: {0}", today.getDayOfYear());
        logger.log(Level.INFO, "   Is Leap Year: {0}", today.isLeapYear());
        logger.log(Level.INFO, "   Month Length: {0}", today.lengthOfMonth());

        // Manipulation (immutable - returns new instance)
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextMonth = today.plusMonths(1);
        LocalDate lastYear = today.minusYears(1);
        LocalDate withDay = today.withDayOfMonth(1); // First of month

        logger.log(Level.INFO, "\n   Tomorrow: {0}", tomorrow);
        logger.log(Level.INFO, "   Next Month: {0}", nextMonth);
        logger.log(Level.INFO, "   Last Year: {0}", lastYear);
        logger.log(Level.INFO, "   First of Month: {0}", withDay);

        // Comparison
        logger.log(Level.INFO, "\n   today.isBefore(tomorrow): {0}", today.isBefore(tomorrow));
        logger.log(Level.INFO, "   today.isAfter(lastYear): {0}", today.isAfter(lastYear));
        logger.log(Level.INFO, "   today.isEqual(today): {0}", today.isEqual(LocalDate.now()));

        logger.info("");
    }

    // ============================================================
    // 2. LocalTime - Time without Date or Timezone
    // ============================================================
    static void localTimeExamples() {
        logger.info("2\uFE0F\u20E3 LOCAL TIME (Time without Date)");
        logger.info(SEPARATOR);

        LocalTime now = LocalTime.now();
        LocalTime morning = LocalTime.of(9, 30);
        LocalTime precise = LocalTime.of(14, 30, 45, 123456789); // h, m, s, ns
        LocalTime parsed = LocalTime.parse("16:45:30");
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalTime noon = LocalTime.NOON;

        logger.log(Level.INFO, FMT_NOW, now);
        logger.log(Level.INFO, "   Morning: {0}", morning);
        logger.log(Level.INFO, "   Precise: {0}", precise);
        logger.log(Level.INFO, FMT_PARSED, parsed);
        logger.log(Level.INFO, "   Midnight: {0}", midnight);
        logger.log(Level.INFO, "   Noon: {0}", noon);

        // Components
        logger.log(Level.INFO, "\n   Hour: {0}", now.getHour());
        logger.log(Level.INFO, "   Minute: {0}", now.getMinute());
        logger.log(Level.INFO, "   Second: {0}", now.getSecond());

        // Manipulation
        LocalTime later = now.plusHours(2).plusMinutes(30);
        logger.log(Level.INFO, "   2h 30m later: {0}", later);

        logger.info("");
    }

    // ============================================================
    // 3. LocalDateTime - Date + Time without Timezone
    // ============================================================
    static void localDateTimeExamples() {
        logger.info("3\uFE0F\u20E3 LOCAL DATE TIME (Date + Time, no TZ)");
        logger.info(SEPARATOR);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime specific = LocalDateTime.of(2024, 12, 25, 10, 30, 0);
        LocalDateTime combined = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
        LocalDateTime parsed = LocalDateTime.parse("2024-06-20T15:30:00");

        logger.log(Level.INFO, FMT_NOW, now);
        logger.log(Level.INFO, "   Christmas Morning: {0}", specific);
        logger.log(Level.INFO, "   Today at Noon: {0}", combined);
        logger.log(Level.INFO, FMT_PARSED, parsed);

        // Convert between types
        LocalDate dateOnly = now.toLocalDate();
        LocalTime timeOnly = now.toLocalTime();
        logger.log(Level.INFO, "\n   Date only: {0}", dateOnly);
        logger.log(Level.INFO, "   Time only: {0}", timeOnly);

        // Truncation
        LocalDateTime truncated = now.truncatedTo(ChronoUnit.HOURS);
        logger.log(Level.INFO, "   Truncated to hour: {0}", truncated);

        logger.info("");
    }

    // ============================================================
    // 4. ZonedDateTime - Full Date/Time with Timezone
    // ============================================================
    static void zonedDateTimeExamples() {
        logger.info("4\uFE0F\u20E3 ZONED DATE TIME (with Timezone)");
        logger.info(SEPARATOR);

        // Available zones
        logger.log(Level.INFO, "   Total timezone IDs: {0}", ZoneId.getAvailableZoneIds().size());

        // Creating ZonedDateTime
        ZonedDateTime nowHere = ZonedDateTime.now();
        ZonedDateTime tokyo   = ZonedDateTime.now(ZoneId.of(ZONE_TOKYO));
        ZonedDateTime newYork = ZonedDateTime.now(ZoneId.of(ZONE_NEW_YORK));
        ZonedDateTime london  = ZonedDateTime.now(ZoneId.of("Europe/London"));
        ZonedDateTime kolkata = ZonedDateTime.now(ZoneId.of(ZONE_KOLKATA));

        logger.log(Level.INFO, "   Local:    {0}", nowHere);
        logger.log(Level.INFO, "   Tokyo:    {0}", tokyo.format(DateTimeFormatter.ofPattern(HH_MM_Z)));
        logger.log(Level.INFO, "   New York: {0}", newYork.format(DateTimeFormatter.ofPattern(HH_MM_Z)));
        logger.log(Level.INFO, "   London:   {0}", london.format(DateTimeFormatter.ofPattern(HH_MM_Z)));
        logger.log(Level.INFO, "   Kolkata:  {0}", kolkata.format(DateTimeFormatter.ofPattern(HH_MM_Z)));

        // Convert between timezones
        ZonedDateTime meeting = ZonedDateTime.of(2024, 6, 20, 14, 0, 0, 0, ZoneId.of(ZONE_NEW_YORK));
        ZonedDateTime meetingInTokyo = meeting.withZoneSameInstant(ZoneId.of(ZONE_TOKYO));
        logger.log(Level.INFO, "\n   Meeting at 2 PM New York = {0} in Tokyo",
            meetingInTokyo.format(DateTimeFormatter.ofPattern("h:mm a z")));

        // ZoneOffset
        ZoneOffset offset = ZoneOffset.of("+05:30"); // IST
        OffsetDateTime offsetDT = OffsetDateTime.now(offset);
        logger.log(Level.INFO, "   IST Offset: {0}", offsetDT);

        logger.info("");
    }

    // ============================================================
    // 5. Instant - Machine Timestamp
    // ============================================================
    static void instantExamples() {
        logger.info("5\uFE0F\u20E3 INSTANT (Machine Timestamp)");
        logger.info(SEPARATOR);

        Instant now = Instant.now();
        Instant epoch = Instant.EPOCH; // 1970-01-01T00:00:00Z
        Instant fromEpochSecond = Instant.ofEpochSecond(1_000_000_000L);
        Instant fromEpochMilli = Instant.ofEpochMilli(System.currentTimeMillis());

        logger.log(Level.INFO, FMT_NOW, now);
        logger.log(Level.INFO, "   Epoch: {0}", epoch);
        logger.log(Level.INFO, "   1 Billion seconds: {0}", fromEpochSecond);
        logger.log(Level.INFO, "   From millis: {0}", fromEpochMilli);

        // Epoch values
        logger.log(Level.INFO, "   Epoch seconds: {0}", now.getEpochSecond());
        logger.log(Level.INFO, "   Epoch millis: {0}", now.toEpochMilli());

        // Arithmetic
        Instant later = now.plus(Duration.ofHours(5));
        logger.log(Level.INFO, "   5 hours later: {0}", later);

        // Convert to ZonedDateTime
        ZonedDateTime zdt = now.atZone(ZoneId.of(ZONE_KOLKATA));
        logger.log(Level.INFO, "   As IST: {0}",
            zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));

        logger.info("");
    }

    // ============================================================
    // 6. Duration and Period
    // ============================================================
    static void durationAndPeriod() {
        logger.info("6\uFE0F\u20E3 DURATION (time-based) & PERIOD (date-based)");
        logger.info(SEPARATOR);

        // Duration - time-based (hours, minutes, seconds, nanos)
        Duration twoHours      = Duration.ofHours(2);
        Duration thirtyMinutes = Duration.ofMinutes(30);
        Duration complex       = Duration.ofHours(2).plusMinutes(30).plusSeconds(45);
        Duration parsed        = Duration.parse("PT2H30M"); // ISO-8601

        logger.log(Level.INFO, "   Duration 2 hours: {0}", twoHours);
        logger.log(Level.INFO, "   Duration 30 min: {0}", thirtyMinutes);
        logger.log(Level.INFO, "   Duration 5 sec: {0}", Duration.ofSeconds(5));
        logger.log(Level.INFO, "   Duration complex: {0}", complex);
        logger.log(Level.INFO, "   Duration parsed: {0}", parsed);

        // Duration between
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 30);
        Duration workDay = Duration.between(start, end);
        logger.log(Level.INFO, "   Work day: {0}h {1}m",
            new Object[]{workDay.toHours(), workDay.toMinutes() % 60});

        // Period - date-based (years, months, days)
        logger.info("");
        Period oneYear      = Period.ofYears(1);
        Period complex2     = Period.of(1, 6, 15); // 1 year, 6 months, 15 days
        Period parsedPeriod = Period.parse("P1Y6M15D"); // ISO-8601

        logger.log(Level.INFO, "   Period 1 year: {0}", oneYear);
        logger.log(Level.INFO, "   Period 2 months: {0}", Period.ofMonths(2));
        logger.log(Level.INFO, "   Period 10 days: {0}", Period.ofDays(10));
        logger.log(Level.INFO, "   Period complex: {0}", complex2);
        logger.log(Level.INFO, "   Period parsed: {0}", parsedPeriod);

        // Period between dates
        LocalDate birthday = LocalDate.of(1990, 5, 15);
        LocalDate today    = LocalDate.now();
        Period age         = Period.between(birthday, today);
        logger.log(Level.INFO, "   Age: {0} years, {1} months, {2} days",
            new Object[]{age.getYears(), age.getMonths(), age.getDays()});

        logger.info("");
    }

    // ============================================================
    // 7. DateTimeFormatter - Formatting & Parsing
    // ============================================================
    static void dateTimeFormatting() {
        logger.info("7\uFE0F\u20E3 DATE TIME FORMATTING & PARSING");
        logger.info(SEPARATOR);

        LocalDateTime now = LocalDateTime.now();

        // Built-in formatters
        logger.log(Level.INFO, "   ISO_LOCAL_DATE: {0}",      now.format(DateTimeFormatter.ISO_LOCAL_DATE));
        logger.log(Level.INFO, "   ISO_LOCAL_TIME: {0}",      now.format(DateTimeFormatter.ISO_LOCAL_TIME));
        logger.log(Level.INFO, "   ISO_LOCAL_DATE_TIME: {0}", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Custom patterns
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter f3 = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        DateTimeFormatter f4 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        DateTimeFormatter f5 = DateTimeFormatter.ofPattern("hh:mm a");

        logger.info("\n   Custom Patterns:");
        logger.log(Level.INFO, "   dd/MM/yyyy:          {0}", now.format(f1));
        logger.log(Level.INFO, "   MMMM dd, yyyy:       {0}", now.format(f2));
        logger.log(Level.INFO, "   dd-MMM-yyyy HH:mm:   {0}", now.format(f3));
        logger.log(Level.INFO, "   E, MMM dd yyyy:      {0}", now.format(f4));
        logger.log(Level.INFO, "   hh:mm a:             {0}", now.format(f5));

        // Localized formatting
        DateTimeFormatter german = DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN);
        DateTimeFormatter french = DateTimeFormatter.ofPattern("dd MMMM yyyy",  Locale.FRENCH);
        logger.log(Level.INFO, "\n   German: {0}", now.format(german));
        logger.log(Level.INFO, "   French: {0}", now.format(french));

        // Parsing
        LocalDate parsedDate = LocalDate.parse("20/06/2024", f1);
        logger.log(Level.INFO, "\n   Parsed ''20/06/2024'': {0}", parsedDate);

        // Common format pattern reference
        logger.info("\n   \uD83D\uDCCB Pattern Symbol Reference:");
        logger.info("   \u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u252C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510");
        logger.info("   \u2502 Symbol   \u2502 Meaning                        \u2502");
        logger.info("   \u251C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2524");
        logger.info("   \u2502 yyyy     \u2502 Year (2024)                    \u2502");
        logger.info("   \u2502 MM       \u2502 Month number (01-12)           \u2502");
        logger.info("   \u2502 MMM      \u2502 Month short (Jan, Feb)         \u2502");
        logger.info("   \u2502 MMMM     \u2502 Month full (January)           \u2502");
        logger.info("   \u2502 dd       \u2502 Day of month (01-31)           \u2502");
        logger.info("   \u2502 E        \u2502 Day of week short (Mon)        \u2502");
        logger.info("   \u2502 EEEE     \u2502 Day of week full (Monday)      \u2502");
        logger.info("   \u2502 HH       \u2502 Hour 24h (00-23)               \u2502");
        logger.info("   \u2502 hh       \u2502 Hour 12h (01-12)               \u2502");
        logger.info("   \u2502 mm       \u2502 Minute (00-59)                 \u2502");
        logger.info("   \u2502 ss       \u2502 Second (00-59)                 \u2502");
        logger.info("   \u2502 a        \u2502 AM/PM                          \u2502");
        logger.info("   \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518");

        logger.info("");
    }

    // ============================================================
    // 8. Temporal Adjusters
    // ============================================================
    static void temporalAdjusters() {
        logger.info("8\uFE0F\u20E3 TEMPORAL ADJUSTERS (Smart Date Math)");
        logger.info(SEPARATOR);

        LocalDate today = LocalDate.now();

        // Built-in adjusters
        LocalDate firstDayOfMonth    = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth     = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfYear     = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfYear      = today.with(TemporalAdjusters.lastDayOfYear());
        LocalDate nextMonday         = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate prevFriday         = today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        LocalDate firstMondayOfMonth = today.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        logger.log(Level.INFO, "   Today: {0}", today);
        logger.log(Level.INFO, "   First of month: {0}", firstDayOfMonth);
        logger.log(Level.INFO, "   Last of month: {0}", lastDayOfMonth);
        logger.log(Level.INFO, "   First of year: {0}", firstDayOfYear);
        logger.log(Level.INFO, "   Last of year: {0}", lastDayOfYear);
        logger.log(Level.INFO, "   Next Monday: {0}", nextMonday);
        logger.log(Level.INFO, "   Previous Friday: {0}", prevFriday);
        logger.log(Level.INFO, "   First Monday of month: {0}", firstMondayOfMonth);

        // Custom adjuster - next working day
        TemporalAdjuster nextWorkingDay = temporal -> {
            LocalDate date = LocalDate.from(temporal);
            DayOfWeek dow = date.getDayOfWeek();
            return switch (dow) {
                case FRIDAY   -> date.plusDays(3);
                case SATURDAY -> date.plusDays(2);
                default       -> date.plusDays(1);
            };
        };
        logger.log(Level.INFO, "   Next working day: {0}", today.with(nextWorkingDay));

        logger.info("");
    }

    // ============================================================
    // 9. Legacy Conversion
    // ============================================================
    static void legacyConversion() {
        logger.info("9\uFE0F\u20E3 LEGACY DATE/CALENDAR CONVERSION");
        logger.info(SEPARATOR);

        // Date → Instant → LocalDateTime
        Date legacyDate = new Date();
        Instant instant = legacyDate.toInstant();
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        logger.log(Level.INFO, "   Date \u2192 LocalDateTime: {0}", ldt);

        // LocalDateTime → Instant → Date
        LocalDateTime now = LocalDateTime.now();
        Instant inst = now.atZone(ZoneId.systemDefault()).toInstant();
        Date backToDate = Date.from(inst);
        logger.log(Level.INFO, "   LocalDateTime \u2192 Date: {0}", backToDate);

        // Calendar → ZonedDateTime
        Calendar calendar = Calendar.getInstance();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(calendar.toInstant(),
            calendar.getTimeZone().toZoneId());
        logger.log(Level.INFO, "   Calendar \u2192 ZonedDateTime: {0}", zdt);

        // Conversion reference
        logger.info("\n   \uD83D\uDCCB Conversion Cheat Sheet:");
        logger.info("   \u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u252C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510");
        logger.info("   \u2502 From                 \u2502 To                                   \u2502");
        logger.info("   \u251C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2524");
        logger.info("   \u2502 Date                 \u2502 date.toInstant()                     \u2502");
        logger.info("   \u2502 Instant              \u2502 Date.from(instant)                   \u2502");
        logger.info("   \u2502 Calendar             \u2502 cal.toInstant() + ZoneId             \u2502");
        logger.info("   \u2502 java.sql.Date        \u2502 sqlDate.toLocalDate()                \u2502");
        logger.info("   \u2502 java.sql.Timestamp   \u2502 ts.toLocalDateTime()                 \u2502");
        logger.info("   \u2502 TimeZone             \u2502 tz.toZoneId()                        \u2502");
        logger.info("   \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518");

        logger.info("");
    }

    // ============================================================
    // 10. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        logger.info("\uD83D\uDD1F REAL-WORLD DATE/TIME EXAMPLES");
        logger.info(SEPARATOR);

        // Example 1: Calculate business days between dates
        logger.info("   \uD83D\uDCCC Business Days Calculator:");
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate   = LocalDate.of(2024, 6, 30);
        long businessDays = startDate.datesUntil(endDate)
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY &&
                             d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
        logger.log(Level.INFO, "   Business days in June 2024: {0}", businessDays);

        // Example 2: Generate monthly report dates
        logger.info("\n   \uD83D\uDCCC Last Day of Each Month 2024:");
        LocalDate jan = LocalDate.of(2024, 1, 1);
        for (int i = 0; i < 12; i++) {
            LocalDate lastDay = jan.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
            logger.log(Level.INFO, "   {0}: {1}", new Object[]{lastDay.getMonth(), lastDay});
        }

        // Example 3: Time zone-aware meeting scheduler
        logger.info("\n   \uD83D\uDCCC Meeting Scheduler (10 AM IST):");
        ZonedDateTime meetingIST = ZonedDateTime.of(2024, 6, 20, 10, 0, 0, 0,
                ZoneId.of(ZONE_KOLKATA));
        String[] cities = {ZONE_NEW_YORK, "Europe/London", ZONE_TOKYO, "Australia/Sydney"};
        for (String city : cities) {
            ZonedDateTime local = meetingIST.withZoneSameInstant(ZoneId.of(city));
            logger.log(Level.INFO, "   {0}: {1}",
                new Object[]{city.split("/")[1],
                             local.format(DateTimeFormatter.ofPattern("hh:mm a (z)"))});
        }

        // Example 4: Age calculator
        logger.info("\n   \uD83D\uDCCC Age Calculator:");
        LocalDate dob = LocalDate.of(1995, 8, 24);
        Period age = Period.between(dob, LocalDate.now());
        logger.log(Level.INFO, "   Born: {0}", dob);
        logger.log(Level.INFO, "   Age: {0} years, {1} months, {2} days",
            new Object[]{age.getYears(), age.getMonths(), age.getDays()});

        logger.info("\n\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        logger.info("  \u2705 Date/Time API: Complete!              ");
        logger.info("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
    }
}
