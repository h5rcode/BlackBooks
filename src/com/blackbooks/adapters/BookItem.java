package com.blackbooks.adapters;

import com.blackbooks.model.nonpersistent.BookInfo;

public class BookItem implements ListItem {

	private final BookInfo mBook;

	public BookItem(BookInfo book) {
		mBook = book;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Entry;
	}

	public BookInfo getBook() {
		return mBook;
	}
}
