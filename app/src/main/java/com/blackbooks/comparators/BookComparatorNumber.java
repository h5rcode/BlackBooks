package com.blackbooks.comparators;

import java.util.Comparator;

import com.blackbooks.model.persistent.Book;

/**
 * An implementation of Comparator to compare books by their number.
 */
public class BookComparatorNumber implements Comparator<Book> {

	@Override
	public int compare(Book lhs, Book rhs) {
		int result;
		if (lhs.number == null && rhs.number == null) {
			result = lhs.title.compareToIgnoreCase(rhs.title);
		} else if (lhs.number == null) {
			result = -1;
		} else if (rhs.number == null) {
			result = 1;
		} else {
			result = lhs.number.compareTo(rhs.number);
		}

		return result;
	}
}
