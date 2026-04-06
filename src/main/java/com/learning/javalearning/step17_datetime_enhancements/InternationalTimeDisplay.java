package com.learning.javalearning.step17_datetime_enhancements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * International Time Display - Locale-Aware DateTime Formatting
 *
 * <p>Demonstrates internationalized date/time displays using Java's chronology
 * system, locale-specific formatting, and the Java 16 'B' day period pattern
 * across multiple locales and calendar systems.</p>
 *
 * <h2>Calendar Systems Covered:</h2>
 * <ul>
 *   <li>ISO (Gregorian) - default worldwide standard</li>
 *   <li>Japanese Imperial calendar (JapaneseChronology)</li>
 *   <li>Thai Buddhist calendar (ThaiBuddhistChronology)</li>
 *   <li>Hijrah (Islamic) calendar (HijrahChronology)</li>
 *   <li>Minguo (Republic of China) calendar (MinguoChronology)</li>
 * </ul>
 *
 * @see java.time.chrono.Chronology
 * @see java.time.format.DecimalStyle
 */
public class InternationalTimeDisplay {

    private static final Logger LOGGER = Logger.getLogger(InternationalTimeDisplay.class.getName());

    private static final String SEPARATOR = "─".repeat(55);
    private static final String SECTION_SEPARATOR = "═".repeat(55);
    private static final String DAY_PERIOD_PATTERN = "h:mm B";

    private InternationalTimeDisplay() {
        // Utility class - prevent instantiation
    }

    public static void main(String[] args) {
        LOGGER.info(SECTION_SEPARATOR);
        LOGGER.info("  International Time Display");
        LOGGER.info(SECTION_SEPARATOR);

        demonstrateDayPeriodsAcrossLocales();
        demonstrateCalendarSystems();
        demonstrateDecimalStyles();
        demonstrateWorldClockDisplay();
        demonstrateGreetingsByLocale();
    }

