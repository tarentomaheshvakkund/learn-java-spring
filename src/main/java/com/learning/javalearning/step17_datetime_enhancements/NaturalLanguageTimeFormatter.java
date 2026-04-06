package com.learning.javalearning.step17_datetime_enhancements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Natural Language Time Formatter - Practical Application of Java 16 Day Periods
 *
 * <p>Demonstrates how to build user-friendly, natural-language time displays
 * using the Java 16 'B' pattern (day periods) combined with relative date
 * formatting techniques.</p>
 *
 * <h2>Real-World Use Cases:</h2>
 * <ul>
 *   <li>Chat applications showing "Today at 3:30 in the afternoon"</li>
 *   <li>Calendar apps with "Tomorrow morning at 9:00"</li>
 *   <li>Social media timestamps like "Yesterday at 8 in the evening"</li>
 *   <li>Scheduling displays with friendly descriptions</li>
 * </ul>
 *
 * @see java.time.format.DateTimeFormatter
 */
public class NaturalLanguageTimeFormatter {

    private static final Logger LOGGER = Logger.getLogger(NaturalLanguageTimeFormatter.class.getName());

    private static final String SEPARATOR = "─".repeat(50);
    private static final String SECTION_SEPARATOR = "═".repeat(50);

    private static final String SENDER_ALICE = "Alice";
    private static final String SENDER_BOB = "Bob";

    // Formatters with day period support
    private static final DateTimeFormatter TIME_WITH_PERIOD =
            DateTimeFormatter.ofPattern("h:mm B", Locale.US);

    private static final DateTimeFormatter FULL_DATETIME_NATURAL =
            DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' h:mm B", Locale.US);

    private static final DateTimeFormatter SHORT_DATETIME_NATURAL =
            DateTimeFormatter.ofPattern("MMM d 'at' h:mm B", Locale.US);

    private NaturalLanguageTimeFormatter() {
        // Utility class - prevent instantiation
    }

    public static void main(String[] args) {
        LOGGER.info(SECTION_SEPARATOR);
        LOGGER.info("  Natural Language Time Formatter");
        LOGGER.info(SECTION_SEPARATOR);

        demonstrateRelativeDateFormatting();
        demonstrateChatStyleTimestamps();
        demonstrateScheduleDisplay();
        demonstrateTimeRangeDescriptions();
    }

    // ============================================================
    // 1. Relative Date Formatting
    // ============================================================
    private static void demonstrateRelativeDateFormatting() {
        LOGGER.info("1️⃣ RELATIVE DATE FORMATTING");
        LOGGER.info(SEPARATOR);

        LocalDateTime referenceNow = LocalDateTime.of(2026, 2, 24, 15, 30);
        LOGGER.info(() -> "   Reference 'now': " + referenceNow);
        LOGGER.info("");

        // Various relative times
        List<LocalDateTime> timestamps = List.of(
                referenceNow.minusMinutes(2),
                referenceNow.minusMinutes(30),
                referenceNow.minusHours(3),
                referenceNow.minusDays(1).withHour(20).withMinute(15),
                referenceNow.minusDays(2).withHour(9).withMinute(0),
                referenceNow.minusDays(5).withHour(14).withMinute(45),
                referenceNow.plusHours(2),
                referenceNow.plusDays(1).withHour(10).withMinute(0)
        );

        for (LocalDateTime timestamp : timestamps) {
            String formatted = formatRelative(timestamp, referenceNow);
            LOGGER.info(() -> "   " + timestamp + " -> " + formatted);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 2. Chat-Style Timestamps
    // ============================================================
    private static void demonstrateChatStyleTimestamps() {
        LOGGER.info("2️⃣ CHAT-STYLE TIMESTAMPS");
        LOGGER.info(SEPARATOR);

        LocalDateTime now = LocalDateTime.of(2026, 2, 24, 15, 30);

        // Simulate a chat conversation with timestamps
        record ChatMessage(String sender, String message, LocalDateTime time) {}

        List<ChatMessage> messages = List.of(
                new ChatMessage(SENDER_ALICE, "Good morning!", now.withHour(8).withMinute(0)),
                new ChatMessage(SENDER_BOB, "Morning! How's the project going?", now.withHour(8).withMinute(5)),
                new ChatMessage(SENDER_ALICE, "Let's sync up after lunch", now.withHour(11).withMinute(45)),
                new ChatMessage(SENDER_BOB, "Sure, 2 PM works?", now.withHour(12).withMinute(30)),
                new ChatMessage(SENDER_ALICE, "Perfect, see you then", now.withHour(12).withMinute(32)),
                new ChatMessage(SENDER_BOB, "Here's the update", now.withHour(14).withMinute(0)),
                new ChatMessage(SENDER_ALICE, "Looks great!", now.withHour(14).withMinute(15))
        );

        LocalDate previousDate = null;
        for (ChatMessage msg : messages) {
            LocalDate messageDate = msg.time().toLocalDate();
            if (!messageDate.equals(previousDate)) {
                String dateHeader = formatDateHeader(messageDate, now.toLocalDate());
                LOGGER.info(() -> "   --- " + dateHeader + " ---");
                previousDate = messageDate;
            }
            String timeStr = msg.time().toLocalTime().format(TIME_WITH_PERIOD);
            LOGGER.info(() -> "   [" + timeStr + "] " + msg.sender() + ": " + msg.message());
        }
        LOGGER.info("");
    }

    // ============================================================
    // 3. Schedule Display
    // ============================================================
    private static void demonstrateScheduleDisplay() {
        LOGGER.info("3️⃣ SCHEDULE DISPLAY");
        LOGGER.info(SEPARATOR);

        record ScheduleItem(String title, LocalDateTime start, LocalDateTime end) {}

        LocalDate today = LocalDate.of(2026, 2, 24);

        List<ScheduleItem> schedule = List.of(
                new ScheduleItem("Team Standup",
                        today.atTime(9, 0), today.atTime(9, 30)),
                new ScheduleItem("Design Review",
                        today.atTime(10, 30), today.atTime(11, 30)),
                new ScheduleItem("Lunch Break",
                        today.atTime(12, 0), today.atTime(13, 0)),
                new ScheduleItem("Sprint Planning",
                        today.atTime(14, 0), today.atTime(15, 30)),
                new ScheduleItem("Code Review",
                        today.atTime(16, 0), today.atTime(17, 0)),
                new ScheduleItem("Team Dinner",
                        today.atTime(19, 0), today.atTime(21, 0))
        );

        LOGGER.info(() -> "   📅 Schedule for " + today.format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.US)));
        LOGGER.info("");

        for (ScheduleItem item : schedule) {
            String startTime = item.start().toLocalTime().format(TIME_WITH_PERIOD);
            String endTime = item.end().toLocalTime().format(TIME_WITH_PERIOD);
            long durationMinutes = ChronoUnit.MINUTES.between(item.start(), item.end());
            String display = String.format("   %-20s %s - %s (%d min)",
                    item.title(), startTime, endTime, durationMinutes);
            LOGGER.info(display);
        }
        LOGGER.info("");
    }

