package com.blackbooks.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date utility class.
 */
public final class DateUtils {

    /**
     * The default date format in the application: dd/MM/yyyy.
     */
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    /**
     * Private constructor.
     */
    private DateUtils() {
    }

    /**
     * <p>
     * Tries to parse a date in three different formats :
     * <ol>
     * <li>A format where the date contains a year, a month a day.</li>
     * <li>A format where the date contains a year and a month.</li>
     * <li>A format where the date contains only a year.</li>
     * </ol>
     * </p>
     * <p>
     * The method returns the first date that was successfully parsed, and
     * throws an {@link IllegalArgumentException} if none of the three parsing
     * attempts succeeded.
     * </p>
     *
     * @param dateString         The string value to parse.
     * @param formatYearMonthDay The format containing the year, the month and the day.
     * @param formatYearMonth    The format containing the year and the month.
     * @param formatYear         The format containing only the year.
     * @param locale             The locale to be used during the attempts to parse.
     * @return The parsed Date or null if the input string was null.
     */
    public static Date parse(String dateString, String formatYearMonthDay, String formatYearMonth, String formatYear,
                             Locale locale) {

        Date date = null;
        if (dateString != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatYearMonthDay, locale);
            dateFormat.setLenient(false);
            date = parse(dateString, dateFormat);

            if (date == null) {
                dateFormat = new SimpleDateFormat(formatYearMonth, locale);
                dateFormat.setLenient(false);
                date = parse(dateString, dateFormat);

                if (date == null) {
                    dateFormat = new SimpleDateFormat(formatYear, locale);
                    dateFormat.setLenient(false);
                    date = parse(dateString, dateFormat);

                    if (date == null) {
                        String message = String.format("Could not parse date \'%s'\'.", dateString);
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }
        return date;
    }

    /**
     * Try to parse a date using a given {@link DateFormat}. If the input string
     * value is null or if the parsing fails, return null.
     *
     * @param dateString The string value.
     * @param dateFormat {@link DateFormat}.
     * @return Date.
     */
    private static Date parse(String dateString, DateFormat dateFormat) {
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }
}
