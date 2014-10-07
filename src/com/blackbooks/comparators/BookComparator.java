package com.blackbooks.comparators;

import java.util.Comparator;

import com.blackbooks.model.persistent.Book;

/**
 * An implementation of Comparator to compare books by their title.
 */
public class BookComparator implements Comparator<Book> {

	@Override
	public int compare(Book lhs, Book rhs) {
		return lhs.title.compareToIgnoreCase(rhs.title);
	}
}
