package com.blackbooks.utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;

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
	 * Test if a string value represents an integer.
	 * 
	 * @param stringValue
	 *            The string value.
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
	 * @param values
	 *            The string values to join.
	 * @param separator
	 *            The separator.
	 * @return The joined string values.
	 */
	public static String join(String[] values, String separator) {
		String result = null;
		if (values != null) {
			if (separator == null) {
				throw new InvalidParameterException("The parameter 'separator' cannot be null");
			}
			StringBuffer sb = new StringBuffer();
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
	 * @param authorList
	 *            List of authors.
	 * @param separator
	 *            The separator.
	 * @return A string value containing the names of the authors.
	 */
	public static String joinAuthorNameList(ArrayList<Author> authorList, String separator) {
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
	 * @param categoryList
	 *            List of categories.
	 * @param separator
	 *            The separator.
	 * @return A string value containing the names of the categories.
	 */
	public static String joinCategoryNameList(ArrayList<Category> categoryList, String separator) {
		String[] categoryNameList = new String[categoryList.size()];
		for (int i = 0; i < categoryList.size(); i++) {
			Category category = categoryList.get(i);
			categoryNameList[i] = category.name;
		}
		return join(categoryNameList, separator);
	}
}
