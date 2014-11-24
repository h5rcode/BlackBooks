package com.blackbooks.adapters;

public class CategoryItem implements ListItem {

	private final String mName;
	private final int mTotal;

	public CategoryItem(String name, int total) {
		mName = name;
		mTotal = total;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header;
	}

	public String getName() {
		return mName;
	}
	
	public int getTotal() {
		return mTotal;
	}
}
