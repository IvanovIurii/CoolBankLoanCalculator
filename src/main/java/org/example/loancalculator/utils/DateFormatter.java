package org.example.loancalculator.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    public static LocalDate format(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static String format(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static String plusMonths(String date, int monthsToAdd) {
        try {
            LocalDate localDate = DateFormatter.format(date).plusMonths(monthsToAdd);
            return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date");
        }
    }
}
