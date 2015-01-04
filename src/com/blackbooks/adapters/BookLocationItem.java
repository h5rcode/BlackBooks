package com.blackbooks.adapters;

import com.blackbooks.model.nonpersistent.BookLocationInfo;

public class BookLocationItem implements ListItem {

	private final BookLocationInfo mBookLocationInfo;

	public BookLocationItem(BookLocationInfo bookLocatonInfo) {
		mBookLocationInfo = bookLocatonInfo;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.HEADER;
	}
	
	public BookLocationInfo getBookLocation() {
		return mBookLocationInfo;
	}
}
