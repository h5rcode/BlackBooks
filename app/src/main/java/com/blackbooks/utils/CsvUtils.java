package com.blackbooks.utils;

import android.util.Log;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * CSV utility class.
 */
public final class CsvUtils {

    /**
     * Private constructor.
     */
    private CsvUtils() {
    }


    /**
     * Return the resource id of a book property.
     *
     * @param bookProperty Book property enum value.
     * @return Resource id.
     */
    public static int getBookPropertyResourceId(CsvColumn.BookProperty bookProperty) {
        int resId;
        switch (bookProperty) {
            case AUTHORS:
                resId = R.string.label_authors;
                break;
            case CATEGORIES:
                resId = R.string.label_categories;
                break;
            case DESCRIPTION:
                resId = R.string.label_description;
                break;
            case ID:
                resId = R.string.label_book_id;
                break;
            case ISBN_10:
                resId = R.string.label_isbn10;
                break;
            case ISBN_13:
                resId = R.string.label_isbn13;
                break;
            case LANGUAGE_CODE:
                resId = R.string.label_language;
                break;
            case NONE:
                resId = R.string.label_none;
                break;
            case NUMBER:
                resId = R.string.label_series_number;
                break;
            case PAGE_COUNT:
                resId = R.string.label_page_count;
                break;
            case PUBLISHED_DATE:
                resId = R.string.label_published_date;
                break;
            case PUBLISHER:
                resId = R.string.label_publisher;
                break;
            case SERIES:
                resId = R.string.label_series;
                break;
            case SUBTITLE:
                resId = R.string.label_subtitle;
                break;
            case TITLE:
                resId = R.string.label_title;
                break;
            default:
                throw new IllegalArgumentException(bookProperty.toString() + " not found.");
        }
        return resId;
    }

