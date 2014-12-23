package com.blackbooks.adapters;

import com.blackbooks.model.nonpersistent.BookShelfInfo;

public class BookShelfItem implements ListItem {

	private final BookShelfInfo mBookShelfInfo;

	public BookShelfItem(BookShelfInfo bookShelfInfo) {
		mBookShelfInfo = bookShelfInfo;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header;
	}
	
	public BookShelfInfo getBookShelf() {
		return mBookShelfInfo;
	}
}
