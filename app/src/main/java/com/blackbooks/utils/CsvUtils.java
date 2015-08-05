package com.blackbooks.utils;

import android.util.Log;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.CsvColumn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
}
