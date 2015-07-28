package com.blackbooks.utils;

import android.util.Log;

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
     * Return the list of columns detected in a CSV file, i.e. the columns read in the first row
     * of the file.
     *
     * @param file            The CSV file.
     * @param columnSeparator The column separator character.
     * @param textQualifier   The text qualifier character.
     * @return The list of columns in the CSV file.
     */
    public static List<String> getCsvFileColumns(File file, char columnSeparator, char textQualifier) {
        List<String> result = new ArrayList<String>();
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
                    if (i < nbColumns - 1 || !column.isEmpty()) {
                        result.add(column);
                    }
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