    // ============================================================
    // 4. Time Range Descriptions
    // ============================================================
    private static void demonstrateTimeRangeDescriptions() {
        LOGGER.info("4️⃣ TIME RANGE DESCRIPTIONS");
        LOGGER.info(SEPARATOR);

        List<LocalTime> times = List.of(
                LocalTime.of(0, 0),
                LocalTime.of(5, 30),
                LocalTime.of(6, 0),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 30),
                LocalTime.of(17, 0),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                LocalTime.of(21, 0),
                LocalTime.of(23, 59)
        );

        LOGGER.info("   Time classification using day periods:");
        LOGGER.info("");

        for (LocalTime time : times) {
            String classification = classifyTimeOfDay(time);
            String formattedTime = time.format(TIME_WITH_PERIOD);
            String emoji = getTimeEmoji(time);
            String display = String.format("   %s %-25s (%s → %s)",
                    emoji, formattedTime, time, classification);
            LOGGER.info(display);
        }
        LOGGER.info("");
    }

    // ============================================================
    // Utility Methods
    // ============================================================

    /**
     * Formats a timestamp relative to a reference "now" time, combining
     * relative date labels with natural day-period time display.
     *
     * @param timestamp the timestamp to format
     * @param now       the reference "now" time
     * @return a human-friendly relative timestamp string
     */
    static String formatRelative(LocalDateTime timestamp, LocalDateTime now) {
        long minutesDiff = ChronoUnit.MINUTES.between(timestamp, now);
        long daysDiff = ChronoUnit.DAYS.between(timestamp.toLocalDate(), now.toLocalDate());

        // Future timestamps
        if (minutesDiff < 0) {
            long absDaysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), timestamp.toLocalDate());
            if (absDaysDiff == 0) {
                return "Later today at " + timestamp.toLocalTime().format(TIME_WITH_PERIOD);
            } else if (absDaysDiff == 1) {
                return "Tomorrow at " + timestamp.toLocalTime().format(TIME_WITH_PERIOD);
            }
            return timestamp.format(FULL_DATETIME_NATURAL);
        }

        // Past timestamps
        if (minutesDiff < 1) {
            return "Just now";
        } else if (minutesDiff < 60) {
            return minutesDiff + " minute" + (minutesDiff == 1 ? "" : "s") + " ago";
        } else if (daysDiff == 0) {
            long hours = ChronoUnit.HOURS.between(timestamp, now);
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (daysDiff == 1) {
            return "Yesterday at " + timestamp.toLocalTime().format(TIME_WITH_PERIOD);
        } else if (daysDiff < 7) {
            return daysDiff + " days ago at " + timestamp.toLocalTime().format(TIME_WITH_PERIOD);
        }
        return timestamp.format(SHORT_DATETIME_NATURAL);
    }

    /**
     * Formats a date as a relative header label (Today, Yesterday, or full date).
     *
     * @param date  the date to format
     * @param today the reference "today" date
     * @return a header label such as "Today", "Yesterday", or "Monday, February 24"
     */
    static String formatDateHeader(LocalDate date, LocalDate today) {
        long daysDiff = ChronoUnit.DAYS.between(date, today);
        if (daysDiff == 0) {
            return "Today";
        } else if (daysDiff == 1) {
            return "Yesterday";
        }
        return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.US));
    }

    /**
     * Classifies a time into a human-readable description of the time of day.
     *
     * @param time the time to classify
     * @return a description such as "Early Morning", "Morning", "Afternoon", etc.
     */
    static String classifyTimeOfDay(LocalTime time) {
        int hour = time.getHour();
        if (hour < 5) {
            return "Night";
        } else if (hour < 8) {
            return "Early Morning";
        } else if (hour < 12) {
            return "Morning";
        } else if (hour < 13) {
            return "Noon";
        } else if (hour < 17) {
            return "Afternoon";
        } else if (hour < 21) {
            return "Evening";
        }
        return "Night";
    }

    /**
     * Returns an emoji representing the time of day.
     *
     * @param time the time of day
     * @return an emoji string corresponding to the time
     */
    private static String getTimeEmoji(LocalTime time) {
        int hour = time.getHour();
        if (hour < 6) {
            return "🌙";
        } else if (hour < 12) {
            return "🌅";
        } else if (hour < 18) {
            return "☀️";
        }
        return "🌆";
    }
}
