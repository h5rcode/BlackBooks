package com.blackbooks.adapters;

import com.blackbooks.model.nonpersistent.LanguageInfo;

public class LanguageItem implements ListItem {

	private final LanguageInfo mLanguage;

	public LanguageItem(LanguageInfo language) {
		mLanguage = language;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.HEADER;
	}

	public LanguageInfo getLanguage() {
		return mLanguage;
	}
}
