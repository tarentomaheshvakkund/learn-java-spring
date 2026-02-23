package com.learning.javalearning.step16_java8_essentials;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

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

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  Step 16: Date/Time API (java.time)      ");
        System.out.println("═══════════════════════════════════════════\n");

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
        System.out.println("1️⃣ LOCAL DATE (Date without Time)");
        System.out.println("─────────────────────────────────\n");

        // Creating LocalDate
        LocalDate today = LocalDate.now();
        LocalDate specific = LocalDate.of(2024, 3, 15);
        LocalDate parsed = LocalDate.parse("2024-06-20");
        LocalDate fromDayOfYear = LocalDate.ofYearDay(2024, 100); // 100th day of 2024

        System.out.println("   Today: " + today);
        System.out.println("   Specific: " + specific);
        System.out.println("   Parsed: " + parsed);
        System.out.println("   100th day of 2024: " + fromDayOfYear);

        // Extracting components
        System.out.println("\n   Components of " + today + ":");
        System.out.println("   Year: " + today.getYear());
        System.out.println("   Month: " + today.getMonth() + " (" + today.getMonthValue() + ")");
        System.out.println("   Day: " + today.getDayOfMonth());
        System.out.println("   Day of Week: " + today.getDayOfWeek());
        System.out.println("   Day of Year: " + today.getDayOfYear());
        System.out.println("   Is Leap Year: " + today.isLeapYear());
        System.out.println("   Month Length: " + today.lengthOfMonth());

        // Manipulation (immutable - returns new instance)
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextMonth = today.plusMonths(1);
        LocalDate lastYear = today.minusYears(1);
        LocalDate withDay = today.withDayOfMonth(1); // First of month

        System.out.println("\n   Tomorrow: " + tomorrow);
        System.out.println("   Next Month: " + nextMonth);
        System.out.println("   Last Year: " + lastYear);
        System.out.println("   First of Month: " + withDay);

        // Comparison
        System.out.println("\n   today.isBefore(tomorrow): " + today.isBefore(tomorrow));
        System.out.println("   today.isAfter(lastYear): " + today.isAfter(lastYear));
        System.out.println("   today.isEqual(today): " + today.isEqual(LocalDate.now()));

        System.out.println();
    }

    // ============================================================
    // 2. LocalTime - Time without Date or Timezone
    // ============================================================
    static void localTimeExamples() {
        System.out.println("2️⃣ LOCAL TIME (Time without Date)");
        System.out.println("─────────────────────────────────\n");

        LocalTime now = LocalTime.now();
        LocalTime morning = LocalTime.of(9, 30);
        LocalTime precise = LocalTime.of(14, 30, 45, 123456789); // h, m, s, ns
        LocalTime parsed = LocalTime.parse("16:45:30");
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalTime noon = LocalTime.NOON;

        System.out.println("   Now: " + now);
        System.out.println("   Morning: " + morning);
        System.out.println("   Precise: " + precise);
        System.out.println("   Parsed: " + parsed);
        System.out.println("   Midnight: " + midnight);
        System.out.println("   Noon: " + noon);

        // Components
        System.out.println("\n   Hour: " + now.getHour());
        System.out.println("   Minute: " + now.getMinute());
        System.out.println("   Second: " + now.getSecond());

        // Manipulation
        LocalTime later = now.plusHours(2).plusMinutes(30);
        System.out.println("   2h 30m later: " + later);

        System.out.println();
    }

    // ============================================================
    // 3. LocalDateTime - Date + Time without Timezone
    // ============================================================
    static void localDateTimeExamples() {
        System.out.println("3️⃣ LOCAL DATE TIME (Date + Time, no TZ)");
        System.out.println("─────────────────────────────────\n");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime specific = LocalDateTime.of(2024, 12, 25, 10, 30, 0);
        LocalDateTime combined = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
        LocalDateTime parsed = LocalDateTime.parse("2024-06-20T15:30:00");

        System.out.println("   Now: " + now);
        System.out.println("   Christmas Morning: " + specific);
        System.out.println("   Today at Noon: " + combined);
        System.out.println("   Parsed: " + parsed);

        // Convert between types
        LocalDate dateOnly = now.toLocalDate();
        LocalTime timeOnly = now.toLocalTime();
        System.out.println("\n   Date only: " + dateOnly);
        System.out.println("   Time only: " + timeOnly);

        // Truncation
        LocalDateTime truncated = now.truncatedTo(ChronoUnit.HOURS);
        System.out.println("   Truncated to hour: " + truncated);

        System.out.println();
    }

    // ============================================================
    // 4. ZonedDateTime - Full Date/Time with Timezone
    // ============================================================
    static void zonedDateTimeExamples() {
        System.out.println("4️⃣ ZONED DATE TIME (with Timezone)");
        System.out.println("─────────────────────────────────\n");

        // Available zones
        System.out.println("   Total timezone IDs: " + ZoneId.getAvailableZoneIds().size());

        // Creating ZonedDateTime
        ZonedDateTime nowHere = ZonedDateTime.now();
        ZonedDateTime tokyo = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime newYork = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime london = ZonedDateTime.now(ZoneId.of("Europe/London"));
        ZonedDateTime kolkata = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        System.out.println("   Local:    " + nowHere);
        System.out.println("   Tokyo:    " + tokyo.format(DateTimeFormatter.ofPattern("HH:mm z")));
        System.out.println("   New York: " + newYork.format(DateTimeFormatter.ofPattern("HH:mm z")));
        System.out.println("   London:   " + london.format(DateTimeFormatter.ofPattern("HH:mm z")));
        System.out.println("   Kolkata:  " + kolkata.format(DateTimeFormatter.ofPattern("HH:mm z")));

        // Convert between timezones
        ZonedDateTime meeting = ZonedDateTime.of(2024, 6, 20, 14, 0, 0, 0, ZoneId.of("America/New_York"));
        ZonedDateTime meetingInTokyo = meeting.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        System.out.println("\n   Meeting at 2 PM New York = " +
            meetingInTokyo.format(DateTimeFormatter.ofPattern("h:mm a z")) + " in Tokyo");

        // ZoneOffset
        ZoneOffset offset = ZoneOffset.of("+05:30"); // IST
        OffsetDateTime offsetDT = OffsetDateTime.now(offset);
        System.out.println("   IST Offset: " + offsetDT);

        System.out.println();
    }

    // ============================================================
    // 5. Instant - Machine Timestamp
    // ============================================================
    static void instantExamples() {
        System.out.println("5️⃣ INSTANT (Machine Timestamp)");
        System.out.println("─────────────────────────────────\n");

        Instant now = Instant.now();
        Instant epoch = Instant.EPOCH; // 1970-01-01T00:00:00Z
        Instant fromEpochSecond = Instant.ofEpochSecond(1_000_000_000L);
        Instant fromEpochMilli = Instant.ofEpochMilli(System.currentTimeMillis());

        System.out.println("   Now: " + now);
        System.out.println("   Epoch: " + epoch);
        System.out.println("   1 Billion seconds: " + fromEpochSecond);
        System.out.println("   From millis: " + fromEpochMilli);

        // Epoch values
        System.out.println("   Epoch seconds: " + now.getEpochSecond());
        System.out.println("   Epoch millis: " + now.toEpochMilli());

        // Arithmetic
        Instant later = now.plus(Duration.ofHours(5));
        System.out.println("   5 hours later: " + later);

        // Convert to ZonedDateTime
        ZonedDateTime zdt = now.atZone(ZoneId.of("Asia/Kolkata"));
        System.out.println("   As IST: " + zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));

        System.out.println();
    }

    // ============================================================
    // 6. Duration and Period
    // ============================================================
    static void durationAndPeriod() {
        System.out.println("6️⃣ DURATION (time-based) & PERIOD (date-based)");
        System.out.println("─────────────────────────────────\n");

        // Duration - time-based (hours, minutes, seconds, nanos)
        Duration twoHours = Duration.ofHours(2);
        Duration thirtyMinutes = Duration.ofMinutes(30);
        Duration fiveSeconds = Duration.ofSeconds(5);
        Duration complex = Duration.ofHours(2).plusMinutes(30).plusSeconds(45);
        Duration parsed = Duration.parse("PT2H30M"); // ISO-8601

        System.out.println("   Duration 2 hours: " + twoHours);
        System.out.println("   Duration 30 min: " + thirtyMinutes);
        System.out.println("   Duration complex: " + complex);
        System.out.println("   Duration parsed: " + parsed);

        // Duration between
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 30);
        Duration workDay = Duration.between(start, end);
        System.out.println("   Work day: " + workDay.toHours() + "h " + 
            (workDay.toMinutes() % 60) + "m");

        // Period - date-based (years, months, days)
        System.out.println();
        Period oneYear = Period.ofYears(1);
        Period twoMonths = Period.ofMonths(2);
        Period tenDays = Period.ofDays(10);
        Period complex2 = Period.of(1, 6, 15); // 1 year, 6 months, 15 days
        Period parsedPeriod = Period.parse("P1Y6M15D"); // ISO-8601

        System.out.println("   Period 1 year: " + oneYear);
        System.out.println("   Period complex: " + complex2);
        System.out.println("   Period parsed: " + parsedPeriod);

        // Period between dates
        LocalDate birthday = LocalDate.of(1990, 5, 15);
        LocalDate today = LocalDate.now();
        Period age = Period.between(birthday, today);
        System.out.println("   Age: " + age.getYears() + " years, " +
            age.getMonths() + " months, " + age.getDays() + " days");

        System.out.println();
    }

    // ============================================================
    // 7. DateTimeFormatter - Formatting & Parsing
    // ============================================================
    static void dateTimeFormatting() {
        System.out.println("7️⃣ DATE TIME FORMATTING & PARSING");
        System.out.println("─────────────────────────────────\n");

        LocalDateTime now = LocalDateTime.now();

        // Built-in formatters
        System.out.println("   ISO_LOCAL_DATE: " + now.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println("   ISO_LOCAL_TIME: " + now.format(DateTimeFormatter.ISO_LOCAL_TIME));
        System.out.println("   ISO_LOCAL_DATE_TIME: " + now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Custom patterns
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter f3 = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        DateTimeFormatter f4 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        DateTimeFormatter f5 = DateTimeFormatter.ofPattern("hh:mm a");

        System.out.println("\n   Custom Patterns:");
        System.out.println("   dd/MM/yyyy:          " + now.format(f1));
        System.out.println("   MMMM dd, yyyy:       " + now.format(f2));
        System.out.println("   dd-MMM-yyyy HH:mm:   " + now.format(f3));
        System.out.println("   E, MMM dd yyyy:      " + now.format(f4));
        System.out.println("   hh:mm a:             " + now.format(f5));

        // Localized formatting
        DateTimeFormatter german = DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN);
        DateTimeFormatter french = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        System.out.println("\n   German: " + now.format(german));
        System.out.println("   French: " + now.format(french));

        // Parsing
        LocalDate parsedDate = LocalDate.parse("20/06/2024", f1);
        System.out.println("\n   Parsed '20/06/2024': " + parsedDate);

        // Common format pattern reference
        System.out.println("\n   📋 Pattern Symbol Reference:");
        System.out.println("   ┌──────────┬────────────────────────────────┐");
        System.out.println("   │ Symbol   │ Meaning                        │");
        System.out.println("   ├──────────┼────────────────────────────────┤");
        System.out.println("   │ yyyy     │ Year (2024)                    │");
        System.out.println("   │ MM       │ Month number (01-12)           │");
        System.out.println("   │ MMM      │ Month short (Jan, Feb)         │");
        System.out.println("   │ MMMM     │ Month full (January)           │");
        System.out.println("   │ dd       │ Day of month (01-31)           │");
        System.out.println("   │ E        │ Day of week short (Mon)        │");
        System.out.println("   │ EEEE     │ Day of week full (Monday)      │");
        System.out.println("   │ HH       │ Hour 24h (00-23)               │");
        System.out.println("   │ hh       │ Hour 12h (01-12)               │");
        System.out.println("   │ mm       │ Minute (00-59)                 │");
        System.out.println("   │ ss       │ Second (00-59)                 │");
        System.out.println("   │ a        │ AM/PM                          │");
        System.out.println("   └──────────┴────────────────────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 8. Temporal Adjusters
    // ============================================================
    static void temporalAdjusters() {
        System.out.println("8️⃣ TEMPORAL ADJUSTERS (Smart Date Math)");
        System.out.println("─────────────────────────────────\n");

        LocalDate today = LocalDate.now();

        // Built-in adjusters
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate prevFriday = today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        LocalDate firstMondayOfMonth = today.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        System.out.println("   Today: " + today);
        System.out.println("   First of month: " + firstDayOfMonth);
        System.out.println("   Last of month: " + lastDayOfMonth);
        System.out.println("   First of year: " + firstDayOfYear);
        System.out.println("   Last of year: " + lastDayOfYear);
        System.out.println("   Next Monday: " + nextMonday);
        System.out.println("   Previous Friday: " + prevFriday);
        System.out.println("   First Monday of month: " + firstMondayOfMonth);

        // Custom adjuster - next working day
        TemporalAdjuster nextWorkingDay = temporal -> {
            LocalDate date = LocalDate.from(temporal);
            DayOfWeek dow = date.getDayOfWeek();
            return switch (dow) {
                case FRIDAY -> date.plusDays(3);
                case SATURDAY -> date.plusDays(2);
                default -> date.plusDays(1);
            };
        };
        System.out.println("   Next working day: " + today.with(nextWorkingDay));

        System.out.println();
    }

    // ============================================================
    // 9. Legacy Conversion
    // ============================================================
    static void legacyConversion() {
        System.out.println("9️⃣ LEGACY DATE/CALENDAR CONVERSION");
        System.out.println("─────────────────────────────────\n");

        // Date → Instant → LocalDateTime
        Date legacyDate = new Date();
        Instant instant = legacyDate.toInstant();
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        System.out.println("   Date → LocalDateTime: " + ldt);

        // LocalDateTime → Instant → Date
        LocalDateTime now = LocalDateTime.now();
        Instant inst = now.atZone(ZoneId.systemDefault()).toInstant();
        Date backToDate = Date.from(inst);
        System.out.println("   LocalDateTime → Date: " + backToDate);

        // Calendar → ZonedDateTime
        Calendar calendar = Calendar.getInstance();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(calendar.toInstant(), 
            calendar.getTimeZone().toZoneId());
        System.out.println("   Calendar → ZonedDateTime: " + zdt);

        // Conversion reference
        System.out.println("\n   📋 Conversion Cheat Sheet:");
        System.out.println("   ┌──────────────────────┬──────────────────────────────────────┐");
        System.out.println("   │ From                 │ To                                   │");
        System.out.println("   ├──────────────────────┼──────────────────────────────────────┤");
        System.out.println("   │ Date                 │ date.toInstant()                     │");
        System.out.println("   │ Instant              │ Date.from(instant)                   │");
        System.out.println("   │ Calendar             │ cal.toInstant() + ZoneId             │");
        System.out.println("   │ java.sql.Date        │ sqlDate.toLocalDate()                │");
        System.out.println("   │ java.sql.Timestamp   │ ts.toLocalDateTime()                 │");
        System.out.println("   │ TimeZone             │ tz.toZoneId()                        │");
        System.out.println("   └──────────────────────┴──────────────────────────────────────┘");

        System.out.println();
    }

    // ============================================================
    // 10. Real-World Examples
    // ============================================================
    static void realWorldExamples() {
        System.out.println("🔟 REAL-WORLD DATE/TIME EXAMPLES");
        System.out.println("─────────────────────────────────\n");

        // Example 1: Calculate business days between dates
        System.out.println("   📌 Business Days Calculator:");
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        long businessDays = startDate.datesUntil(endDate)
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY &&
                            d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
        System.out.println("   Business days in June 2024: " + businessDays);

        // Example 2: Generate monthly report dates
        System.out.println("\n   📌 Last Day of Each Month 2024:");
        LocalDate jan = LocalDate.of(2024, 1, 1);
        for (int i = 0; i < 12; i++) {
            LocalDate lastDay = jan.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
            System.out.println("   " + lastDay.getMonth() + ": " + lastDay);
        }

        // Example 3: Time zone-aware meeting scheduler
        System.out.println("\n   📌 Meeting Scheduler (10 AM IST):");
        ZonedDateTime meetingIST = ZonedDateTime.of(2024, 6, 20, 10, 0, 0, 0,
                ZoneId.of("Asia/Kolkata"));
        String[] cities = {"America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney"};
        for (String city : cities) {
            ZonedDateTime local = meetingIST.withZoneSameInstant(ZoneId.of(city));
            System.out.println("   " + city.split("/")[1] + ": " +
                local.format(DateTimeFormatter.ofPattern("hh:mm a (z)")));
        }

        // Example 4: Age calculator
        System.out.println("\n   📌 Age Calculator:");
        LocalDate dob = LocalDate.of(1995, 8, 24);
        Period age = Period.between(dob, LocalDate.now());
        System.out.println("   Born: " + dob);
        System.out.printf("   Age: %d years, %d months, %d days%n",
            age.getYears(), age.getMonths(), age.getDays());

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  ✅ Date/Time API: Complete!              ");
        System.out.println("═══════════════════════════════════════════");
    }
}
