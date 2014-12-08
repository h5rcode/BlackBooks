package com.blackbooks.comparators;

import java.util.Comparator;

import com.blackbooks.model.persistent.Book;

public class BookComparatorNumber implements Comparator<Book> {
	
	private final AlphanumComparator<String> mAlphanumComparator = new AlphanumComparator<String>();

	@Override
	public int compare(Book lhs, Book rhs) {
		int result = 0;
		if (lhs.number == null && rhs.number == null) {
			result = mAlphanumComparator.compare(lhs.title, rhs.title);
		} else if (lhs.number == null && rhs.number != null) {
			result = -1;
		} else if (lhs.number != null && rhs.number == null) {
			result = 1;
		} else {
			result = mAlphanumComparator.compare(lhs.number, rhs.number);
		}
		
		return result;
	}
}