    // ============================================================
    // 1. Day Periods Across Global Locales
    // ============================================================
    private static void demonstrateDayPeriodsAcrossLocales() {
        LOGGER.info("1️⃣ DAY PERIODS ACROSS GLOBAL LOCALES");
        LOGGER.info(SEPARATOR);

        // Different times of day to show period transitions
        List<LocalTime> timesOfDay = List.of(
                LocalTime.of(6, 0),
                LocalTime.of(9, 30),
                LocalTime.of(12, 0),
                LocalTime.of(15, 30),
                LocalTime.of(19, 0),
                LocalTime.of(23, 0)
        );

        // Rich set of locales
        List<Locale> locales = List.of(
                Locale.US,
                Locale.FRANCE,
                Locale.GERMANY,
                Locale.JAPAN,
                Locale.CHINA,
                Locale.of("ko", "KR"),
                Locale.of("ar", "SA"),
                Locale.of("hi", "IN"),
                Locale.of("ru", "RU"),
                Locale.of("pt", "BR"),
                Locale.of("es", "MX"),
                Locale.of("th", "TH")
        );

        for (LocalTime time : timesOfDay) {
            LOGGER.info(() -> "   ⏰ " + time + ":");
            for (Locale locale : locales) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_PERIOD_PATTERN, locale);
                String formatted = time.format(formatter);
                String display = String.format("      %-25s: %s",
                        locale.getDisplayName(Locale.US), formatted);
                LOGGER.info(display);
            }
            LOGGER.info("");
        }
    }

    // ============================================================
    // 2. Calendar Systems
    // ============================================================
    private static void demonstrateCalendarSystems() {
        LOGGER.info("2️⃣ CALENDAR SYSTEMS");
        LOGGER.info(SEPARATOR);

        LocalDate isoDate = LocalDate.of(2026, 2, 24);
        LOGGER.info(() -> "   ISO (Gregorian): " + isoDate);
        LOGGER.info("");

        // Japanese Imperial calendar
        JapaneseDate japaneseDate = JapaneseDate.from(isoDate);
        DateTimeFormatter japaneseFormatter = DateTimeFormatter.ofPattern("GGGG y年 M月 d日")
                .withChronology(JapaneseChronology.INSTANCE)
                .withLocale(Locale.JAPAN);
        LOGGER.info(() -> "   Japanese: " + japaneseDate);
        LOGGER.info(() -> "   Japanese (formatted): " + japaneseFormatter.format(japaneseDate));
        LOGGER.info(() -> "   Japanese Era: " + japaneseDate.getChronology().getId());
        LOGGER.info("");

        // Thai Buddhist calendar
        ThaiBuddhistDate thaiDate = ThaiBuddhistDate.from(isoDate);
        DateTimeFormatter thaiFormatter = DateTimeFormatter.ofPattern("d MMMM GGGG y")
                .withChronology(ThaiBuddhistChronology.INSTANCE)
                .withLocale(Locale.of("th", "TH"));
        LOGGER.info(() -> "   Thai Buddhist: " + thaiDate);
        LOGGER.info(() -> "   Thai (formatted): " + thaiFormatter.format(thaiDate));
        LOGGER.info("");

        // Hijrah (Islamic) calendar
        HijrahDate hijrahDate = HijrahDate.from(isoDate);
        DateTimeFormatter hijrahFormatter = DateTimeFormatter.ofPattern("d MMMM y GGGG")
                .withChronology(HijrahChronology.INSTANCE)
                .withLocale(Locale.of("ar", "SA"));
        LOGGER.info(() -> "   Hijrah: " + hijrahDate);
        LOGGER.info(() -> "   Hijrah (formatted): " + hijrahFormatter.format(hijrahDate));
        LOGGER.info("");

        // Minguo (Republic of China) calendar
        MinguoDate minguoDate = MinguoDate.from(isoDate);
        DateTimeFormatter minguoFormatter = DateTimeFormatter.ofPattern("GGGG y年 M月 d日")
                .withChronology(MinguoChronology.INSTANCE)
                .withLocale(Locale.TAIWAN);
        LOGGER.info(() -> "   Minguo: " + minguoDate);
        LOGGER.info(() -> "   Minguo (formatted): " + minguoFormatter.format(minguoDate));
        LOGGER.info("");
    }

    // ============================================================
    // 3. Decimal Styles (Numeral Systems)
    // ============================================================
    private static void demonstrateDecimalStyles() {
        LOGGER.info("3️⃣ DECIMAL STYLES (NUMERAL SYSTEMS)");
        LOGGER.info(SEPARATOR);

        LocalDateTime dateTime = LocalDateTime.of(2026, 2, 24, 15, 30, 0);

        // Locales with different numeral systems
        Map<String, Locale> numeralLocales = Map.of(
                "Latin (US)", Locale.US,
                "Arabic-Indic", Locale.of("ar", "SA"),
                "Devanagari (Hindi)", Locale.of("hi", "IN"),
                "Thai", Locale.of("th", "TH"),
                "Japanese", Locale.JAPAN
        );

        for (Map.Entry<String, Locale> entry : numeralLocales.entrySet()) {
            Locale locale = entry.getValue();
            DecimalStyle decimalStyle = DecimalStyle.of(locale);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withDecimalStyle(decimalStyle)
                    .withLocale(locale);

            String formatted = dateTime.format(formatter);
            String display = String.format("   %-25s: %s (zero='%c')",
                    entry.getKey(), formatted, decimalStyle.getZeroDigit());
            LOGGER.info(display);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 4. World Clock Display
    // ============================================================
    private static void demonstrateWorldClockDisplay() {
        LOGGER.info("4️⃣ WORLD CLOCK WITH DAY PERIODS");
        LOGGER.info(SEPARATOR);

        // Reference time: Feb 24, 2026 15:30 UTC
        ZonedDateTime utcTime = ZonedDateTime.of(
                LocalDateTime.of(2026, 2, 24, 15, 30, 0),
                ZoneId.of("UTC")
        );
        LOGGER.info(() -> "   Reference: " + utcTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        LOGGER.info("");

        record CityInfo(String city, ZoneId zoneId, Locale locale) {}

        List<CityInfo> cities = List.of(
                new CityInfo("New York", ZoneId.of("America/New_York"), Locale.US),
                new CityInfo("Los Angeles", ZoneId.of("America/Los_Angeles"), Locale.US),
                new CityInfo("São Paulo", ZoneId.of("America/Sao_Paulo"), Locale.of("pt", "BR")),
                new CityInfo("London", ZoneId.of("Europe/London"), Locale.UK),
                new CityInfo("Paris", ZoneId.of("Europe/Paris"), Locale.FRANCE),
                new CityInfo("Berlin", ZoneId.of("Europe/Berlin"), Locale.GERMANY),
                new CityInfo("Moscow", ZoneId.of("Europe/Moscow"), Locale.of("ru", "RU")),
                new CityInfo("Dubai", ZoneId.of("Asia/Dubai"), Locale.of("ar", "AE")),
                new CityInfo("Mumbai", ZoneId.of("Asia/Kolkata"), Locale.of("hi", "IN")),
                new CityInfo("Tokyo", ZoneId.of("Asia/Tokyo"), Locale.JAPAN),
                new CityInfo("Shanghai", ZoneId.of("Asia/Shanghai"), Locale.CHINA),
                new CityInfo("Sydney", ZoneId.of("Australia/Sydney"), Locale.of("en", "AU"))
        );

        for (CityInfo city : cities) {
            ZonedDateTime cityTime = utcTime.withZoneSameInstant(city.zoneId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "EEE, h:mm B (z)", city.locale());
            String formatted = cityTime.format(formatter);
            String dayIndicator = getDayDiffIndicator(utcTime.toLocalDate(), cityTime.toLocalDate());
            String display = String.format("   🕐 %-15s: %s %s",
                    city.city(), formatted, dayIndicator);
            LOGGER.info(display);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 5. Locale-Appropriate Greetings
    // ============================================================
    private static void demonstrateGreetingsByLocale() {
        LOGGER.info("5️⃣ LOCALE-APPROPRIATE GREETINGS");
        LOGGER.info(SEPARATOR);

        List<GreetingSet> greetings = List.of(
                new GreetingSet(Locale.US, "Good morning", "Good afternoon", "Good evening"),
                new GreetingSet(Locale.FRANCE, "Bonjour", "Bon après-midi", "Bonsoir"),
                new GreetingSet(Locale.GERMANY, "Guten Morgen", "Guten Tag", "Guten Abend"),
                new GreetingSet(Locale.JAPAN, "おはようございます", "こんにちは", "こんばんは"),
                new GreetingSet(Locale.of("es", "ES"), "Buenos días", "Buenas tardes", "Buenas noches"),
                new GreetingSet(Locale.of("hi", "IN"), "सुप्रभात", "नमस्कार", "शुभ संध्या"),
                new GreetingSet(Locale.of("pt", "BR"), "Bom dia", "Boa tarde", "Boa noite"),
                new GreetingSet(Locale.of("ko", "KR"), "좋은 아침", "안녕하세요", "좋은 저녁")
        );

        List<LocalTime> timesForGreeting = List.of(
                LocalTime.of(9, 0),
                LocalTime.of(14, 0),
                LocalTime.of(20, 0)
        );

        for (GreetingSet gs : greetings) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_PERIOD_PATTERN, gs.locale());
            String localeName = gs.locale().getDisplayName(Locale.US);

            LOGGER.info(() -> "   " + localeName + ":");
            for (LocalTime time : timesForGreeting) {
                String formattedTime = time.format(formatter);
                String greeting = selectGreeting(time, gs);
                String display = String.format("      %s -> %s (%s)", formattedTime, greeting, time);
                LOGGER.info(display);
            }
            LOGGER.info("");
        }
    }

    // ============================================================
    // Utility Methods
    // ============================================================

    /**
     * Returns a day difference indicator for world clock display.
     * Shows +1 or -1 if the date differs from the reference date.
     *
     * @param referenceDate the reference date
     * @param targetDate    the target city's date
     * @return "+1" if next day, "-1" if previous day, empty string if same day
     */
    private static String getDayDiffIndicator(LocalDate referenceDate, LocalDate targetDate) {
        long diff = targetDate.toEpochDay() - referenceDate.toEpochDay();
        if (diff > 0) {
            return "(+1 day)";
        } else if (diff < 0) {
            return "(-1 day)";
        }
        return "";
    }

    /**
     * Selects the appropriate greeting based on time of day.
     *
     * @param time the current time
     * @param gs   the greeting set for the locale
     * @return the appropriate greeting string
     */
    private static String selectGreeting(LocalTime time, GreetingSet gs) {
        int hour = time.getHour();
        if (hour < 12) {
            return gs.morning();
        } else if (hour < 18) {
            return gs.afternoon();
        }
        return gs.evening();
    }

    /**
     * Holds locale-specific greetings for morning, afternoon, and evening.
     *
     * @param locale    the locale this greeting set applies to
     * @param morning   greeting for morning hours (before 12:00)
     * @param afternoon greeting for afternoon hours (12:00-17:59)
     * @param evening   greeting for evening hours (18:00+)
     */
    private record GreetingSet(Locale locale, String morning, String afternoon, String evening) {}
}
