package com.blackbooks.adapters;

public class FirstLetterItem implements ListItem {

	private final String mValue;
	private final int mTotalBook;

	public FirstLetterItem(String value, int totalBooks) {
		mValue = value;
		mTotalBook = totalBooks;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header;
	}
	
	public String getValue() {
		return mValue;
	}
	
	public int getTotalBooks() {
		return mTotalBook;
	}
}
