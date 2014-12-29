package com.blackbooks.adapters;

import com.blackbooks.model.nonpersistent.CategoryInfo;

public class CategoryItem implements ListItem {

	private final CategoryInfo mCategory;

	public CategoryItem(CategoryInfo category) {
		mCategory = category;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.HEADER;
	}

	public CategoryInfo getCategory() {
		return mCategory;
	}
}
