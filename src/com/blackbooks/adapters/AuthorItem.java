package com.blackbooks.adapters;

import com.blackbooks.model.nonpersistent.AuthorInfo;

public class AuthorItem implements ListItem {

	private final AuthorInfo mAuthor;

	public AuthorItem(AuthorInfo author) {
		this.mAuthor = author;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.HEADER;
	}

	public AuthorInfo getAuthor() {
		return mAuthor;
	}
}