    /**
     * Return the list of columns detected in a CSV file, i.e. the columns read in the first row
     * of the file.
     *
     * @param file            The CSV file.
     * @param columnSeparator The column separator character.
     * @param textQualifier   The text qualifier character.
     * @return The list of columns in the CSV file.
     */
    public static List<CsvColumn> getCsvFileColumns(File file, char columnSeparator, char textQualifier) {
        List<CsvColumn> result = new ArrayList<CsvColumn>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line != null) {
                if (line.startsWith(FileUtils.UTF8_BOM)) {
                    line = line.substring(1);
                }
                String regex = new String(new char[]{columnSeparator});
                String textQualifierString = new String(new char[]{textQualifier});
                String[] columns = line.split(regex);
                int nbColumns = columns.length;
                for (int i = 0; i < nbColumns; i++) {
                    String column = columns[i].trim();
                    column = column.replace(textQualifierString, "");
                    CsvColumn csvColumn = new CsvColumn(i, column);
                    result.add(csvColumn);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LogUtils.TAG, "Could not close buffered reader.", e);
                }
            }
        }
        return result;
    }

    /**
     * Parse the content of a CSV file to a list of BookInfo instances.
     *
     * @param file                   The file to parse.
     * @param columnSeparator        The column separator.
     * @param textQualifier          The text qualifier.
     * @param firstRowContainsHeader A boolean indicating whether the first row of the file contains a header or not.
     * @param csvColumns             The CSV column mapping settings.
     * @return A list of BookInfo instances.
     * @throws InterruptedException if the parsing is interrupted.
     */
    public static List<BookInfo> parseCsvFile(
            File file, char columnSeparator, char textQualifier,
            boolean firstRowContainsHeader, List<CsvColumn> csvColumns) throws InterruptedException {

        final List<BookInfo> books = new ArrayList<BookInfo>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            final String textQualifierString = Character.toString(textQualifier);
            final String regexColumnSeparator = Character.toString(columnSeparator) + "(?=([^" + textQualifierString + "]*" + textQualifierString + "[^" + textQualifierString + "]*" + textQualifierString + ")*[^" + textQualifierString + "]*$)";

            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                lineNumber++;
                if (lineNumber == 1) {
                    if (firstRowContainsHeader) {
                        continue;
                    }
                    if (line.startsWith(FileUtils.UTF8_BOM)) {
                        line = line.substring(1);
                    }
                }

                BookInfo bookInfo = parseLine(csvColumns, regexColumnSeparator, textQualifierString, line, lineNumber);
                if (bookInfo != null) {
                    books.add(bookInfo);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LogUtils.TAG, "Could not close buffered reader.", e);
                }
            }
        }

        return books;
    }

    /**
     * Parse a row to a new instance of BookInfo.
     *
     * @param csvColumns      The CSV column mapping settings for the parsing.
     * @param columnSeparator The column separator.
     * @param textQualifier   The text qualifier.
     * @param line            The line to parse.
     * @param lineNumber      The line number.
     * @return A new instance of BookInfo if the line could be parsed, null otherwise.
     */
    private static BookInfo parseLine(List<CsvColumn> csvColumns, String columnSeparator, String textQualifier, String line, int lineNumber) {
        BookInfo bookInfo;
        final String[] values = line.split(columnSeparator);

        final int nbCsvColumns = csvColumns.size();
        if (values.length <= nbCsvColumns) {
            bookInfo = new BookInfo();
            for (int i = 0; i < values.length; i++) {
                final CsvColumn csvColumn = csvColumns.get(i);
                final CsvColumn.BookProperty bookProperty = csvColumn.getBookProperty();

                if (bookProperty != null && bookProperty != CsvColumn.BookProperty.NONE) {
                    String value = values[i];
                    if (value.startsWith(textQualifier)) {
                        value = value.substring(1);
                    }
                    if (value.endsWith(textQualifier)) {
                        value = value.substring(0, value.length() - 1);
                    }
                    parseValue(bookProperty, value, bookInfo);
                }
            }
        } else {
            bookInfo = null;
            final String msg = String.format("Line %d has %d columns. %d expected.", lineNumber, values.length, nbCsvColumns);
            Log.w(LogUtils.TAG, msg);
        }

        return bookInfo;
    }

    /**
     * Parse the value of a book property and set it to the corresponding instance of BookInfo.
     *
     * @param bookProperty The book property to parse.
     * @param value        The value to parse.
     * @param bookInfo     The book.
     */
    private static void parseValue(CsvColumn.BookProperty bookProperty, String value, BookInfo bookInfo) {
        value = value.trim();

        if (!value.isEmpty()) {

            switch (bookProperty) {
                case ID:
                    bookInfo.id = parseLong(value);
                    break;
                case TITLE:
                    bookInfo.title = value;
                    break;
                case SUBTITLE:
                    bookInfo.subtitle = value;
                    break;
                case AUTHORS:
                    // TODO : Handle the commas.
                    String[] authorNames = parseStringArray(value);
                    for (String authorName : authorNames) {
                        Author author = new Author();
                        author.name = authorName;
                        bookInfo.authors.add(author);
                    }
                    break;
                case CATEGORIES:
                    // TODO : Handle the commas.
                    String[] categoryNames = parseStringArray(value);
                    for (String categoryName : categoryNames) {
                        Category category = new Category();
                        category.name = categoryName;
                        bookInfo.categories.add(category);
                    }
                    break;
                case SERIES:
                    bookInfo.series.name = value;
                    break;
                case NUMBER:
                    bookInfo.number = parseLong(value);
                    break;
                case PAGE_COUNT:
                    bookInfo.pageCount = parseLong(value);
                    break;
                case LANGUAGE_CODE:
                    bookInfo.languageCode = value;
                    break;
                case DESCRIPTION:
                    bookInfo.description = value;
                    break;
                case PUBLISHER:
                    bookInfo.publisher.name = value;
                    break;
                case PUBLISHED_DATE:
                    bookInfo.publishedDate = parseDate(value);
                    break;
                case ISBN_10:
                    bookInfo.isbn10 = value;
                    break;
                case ISBN_13:
                    bookInfo.isbn13 = value;
                    break;
            }
        }
    }

    /**
     * Tries to parse a string value to a date.
     *
     * @param value The value to parse.
     * @return The date value corresponding to the string value, or null if the parsing failed.
     */
    private static Date parseDate(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(value);
        } catch (ParseException e) {
            String msg = String.format("Could not parse string value '%s' to date.", value);
            Log.w(LogUtils.TAG, msg, e);
        }
        return date;
    }

    /**
     * Tries to parse a string value to a long.
     *
     * @param value The value to parse.
     * @return The long value corresponding to the string value, or null if the parsing failed.
     */
    private static Long parseLong(String value) {
        Long longValue = null;
        try {
            longValue = Long.parseLong(value);
        } catch (NumberFormatException e) {
            String msg = String.format("Could not parse string value '%s' to long.", value);
            Log.w(LogUtils.TAG, msg, e);
        }
        return longValue;
    }

    /**
     * Split a string value.
     * TODO Make the separator a parameter.
     *
     * @param value The string value.
     * @return The sub-values.
     */
    private static String[] parseStringArray(String value) {
        String[] strings = value.split(",");
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].trim();
        }
        return strings;
    }
}
