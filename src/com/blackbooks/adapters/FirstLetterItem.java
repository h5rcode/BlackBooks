package com.blackbooks.adapters;

public class FirstLetterItem implements ListItem {

	private String mValue;

	public FirstLetterItem(String value) {
		mValue = value;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header;
	}
	
	public String getValue() {
		return mValue;
	}
}
