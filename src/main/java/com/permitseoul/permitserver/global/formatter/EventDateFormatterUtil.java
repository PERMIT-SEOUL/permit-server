package com.permitseoul.permitserver.global.formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class EventDateFormatterUtil {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.ENGLISH);         //Sun
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH);       //25
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH); //May 2025

    public static String formatEventDate(final LocalDateTime startDate, final LocalDateTime endDate) {
        final StringBuilder sb = new StringBuilder();
        if (startDate.toLocalDate().equals(endDate.toLocalDate())) {
            sb.append(startDate.format(DAY_FORMATTER))
                    .append(", ")
                    .append(startDate.format(DATE_FORMATTER))
                    .append(" ")
                    .append(startDate.format(MONTH_YEAR_FORMATTER));
        } else {
            sb.append(startDate.format(DAY_FORMATTER))
                    .append("–")
                    .append(endDate.format(DAY_FORMATTER))
                    .append(", ")
                    .append(startDate.format(DATE_FORMATTER))
                    .append("–")
                    .append(endDate.format(DATE_FORMATTER))
                    .append(" ")
                    .append(endDate.format(MONTH_YEAR_FORMATTER));
        }
        return sb.toString();
    }
}
