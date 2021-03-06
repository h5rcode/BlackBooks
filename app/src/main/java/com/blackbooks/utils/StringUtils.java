package com.blackbooks.utils;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;

import java.security.InvalidParameterException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * String utility class.
 */
public final class StringUtils {

    /**
     * Private constructor.
     */
    private StringUtils() {
    }

    /**
     * Capitalize a string value.
     *
     * @param stringValue The string value.
     * @return Capitalized string value.
     */
    public static String capitalize(String stringValue) {
        String result = null;
        if (stringValue != null && stringValue.length() > 0) {
            int length = stringValue.length();
            result = stringValue.substring(0, 1).toUpperCase(Locale.getDefault());
            if (length > 1) {
                result += stringValue.substring(1, length);
            }
        }
        return result;
    }

    /**
     * Test if a string value represents an integer.
     *
     * @param stringValue The string value.
     * @return True if the string value represents an integer, false otherwise.
     */
    public static boolean isInteger(String stringValue) {
        boolean isInteger = true;
        if (stringValue == null || stringValue.isEmpty()) {
            isInteger = false;
        } else {
            stringValue = stringValue.trim();
            int stringLength = stringValue.length();

            for (int i = 0; i < stringLength; i++) {
                char c = stringValue.charAt(i);
                if (!Character.isDigit(c) && c != '-' && i != 0) {
                    isInteger = false;
                    break;
                }
            }
        }
        return isInteger;
    }

    /**
     * Join several string values and separate them using a specified separator.
     *
     * @param values    The string values to join.
     * @param separator The separator.
     * @return The joined string values.
     */
    public static String join(String[] values, String separator) {
        String result = null;
        if (values != null) {
            if (separator == null) {
                throw new InvalidParameterException("The parameter 'separator' cannot be null");
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sb.append(values[i]);
                if (i < values.length - 1) {
                    sb.append(separator);
                }
            }
            result = sb.toString();
        }
        return result;
    }

    /**
     * Join the names of a list of authors using the specified separator.
     *
     * @param authorList List of authors.
     * @param separator  The separator.
     * @return A string value containing the names of the authors.
     */
    public static String joinAuthorNameList(List<Author> authorList, String separator) {
        String[] authorNameList = new String[authorList.size()];
        for (int i = 0; i < authorList.size(); i++) {
            Author author = authorList.get(i);
            authorNameList[i] = author.name;
        }
        return join(authorNameList, separator);
    }

    /**
     * Join the names of a list of categories using the specified separator.
     *
     * @param categoryList List of categories.
     * @param separator    The separator.
     * @return A string value containing the names of the categories.
     */
    public static String joinCategoryNameList(List<Category> categoryList, String separator) {
        String[] categoryNameList = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            categoryNameList[i] = category.name;
        }
        return join(categoryNameList, separator);
    }

    /**
     * Normalize a String.
     *
     * @param text String value to normalize.
     * @return Normalized string value.
     */
    public static String normalize(String text) {
        String normalizedText = null;
        if (text != null) {
            normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD) //
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") //
                    .toLowerCase(Locale.getDefault());
        }
        return normalizedText;
    }
}
