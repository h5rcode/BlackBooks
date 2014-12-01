package com.blackbooks.adapters;

public class LanguageItem implements ListItem {

	private final String mDisplayName;
	private final int mTotal;

	public LanguageItem(String displayName, int total) {
		mDisplayName = displayName;
		mTotal = total;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public int getTotal() {
		return mTotal;
	}
}
