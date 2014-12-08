package com.blackbooks.comparators;

import java.util.Comparator;

import com.blackbooks.model.persistent.Book;

/**
 * An implementation of Comparator to compare books by their title.
 */
public class BookComparatorTitle implements Comparator<Book> {

	private final AlphanumComparator<String> mAlphanumComparator = new AlphanumComparator<String>();

	@Override
	public int compare(Book lhs, Book rhs) {
		return mAlphanumComparator.compare(lhs.title, rhs.title);
	}
}
